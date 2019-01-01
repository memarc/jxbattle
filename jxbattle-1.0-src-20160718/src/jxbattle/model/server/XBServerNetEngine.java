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

import jxbattle.bean.common.game.UserCommand;
import jxbattle.bean.common.message.XBNetMessage;
import jxbattle.bean.common.message.XBNetMessageId;
import jxbattle.bean.common.parameters.game.GameParameters;
import jxbattle.bean.common.player.ColorId;
import jxbattle.bean.common.player.PlayerInfo;
import jxbattle.common.net.XBPeer;
import jxbattle.model.common.DebugLog;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.net.NetMessage;
import org.generic.net.NetModelChangeId;
import org.generic.net.NetPeer;
import org.generic.net.ServerNetEngine;

//public class ServerSideConnectionModel extends MVCModelImpl implements MVCModelObserver
public class XBServerNetEngine extends ServerNetEngine implements MVCModelObserver
{
    //private PortParameter serverPort;

    //    private ServerSocketChannel serverSocketChannel;

    private ServerModel serverModel;

    //    /**
    //     * channel selector
    //     */
    //    private Selector selector;
    //
    //    /**
    //     * currently connected clients;
    //     */
    //    private List<XBPeer> connectedClients;
    //
    //    /**
    //     * list of pending input network messages
    //     */
    //    private List<XBNetMessage> inputMessages;
    //
    //    /**
    //     * list of pending output network messages
    //     */
    //    private List<XBNetMessage> outputMessages;

    /**
     * list of pending output network messages requiring acknowledge
     */
    //    private List<XBNetMessage> ackPendingMessages;
    private ArrayBlockingQueue<XBNetMessage> ackPendingMessages;

    /**
     * check clients liveness by send ping message
     */
    //    private boolean checkClientLiveness;

    /**
     * ping (liveness) sequence number
     */
    //    private int pingSequence;

    /**
     * ping maximum response time (ms)
     */
    //    private int pingTimeout;

    /**
     * checkPingAcks() time stamp
     */
    //    private long lastCheckPingTime;

    //    /**
    //     * sendPingToClients() time stamp
    //     */
    //    private long lastSendPingTime;

    /**
     * automatically flush output messages
     */
    //private boolean autoFlush;

    /**
     * console log facility
     */
    private DebugLog debugLog;

    XBServerNetEngine( ServerModel sm )
    {
        serverModel = sm;
        // playersModel = sm.getGameModel().getPlayersModel();
        //serverPort = new PortParameter();
        //connectedClients = new ArrayList<>();
        //        inputMessages = new ArrayList<>();
        //        outputMessages = new ArrayList<>();
        //        ackPendingMessages = new ArrayList<>();
        ackPendingMessages = new ArrayBlockingQueue<>( 100 );
        //        checkClientLiveness = true;
        //autoFlush = true;
        setAutoListen( false );
        //        pingSequence = 0;
        //        pingTimeout = 500;
        //        lastCheckPingTime = System.currentTimeMillis();
        subscribeModel();
    }

    //    @Override
    //    protected void finalize() throws Throwable
    //    {
    //        unsubscribeModel();
    //        close();
    //        super.finalize();
    //    }

    //    private String logPendingNetMessages()
    //    {
    //        StringBuilder sb = new StringBuilder();
    //        for ( XBNetMessage nm : ackPendingMessages )
    //        {
    //            sb.append( " id " );
    //            sb.append( nm.getMessageId() );
    //            sb.append( " seq " );
    //            sb.append( nm.getSequence() );
    //        }
    //
    //        return sb.toString();
    //    }
    //
    //    /**
    //     * send ping message to check peers liveness
    //     * @throws EOFException
    //     * @throws IOException
    //     */
    //    private void sendPingToClients() throws EOFException, IOException
    //    {
    //        if ( checkClientLiveness )
    //        {
    //            long dur = System.currentTimeMillis() - lastSendPingTime;
    //            if ( dur > pingTimeout / 10 )
    //                for ( XBPeer clientPeer : getWritableClients() )
    //                {
    //                    // ping already sent ?
    //
    //                    boolean hasPing = false;
    //                    for ( XBNetMessage nm : ackPendingMessages )
    //                        if ( nm.getMessageId() == XBNetMessageId.Ping && nm.getPeer() == clientPeer )
    //                        {
    //                            debugLog( "ping pending seq=" + nm.getSequence() + " client=" + nm.getPeer() + " since " + (System.currentTimeMillis() - nm.getCreationTime()) + " ms" );
    //                            hasPing = true;
    //                            break;
    //                        }
    //
    //                    // no, do it !
    //
    //                    if ( !hasPing )
    //                    {
    //                        XBNetMessage m = new XBNetMessage( XBNetMessageId.Ping, clientPeer );
    //                        m.setSequence( pingSequence++ );
    //                        ackPendingMessages.add( m );
    //                        debugLog( "send ping seq=" + m.getSequence() + " to " + m.getPeer() );
    //                        clientPeer.sendPing( m.getSequence() );
    //                    }
    //                }
    //        }
    //
    //        lastSendPingTime = System.currentTimeMillis();
    //    }
    //
    //    /**
    //     * check ack to previously sent pings
    //     * @throws IOException 
    //     */
    //    private void checkPingAcks() throws IOException
    //    {
    //        if ( checkClientLiveness )
    //        {
    //            // ack messages
    //
    //            Iterator<XBNetMessage> it = inputMessages.iterator();
    //            while ( it.hasNext() )
    //            {
    //                XBNetMessage m = it.next();
    //                if ( m.getMessageId() == XBNetMessageId.PingAck )
    //                {
    //                    // find matching original ping message
    //
    //                    Iterator<XBNetMessage> it2 = ackPendingMessages.iterator();
    //                    while ( it2.hasNext() )
    //                    {
    //                        XBNetMessage pm = it2.next();
    //
    //                        if ( pm.getMessageId() == XBNetMessageId.Ping && pm.getSequence() == m.getSequence() )
    //                        {
    //                            // matching message found, clear both ping and answer ans set client to "alive"
    //
    //                            it2.remove(); // remove ping message
    //                            debugLog( "ping seq=" + m.getSequence() + " from " + m.getPeer() + " ACKED after " + (System.currentTimeMillis() - pm.getCreationTime()) + " ms" );
    //                        }
    //                    }
    //
    //                    it.remove(); // remove ping ack message
    //                }
    //            }
    //
    //            // unanswered ping messages after timeout
    //
    //            // time between calls
    //            int lastCallDuration = (int)(System.currentTimeMillis() - lastCheckPingTime);
    //
    //            List<XBPeer> unresponsiveClients = new ArrayList<>();
    //
    //            Iterator<XBNetMessage> it3 = ackPendingMessages.iterator();
    //            debugLog( "checking unresponsive clients, " + ackPendingMessages.size() + " pending messages " );
    //            debugLog( logPendingNetMessages() );
    //            while ( it3.hasNext() )
    //            {
    //                XBNetMessage pm = it3.next();
    //
    //                long delay = System.currentTimeMillis() - pm.getCreationTime();
    //                delay -= lastCallDuration; // take into account time between calls
    //
    //                if ( pm.getMessageId() == XBNetMessageId.Ping && delay > pingTimeout )
    //                {
    //                    XBPeer clientPeer = pm.getPeer();
    //                    debugLog( "client " + clientPeer + " MISSED ack to seq=" + pm.getSequence() + " after " + delay + " ms" );
    //
    //                    it3.remove();
    //                    unresponsiveClients.add( clientPeer );
    //                }
    //            }
    //
    //            // indeed remove unresponsive clients
    //            // (ConcurrentModification exception on ackPendingMessages list if done in previous loop due to cleanupClient())
    //
    //            for ( XBPeer clientPeer : unresponsiveClients )
    //            {
    //                cleanupClient( clientPeer );
    //                connectedClients.remove( clientPeer );
    //                logMessage( "client at " + clientPeer + " disconnected (timeout)" );
    //                notifyObservers( new MVCModelChange( this, this, MVCModelChangeId.PeerDisconnected, clientPeer ) );
    //            }
    //        }
    //
    //        lastCheckPingTime = System.currentTimeMillis();
    //    }

    //    public void checkClientsLiveness( boolean check )
    //    {
    //        checkClientLiveness = check;
    //    }

    //    void doReceive() throws EOFException, IOException
    //    {
    //        receiveNetMessages();
    //        checkPingAcks();
    //        processMessages();
    //    }
    //
    //    void doSend() throws IOException
    //    {
    //        sendPingToClients();
    //        sendNetMessages();
    //        flushOutputs();
    //    }

    //    void doFlushOutputMessages() throws EOFException, IOException
    //    {
    //        while ( outputMessages.size() > 0 )
    //            doSendNetMessage();
    //    }

    //    private void flushOutputs() throws IOException
    //    {
    //        for ( XBPeer clientPeer : getWritableClients() )
    //            clientPeer.flushOutputBuffer();
    //    }
    //
    //    private void logMessage( String s )
    //    {
    //        Global.logMessageModel.infoMessage( this, s );
    //    }
    //
    //    private void logMessage( String s, Exception e )
    //    {
    //        Global.logMessageModel.errorMessage( this, s, e );
    //    }

    //    private void doSendNetMessage() throws EOFException, IOException
    //    {
    //        XBNetMessage nm = outputMessages.remove( 0 );
    //        XBPeer clientPeer = nm.getPeer();
    //
    //        if ( clientPeer.isConnected() )
    //        {
    //            debugLog( XBPeer.logNetMessage( "doSendNetMessage", nm ) );
    //
    //            switch ( nm.getMessageId() )
    //            {
    //                case PlayerGranted:
    //                    clientPeer.sendGrantPlayer( (ColorId)nm.getData() );
    //                    break;
    //
    //                case PlayerDenied:
    //                    clientPeer.sendDenyPlayer();
    //                    break;
    //
    //                case PlayerState:
    //                    clientPeer.sendPlayerState( (PeerDTO)nm.getData() );
    //                    break;
    //
    //                case PlayerName:
    //                    clientPeer.sendPlayerName( (PeerDTO)nm.getData() );
    //                    break;
    //
    //                case PlayerId:
    //                    clientPeer.sendPlayerId( (PeerDTO)nm.getData() );
    //                    break;
    //
    //                case GameData:
    //                    clientPeer.sendGameParameters( (GameParameters)nm.getData() );
    //                    break;
    //
    //                case StartGame:
    //                    clientPeer.sendGameStart();
    //                    break;
    //
    //                case DoBoardUpdate:
    //                    clientPeer.sendBoardUpdateCommand( nm.getSequence() );
    //                    ackPendingMessages.add( nm );
    //                    break;
    //
    //                case UserCommand:
    //                    clientPeer.sendUserCommand( (UserCommand)nm.getData(), nm.getSequence() );
    //                    ackPendingMessages.add( nm );
    //                    break;
    //
    //                case GameNextStep:
    //                    clientPeer.sendGameNextStep( (Boolean)nm.getData() );
    //                    break;
    //
    //                case GameStateQuery:
    //                    clientPeer.sendGameStateQuery();
    //                    break;
    //
    //                default:
    //                    throw new MVCModelError( "non processed output network message" );
    //            }
    //        }
    //    }
    //
    //    private void sendNetMessages() throws EOFException, IOException
    //    {
    //        if ( autoFlush )
    //            doFlushOutputMessages();
    //    }

    private XBNetMessage hasPendingAck( XBNetMessageId mid, XBPeer peer, int seq )
    {
        for ( XBNetMessage om : ackPendingMessages )
            if ( om.getMessageId() == mid && om.getPeer() == peer && om.getCommandSequence() == seq )
                return om;

        return null;
    }

    boolean isSequenceAcked( int seq )
    {
        List<XBNetMessage> acked = new ArrayList<>();
        for ( XBNetMessage om : ackPendingMessages )
            if ( om.isAck() && om.getCommandSequence() == seq )
                acked.add( om );

        boolean res = acked.size() == getConnectedPeerCount();
        if ( res )
            ackPendingMessages.removeAll( acked );

        return res;
    }

    void clearClientPendingAcks( XBPeer clientPeer )
    {
        Iterator<XBNetMessage> it = ackPendingMessages.iterator();
        while ( it.hasNext() )
        {
            XBNetMessage ack = it.next();
            if ( ack.getPeer() == clientPeer )
                it.remove();
        }
    }

    void sendGrantPlayer( XBPeer clientPeer, ColorId colorId )
    {
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.PlayerGranted, clientPeer );
        //        nm.setPeer( clientPeer );
        nm.addData( colorId );
        enqueueOutputMessage( nm );
    }

    void sendDenyPlayer( XBPeer clientPeer )
    {
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.PlayerDenied, clientPeer );
        //        nm.setPeer( clientPeer );
        enqueueOutputMessage( nm );
    }

    private void sendPlayerState( PlayerInfo pi, XBPeer clientPeer )
    {
        //        PeerDTO data = new PeerDTO();
        //        data.setColorId( pi.getColorId() );
        //        data.setData( pi.getPlayerState() );
        //Pair<ColorId, PlayerState> data = new Pair<ColorId, PlayerState>( pi.getColorId(), pi.getPlayerState() );

        XBNetMessage nm = new XBNetMessage( XBNetMessageId.PlayerState, clientPeer );
        //        nm.setPeer( clientPeer );
        //        nm.addData( data );
        nm.addData( pi.getColorId() );
        nm.addData( pi.getPlayerState() );
        enqueueOutputMessage( nm );
    }

    private void sendPlayerName( PlayerInfo pi, XBPeer clientPeer )
    {
        //        PeerDTO data = new PeerDTO();
        //        data.setColorId( pi.getColorId() );
        //        data.setData( pi.getPlayerName() );
        //Pair<ColorId, String> data = new Pair<ColorId, String>( pi.getColorId(), pi.getPlayerName() );

        XBNetMessage nm = new XBNetMessage( XBNetMessageId.PlayerNameChanged, clientPeer );
        //        nm.setPeer( clientPeer );
        //        nm.addData( data );
        nm.addData( pi.getColorId() );
        nm.addData( pi.getPlayerName() );
        enqueueOutputMessage( nm );
    }

    private void sendPlayerId( PlayerInfo pi, XBPeer clientPeer )
    {
        //        PeerDTO data = new PeerDTO();
        //        data.setColorId( pi.getColorId() );
        //        data.setData( Integer.valueOf( pi.getPlayerId() ) );
        //Pair<ColorId, Integer> data = new Pair<ColorId, Integer>( pi.getColorId(), pi.getPlayerId() );

        XBNetMessage nm = new XBNetMessage( XBNetMessageId.PlayerId, clientPeer );
        //        nm.setPeer( clientPeer );
        //        nm.addData( data );
        nm.addData( pi.getColorId() );
        nm.addData( pi.getPlayerId() );
        enqueueOutputMessage( nm );
    }

    void sendPlayerInfo( PlayerInfo pi, XBPeer clientPeer )
    {
        // id

        sendPlayerId( pi, clientPeer );

        // name

        sendPlayerName( pi, clientPeer );

        // state

        sendPlayerState( pi, clientPeer );
    }

    void sendGameParameters( GameParameters gp, XBPeer clientPeer )
    {
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.GameData, clientPeer );
        //        nm.setPeer( clientPeer );
        nm.addData( gp );
        enqueueOutputMessage( nm );
    }

    void sendGameStartToClients()
    {
        //        for ( NetworkPeer clientPeer : getWritableClients() )
        //        {
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.StartGame );
        enqueueOutputMessage( nm );
        //        }
    }

    void sendUserCommandToClients( UserCommand cmd, int seq )
    {
        //        for ( NetworkPeer clientPeer : getWritableClients() )
        //        {
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.UserCommand );
        nm.addData( cmd );
        nm.setCommandSequence( seq );
        enqueueOutputMessage( nm );

        //        ackPendingMessages.add( nm );
        //        }
    }

    void sendGameStateQueryToClients()
    {
        //        debugLog( "send net message GameStateQuery" );

        XBNetMessage nm = new XBNetMessage( XBNetMessageId.GameStateQuery );
        enqueueOutputMessage( nm );
    }

    void sendBoardUpdateToClients( int seq )
    {
        //        debugLog( "send net message DoBoardUpdate seq=" + seq );

        XBNetMessage nm = new XBNetMessage( XBNetMessageId.DoBoardUpdate );
        nm.setCommandSequence( seq );
        enqueueOutputMessage( nm );
    }

    void sendGameNextStepToClients( boolean keepOnGaming )
    {
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.GameNextStep );
        nm.addData( Boolean.valueOf( keepOnGaming ) );
        enqueueOutputMessage( nm );
    }

    /**
     * process acknowledge to DoBoardUpdate message from client
     */
    private void boardUpdateAck( XBNetMessage nm )
    {
        XBNetMessage om = hasPendingAck( XBNetMessageId.DoBoardUpdate, nm.getPeer(), nm.getCommandSequence() );
        if ( om == null )
            throw new MVCModelError( "unmatched BoardUpdateAck" );
        //        debugLog( "client " + nm.getPeer() + " player " + serverModel.getClientInfoModelFromPeer( nm.getPeer() ) + " acked DoBoardUpdate #" + nm.getCommandSequence() );

        om.setAck( true );
    }

    private void userCommandAck( XBNetMessage nm )
    {
        XBNetMessage om = hasPendingAck( XBNetMessageId.UserCommand, nm.getPeer(), nm.getCommandSequence() );
        if ( om == null )
            throw new MVCModelError( "unmatched UserCommandAck" );
        om.setAck( true );
    }

    //    private void cleanMessages( XBPeer peer )
    //    {
    //        if ( peer == null )
    //        {
    //            inputMessages.clear();
    //            outputMessages.clear();
    //            ackPendingMessages.clear();
    //        }
    //        else
    //        {
    //            cleanMessages( inputMessages, peer );
    //            cleanMessages( outputMessages, peer );
    //            cleanMessages( ackPendingMessages, peer );
    //        }
    //    }
    //
    //    static void cleanMessages( List<? extends Message> messages, XBPeer peer )
    //    {
    //        Iterator<? extends Message> it = messages.iterator();
    //        while ( it.hasNext() )
    //        {
    //            Message m = it.next();
    //            if ( m instanceof XBNetMessage )
    //            {
    //                XBNetMessage nm = (XBNetMessage)m;
    //                if ( nm.getPeer() == peer )
    //                    it.remove();
    //            }
    //        }
    //    }

    private void processMVCMessage( MVCModelChange change )
    {
        PlayerInfo pi = (PlayerInfo)change.getData();
        switch ( (XBMVCModelChangeId)change.getChangeId() )
        {
            case PlayerNameChanged:
                sendPlayerName( pi, null );
                break;

            case PlayerIdChanged:
                sendPlayerId( pi, null );
                break;

            case PlayerStateChanged:
                //                    SyncIterator<NetPeer> it = getConnectedPeerIterator();
                //                    try
                //                    {
                //                        while ( it.hasNext() )
                //                        {
                //                            XBPeer clientPeer = (XBPeer)it.next();
                //                            sendPlayerState( pi, clientPeer );
                //                        }
                //                    }
                //                    finally
                //                    {
                //                        it.close();
                //                    }

                sendPlayerState( pi, null );
                break;

            default:
                break;
        }
    }

    @Override
    public void notifyMessageSent( NetMessage nm )
    {
        switch ( (XBNetMessageId)nm.getMessageId() )
        {
            case UserCommand:
            case DoBoardUpdate:
                ackPendingMessages.add( (XBNetMessage)nm );
                break;

            default:
                break;
        }
    }

    public void setDebugLog( DebugLog dl )
    {
        debugLog = dl;
    }

    private void debugLog( String m )
    {
        if ( debugLog != null )
            //            debugLog.log( "thread id=" + Thread.currentThread().getId() + " " + m );
            debugLog.log( "net engine : " + m );
    }

    // ServerNetEngine interface

    @Override
    protected NetPeer createPeer()
    {
        return new XBPeer();
    }

    @Override
    protected void processInputNetMessage( NetMessage m )
    {
        XBNetMessage nm = (XBNetMessage)m;
        //        debugLog( "processNetMessage" + nm );

        switch ( (XBNetMessageId)nm.getMessageId() )
        {
            case BoardUpdateAck:
                boardUpdateAck( nm );
                break;

            case UserCommandAck:
                userCommandAck( nm );
                break;

            default:
                // forward unprocessed message to observers
                notifyObservers( new MVCModelChange( this, this, NetModelChangeId.NetMessageReceived, nm ) );
                break;
        }
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( MVCModelChange change )
    {
        processMVCMessage( change );
    }

    @Override
    public void subscribeModel()
    {
        //serverModel.getClientInfosModel().addObserver( this );
        serverModel.getGameModel().getPlayersModel().addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        //serverModel.getClientInfosModel().removeObserver( this );
        serverModel.getGameModel().getPlayersModel().removeObserver( this );
    }
}
