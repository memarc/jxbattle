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

public abstract class ServerNetEngine extends NetEngine
{
    private int serverPort;

    //    private ServerSocketChannel serverSocketChannel;

    //    private Mutex mutex; // to protect ServerSocketChannel

    //    @Override
    //    protected void finalize() throws Throwable
    //    {
    //        shutdown();
    //        super.finalize();
    //    }

    //    @Override
    //    protected void bufferThreadLoopStart()
    //    {
    //        listenClientConnection();
    //    }

    //    private void openListenSocket()
    //    {
    //        try
    //        {
    //            if ( serverSocketChannel == null )
    //            {
    //                logMessage( "opening listen socket on port " + serverPort + "..." );
    //                serverSocketChannel = ServerSocketChannel.open();
    //                serverSocketChannel.configureBlocking( false );
    //                serverSocketChannel.socket().bind( new InetSocketAddress( serverPort ) );
    //
    //                //                selector.open();
    //
    //                // Register the server socket channel, indicating an interest in accepting new connections
    //                //                serverSocketChannel.register( getSelector(), SelectionKey.OP_ACCEPT );
    //                registerAcceptOperation( serverSocketChannel );
    //                //
    //                //                logMessage( "successfully opened listen socket on port " + serverPort );
    //                //
    //                //                MVCModelChange msg = new MVCModelChange( this, this, NetModelChangeId.ListenSocketOpenSucceeded );
    //                //                notifyObservers( msg );
    //            }
    //        }
    //        catch( IOException e )
    //        {
    //            logMessage( "error opening listen socket", e );
    //
    //            close();
    //            MVCModelChange msg = new MVCModelChange( this, this, NetModelChangeId.ListenSocketOpenFailed, e );
    //            notifyObservers( msg );
    //        }
    //    }

    //    private void accept( SelectionKey key )
    //    {
    //        try
    //        {
    //            ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
    //            SocketChannel socketChannel = ssc.accept();
    //            addPeer( socketChannel, SelectionKey.OP_READ );
    //
    //            //            logMessage( "got client connection from " + peer.getIPPort() );
    //            //            notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionSucceeded, peer ) );
    //        }
    //        catch( IOException e )
    //        {
    //            logMessage( "error accepting client connection", e );
    //            notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionFailed, e ) );
    //        }
    //    }

    @Override
    public void notifyListenSocketSuccess()
    {
        logMessage( "successfully opened listen socket on port " + serverPort );
        super.notifyListenSocketSuccess();
    }

    @Override
    public void notifyPeerConnected( NetPeer peer )
    {
        logMessage( "got client connection from " + peer.getIPPort() );
        super.notifyPeerConnected( peer );
    }

    //    @Override
    //    protected void bufferThreadLoopStart()
    //    {
    //        listenClientConnection();
    //    }

    //    private void openListenSocket()
    //    {
    //        try
    //        {
    //            if ( serverSocketChannel == null )
    //            {
    //                logMessage( "opening listen socket on port " + serverPort + "..." );
    //                serverSocketChannel = ServerSocketChannel.open();
    //                serverSocketChannel.configureBlocking( false );
    //                serverSocketChannel.socket().bind( new InetSocketAddress( serverPort ) );
    //
    //                //                selector.open();
    //
    //                // Register the server socket channel, indicating an interest in accepting new connections
    //                //                serverSocketChannel.register( getSelector(), SelectionKey.OP_ACCEPT );
    //                registerAcceptOperation( serverSocketChannel );
    //                //
    //                //                logMessage( "successfully opened listen socket on port " + serverPort );
    //                //
    //                //                MVCModelChange msg = new MVCModelChange( this, this, NetModelChangeId.ListenSocketOpenSucceeded );
    //                //                notifyObservers( msg );
    //            }
    //        }
    //        catch( IOException e )
    //        {
    //            logMessage( "error opening listen socket", e );
    //
    //            close();
    //            MVCModelChange msg = new MVCModelChange( this, this, NetModelChangeId.ListenSocketOpenFailed, e );
    //            notifyObservers( msg );
    //        }
    //    }

    //    private void accept( SelectionKey key )
    //    {
    //        try
    //        {
    //            ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
    //            SocketChannel socketChannel = ssc.accept();
    //            addPeer( socketChannel, SelectionKey.OP_READ );
    //
    //            //            logMessage( "got client connection from " + peer.getIPPort() );
    //            //            notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionSucceeded, peer ) );
    //        }
    //        catch( IOException e )
    //        {
    //            logMessage( "error accepting client connection", e );
    //            notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionFailed, e ) );
    //        }
    //    }

    public void openListenSocket( int port )
    {
        serverPort = port;
        selectorThread.openListenSocket( port );
    }

    public void closeListenSocket()
    {
        selectorThread.closeListenSocket();
    }

    /**
     * one-shot listen socket scan
     */
    public void listenClientConnection()
    {
        selectorThread.listenClientConnections();
    }

    public void setAutoListen( boolean b )
    {
        selectorThread.autoListen = b;
    }

    //    public void listenClientConnection()
    //    {
    //        //        try
    //        //        {
    //        //            mutex.lock();
    //
    //        openListenSocket();
    //
    //        if ( serverSocketChannel != null )
    //        {
    //            try
    //            {
    //                //                List<SelectionKey> keys = selector.getReadyKeys( SelectionKey.OP_ACCEPT );
    //                List<SelectionKey> keys = getAcceptableKeys();
    //                for ( SelectionKey key : keys )
    //                    accept( key );
    //            }
    //            catch( IOException e )
    //            {
    //                logMessage( "error listening server socket", e );
    //                notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionFailed, e ) );
    //            }
    //        }
    //        //        }
    //        //        finally
    //        //        {
    //        //            mutex.unlock();
    //        //        }
    //    }

    //    public void closeListenSocket()
    //    {
    //        try
    //        {
    //            mutex.lock();
    //            if ( serverSocketChannel != null )
    //            {
    //                logMessage( "stopping listen socket" );
    //                serverSocketChannel.close();
    //                serverSocketChannel = null;
    //            }
    //        }
    //        catch( IOException e )
    //        {
    //            logMessage( "error closing listen socket", e );
    //        }
    //        finally
    //        {
    //            serverSocketChannel = null;
    //            mutex.unlock();
    //        }
    //    }

    public int getPort()
    {
        return serverPort;
    }

    public void setListenSocketTimeout( int t )
    {
        selectorThread.setListenSocketTimeout( t );
    }

    //    @Override
    //    public void close()
    //    {
    //        closeListenSocket();
    //        super.close();
    //    }
}
