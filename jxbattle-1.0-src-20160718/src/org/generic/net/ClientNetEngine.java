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

public abstract class ClientNetEngine extends NetEngine
{
    //private NetworkPeer serverPeer;

    /**
     * server host/port
     */
    private PeerInfo serverInfo;

    //private boolean isConnected;

    public ClientNetEngine()
    {
        super();
        //        isConnected = false;
    }

    public void connectServer( PeerInfo pi )
    {
        serverInfo = pi;
        connectToServer( pi.getHost(), pi.getPort() );
    }

    public void connectServer( String host, int port )
    {
        serverInfo = new PeerInfo( host, port );
        connectToServer( host, port );
    }

    //    private void doConnect( String host, int port )
    //    {
    //        //isConnected = false;
    //        SocketChannel socketChannel = null;
    //        try
    //        {
    //            SocketAddress adr = new InetSocketAddress( host, port );
    //            socketChannel = SocketChannel.open();
    //            socketChannel.socket().connect( adr, 100 );
    //
    //            addPeer( socketChannel, SelectionKey.OP_WRITE );
    //
    //            //            isConnected = true;
    //
    //            //            notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionSucceeded, peer ) );
    //        }
    //        catch( IOException e )
    //        {
    //            if ( socketChannel != null )
    //            {
    //                try
    //                {
    //                    socketChannel.close();
    //                }
    //                catch( IOException e1 )
    //                {
    //                }
    //                socketChannel = null;
    //            }
    //            logMessage( "error connecting to server at " + host + ":" + port, e );
    //            //            notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionFailed, e ) );
    //            notifyPeerConnectionFailed( null, e );
    //
    //            //            try
    //            //            {
    //            //                if ( socketChannel != null )
    //            //                    socketChannel.close();
    //            //            }
    //            //            catch( IOException e1 )
    //            //            {
    //            //                logMessage( getIdentity() + " : error closing socketchannel", e1 );
    //            //            }
    //        }
    //    }

    //    private void doConnect( String host, int port )
    //    {
    //        try
    //        {
    //            //            int timeout = systemParametersModel.getSocketTimeoutModel().getValue();
    //            //            PeerInfoModel serverInfo = systemParametersModel.getServerSocketModel();
    //
    //            SocketAddress adr = new InetSocketAddress( host, port );
    //            //SocketAddress adr = new InetSocketAddress( serverHost, serverPort );
    //            //SocketChannel socketChannel = SocketChannel.open( adr );
    //            SocketChannel socketChannel = SocketChannel.open();
    //            socketChannel.socket().connect( adr, timeout );
    //            socketChannel.configureBlocking( false );
    //
    //            //serverPeer = createPeer( socketChannel );
    //            serverPeer = createPeer();
    //            serverPeer.setIdentity( getIdentity() );
    //            serverPeer.open( socketChannel );
    //            //serverPeer.setDebugLog( debugLog );
    //            //serverPeer.setNetworkBias( systemParametersModel.getNetworkBiasModel().getValue() );
    //            serverPeer.setSocketTimeout( timeout );
    //            //serverPeer.setLogLocalPort( true );
    //
    //            logMessage( getIdentity() + " connected from port " + serverPeer.getLocalPort() );
    //            notifyObservers( new MVCModelChange( this, this, NetworkModelChangeId.PeerConnectionSucceeded, serverPeer ) );
    //        }
    //        catch( Exception e )
    //        {
    //            closePeer();
    //            notifyObservers( new MVCModelChange( this, this, NetworkModelChangeId.PeerConnectionFailed, e ) );
    //        }
    //    }

    public PeerInfo getPeerInfo()
    {
        return serverInfo;
    }

    //    public NetPeer getPeer()
    //    {
    //        //        if ( connectedPeers.size() > 0 )
    //        //            return connectedPeers.get( 0 );
    //        //
    //        //        return null;
    //        return connectedPeers.peek();
    //    }

    //    private boolean closePeer()
    //    {
    //        closeMessageTimer();
    //
    //        if ( serverPeer != null )
    //        {
    //            serverPeer.close();
    //            serverPeer = null;
    //            return true;
    //        }
    //
    //        return false;
    //    }

    //    public void disconnectServer()
    //    {
    //        //        if ( closePeer() )
    //        //        {
    //        //            //logMessage( "cleaning up connection to server at " + systemParametersModel.getServerSocketModel().toString() );
    //        //            notifyObservers( new MVCModelChange( this, this, NetworkModelChangeId.ConnectionShutdown ) );
    //        //        }
    //
    //        disconnectPeers();
    //        //            close();
    //    }

    public boolean isConnected()
    {
        //        try
        //        {
        //            connectedPeers.getMutex().lock();
        //            if ( connectedPeers.size() == 0 )
        //                return false;
        //            return connectedPeers.unsyncGet( 0 ).isConnected();
        //        }
        //        finally
        //        {
        //            connectedPeers.getMutex().unlock();
        //        }
        return hasConnectedPeers();
    }

    @Override
    public void notifyPeerConnected( NetPeer peer )
    {
        logMessage( "connected to server at " + peer.getIPPort() );
        super.notifyPeerConnected( peer );
    }

    @Override
    public void notifyPeerConnectionFailed( Exception e )
    {
        logMessage( "error connecting to server at " + serverInfo, e );
        super.notifyPeerConnectionFailed( e );
    }
}
