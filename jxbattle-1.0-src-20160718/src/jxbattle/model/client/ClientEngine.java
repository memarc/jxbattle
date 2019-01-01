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

package jxbattle.model.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxbattle.bean.common.game.UserCommand;
import jxbattle.bean.common.message.XBNetMessage;
import jxbattle.bean.common.message.XBNetMessageId;
import jxbattle.bean.common.parameters.game.GameParameters;
import jxbattle.bean.common.player.ColorId;
import jxbattle.bean.common.player.PlayerInfo;
import jxbattle.bean.common.player.PlayerState;
import jxbattle.bean.common.player.XBColor;
import jxbattle.gui.composite.client.gameframe.GameFrameController;
import jxbattle.gui.composite.client.gameframe.GameFrameModel;
import jxbattle.model.PeerEngine;
import jxbattle.model.client.automaton.ClientAutomatonStateId;
import jxbattle.model.client.automaton.ClientAutomatonTransitionId;
import jxbattle.model.client.automaton.ClientConditionId;
import jxbattle.model.common.DebugLog;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.game.GameModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;
import jxbattle.model.common.parameters.SystemParametersModel;

import org.generic.bean.Message;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.automaton.AutomatonModel2;
import org.generic.mvc.model.automaton.TransitionRule;
import org.generic.mvc.model.logmessage.LogMessageModel;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.net.NetException;
import org.generic.net.NetModelChangeId;
import org.generic.net.PeerInfo;
import org.generic.thread.ThreadUtils;

//public class ClientEngine extends AutomatonImpl<ClientAutomatonStateId, ClientConditionId, ClientTransitionComputationId> implements MVCModelObserver, PeerEngine
public class ClientEngine extends AutomatonModel2<ClientAutomatonStateId, ClientConditionId, ClientAutomatonTransitionId> implements MVCModelObserver, PeerEngine
{
    /**
     * is engine used ?
     */
    private boolean isEngineActive;

    private SystemParametersModel systemParametersModel;

    /**
     * game info
     */
    private GameModel gameModel;

    /**
     * this client's player infos (reference to one element of the player list)
     * (used on client side only)
     */
    //private PlayerInfoModel clientPlayerInfoModel;

    /**
     * received network messages list
     */
    private List<Message> inputMessages;

    private GameEngine gameEngine;

    private GameFrameController gameFrameController;

    private XBClientNetEngine clientNetEngine;

    public static final LogMessageModel logMessageModel = new LogMessageModel();

    /**
     * requested player during setup
     */
    private PlayerInfo requestedPlayer;

    /**
     * game session log file
     */
    private GameLogFile gameLogFile;

    /**
     * next sequence number to be acknowledged
     */
    private int nextSeqAck;

    /**
     * requested client state (from game GUI during game session)
     */
    private PlayerState nextPlayerState;

    private DebugLog debugLog;

    public ClientEngine( SystemParametersModel spm )
    {
        // automaton initialisation
        //        super( new AutomatonModel<ClientAutomatonStateId, ClientTransitionComputationId>( ClientAutomatonStateId.Init ) );
        super( ClientAutomatonStateId.Init );
        setupTransitionRules();
        // end automaton initialisation

        inputMessages = new ArrayList<>();
        isEngineActive = false;
        systemParametersModel = spm;
        gameModel = new GameModel();
        //        gameEngine = new GameEngine( gameModel );
        clientNetEngine = new XBClientNetEngine( this );
        clientNetEngine.setIdentity( "XBclient" );

        //        new ConsoleLogger().setModel( logMessageModel );

        subscribeModel();
    }

    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }

    public XBClientNetEngine getClientSideConnectionModel()
    {
        return clientNetEngine;
    }

    public GameModel getGameModel()
    {
        return gameModel;
    }

    public GameEngine getGameEngine() // TODO a virer
    {
        return gameEngine;
    }

    private void sendPlayerRequest( PlayerInfo pi )
    {
        logMessage( "requesting player " + pi.getPlayerColor() );
        clientNetEngine.sendPlayerAllocationRequest( pi.getColorId() );
    }

    private void sendPlayerCancel()
    {
        clientNetEngine.sendPlayerCancellation();
    }

    private void openLogFile()
    {
        closeGameLogFile();

        if ( systemParametersModel.getLogGameReplayModel().getValue() )
        {
            try
            {
                //gameLogFile = new GameLogFile( gameModel, clientPlayerInfoModel );
                gameLogFile = new GameLogFile( gameModel );
                logMessage( "opening game log file " + gameLogFile.getFilename() );
            }
            catch( IOException e )
            {
                logMessage( "error opening game log file ", e );
                try
                {
                    gameLogFile.close();
                }
                catch( IOException e1 )
                {
                    logMessage( "error closing game log file", e1 );
                }
                gameLogFile = null;
            }
        }

        //        if ( gameLogFile != null )
        //        {
        //            try
        //            {
        //                gameLogFile.writeHeader();
        //            }
        //            catch( IOException e )
        //            {
        //                logMessage( "error writing game log file header", e );
        //                try
        //                {
        //                    gameLogFile.close();
        //                }
        //                catch( IOException e1 )
        //                {
        //                    logMessage( "error closing game log file", e );
        //                }
        //                gameLogFile = null;
        //            }
        //        }
    }

    private void closeGameLogFile()
    {
        try
        {
            if ( gameLogFile != null )
            {
                logMessage( "closing game log file " + gameLogFile.getFilename() );
                gameLogFile.close();
            }
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
        finally
        {
            gameLogFile = null;
        }
    }

    private void sendReadyGaming()
    {
        clientNetEngine.sendReadyGaming();
    }

    private void sendStartedGame()
    {
        clientNetEngine.sendStartedGame();
    }

    private void sendBoardUpdateAck( int seq )
    {
        clientNetEngine.sendBoardUpdateAck( seq );
    }

    /**
     * compute game step/send ack
     */
    private void doBoardUpdate( int seq )
    {
        //        if ( gameEngine != null )
        {
            //            long start = System.nanoTime();
            gameEngine.computeStep();
            //            MainModel.printDur( "client doBoardupdate(computeStep) ms=", start, 0.3f );

            //            long start = System.nanoTime();
            sendBoardUpdateAck( seq );
            //            MainModel.printDur( "client doBoardupdate(sendBoardUpdateAck) ms=", start, 0.3f );

            if ( gameLogFile != null )
                try
                {
                    gameLogFile.writeStep();
                }
                catch( IOException e )
                {
                    logMessage( "error writing step to log file", e );
                }
        }
    }

    /**
     * application clean up 
     */
    private void doCleanup( boolean shutdown )
    {
        inputMessages.clear();
        closeGameLogFile();
        cancelClientPlayer();

        removeAllConditions();

        //gameModel.getPlayersModel().clear( this );
        //gameModel.getPlayersModel().resetAllPlayers( this );

        //        gameModel.setGameParameters( this, null );

        if ( gameFrameController != null )
        {
            gameFrameController.close();
            gameFrameController = null;
        }

        if ( gameEngine != null )
        {
            //gameEngine.abortGame();
            gameEngine.close();
            gameEngine = null;
        }

        if ( shutdown )
            clientNetEngine.close();
        else
            clientNetEngine.closeConnections();
        //saveConfig();

        if ( debugLog != null )
            debugLog.close();
    }

    /**
     * process user command received from server/send ack
     */
    private void processUserCommand( XBNetMessage nm )
    {
        //long t1 = System.currentTimeMillis();

        UserCommand cmd = (UserCommand)nm.getData();

        gameEngine.processUserCommand( cmd );

        // maybe we've just aborted game, but meantime the server sent UserCommand message
        // so not to block server, send ack anyway
        sendUserCommandAck( nm.getCommandSequence() );

        //        long t2 = System.currentTimeMillis();
        //        debugLog( "netEventUserCommand processing time (ms) " + (t2 - t1) );
        //        debugLog( "total UserCommand time since emit (ms) " + (t2 - userCommandStartTime) );
    }

    public void connectServer( Object sender, PeerInfo pi )
    {
        systemParametersModel.getServerSocketModel().set( sender, pi );
        setTemporaryCondition( ClientConditionId.ServerConnectionRequested );
    }

    public void disconnectServer()
    {
        setTemporaryCondition( ClientConditionId.ServerDisconnectionRequested );
    }

    public boolean canRequestPlayer( PlayerInfoModel pim )
    {
        boolean setup = isSettingUp();
        boolean isChosen = pim.isPlayerSelected();

        return setup && !isChosen && isClientNameOk();
    }

    public void requestPlayer( PlayerInfoModel pim )
    {
        // set client player until confirmed by server, reset if server denies player allocation
        requestedPlayer = pim.getPlayerInfo();
        setTemporaryCondition( ClientConditionId.RequestPlayer );
    }

    public void cancelPlayer()
    {
        setTemporaryCondition( ClientConditionId.CancelPlayer );
    }

    //    //private boolean AllPlayersReadyGaming()
    //    private boolean AllPlayersCompletedSetup()
    //    {
    //        for ( PlayerInfoModel pim : gameModel.getPlayersModel().selectedPlayers() )
    //            //if ( pim.getPlayerStateId() != PlayerStateId.ReadyGaming )
    //            if ( pim.getPlayerStateId() != PlayerStateId.CompletedSetup )
    //                return false;
    //
    //        return true;
    //    }

    public boolean isSettingUp()
    {
        return getCurrentState() == ClientAutomatonStateId.Setup;
    }

    //    public boolean hasGameParameters()
    //    {
    //        //            return gameModel.getGameParametersModel().getGameParameters() != null;
    //        return isGaming();
    //    }

    public boolean isGaming()
    {
        switch ( getCurrentState() )
        {
            case GameStep:
            case GameNextState:
                return true;

            default:
                return false;
        }
    }

    public boolean isGameEnded()
    {
        return getCurrentState() == ClientAutomatonStateId.GameEnd;
    }

    public boolean isSetupCompleted()
    {
        switch ( getCurrentState() )
        {
        //case WaitPlayersCompletedSetup:
            case WaitGameStart:
                return true;

            default:
                return false;
        }
    }

    public boolean isClientPlayerChosen()
    {
        //        if ( clientPlayerInfoModel == null )
        //            return false;
        //        return clientPlayerInfoModel.isPlayerSelected();
        return gameModel.isPlayerSelected();
    }

    private void setClientPlayerInfoModel( ColorId id )
    {
        //clientPlayerInfoModel = gameModel.getPlayersModel().getPlayerInfoModel( id );
        gameModel.setClientPlayerColorId( id );
    }

    private void cancelClientPlayer()
    {
        //clientPlayerInfoModel = null;
        gameModel.setClientPlayerColorId( null );
    }

    private void sendUserCommandAck( int seq )
    {
        clientNetEngine.sendUserCommandAck( seq );
    }

    public void watchGame() // TODO remettre en private
    {
        nextPlayerState = PlayerState.WatchingGame;
        //    debugLog( "WATCH GAME" );
    }

    private void quitGame()
    {
        nextPlayerState = PlayerState.LeftGame;
        //        setTemporaryCondition( ClientConditionId.QuitGame );
        setTemporaryCondition( ClientConditionId.GameAborted );
    }

    public XBColor getClientPlayerColor()
    {
        //return clientPlayerInfoModel.getPlayerColor();
        return gameModel.getClientPlayer().getPlayerColor();
    }

    public boolean isClientNameOk()
    {
        return systemParametersModel.getClientName() != null && systemParametersModel.getClientName().length() > 0;
    }

    public SystemParametersModel getSystemParametersModel()
    {
        return systemParametersModel;
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
        logMessageModel.infoMessage( this, s );
        ThreadUtils.sleep( 0, 100 ); // enable EDT to get CPU time to process display update
    }

    private void logMessage( String s, Exception e )
    {
        logMessageModel.errorMessage( this, s, e );
        ThreadUtils.sleep( 0, 100 ); // enable EDT to get CPU time to process display update
    }

    private void processMVCMessage( MVCModelChange m )
    {
        if ( m.getChangeId() instanceof NetModelChangeId )
            switch ( (NetModelChangeId)m.getChangeId() )
            {
                case PeerConnectionSucceeded:
                    setTemporaryCondition( ClientConditionId.ServerConnectionSucceeded );
                    break;

                case PeerConnectionFailed:
                    setTemporaryCondition( ClientConditionId.ServerConnectionFailed );
                    logMessage( "failed to connect to server at " + systemParametersModel.getServerSocketModel().toString() );
                    break;

                case NetError:
                    //logMessage( "network error", (Exception)m.getChangedObject() );
                    logMessage( "network error", (Exception)m.getData() );
                    setTemporaryCondition( ClientConditionId.NetworkError );
                    break;

                case PeerDisconnected:
                    logMessage( "disconnected from server" );
                    setTemporaryCondition( ClientConditionId.NetworkError );
                    break;

                default:
                    break;
            }
    }

    private void processWinner( ColorId id )
    {
        PlayerInfoModel winner = gameModel.getPlayersModel().getPlayerInfoModel( id );
        //gameEngine.setWinner( winner );
        gameEngine.setWatchMode();
        String m = null;
        if ( gameModel.isClientColorId( winner.getColorId() ) )
            m = "you win !";
        else
            m = winner.getPlayerName() + " wins !";

        gameFrameController.setGlassPaneText( m, 0.9f );
    }

    private void netMessagePlayerState( XBNetMessage nm )
    {
        ColorId cid = (ColorId)nm.getDataArray().get( 0 );
        PlayerState ps = (PlayerState)nm.getDataArray().get( 1 );

        switch ( getCurrentState() )
        {
            case GameNextState:
                switch ( ps )
                {
                    case LostGame:
                        //if ( cid == clientPlayerInfoModel.getColorId() )
                        if ( gameModel.isClientColorId( cid ) )
                        {
                            //gameEngine.setLostGame();

                            if ( !gameModel.getPlayersModel().hasWinner() ) // if not at game end
                            {
                                gameEngine.setWatchMode();
                                //setWatchMode( "you loose", 0.5f );}
                                gameFrameController.setGlassPaneText( "you loose", 0.5f );
                            }
                        }
                        break;

                    case WonGame:
                        processWinner( cid );
                        break;

                    case PlayingGame:
                    case WatchingGame:
                    case LeftGame:
                        break;

                    case Desynchronised:
                        gameEngine.setWatchMode();
                        gameFrameController.setGlassPaneText( "out of sync !", 0.5f );
                        break;

                    default:
                        throw new MVCModelError( "unprocessed player state " + ps );
                }
                break;

            default:
                break;
        }

        gameModel.getPlayersModel().setPlayerState( this, cid, ps );
    }

    /**
     * process "game step" message : compute game step and send ack to server
     */
    private boolean netMessageBoardUpdate( XBNetMessage nm )
    {
        boolean res = false;

        switch ( getCurrentState() )
        {
            case GameStep:
                int seq = nm.getCommandSequence();
                //   debugLog( "got boardupdate seq=" + seq + " nextseq=" + nextSeqAck );
                if ( seq == nextSeqAck )
                {
                    // update board

                    doBoardUpdate( seq );
                    nextSeqAck++;
                    res = true;
                }
                break;

            case GameNextState:
            case Cleanup:
                res = true; // ignore message
                break;

            default:
                break;
        }

        if ( !res )
            throw new MVCModelError( "wrong time for board update !" );

        return res;
    }

    /**
     * process "user command" message and send ack to server
     */
    private boolean netMessageUserCommand( XBNetMessage nm )
    {
        boolean res = false;

        if ( getCurrentState() == ClientAutomatonStateId.GameStep )
        {
            int seq = nm.getCommandSequence();
            if ( seq == nextSeqAck )
            {
                debugLog( "got user command=" + nm.getData() + ", seq=" + seq + ", nextseq=" + nextSeqAck );
                processUserCommand( nm );
                nextSeqAck++;
                res = true;
            }
        }

        if ( !res )
            throw new MVCModelError( "wrong user command sequence !" ); // TODO a virer

        return res;
    }

    private void netMessageGameStateQuery()
    {
        if ( getCurrentState() == ClientAutomatonStateId.GameStep )
        {
            // next state

            PlayerState ps;
            if ( nextPlayerState == null )
                ps = gameModel.getClientPlayer().getPlayerState();
            else
            {
                ps = nextPlayerState;
                nextPlayerState = null;
            }

            // send

            clientNetEngine.sendGameState( ps );
            //clientSideConnectionModel.sendOutputMessages();
            clientNetEngine.doFlush();

            // what's next ?

            switch ( ps )
            {
                case LeftGame:
                    //                    setTemporaryCondition( ClientConditionId.QuitGame );
                    setTemporaryCondition( ClientConditionId.GameAborted );
                    break;

                default:
                    setTemporaryCondition( ClientConditionId.GameStepCompleted );
                    break;
            }
        }
    }

    /**
     * process one net message
     * @return true if message has been processed
     */
    private boolean processNetMessage( XBNetMessage nm )
    {
        boolean res = true;

        //        debugLog( "processNetMessage " + nm );

        //        long start = System.nanoTime();
        switch ( (XBNetMessageId)nm.getMessageId() )
        {
            case PlayerGranted:
                ColorId sCid = (ColorId)nm.getData();
                if ( requestedPlayer.getColorId().value != sCid.value )
                    throw new MVCModelError( "invalid granted player id, server=" + sCid + ", requested=" + requestedPlayer.getColorId() );

                setClientPlayerInfoModel( requestedPlayer.getColorId() );
                logMessage( "server granted player " + requestedPlayer );
                requestedPlayer = null;
                setTemporaryCondition( ClientConditionId.PlayerGranted );
                break;

            case PlayerDenied:
                logMessage( "server denied player " + requestedPlayer );
                requestedPlayer = null;
                cancelClientPlayer();
                setTemporaryCondition( ClientConditionId.PlayerDenied );
                break;

            case GameData:
                GameParameters gp = (GameParameters)nm.getData();
                gameModel.setGameParameters( this, gp );
                logMessage( "random seed " + gp.getInitialisationParameters().getRandomSeed().getValue() );
                setTemporaryCondition( ClientConditionId.ReceivedGameData );
                break;

            case StartGame:
                setTemporaryCondition( ClientConditionId.StartGame );
                break;

            case DoBoardUpdate:
                res = netMessageBoardUpdate( nm );
                break;

            case UserCommand:
                res = netMessageUserCommand( nm );
                break;

            case GameStateQuery:
                netMessageGameStateQuery();
                break;

            case PlayerState:
                netMessagePlayerState( nm );
                break;

            case GameNextStep:
                Boolean keepGaming = (Boolean)nm.getData();
                if ( keepGaming.booleanValue() )
                    setTemporaryCondition( ClientConditionId.Gaming );
                else
                    setTemporaryCondition( ClientConditionId.GameEndedByServer );
                break;

            default:
                throw new MVCModelError( "invalid net message " + nm.getMessageId() );
        }
        //        MainModel.printDur( "client procmess " + nm.getMessageId() + " ms=", start, 0.3f );

        return res;
    }

    private void processMessages()
    {
        switch ( getCurrentState() )
        {
            case ApplicationShutdown:
            case Final:
                break;

            default:
                if ( inputMessages.size() > 0 )
                {
                    Message m = inputMessages.get( 0 );

                    if ( m instanceof XBNetMessage )
                    {
                        XBNetMessage nm = (XBNetMessage)inputMessages.remove( 0 );
                        if ( !processNetMessage( nm ) )
                        {
                            // reinsert message
                            int ind = inputMessages.size() == 0 ? 0 : 1;
                            inputMessages.add( ind, nm );
                        }
                    }
                    else if ( m instanceof MVCModelChange )
                    {
                        processMVCMessage( (MVCModelChange)m );
                        inputMessages.remove( 0 );
                    }
                }
        }
    }

    // Automaton interface

    private void setupTransitionRules()
    {
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.Init, ClientAutomatonStateId.NotConnected, ClientAutomatonTransitionId.True ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.NotConnected, ClientAutomatonStateId.TryConnect, ClientAutomatonTransitionId.AttemptConnectServer ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.NotConnected, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.TryConnect, ClientAutomatonStateId.Connected, ClientAutomatonTransitionId.ServerConnectionSucceeded ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.TryConnect, ClientAutomatonStateId.WaitConnection, ClientAutomatonTransitionId.NoServerConnectionYet ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitConnection, ClientAutomatonStateId.Connected, ClientAutomatonTransitionId.ServerConnectionSucceeded ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitConnection, ClientAutomatonStateId.NotConnected, ClientAutomatonTransitionId.ServerConnectionFailed ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitConnection, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.Connected, ClientAutomatonStateId.RequestAvailableColors, ClientAutomatonTransitionId.RequestColors ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.Connected, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.RequestAvailableColors, ClientAutomatonStateId.Setup, ClientAutomatonTransitionId.DoSetup ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.RequestAvailableColors, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.NetworkError ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.RequestAvailableColors, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.Setup, ClientAutomatonStateId.RequestPlayer, ClientAutomatonTransitionId.RequestPlayer ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.Setup, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.NetworkError ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.Setup, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.RequestPlayer, ClientAutomatonStateId.WaitPlayerAck, ClientAutomatonTransitionId.WaitPlayerAck ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.RequestPlayer, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.NetworkError ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.RequestPlayer, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.CancelPlayer, ClientAutomatonStateId.Setup, ClientAutomatonTransitionId.PlayerCanceled ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.CancelPlayer, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.NetworkError ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.CancelPlayer, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitPlayerAck, ClientAutomatonStateId.RequestGameData, ClientAutomatonTransitionId.PlayerGranted ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitPlayerAck, ClientAutomatonStateId.Setup, ClientAutomatonTransitionId.PlayerDenied ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitPlayerAck, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.NetworkError ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitPlayerAck, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.RequestGameData, ClientAutomatonStateId.WaitGameData, ClientAutomatonTransitionId.WaitGameData ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.RequestGameData, ClientAutomatonStateId.PrepareGame, ClientAutomatonTransitionId.ReceivedGameData ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.RequestGameData, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitGameData, ClientAutomatonStateId.PrepareGame, ClientAutomatonTransitionId.ReceivedGameData ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitGameData, ClientAutomatonStateId.CancelPlayer, ClientAutomatonTransitionId.CancelPlayer ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitGameData, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.NetworkError ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitGameData, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.PrepareGame, ClientAutomatonStateId.WaitGameStart, ClientAutomatonTransitionId.WaitGameStart ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.PrepareGame, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.NetworkError ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.PrepareGame, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitGameStart, ClientAutomatonStateId.LaunchGame, ClientAutomatonTransitionId.StartGame ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitGameStart, ClientAutomatonStateId.CancelPlayer, ClientAutomatonTransitionId.CancelPlayer ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitGameStart, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.NetworkError ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.WaitGameStart, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.LaunchGame, ClientAutomatonStateId.GameStep, ClientAutomatonTransitionId.LaunchGame ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.LaunchGame, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.NetworkError ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.LaunchGame, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.GameStep, ClientAutomatonStateId.GameNextState, ClientAutomatonTransitionId.GameStep ) );
        //        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.GameStep, ClientAutomatonStateId.NotifyQuit, ClientAutomatonTransitionId.QuitGame ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.GameStep, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.GameAbort ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.GameStep, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.GameNextState, ClientAutomatonStateId.GameStep, ClientAutomatonTransitionId.KeepGaming ) );
        //        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.GameNextState, ClientAutomatonStateId.NotifyQuit, ClientAutomatonTransitionId.QuitGame ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.GameNextState, ClientAutomatonStateId.GameEnd, ClientAutomatonTransitionId.GameEndByServer ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.GameNextState, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.GameAbort ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.GameNextState, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        //        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.NotifyQuit, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.True ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.GameEnd, ClientAutomatonStateId.Cleanup, ClientAutomatonTransitionId.GameAbort ) );
        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.GameEnd, ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonTransitionId.Shutdown ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.Cleanup, ClientAutomatonStateId.Init, ClientAutomatonTransitionId.True ) );

        addTransitionRule( new TransitionRule<>( ClientAutomatonStateId.ApplicationShutdown, ClientAutomatonStateId.Final, ClientAutomatonTransitionId.True ) );
    }

    @Override
    protected boolean evalTransition( ClientAutomatonTransitionId id )
    {
        //        ClientTransitionComputationId id = (ClientTransitionComputationId)computationId;

        if ( id == ClientAutomatonTransitionId.True )
            return true;

        //        boolean quitGame = hasCondition( ClientConditionId.QuitGame );
        boolean abortGame = hasCondition( ClientConditionId.GameAborted );
        boolean shutdown = hasCondition( ClientConditionId.ShutdownRequested );
        boolean networkError = hasCondition( ClientConditionId.NetworkError );

        switch ( id )
        {
            case AttemptConnectServer:
                return hasCondition( ClientConditionId.ServerConnectionRequested ) && !shutdown;

            case Shutdown:
                return shutdown;

            case NoServerConnectionYet:
                return !hasCondition( ClientConditionId.ServerConnectionSucceeded ) && !hasCondition( ClientConditionId.ServerConnectionFailed ) && !shutdown;

            case ServerConnectionSucceeded:
                return hasCondition( ClientConditionId.ServerConnectionSucceeded ) && !shutdown;

            case ServerConnectionFailed:
                return hasCondition( ClientConditionId.ServerConnectionFailed ) && !shutdown;

            case RequestColors:
                return !(networkError || shutdown);

            case DoSetup:
                return !(networkError || shutdown);

            case RequestPlayer:
                return hasCondition( ClientConditionId.RequestPlayer ) && !(networkError || shutdown);

            case WaitPlayerAck:
                return !(networkError || shutdown);

            case PlayerCanceled:
                return !(networkError || shutdown);

            case NetworkError:
                return (hasCondition( ClientConditionId.ServerDisconnectionRequested ) || networkError) && !shutdown;

            case PlayerGranted:
                return hasCondition( ClientConditionId.PlayerGranted ) && !(networkError || shutdown);

            case PlayerDenied:
                return hasCondition( ClientConditionId.PlayerDenied ) && !(networkError || shutdown);

            case WaitGameData:
                return !hasCondition( ClientConditionId.ReceivedGameData ) && !(networkError || shutdown);

            case ReceivedGameData:
                return hasCondition( ClientConditionId.ReceivedGameData ) && !(networkError || shutdown);

            case CancelPlayer:
                return hasCondition( ClientConditionId.CancelPlayer ) && !(networkError || shutdown);

            case WaitGameStart:
                return !(networkError || shutdown);

            case LaunchGame:
                return !(networkError || shutdown);

            case StartGame:
                return hasCondition( ClientConditionId.StartGame ) && !(networkError || shutdown);

            case GameStep:
                return hasCondition( ClientConditionId.GameStepCompleted ) && !(networkError || shutdown);

            case KeepGaming:
                return hasCondition( ClientConditionId.Gaming ) && !(abortGame || networkError || shutdown);

                //            case QuitGame:
                //                return quitGame && !shutdown;

            case GameAbort:
                return (abortGame || hasCondition( ClientConditionId.ServerDisconnectionRequested ) || networkError) && !shutdown;

            case GameEndByServer:
                return hasCondition( ClientConditionId.GameEndedByServer );

            default:
                throw new MVCModelError( "unprocessed transition computation " + id );
        }
    }

    //    @Override
    //    public ClientAutomatonStateId getCurrentState()
    //    {
    //        return (ClientAutomatonStateId)super.getCurrentState();
    //    }

    @Override
    protected void prepareConditions()
    {
        processMessages();
    }

    @Override
    protected void processState()
    {
        switch ( getCurrentState() )
        {
            case Init:
                clientNetEngine.setAutoFlush( true );
                gameModel.resetRandomGenerator();
                nextSeqAck = 0;
                nextPlayerState = null;
                break;

            case NotConnected:
                break;

            case TryConnect:
                //                System.out.println( "try connect " + System.currentTimeMillis() + " " + Thread.currentThread() ); // TODO a virer
                logMessage( "connecting to server at " + systemParametersModel.getServerSocketModel().toString() );
                clientNetEngine.connectServer( systemParametersModel.getServerSocketModel().getHost(), systemParametersModel.getServerSocketModel().getPort() );
                break;

            case WaitConnection:
                break;

            case Connected:
                clientNetEngine.sendClientName( systemParametersModel.getClientName() );
                break;

            case RequestAvailableColors:
                logMessage( "requesting players state" );
                clientNetEngine.requestPlayableColors();
                break;

            case Setup:
                break;

            case RequestPlayer:
                sendPlayerRequest( requestedPlayer );
                break;

            case WaitPlayerAck:
                break;

            case CancelPlayer:
                cancelClientPlayer();
                sendPlayerCancel();
                clientNetEngine.setAutoFlush( true );
                break;

            case RequestGameData:
                clientNetEngine.sendGetGameData();
                break;

            case WaitGameData:
                break;

            case PrepareGame:
                sendReadyGaming();
                clientNetEngine.setAutoFlush( false );
                clientNetEngine.doFlush();
                break;

            case WaitGameStart:
                // wait for net message "start game"
                break;

            case LaunchGame:
                openLogFile();

                gameModel.newGame();
                gameEngine = new GameEngine( gameModel );

                if ( false )
                {
                    DebugLog gameEngineLog = new DebugLog();
                    gameEngineLog.setHeader( "[GameEngine] " );
                    gameEngineLog.setEnabled( debugLog.isEnabled() );
                    gameEngine.setDebugLog( gameEngineLog );
                }

                gameFrameController = new GameFrameController();
                gameFrameController.setModel( new GameFrameModel( gameEngine ) );
                gameFrameController.run();
                gameFrameController.countDown();

                sendStartedGame();
                clientNetEngine.doFlush();
                break;

            case GameStep:
                // wait for user commands and "board update" command, execute board update, send game state
                break;

            case GameNextState:
                // wait for server to send next game state (stay in game session/won or lost game)
                break;

            case GameEnd:
                // wait for user to close game window
                break;

            //            case NotifyQuit:
            //                qsd

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

    //    public void setEngineActive( Object sender, boolean active )
    //    {
    //        boolean changed = isEngineActive != active;
    //        if ( changed )
    //        {
    //            isEngineActive = active;
    //            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.ClientEngineStart, this ) );
    //        }
    //    }

    public void setEngineActive( Object sender )
    {
        boolean changed = !isEngineActive;
        if ( changed )
        {
            isEngineActive = true;
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.ClientEngineStart, this ) );
        }
    }

    public GameFrameController getGameFrameController() // TODO a virer
    {
        return gameFrameController;
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof NetModelChangeId )
            switch ( (NetModelChangeId)change.getChangeId() )
            {
                case PeerConnectionSucceeded:
                case PeerConnectionFailed:
                case PeerDisconnected:
                case NetError:
                    inputMessages.add( change );
                    break;

                case NetMessageReceived:
                    //inputMessages.add( (NetMessage)change.getChangedObject() );
                    inputMessages.add( (XBNetMessage)change.getData() );
                    break;

                default:
                    break;
            }
        else if ( change.getChangeId() instanceof XBMVCModelChangeId )
            switch ( (XBMVCModelChangeId)change.getChangeId() )
            {
                case WatchGame:
                    watchGame();
                    break;

                case AbortGame:
                    quitGame();
                    break;

                default:
                    break;
            }
    }

    @Override
    public void subscribeModel()
    {
        clientNetEngine.addObserver( this );
        gameModel.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        clientNetEngine.removeObserver( this );
        gameModel.removeObserver( this );
    }

    @Override
    public void close()
    {
        //        if ( gameEngine != null )
        //            gameEngine.close();
        //
        //        closeGameLogFile();
        //
        //        clientSideConnectionModel.close();

        doCleanup( true );

        unsubscribeModel();
    }

    // PeerEngine interface

    @Override
    public void doLoop( Object sender )
    {
        try
        {
            clientNetEngine.doMessageLoop();
            automatonStep( sender );
        }
        catch( NetException e )
        {
            logMessageModel.errorMessage( this, "error in network engine", e );
            e.printStackTrace();
            isEngineActive = false;
        }
    }

    @Override
    public void shutdown()
    {
        setTemporaryCondition( ClientConditionId.ShutdownRequested );
    }

    @Override
    public boolean isShutdown()
    {
        return getCurrentState() == ClientAutomatonStateId.Final;
    }

    @Override
    public boolean isEngineActive()
    {
        return isEngineActive;
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        super.addObserver( obs );
    }

    //        systemParametersModel.addObserver( obs );
    //        clientSideConnectionModel.addObserver( obs );
    //        gameModel.addObserver( obs );
    //    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        super.removeObserver( obs );
    }

    //        systemParametersModel.removeObserver( obs );
    //        clientSideConnectionModel.removeObserver( obs );
    //        gameModel.removeObserver( obs );
    //    }

    @Override
    public String toString()
    {
        if ( gameModel.isPlayerSelected() )
        {
            PlayerInfoModel p = gameModel.getClientPlayer();
            return p.toString();
        }
        return "no player selected";
    }
}
