/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package jxbattle.model.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import jxbattle.bean.common.game.GameState;
import jxbattle.bean.common.game.UserCommand;
import jxbattle.bean.common.message.XBNetMessage;
import jxbattle.bean.common.message.XBNetMessageId;
import jxbattle.bean.common.parameters.game.InitialisationParameters;
import jxbattle.bean.common.player.ColorId;
import jxbattle.bean.common.player.PlayerState;
import jxbattle.bean.server.ClientInfo;
import jxbattle.bean.server.GameParametersProfile;
import jxbattle.common.net.XBPeer;
import jxbattle.model.PeerEngine;
import jxbattle.model.common.DebugLog;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.game.PlayerInfos.SelectedPlayerIterator;
import jxbattle.model.common.observer.XBMVCModelChangeId;
import jxbattle.model.common.parameters.SystemParametersModel;
import jxbattle.model.common.parameters.game.GameParametersModel;
import jxbattle.model.common.timer.EngineTimer;
import jxbattle.model.common.timer.TimerManager;
import jxbattle.model.server.automaton.ServerAutomatonStateId;
import jxbattle.model.server.automaton.ServerConditionId;
import jxbattle.model.server.automaton.ServerTransitionComputationId;

import org.generic.bean.Message;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.automaton.AutomatonModel2;
import org.generic.mvc.model.automaton.TransitionRule;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.net.NetException;
import org.generic.net.NetModelChangeId;
import org.generic.thread.ThreadUtils;

//public class ServerEngine extends AutomatonImpl<ServerAutomatonStateId, ServerConditionId, ServerTransitionComputationId> implements MVCModelObserver, PeerEngine
public class ServerEngine extends AutomatonModel2<ServerAutomatonStateId, ServerConditionId, ServerTransitionComputationId> implements MVCModelObserver, PeerEngine
{
    /**
     * list of received network/MVC messages
     */
    private ArrayBlockingQueue<Message> inputMessages;

    private List<UserCommand> pendingUserCommands;

    /**
     * timers manager
     */
    private TimerManager timerManager;

    /**
     * board update ticks timer
     */
    private UpdateTickTimer updateTickTimer;

    /**
     * client disconnection detection timer
     */
    //private EngineTimer clientDisconnectTimer;

    /**
     * board update tick counter, used to implement flush frequency
     */
    private int updateTickCount;

    /**
     * message sequence number for "board update" and "user command" messages
     */
    private int commandSeqGen;

    /**
     * last message sequence number to be acknowledged
     */
    private int lastNotAckedSeq;

    private DebugLog debugLog;

    /**
     * stepping mode in game session (debug purpose)
     */
    private boolean stepMode;

    /**
     * execute one step in step mode
     */
    private boolean canStep;

    /**
     * is engine used ?
     */
    private boolean isEngineActive;

    // models

    private ServerModel serverModel;

    public ServerEngine( SystemParametersModel systemParametersModel, GameParametersProfilesModel gameParametersProfilesModel )
    {
        // automaton initialisation
        //        super( new AutomatonModel( ServerAutomatonStateId.Init ) );
        super( ServerAutomatonStateId.Init );
        setupTransitionRules();
        // automaton initialisation end

        isEngineActive = false;
        inputMessages = new ArrayBlockingQueue<>( 100 );
        pendingUserCommands = new ArrayList<>();
        timerManager = new TimerManager();

        // models 

        serverModel = new ServerModel( systemParametersModel, gameParametersProfilesModel );

        buildinGameProfiles( this );

        subscribeModel();
    }

    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }

    private void buildinGameProfiles( Object sender )
    {
        // slow game

        GameParametersProfile profile = new GameParametersProfile();
        profile.setProfileName( "<built-in> slow" );
        profile.setReadOnly( true );

        InitialisationParameters ip = profile.getGameParameters().getInitialisationParameters();
        ip.getArmies().setValue( 5 );

        profile.getGameParameters().getTroopsMove().setValue( 1 );
        profile.getGameParameters().getFightIntensity().setValue( 1 );
        serverModel.addGameProfile( sender, profile );

        // fast game

        profile = new GameParametersProfile();

        profile.setProfileName( "<built-in> fast" );
        profile.setReadOnly( true );
        profile.getGameParameters().getTroopsMove().setValue( 9 );
        profile.getGameParameters().getFightIntensity().setValue( 9 );
        serverModel.addGameProfile( sender, profile );
    }

    public ServerModel getServerModel()
    {
        return serverModel;
    }

    private void createTimers()
    {
        //        netSpeedTimer = new NetSpeedTimer( 1000, 1 );
        //        timers.add( netSpeedTimer );

        updateTickTimer = new UpdateTickTimer( serverModel.getSystemParametersModel().getGameTickIntervalModel().getValue(), 2 );
        timerManager.addTimer( updateTickTimer );

        //        clientDisconnectTimer = new EngineTimer( 500, 1 );
        //        timerManager.addTimer( clientDisconnectTimer );
    }

    private void deleteTimers()
    {
        timerManager.clearAll();
        //        updateTickTimer = null;
        //clientDisconnectTimer = null;
        //      netSpeedTimer = null;
    }

    //    private void resetCommandSequence()
    //    {
    //        seqGen = 0;
    //        lastPendingAck = 0;
    //    }

    /**
     * check if some messages still need to be acked by clients
     * @return true if one or more messages ack are pending
     */
    private boolean ackPending()
    {
        return lastNotAckedSeq < commandSeqGen;
    }

    private int getNextSequence()
    {
        return commandSeqGen++;
    }

    private boolean allClientsAck()
    {
        if ( serverModel.getServerSideConnectionModel().isSequenceAcked( lastNotAckedSeq ) )
            lastNotAckedSeq++;

        return !ackPending();
    }

    private void prepareRandomSeed()
    {
        GameParametersModel gpm = serverModel.getGameModel().getGameParametersModel();
        gpm.prepareRandomSeed();
        //        LongParameterModel seed = gpm.getInitialisationParametersModel().getRandomSeedModel();
        //
        //        if ( gpm.getInitialisationParametersModel().getGenerateRandomSeedModel().getValue() )
        //            seed.setValue( this, System.currentTimeMillis() );

        //        logMessage( "random seed : " + seed.getValue() );
        logMessage( "random seed : " + gpm.getInitialisationParametersModel().getRandomSeedModel().getValue() );
    }

    private boolean allClientsConnected()
    {
        int connectedCount = serverModel.getServerSideConnectionModel().getConnectedPeerCount();
        return connectedCount == serverModel.getPlayerCountModel().getValue();
    }

    private boolean allClientsReadyStartGaming()
    {
        for ( ClientInfoModel cim : serverModel.getClientInfosModel() )
        {
            if ( !cim.isClientPlayerChosen() )
                return false;

            if ( cim.getPlayerState() != PlayerState.ReadyStartGaming )
                return false;
        }

        return true;
    }

    private boolean allClientsStartedGame()
    {
        if ( serverModel.getClientInfosModel().size() == 0 )
            return false;

        for ( ClientInfoModel cim : serverModel.getClientInfosModel() )
        {
            if ( !cim.isClientPlayerChosen() )
                return false;

            if ( cim.getPlayerState() != PlayerState.StartedGame )
                return false;
        }

        return true;
    }

    //    private int playerStateCount( PlayerState state )
    //    {
    //        int res = 0;
    //
    //        //for ( PlayerInfoModel pim : serverModel.getGameModel().getPlayersModel().selectedPlayers() )
    //        SelectedPlayerIterator it = serverModel.getGameModel().getPlayersModel().selectedPlayers();
    //        while ( it.hasNext() )
    //            if ( it.next().getPlayerState() == state )
    //                res++;
    //        it.close();
    //
    //        return res;
    //    }

    //    private int inGamePlayerCount()
    //    {
    //        int playingCount = playerStateCount( PlayerState.PlayingGame );
    //        int watchingCount = playerStateCount( PlayerState.WatchingGame );
    //        return playingCount + watchingCount;
    //    }

    //    /**
    //     * find "user command" net message in input message queue
    //     */
    //    private NetMessage pendingUserCommandMessage()
    //    {
    //        NetMessage res = null;
    //        //int minSeq = Integer.MAX_VALUE;
    //
    //        for ( Message m : inputMessages )
    //        {
    //            if ( m instanceof NetMessage )
    //            {
    //                NetMessage nm = (NetMessage)m;
    //                if ( nm.getMessageId() == NetMessageId.UserCommand )
    //                {
    //                    res = (NetMessage)m;
    //                    break;
    //                }
    //            }
    //        }
    //
    //        return res;
    //    }

    private void clientConnected( XBPeer clientPeer )
    {
        if ( clientPeer.isConnected() ) // if client didn't disconnect in the meantime...
        {
            logMessage( "received connection from client at " + clientPeer.getIPPort() );
            ClientInfo ci = new ClientInfo( clientPeer );
            ClientInfoModel cim = new ClientInfoModel( ci );

            serverModel.getClientInfosModel().add( this, cim );
        }
    }

    private void cleanupClientNetMessages( XBPeer peer )
    {
        Iterator<Message> it = inputMessages.iterator();
        while ( it.hasNext() )
        {
            Message m = it.next();
            if ( m instanceof XBNetMessage )
            {
                XBNetMessage nm = (XBNetMessage)m;
                if ( nm.getPeer() == peer )
                    it.remove();
            }
        }

        serverModel.getServerSideConnectionModel().clearClientPendingAcks( peer );
    }

    private void clientDisconnected( XBPeer clientPeer )
    {
        //        try
        //        {
        ClientInfoModel cim = serverModel.getClientInfoModelFromPeer( clientPeer );
        cim.unsetClientPeer();

        //int clientCount = serverModel.getServerSideConnectionModel().getConnectedPeerCount();
        //switch ( clientCount )
        switch ( serverModel.getServerSideConnectionModel().getConnectedPeerCount() )
        {
            case 0:
                // only one client was remaining
                cim.setPlayer( this, null );
                break;

            default:
                if ( isGaming() || getCurrentState() == ServerAutomatonStateId.WaitClientClose )
                    cim.setPlayerState( this, PlayerState.LeftGame );
                else
                    cim.setPlayer( this, null );
                break;
        }

        serverModel.getClientInfosModel().remove( this, cim );
        //cim.setPlayer( this, null );
        //        }
        //        catch( MVCModelError e )
        //        {
        //        }

        //switch ( clientCount )
        switch ( serverModel.getServerSideConnectionModel().getConnectedPeerCount() )
        {
            case 0:
                // only one client was remaining
                logMessage( "AllClientsDisconnected" );
                setTemporaryCondition( ServerConditionId.AllClientsDisconnected );
                break;

            default:
                logMessage( "ClientDisconnected " + clientPeer );
                setTemporaryCondition( ServerConditionId.ClientDisconnected );
                break;
        }

        // clear messages sent by disconnected client

        //ServerSideConnectionModel.cleanMessages( inputMessages, clientPeer );
        //model.getServerSideConnectionModel().removeClient( clientPeer );

        cleanupClientNetMessages( clientPeer );
    }

    private void sendAllPlayers( XBPeer clientPeer )
    {
        logMessage( "sending players information to client " + clientPeer.getIPPort() );

        for ( PlayerInfoModel pim : serverModel.getGameModel().getPlayersModel() )
        {
            String playerName = pim.getPlayerName();
            if ( playerName == null )
                logMessage( "sending available player color " + pim.getPlayerColor().toString() );
            else
                logMessage( "sending played color " + pim.getPlayerColor().toString() + " , name=" + playerName );

            serverModel.getServerSideConnectionModel().sendPlayerInfo( pim.getPlayerInfo(), clientPeer );
        }
    }

    private void sendGrantPlayer( XBPeer clientPeer, ColorId colorId )
    {
        serverModel.getServerSideConnectionModel().sendGrantPlayer( clientPeer, colorId );
    }

    private void sendDenyPlayer( XBPeer clientPeer )
    {
        serverModel.getServerSideConnectionModel().sendDenyPlayer( clientPeer );
    }

    private void sendGameStart()
    {
        serverModel.getServerSideConnectionModel().sendGameStartToClients();
    }

    private void sendUserCommandToClients( UserCommand cmd, int seq )
    {
        serverModel.getServerSideConnectionModel().sendUserCommandToClients( cmd, seq );
    }

    private void sendGameStateQueryToClients()
    {
        serverModel.getServerSideConnectionModel().sendGameStateQueryToClients();
    }

    private void sendUpdateBoardTick()
    {
        boolean stopRequested = false;
        if ( stepMode )
            while ( !canStep && !stopRequested )
            {
                ThreadUtils.sleep( 10 );
                stopRequested = hasCondition( ServerConditionId.AbortGameRequested );
            }
        canStep = false;

        if ( !stopRequested )
            serverModel.getServerSideConnectionModel().sendBoardUpdateToClients( getNextSequence() );
    }

    private boolean checkGameStates()
    {
        boolean first = true;
        GameState ref = null;

        for ( ClientInfoModel cim : serverModel.getClientInfosModel() )
        {
            if ( first )
            {
                ref = cim.getGameState();
                first = false;
            }
            else
            {
                if ( !ref.equals( cim.getGameState() ) )
                {
                    String m = "ref " + ref.toString() + " diff with " + cim.getGameState().toString();
                    debugLog( m );
                    return false;
                }
            }
        }

        return true;
    }

    //    public void setEngineActive( Object sender, boolean active )
    //    {
    //        boolean changed = isEngineActive != active;
    //
    //        if ( changed )
    //        {
    //            isEngineActive = active;
    //            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.ServerEngineStart, this ) );
    //        }
    //    }

    public void setEngineActive( Object sender )
    {
        boolean changed = !isEngineActive;
        if ( changed )
        {
            isEngineActive = true;
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.ServerEngineStart, this ) );
        }
    }

    public boolean isGaming()
    {
        switch ( getCurrentState() )
        {
            case GameStep:
            case GameNextState:
                //case SendUserCommand:
            case SendBoardUpdate:
            case WaitClientsAcks:
                return true;

            default:
                return false;
        }
    }

    public XBServerNetEngine getServerSideConnectionModel()
    {
        return serverModel.getServerSideConnectionModel();
    }

    public GameParametersProfilesModel getGameParametersProfilesModel()
    {
        return serverModel.getGameParametersProfilesModel();
    }

    public SystemParametersModel getSystemParametersModel()
    {
        return serverModel.getSystemParametersModel();
    }

    public boolean getStepMode()
    {
        return stepMode;
    }

    public void setStepMode( boolean mode )
    {
        stepMode = mode;
    }

    public boolean getCanStep()
    {
        return canStep;
    }

    public void setCanStep( boolean b )
    {
        canStep = b;
    }

    public boolean isSettingUp()
    {
        return getCurrentState() == ServerAutomatonStateId.SetupGame;
    }

    public void setPlayerCount( Object sender, int count )
    {
        serverModel.getPlayerCountModel().setValue( sender, count );
    }

    public void startListen()
    {
        setTemporaryCondition( ServerConditionId.ListenClientsRequested );
    }

    public void stopGame()
    {
        setTemporaryCondition( ServerConditionId.AbortGameRequested );
    }

    public void setDebugLog( DebugLog dl )
    {
        debugLog = dl;
    }

    //    public void setAutomatonDebugLog( DebugLog dl )
    //    {
    //        super.setDebugLog( dl );
    //    }

    @Override
    protected void debugLog( String m )
    {
        if ( debugLog != null )
            //            debugLog.log( "{" + getCurrentState() + "} thread id=" + Thread.currentThread().getId() + " " + m );
            debugLog.log( "{" + getCurrentState() + "} " + m );
    }

    private void logMessage( String s )
    {
        ServerModel.logMessageModel.infoMessage( this, s );
        ThreadUtils.sleep( 1 ); // enable EDT to get CPU time to process display update
    }

    public void checkGameParameters() throws Exception
    {
        //        serverModel.getGameParametersProfilesModel().getCurrentProfileModel().getGameParametersModel().checkParameters();
        serverModel.getGameParametersProfilesModel().getCurrent().getGameParametersModel().checkParameters();
    }

    /**
     * @return true if message has been processed
     */
    private void processNetMessage( XBNetMessage nm )
    {
        //  debugLog( "processNetMessage " + nm + " from " + serverModel.getClientInfoModelFromPeer( nm.getPeer() ) + " seq " + nm.getCommandSequence() );

        XBPeer clientPeer = nm.getPeer();
        if ( clientPeer.isConnected() )
        {
            ClientInfoModel cim = null;

            switch ( (XBNetMessageId)nm.getMessageId() )
            {
                case ClientName:
                    cim = serverModel.getClientInfoModelFromPeer( clientPeer );
                    cim.setClientName( this, (String)nm.getData() );
                    break;

                case GetAllPlayers:
                    if ( clientPeer.isConnected() )
                        sendAllPlayers( clientPeer );
                    break;

                case RequestPlayer:
                    ColorId requestedColorId = (ColorId)nm.getData();
                    PlayerInfoModel pim = serverModel.isPlayerAvailable( requestedColorId );

                    if ( pim != null )
                    {
                        cim = serverModel.getClientInfoModelFromPeer( clientPeer );
                        cim.setPlayer( this, pim );
                        cim.setPlayerState( this, PlayerState.CompletedSetup );

                        sendGrantPlayer( clientPeer, requestedColorId );
                        logMessage( "granted color " + cim.getPlayerColor() + " to client " + clientPeer.getIPPort() );
                    }
                    else
                    {
                        sendDenyPlayer( clientPeer );
                        logMessage( "denied allocation of player " + requestedColorId + " to client " + clientPeer.getIPPort() );
                    }
                    break;

                case CancelPlayer:
                    cim = serverModel.getClientInfoModelFromPeer( clientPeer );
                    logMessage( cim.getPlayerColor() + " player cancellation from client at " + clientPeer.getIPPort() );
                    cim.setPlayerState( this, PlayerState.SettingUp );
                    cim.setPlayer( this, null );
                    break;

                case GetGameData:
                    GameParametersModel gpm = serverModel.getGameModel().getGameParametersModel();
                    serverModel.getServerSideConnectionModel().sendGameParameters( gpm.getGameParameters(), clientPeer );
                    break;

                case GameState:
                    cim = serverModel.getClientInfoModelFromPeer( clientPeer );
                    cim.setGameState( this, (GameState)nm.getData() );
                    break;

                case UserCommand:
                    UserCommand cmd = (UserCommand)nm.getData();
                    pendingUserCommands.add( cmd );
                    break;

                case ReadyGaming:
                    cim = serverModel.getClientInfoModelFromPeer( clientPeer );
                    cim.setPlayerState( this, PlayerState.ReadyStartGaming );
                    break;

                case StartedGame:
                    cim = serverModel.getClientInfoModelFromPeer( clientPeer );
                    cim.setPlayerState( this, PlayerState.StartedGame );
                    break;

                default:
                    throw new MVCModelError( "invalid net message " + nm.getMessageId() );
            }
        }
    }

    private void processMVCMessage( MVCModelChange m )
    {
        switch ( (NetModelChangeId)m.getChangeId() )
        {
            case ListenSocketOpenSucceeded:
                setTemporaryCondition( ServerConditionId.ListenSocketOpenOk );
                break;

            case ListenSocketOpenFailed:
                Exception e = (Exception)m.getData();
                logMessage( "error : couldn't open listen socket on port " + serverModel.getServerSideConnectionModel().getPort() + " : " + e.getMessage() );
                setTemporaryCondition( ServerConditionId.ListenSocketOpenFailed );
                break;

            case PeerConnectionSucceeded:
                clientConnected( (XBPeer)m.getData() );
                if ( allClientsConnected() )
                {
                    logMessage( "all clients connected, waiting for all clients to finish setup..." );
                    setTemporaryCondition( ServerConditionId.AllClientsConnected );
                }
                break;

            case PeerDisconnected:
                clientDisconnected( (XBPeer)m.getData() );
                break;

            case NetError:
                setTemporaryCondition( ServerConditionId.ShutdownRequested );
                break;

            default:
                break;
        }
    }

    private void processMessages()
    {
        switch ( getCurrentState() )
        {
            case ApplicationShutdown:
            case Final:
                break;

            default:
                Iterator<Message> it = inputMessages.iterator();
                while ( it.hasNext() )
                {
                    Message m = it.next();
                    if ( m instanceof MVCModelChange )
                    {
                        processMVCMessage( (MVCModelChange)m );
                        it.remove();
                    }
                }

                it = inputMessages.iterator();
                while ( it.hasNext() )
                {
                    Message m = it.next();
                    if ( m instanceof XBNetMessage )
                    {
                        processNetMessage( (XBNetMessage)m );
                        it.remove();
                    }
                }
                break;
        }
    }

    private boolean allClientsSentGameState()
    {
        for ( ClientInfoModel cim : serverModel.getClientInfosModel() )
            if ( cim.getGameState() == null )
                return false;

        return true;
    }

    private void resetClientsSendGameState()
    {
        for ( ClientInfoModel cim : serverModel.getClientInfosModel() )
            cim.setGameState( this, null );
    }

    private void processPendingGameStateMessages()
    {
        int n = 0;
        int max = serverModel.getGameModel().getPlayersModel().getInGamePlayerCount();

        Iterator<Message> it = inputMessages.iterator();
        while ( it.hasNext() && n < max )
        {
            Message m = it.next();
            if ( m instanceof XBNetMessage )
            {
                XBNetMessage nm = (XBNetMessage)m;
                if ( nm.getMessageId() == XBNetMessageId.GameState )
                {
                    processNetMessage( nm );
                    it.remove();
                    n++;
                }
            }
        }
    }

    private void processGameNextState()
    {
        processPendingGameStateMessages();

        if ( allClientsSentGameState() )
        {
            boolean done = false;

            // optionally check clients state consistency

            if ( serverModel.getSystemParametersModel().getCheckClientsStateModel().getValue() )
            {
                if ( !checkGameStates() )
                {
                    logMessage( "clients DESYNCHRONISED !" );

                    for ( ClientInfoModel cim : serverModel.getClientInfosModel() )
                        cim.setPlayerState( this, PlayerState.Desynchronised );
                    done = true;
                    setTemporaryCondition( ServerConditionId.AbortGameRequested );
                }
            }

            // winner ?

            if ( serverModel.getClientInfosModel().size() > 0 ) // if clients did not disconnect if the meantime
            {
                //List<PlayerInfoModel> loosers = new ArrayList<PlayerInfoModel>();
                int looserCount = 0;
                PlayerInfoModel winner = null;
                if ( !done )
                {
                    // list loosers/winner

                    // use 1st player game state as reference (could be any, since all clients are supposedly in the same state)
                    //GameState gameState = model.getClientInfosModel().get( 0 ).getGameState();
                    ClientInfoModel cim = serverModel.getClientInfosModel().get( 0 );

                    //for ( PlayerInfoModel pim : serverModel.getGameModel().getPlayersModel().selectedPlayers() )
                    SelectedPlayerIterator it = serverModel.getGameModel().getPlayersModel().selectedPlayers();
                    while ( it.hasNext() )
                    {
                        PlayerInfoModel pim = it.next();
                        // get stats of this player
                        //double cp = gameState.getPlayerStatistics().get( pim.getPlayerId() ).getCoveragePercent();
                        double cp = cim.getPlayerStatistics( pim.getPlayerId() ).getCoveragePercent();

                        switch ( pim.getPlayerState() )
                        {
                            case Init:
                                break;

                            case PlayingGame:
                                if ( cp == 0.0 )
                                {
                                    pim.setPlayerState( this, PlayerState.LostGame );
                                    //loosers.add( pim );
                                    looserCount++;
                                }
                                else
                                    winner = pim; // potential winnner
                                break;

                            case WatchingGame:
                                if ( cp == 0.0 )
                                {
                                    pim.setPlayerState( this, PlayerState.LostGame );
                                    //loosers.add( pim );
                                    looserCount++;
                                }
                                break;

                            case LostGame:
                                //loosers.add( pim );
                                looserCount++;
                                break;

                            case LeftGame:
                                if ( cp == 0.0 )
                                    //loosers.add( pim );
                                    looserCount++;
                                break;

                            default:
                                break;
                        }
                    }
                    //                    it.close();

                    // game won ?

                    int selCount = serverModel.getGameModel().getPlayersModel().getSelectedPlayerCount();
                    if ( selCount > 1 ) // do not stop if playing alone (in the dark hihi)
                        //if ( loosers.size() >= selCount - 1 ) // if winner, ie. only one remaining, or everybody dead
                        if ( looserCount >= selCount - 1 ) // if winner, ie. only one remaining, or everybody dead
                        {
                            //                            if ( winner == null )
                            //                                throw new MVCModelError( "internal error : invalid null winner" );
                            //                            logMessage( "game won by " + winner.getPlayerName() + " (" + winner.getPlayerColor() + ")" );
                            //                            winner.setPlayerState( this, PlayerState.WonGame );

                            if ( winner != null )
                            {
                                logMessage( "game won by " + winner.getPlayerName() + " (" + winner.getPlayerColor() + ")" );
                                winner.setPlayerState( this, PlayerState.WonGame );
                            }

                            serverModel.getServerSideConnectionModel().setAutoFlush( true );
                            serverModel.getServerSideConnectionModel().sendGameNextStepToClients( false );
                            setTemporaryCondition( ServerConditionId.GameEndRequested );
                            done = true;
                        }
                }

                if ( !done )
                {
                    // send "go on !" to players
                    setTemporaryCondition( ServerConditionId.KeepOnGaming );
                    serverModel.getServerSideConnectionModel().sendGameNextStepToClients( true );
                }
            }
            else
                // all  clients disconnected
                setTemporaryCondition( ServerConditionId.GameEndRequested );

            //serverModel.getServerSideConnectionModel().sendOutputMessages();
            serverModel.getServerSideConnectionModel().doFlush();
            resetClientsSendGameState();
        }
    }

    // Automaton interface

    private void setupTransitionRules()
    {
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.Init, ServerAutomatonStateId.SetupGame, ServerTransitionComputationId.True ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.SetupGame, ServerAutomatonStateId.StartListen, ServerTransitionComputationId.StartListen ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.SetupGame, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.StartListen, ServerAutomatonStateId.ListenClients, ServerTransitionComputationId.OpenListenSuccess ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.StartListen, ServerAutomatonStateId.WaitListenSocket, ServerTransitionComputationId.NotListenSocketYet ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitListenSocket, ServerAutomatonStateId.ListenClients, ServerTransitionComputationId.OpenListenSuccess ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitListenSocket, ServerAutomatonStateId.SetupGame, ServerTransitionComputationId.OpenListenFailed ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitListenSocket, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.ListenClients, ServerAutomatonStateId.StopListen, ServerTransitionComputationId.AllClientsConnected ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.ListenClients, ServerAutomatonStateId.Cleanup, ServerTransitionComputationId.AbortGame ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.ListenClients, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.StopListen, ServerAutomatonStateId.WaitClientsReadyGaming, ServerTransitionComputationId.WaitClientsReadyGaming ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.StopListen, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitClientsReadyGaming, ServerAutomatonStateId.SendGameStart, ServerTransitionComputationId.AllClientsReadyGaming ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitClientsReadyGaming, ServerAutomatonStateId.StartListen, ServerTransitionComputationId.ClientDisconnected ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitClientsReadyGaming, ServerAutomatonStateId.Cleanup, ServerTransitionComputationId.AbortGame ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitClientsReadyGaming, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.SendGameStart, ServerAutomatonStateId.WaitGameStarted, ServerTransitionComputationId.WaitGameStarted ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.SendGameStart, ServerAutomatonStateId.Cleanup, ServerTransitionComputationId.AbortGameOrClientsDisconnected ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.SendGameStart, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitGameStarted, ServerAutomatonStateId.GameStep, ServerTransitionComputationId.AllClientsStartedGame ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitGameStarted, ServerAutomatonStateId.Cleanup, ServerTransitionComputationId.AbortGameOrClientsDisconnected ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitGameStarted, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.GameStep, ServerAutomatonStateId.SendBoardUpdate, ServerTransitionComputationId.BoardUpdateTick ) );
        //addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.GameStep, ServerAutomatonStateId.SendUserCommand, ServerTransitionComputationId.ReceivedUserCommand ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.GameStep, ServerAutomatonStateId.Cleanup, ServerTransitionComputationId.AbortGameOrClientsDisconnected ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.GameStep, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.SendBoardUpdate, ServerAutomatonStateId.GameStep, ServerTransitionComputationId.GameStep ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.SendBoardUpdate, ServerAutomatonStateId.WaitClientsAcks, ServerTransitionComputationId.WaitAllAcks ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.SendBoardUpdate, ServerAutomatonStateId.Cleanup, ServerTransitionComputationId.AbortGameOrClientsDisconnected ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.SendBoardUpdate, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        //        addTransitionRule( new TransitionRule( ServerAutomatonStateId.SendUserCommand, ServerAutomatonStateId.GameStep, ServerTransitionComputationId.SendUserCommand ) );
        //        addTransitionRule( new TransitionRule( ServerAutomatonStateId.SendUserCommand, ServerAutomatonStateId.Cleanup, ServerTransitionComputationId.AbortGame2 ) );
        //        addTransitionRule( new TransitionRule( ServerAutomatonStateId.SendUserCommand, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitClientsAcks, ServerAutomatonStateId.GameNextState, ServerTransitionComputationId.AllClientsAcked ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitClientsAcks, ServerAutomatonStateId.Cleanup, ServerTransitionComputationId.AbortGameOrClientsDisconnected ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitClientsAcks, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.GameNextState, ServerAutomatonStateId.GameStep, ServerTransitionComputationId.KeepOnGaming ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.GameNextState, ServerAutomatonStateId.WaitClientClose, ServerTransitionComputationId.GameEnd ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.GameNextState, ServerAutomatonStateId.Cleanup, ServerTransitionComputationId.AbortGameOrClientsDisconnected ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.GameNextState, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitClientClose, ServerAutomatonStateId.Cleanup, ServerTransitionComputationId.AbortGameOrClientsDisconnected ) );
        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.WaitClientClose, ServerAutomatonStateId.ApplicationShutdown, ServerTransitionComputationId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.Cleanup, ServerAutomatonStateId.Init, ServerTransitionComputationId.True ) );

        addTransitionRule( new TransitionRule<>( ServerAutomatonStateId.ApplicationShutdown, ServerAutomatonStateId.Final, ServerTransitionComputationId.True ) );
    }

    @Override
    protected boolean evalTransition( ServerTransitionComputationId id )
    {
        //        ServerTransitionComputationId id = (ServerTransitionComputationId)computationId;

        if ( id == ServerTransitionComputationId.True )
            return true;

        boolean shutdown = hasCondition( ServerConditionId.ShutdownRequested );
        boolean abortGame = hasCondition( ServerConditionId.AbortGameRequested );
        boolean clientsDisconnected = hasCondition( ServerConditionId.AllClientsDisconnected );
        boolean canGoOn = !(clientsDisconnected || abortGame || shutdown);

        switch ( id )
        {
            case StartListen:
                return hasCondition( ServerConditionId.ListenClientsRequested ) && !(abortGame || shutdown);

            case NotListenSocketYet:
                return !hasCondition( ServerConditionId.ListenSocketOpenOk ) && !hasCondition( ServerConditionId.ListenSocketOpenFailed ) && !(abortGame || shutdown);

            case OpenListenSuccess:
                return hasCondition( ServerConditionId.ListenSocketOpenOk ) && !(abortGame || shutdown);

            case OpenListenFailed:
                return hasCondition( ServerConditionId.ListenSocketOpenFailed ) && !(abortGame || shutdown);

            case Shutdown:
                return shutdown;

            case AllClientsConnected:
                return hasCondition( ServerConditionId.AllClientsConnected ) && !(abortGame || shutdown);

            case ClientDisconnected:
                return (hasCondition( ServerConditionId.ClientDisconnected ) || clientsDisconnected) && !(abortGame || shutdown);

            case WaitClientsReadyGaming:
                return !(abortGame || shutdown);

            case WaitGameStarted:
                return canGoOn;

            case AbortGame:
                return abortGame && !shutdown;

            case AbortGameOrClientsDisconnected:
                return (abortGame || clientsDisconnected) && !shutdown;

            case AllClientsReadyGaming:
                return hasCondition( ServerConditionId.AllClientsReadyGaming ) && canGoOn;

            case AllClientsStartedGame:
                return hasCondition( ServerConditionId.AllClientsStartedGame ) && canGoOn;

            case GameStep:
                return !hasCondition( ServerConditionId.WaitAllAcks ) && canGoOn;

            case BoardUpdateTick:
                return hasCondition( ServerConditionId.BoardUpdateTick ) && canGoOn;

                //            case ReceivedUserCommand:
                //                return hasCondition( ServerConditionId.ReceivedUserCommand ) && !(clientsDisconnected || abort || shutdown);

            case SendUserCommand:
                return !(abortGame || clientsDisconnected || shutdown);

            case WaitAllAcks:
                return hasCondition( ServerConditionId.WaitAllAcks ) && canGoOn;

            case AllClientsAcked:
                return hasCondition( ServerConditionId.AllClientsAcked ) && canGoOn;

            case GameEnd:
                return hasCondition( ServerConditionId.GameEndRequested ) && canGoOn;

            case KeepOnGaming:
                return hasCondition( ServerConditionId.KeepOnGaming ) && canGoOn;

            default:
                throw new MVCModelError( "unprocessed transition computation " + id );
        }
    }

    @Override
    protected void prepareConditions()
    {
        timerManager.processTimers();
        processMessages();
    }

    //    @Override
    //    public ServerAutomatonStateId getCurrentState()
    //    {
    //        return (ServerAutomatonStateId)super.getCurrentState();
    //    }

    private int loopCount; // TODO a virer

    @Override
    protected void processState()
    {
        switch ( getCurrentState() )
        {
            case Init:
                serverModel.getServerSideConnectionModel().setAutoFlush( true );
                serverModel.getGameModel().getPlayersModel().resetAllPlayers( this );
                //resetCommandSequence();
                commandSeqGen = 0;
                lastNotAckedSeq = 0;

                updateTickCount = 0;
                //                loopCount = 0;
                break;

            case SetupGame: // waiting for GUI events
                break;

            case StartListen:
                serverModel.getGameModel().getPlayersModel().setAllPlayersStateId( this, PlayerState.SettingUp );
                //serverModel.getServerSideConnectionModel().startSelectorThread();
                serverModel.getServerSideConnectionModel().openListenSocket( serverModel.getSystemParametersModel().getSystemParameters().getServerListenPort().getValue() );
                createTimers();
                serverModel.setGameParametersFromCurrentProfile();
                prepareRandomSeed();
                break;

            case WaitListenSocket:
                break;

            case ListenClients:
                serverModel.getServerSideConnectionModel().listenClientConnection();
                break;

            case StopListen:
                serverModel.getServerSideConnectionModel().closeListenSocket();
                logMessage( "all clients connected, stopped listen socket" );
                break;

            case WaitClientsReadyGaming:
                if ( allClientsReadyStartGaming() )
                    setTemporaryCondition( ServerConditionId.AllClientsReadyGaming );
                break;

            case SendGameStart:
                logMessage( "all clients ready gaming, sending game start signal !" );

                int pid = 0;
                for ( ClientInfoModel cim : serverModel.getClientInfosModel() )
                    cim.setPlayerId( this, pid++ );

                sendGameStart();
                serverModel.getServerSideConnectionModel().setAutoFlush( false );
                //serverModel.getServerSideConnectionModel().sendOutputMessages();
                serverModel.getServerSideConnectionModel().doFlush();
                //                try
                //                {
                //                }
                //                catch( IOException e )
                //                {
                //                    e.printStackTrace();
                //                    logMessage( "network error", e );
                //                    setCondition( ServerConditionId.AbortGameRequested );
                //                }
                break;

            case WaitGameStarted:
                if ( allClientsStartedGame() )
                {
                    for ( ClientInfoModel cim : serverModel.getClientInfosModel() )
                        cim.setPlayerState( this, PlayerState.PlayingGame );

                    updateTickTimer.setActive( true );

                    setTemporaryCondition( ServerConditionId.AllClientsStartedGame );
                }
                break;

            case GameStep:
                //                if ( inGamePlayerCount() == 0 )
                //                    setCondition( ServerConditionId.AbortGameRequested );
                //                else
                //                {
                boolean boardUpdateTick = updateTickTimer.periodCompleted();

                // send user commands and board update

                //if ( boardUpdateTick && (updateTickCount % model.getSystemParametersModel().getFlushFrequencyModel().getValue() == 0) )
                if ( boardUpdateTick )
                    setTemporaryCondition( ServerConditionId.BoardUpdateTick );
                //                }
                break;

            //            case SendUserCommand:
            //                NetMessage ucMessage = pendingUserCommandMessage();
            //                inputMessages.remove( ucMessage );
            //                UserCommand cmd = (UserCommand)ucMessage.getData();
            //
            //                sendUserCommandToClients( cmd, getNextSequence() );
            //                break;

            case SendBoardUpdate:
                //                // send some pending user commands
                //
                //                for ( int i = 0; i < 3; i++ )
                //                {
                //                    ucMessage = pendingUserCommandMessage();
                //                    if ( ucMessage == null )
                //                        break;
                //
                //                    inputMessages.remove( ucMessage );
                //                    cmd = (UserCommand)ucMessage.getData();
                //
                //                    sendUserCommandToClients( cmd, getNextSequence() );
                //                }

                for ( UserCommand cmd : pendingUserCommands )
                    sendUserCommandToClients( cmd, getNextSequence() );
                pendingUserCommands.clear();

                // send board update

                //                debugLog( "update tick, count=" + updateTickCount );
                sendUpdateBoardTick();

                // flush pending messages to clients

                if ( updateTickCount % serverModel.getSystemParametersModel().getFlushFrequencyModel().getValue() == 0 )
                {
                    sendGameStateQueryToClients();
                    //debugLog( "flush net messages -> clients" );
                    serverModel.getServerSideConnectionModel().doFlush();

                    setTemporaryCondition( ServerConditionId.WaitAllAcks );
                    //                    loopCount = 0;
                }

                updateTickTimer.start();
                break;

            case WaitClientsAcks:
                if ( allClientsAck() )
                {
                    setTemporaryCondition( ServerConditionId.AllClientsAcked );
                    loopCount = 0;
                }
                else
                {
                    //                    System.out.println( "loopCount " + loopCount );
                    //                    loopCount++;
                    //                    if ( loopCount > 200 )
                    //                        throw new MVCModelError( "too many WaitClientsAcks" );
                    //                        setCondition( ServerConditionId.AbortGameRequested );
                }
                break;

            case GameNextState:
                processGameNextState();
                break;

            case WaitClientClose:
                if ( serverModel.getServerSideConnectionModel().getConnectedPeerCount() == 0 )
                    setTemporaryCondition( ServerConditionId.AllClientsDisconnected );
                break;

            case Cleanup:
                doCleanup( false );
                break;

            case ApplicationShutdown:
                doCleanup( true );
                break;

            case Final:
                break;

            default:
                throw new MVCModelError( "invalid automaton state " + getCurrentState() );
        }
    }

    private void doCleanup( boolean shutdown )
    {
        inputMessages.clear();
        pendingUserCommands.clear();
        serverModel.getClientInfosModel().clear( this );
        canStep = true;
        removeAllConditions();
        deleteTimers();

        if ( shutdown )
            serverModel.getServerSideConnectionModel().close();
        else
            serverModel.getServerSideConnectionModel().closeConnections();
        logMessage( "stopped listen socket" );
    }

    private class UpdateTickTimer extends EngineTimer
    {
        public UpdateTickTimer( long p, int prio )
        {
            super( p, prio );
        }

        @Override
        public void handler()
        {
            updateTickCount++;
        }
    }

    // PeerEngine interface

    @Override
    public void doLoop( Object sender )
    {
        //        try
        //        {
        //            if ( isEngineActive )
        //            {
        //                                serverModel.getServerSideConnectionModel().doReceive();
        //                automatonStep( sender );
        //                                serverModel.getServerSideConnectionModel().doSend();
        //            }
        //        }
        //        catch( IOException e )
        //        {
        //            e.printStackTrace();
        //        }
        try
        {
            serverModel.getServerSideConnectionModel().doMessageLoop();
            automatonStep( sender );
        }
        catch( NetException e )
        {
            e.printStackTrace();
            isEngineActive = false;
        }
    }

    @Override
    public boolean isEngineActive()
    {
        return isEngineActive;
    }

    @Override
    public void shutdown()
    {
        setTemporaryCondition( ServerConditionId.ShutdownRequested );
    }

    @Override
    public boolean isShutdown()
    {
        return getCurrentState() == ServerAutomatonStateId.Final;
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof NetModelChangeId )
            switch ( (NetModelChangeId)change.getChangeId() )
            {
                case ListenSocketOpenSucceeded:
                    logMessage( "started listening to client connections on port " + serverModel.getSystemParametersModel().getServerListenPortModel().getValue() );
                    inputMessages.add( change );
                    break;

                case ListenSocketOpenFailed:
                case PeerConnectionSucceeded:
                case PeerDisconnected:
                    inputMessages.add( change );
                    break;

                case NetMessageReceived:
                    //inputMessages.add( (NetMessage)change.getChangedObject() );
                    inputMessages.add( (XBNetMessage)change.getData() );
                    //   displayMessages( "modelChanged : received net message " + ((NetMessage)change.getChangedObject()).getMessageId() );
                    break;

                default:
                    break;
            }
    }

    @Override
    public void subscribeModel()
    {
        serverModel.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        serverModel.removeObserver( this );
    }

    @Override
    public void close()
    {
        unsubscribeModel();
        serverModel.getServerSideConnectionModel().close();

        if ( debugLog != null )
            debugLog.close();
    }
}
