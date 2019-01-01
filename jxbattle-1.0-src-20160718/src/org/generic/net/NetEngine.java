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

package org.generic.net;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.generic.mvc.model.logmessage.LogMessageModel;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public abstract class NetEngine extends MVCModelImpl
{
    /**
     * channel selector/IO managing thread
     */
    protected SelectorThread selectorThread;

    /**
     * list of output network messages
     */
    //private ArrayBlockingQueue<NetMessage> outputMessages;

    /**
     * list of reply messages to synchronous requests
     */
    private ArrayBlockingQueue<NetMessage> replyMessages;

    /**
     * peer connection timeout
     */
    //    protected int peerSocketTimeout = 1000;

    /**
     * loops (sync message, wait for connection to server) is active
     */
    protected boolean active = true;

    private final int maxSyncRetries = 1000; // TODO debug : 1000, release : 10

    /**
     * timer used in autorun mode to periodically receive/send messages
     */
    private Timer messageTimer;

    /**
     * identity string used for logging
     */
    private String identity = "<netengine>";

    /**
     * global logging switch (if false, inhibits all log from network classes)
     */
    static boolean doNetLog = false;

    /**
     * logging switch for this class
     */
    private boolean doLog = false;

    public final static LogMessageModel logMessageModel = new LogMessageModel();

    public NetEngine()
    {
        selectorThread = new SelectorThread( this );
        selectorThread.setIdentity( identity );
        selectorThread.startThread();

        replyMessages = new ArrayBlockingQueue<>( 1000 );
    }

    protected void connectToServer( String host, int port )
    {
        selectorThread.connectToServer( host, port );
    }

    public int getConnectedPeerCount()
    {
        //   return connectedPeers.size();
        return selectorThread.getConnectedPeerCount();
    }

    protected boolean hasConnectedPeers()
    {
        return selectorThread.getConnectedPeerCount() > 0;
    }

    //    public void startSelectorThread()
    //    {
    //        selectorThread.startThread();
    //    }

    public void setAutoFlush( boolean b )
    {
        selectorThread.setAutoFlush( b );
    }

    public void doFlush()
    {
        //        selectorThread.doFlush.set( true );
        selectorThread.flush();
    }

    public void autoMessageLoop()
    {
        if ( messageTimer == null )
        {
            messageTimer = new Timer();
            messageTimer.schedule( new TimerTask()
            {
                @Override
                public void run()
                {
                    try
                    {
                        doMessageLoop();
                    }
                    catch( NetException e )
                    {
                        logMessage( "error sending network messages", e );
                    }
                }
            }, 0, 1 );
        }
    }

    private void closeMessageTimer()
    {
        if ( messageTimer != null )
        {
            messageTimer.cancel();
            messageTimer = null;
        }
    }

    public void doMessageLoop() throws NetException
    {
        //        startSelectorThread();
        processInputMessages();
    }

    protected abstract NetPeer createPeer();

    /**
     * process received network messages
     * @throws NetException 
     * @throws  
     */
    protected abstract void processInputNetMessage( NetMessage m ) throws NetException;

    private void processInputMessages() throws NetException
    {
        NetMessage nm = selectorThread.inputMessages.poll();
        while ( nm != null )
        {
            //            logMessage( "processInputMessages " + nm );
            processInputNetMessage( nm );
            nm = selectorThread.inputMessages.poll();
        }
    }

    protected void enqueueOutputMessage( NetMessage nm )
    {
        selectorThread.enqueueOutputMessage( nm );
    }

    protected void enqueueReplyMessage( NetMessage nm )
    {
        //        while ( !replyMessages.offer( nm ) )
        //            ThreadUtils.sleep( 1 );

        try
        {
            replyMessages.put( nm );
        }
        catch( InterruptedException e )
        {
            notifyException( e );
        }
    }

    protected NetMessage sendAndWaitReplyMessage( NetMessage request ) throws InterruptedException, NetException
    {
        request.generateRequestId();
        enqueueOutputMessage( request );

        for ( int i = 0; i < maxSyncRetries && active; i++ )
        {
            try
            {
                //                NetMessage reply = selectorThread.inputMessages.poll( 1, TimeUnit.SECONDS );
                logMessage( "waiting for sync reply to " + request.getMessageId() );
                NetMessage reply = replyMessages.poll( 1, TimeUnit.SECONDS );
                if ( reply != null )
                {
                    if ( reply.getReplyToRequestId() == request.getRequestId() )
                        return reply;

                    //throw new NetException( "unexpected sync network message sequence !", null );
                    enqueueReplyMessage( reply );
                }
            }
            catch( InterruptedException e )
            {
                if ( i == maxSyncRetries )
                    throw new InterruptedException( "reached max sync retry count" );
            }
        }

        throw new InterruptedException( "reached max sync retry count" );
    }

    public List<NetPeer> getConnectedPeers()
    {
        return selectorThread.getPeers();
    }

    public String getIdentity()
    {
        return identity;
    }

    public void setIdentity( String s )
    {
        identity = s;
        selectorThread.setIdentity( identity );
    }

    public void setPeerSocketTimeout( int t )
    {
        selectorThread.setPeerSocketTimeout( t );
    }

    protected void logMessage( String s )
    {
        if ( doNetLog && doLog )
            logMessageModel.infoMessage( this, identity + " (netengine) : " + s );
    }

    protected void logMessage( String s, Exception e )
    {
        //        if ( doNetLog && doLog )
        logMessageModel.errorMessage( this, identity + " (netengine) : " + s, e );
    }

    public void disconnectPeers()
    {
        selectorThread.disconnectPeers();
    }

    public void closeConnections()
    {
        selectorThread.closeConnections();
    }

    public void close()
    {
        active = false;
        closeMessageTimer();
        selectorThread.shutdown();
    }

    protected void notifyException( Throwable t )
    {
        notifyObservers( new MVCModelChange( this, this, NetModelChangeId.NetError, t ) );
    }

    void notifyPeerDisconnected( NetPeer peer )
    {
        notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerDisconnected, peer ) );
    }

    public void notifyMessageSent( NetMessage message )
    {
        notifyObservers( new MVCModelChange( this, this, NetModelChangeId.MessageSent, message ) );
    }

    void notifyPeerShutdown( NetPeer peer )
    {
        notifyObservers( new MVCModelChange( this, this, NetModelChangeId.ConnectionShutdown, peer ) );
    }

    void notifyListenSocketSuccess()
    {
        notifyObservers( new MVCModelChange( this, this, NetModelChangeId.ListenSocketOpenSucceeded ) );
    }

    void notifyListenSocketFailure( Exception e )
    {
        MVCModelChange msg = new MVCModelChange( this, this, NetModelChangeId.ListenSocketOpenFailed, e );
        notifyObservers( msg );
    }

    void notifyPeerConnected( NetPeer peer )
    {
        notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionSucceeded, peer ) );
    }

    void notifyPeerConnectionFailed( Exception e )
    {
        notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionFailed, e ) );
    }
}
