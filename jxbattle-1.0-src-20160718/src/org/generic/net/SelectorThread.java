package org.generic.net;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.generic.thread.Mutex;
import org.generic.thread.ThreadUtils;

class SelectorThread
{
    private Mutex mutex;

    private Selector selector;

    /**
     * currently connected peers;
     */
    private List<NetPeer> connectedPeers;

    /**
     * read/write buffers thread
     */
    private Thread bufferThread;

    private boolean bufferThreadActive;

    /**
     * list of commands requests
     */
    private ArrayBlockingQueue<SelectorCommand> commands;

    /**
     * list of pending output message commands
     */
    //    private ArrayBlockingQueue<SelectorCommand> pendingOutputMessages;

    /**
     * list of input network messages
     */
    ArrayBlockingQueue<NetMessage> inputMessages;

    /**
     * automatically flush output messages when network output stream is ready
     */
    private boolean autoFlush = true;

    /**
     * one-shot messages flush in case of autoFlush==false
     */
    private boolean doFlushMessages = false;

    //    SyncBool doFlush;
    //    boolean doFlush = false;

    boolean autoListen = true;

    private boolean doListen = false; // one-shot server socket listen

    /**
     * logging switch for this class
     */
    private final static boolean doLog = true;

    private String identity;

    private NetEngine netEngine;

    /**
     * listen port number if listen socket must be opened
     */
    private int listenPort;

    /**
     * server socket, in case isServer==true
     */
    private ServerSocketChannel serverSocketChannel;

    /**
     * peer connection timeout
     */
    protected int peerSocketTimeout = 1000;

    /**
     * listen socket timeout
     */
    protected int listenSocketTimeout = 1000;

    SelectorThread( NetEngine ne )
    {
        netEngine = ne;
        listenPort = -1;
        connectedPeers = new ArrayList<>();
        mutex = new Mutex();
        commands = new ArrayBlockingQueue<>( 100 );
        inputMessages = new ArrayBlockingQueue<>( 100 );
        //        pendingOutputMessages = new ArrayBlockingQueue<SelectorThread.SelectorCommand>( 10000 );
    }

    void setListenSocketTimeout( int t )
    {
        listenSocketTimeout = t;
    }

    void setPeerSocketTimeout( int t )
    {
        peerSocketTimeout = t;
    }

    /**
     * start I/O buffer managing thread
     */
    void startThread()
    {
        try
        {
            openSelector();

            if ( bufferThread == null )
            {
                bufferThreadActive = true;
                bufferThread = new Thread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // read/write input/output peers buffers

                        while ( bufferThreadActive )
                        {
                            mutex.lock();

                            try
                            {
                                processCommands();

                                //                                if ( (autoListen || doListen) && listenPort != -1 )
                                if ( (autoListen || doListen) && serverSocketChannel != null )
                                    doListenClientConnection();

                                receiveInputMessages();

                                sendOutputMessages();

                                //                                if ( autoFlush || doFlush.getAndReset() )
                                //                                    flushOutputBuffers();
                            }
                            catch( Throwable e )
                            {
                                e.printStackTrace();
                                netEngine.notifyException( e );
                            }
                            finally
                            {
                                mutex.unlock();
                            }

                            ThreadUtils.sleep( 1 );
                        }

                        doClose();
                    }
                } );
                bufferThread.start();
            }
        }
        catch( IOException e )
        {
            e.printStackTrace();
            netEngine.notifyException( e );
        }
    }

    private void receiveInputMessages() throws IOException
    {
        if ( selector == null )
            return;

        //        try
        //        {
        //            mutex.lock();

        // set interest for readable keys

        //        for ( NetPeer peer : connectedPeers )
        //            //if ( peer.hasWritableSpace() )
        //            setKeyReadMode( peer );
        for ( SelectionKey key : selector.keys() )
            if ( key.isValid() )
                if ( !hasKeyMode( key, SelectionKey.OP_ACCEPT ) )
                    setKeyReadMode( key );

        selector.selectNow();

        // do read into peer's buffer

        //        for ( NetPeer peer : getReadablePeers() )
        //        for ( SelectionKey key : getReadyKeys( SelectionKey.OP_READ ) )
        for ( SelectionKey key : selector.keys() )
            if ( key.isReadable() )
            {
                NetPeer peer = (NetPeer)key.attachment();
                try
                {
                    ByteBuffer inputBuffer;
                    if ( peer.pendingInputBuffer == null )
                    {
                        int size = peer.tryReceiveInt(); // message size
                        inputBuffer = ByteBuffer.allocate( size );
                    }
                    else
                        inputBuffer = peer.pendingInputBuffer;

                    if ( peer.readByteBuffer( inputBuffer ) )
                    {
                        inputBuffer.flip();
                        NetMessage nm = peer.receiveNetMessage( inputBuffer );
                        //                        logMessage( "received message " + nm.getMessageId() );
                        logMessage( "received message " + nm );
                        inputMessages.put( nm );
                        peer.pendingInputBuffer = null;
                    }
                    else
                    {
                        if ( peer.pendingInputBuffer == null )
                            peer.pendingInputBuffer = inputBuffer;

                        //                        logMessage( "read incomplete message " + inputBuffer.position() + "/" + inputBuffer.limit() );
                    }
                }
                catch( SocketTimeoutException e )
                {
                }
                catch( EOFException e )
                {
                    logMessage( "peer at " + peer.getIPPort() + " disconnected ", e );
                    doRemovePeer( key );
                    netEngine.notifyPeerDisconnected( peer );
                }
                catch( NetException e )
                {
                    logMessage( "network error ", e );
                    doRemovePeer( key );
                    netEngine.notifyException( e );
                }
                catch( Exception e )
                {
                    logMessage( "network error ", e );
                    doRemovePeer( key );
                    netEngine.notifyException( new NetException( e, peer ) );
                }
            }

        // reset interest for keys

        resetKeys();
    }

    //    private void receiveInputMessages() throws IOException
    //    {
    //        for ( NetPeer peer : getReadablePeers() )
    //        {
    //            try
    //            {
    //                int size = peer.tryReceiveInt();
    //            }
    //            catch( SocketTimeoutException e )
    //            {
    //            }
    //        }
    //    }

    private void sendOutputMessages() throws IOException
    {
        if ( autoFlush || doFlushMessages )
        {
            doFlushMessages = false;

            if ( selector == null )
                return;

            // writable keys

            //        for ( NetPeer peer : connectedPeers )
            //            setKeyWriteMode( peer );
            for ( SelectionKey key : selector.keys() )
                if ( key.isValid() )
                    if ( !hasKeyMode( key, SelectionKey.OP_ACCEPT ) )
                        setKeyWriteMode( key );

            selector.selectNow();

            int flushed = 0;
            //        for ( NetPeer peer : getWritablePeers() )
            //        for ( SelectionKey key : getReadyKeys( SelectionKey.OP_WRITE ) )
            for ( SelectionKey key : selector.keys() )
                if ( key.isWritable() )
                {
                    NetPeer peer = (NetPeer)key.attachment();

                    Iterator<ByteBuffer> it = peer.pendingOutputBuffers.iterator();
                    while ( it.hasNext() )
                        try
                        {
                            if ( peer.writeByteBuffer( it.next() ) )
                            {
                                it.remove();
                                flushed++;
                            }
                            else
                                break;
                        }
                        catch( IOException e )
                        {
                            logMessage( "network error ", e );
                            doRemovePeer( key );
                            netEngine.notifyException( new NetException( e, peer ) );
                        }
                }

            if ( flushed > 0 )
                logMessage( "flushed " + flushed + " message(s)" );

            // reset interest for keys

            resetKeys();
        }
    }

    //    private void processInputs()
    //    {
    //        // process inputs
    //
    //        //            for ( NetPeer peer : connectedPeers )
    //        Iterator<NetPeer> it = connectedPeers.iterator();
    //        while ( it.hasNext() )
    //        {
    //            NetPeer peer = it.next();
    //            try
    //            {
    //                if ( peer.isConnected() )
    //                    peer.processPendingInputs();
    //            }
    //            catch( EOFException e )
    //            {
    //                logMessage( "peer at " + peer.getIPPort() + " disconnected " );
    //                doRemovePeer( peer, it );
    //                netEngine.notifyPeerDisconnected( peer );
    //            }
    //            catch( Exception e )
    //            {
    //                logMessage( "network error ", e );
    //                doRemovePeer( peer, it );
    //                netEngine.notifyException( new NetException( e, peer ) );
    //            }
    //        }
    //        //        }
    //        //        finally
    //        //        {
    //        //            mutex.unlock();
    //        //        }
    //    }

    //    private void flushOutputBuffers() throws IOException
    //    {
    //        // process outputs
    //        //        try
    //        //        {
    //        //        mutex.lock();
    //
    //        //            for ( NetPeer peer : connectedPeers )
    //        Iterator<NetPeer> it = connectedPeers.iterator();
    //        while ( it.hasNext() )
    //        {
    //            NetPeer peer = it.next();
    //            try
    //            {
    //                if ( peer.isConnected() )
    //                    peer.processPendingOutputs();
    //            }
    //            catch( EOFException e )
    //            {
    //                logMessage( "peer at " + peer.getIPPort() + " disconnected " );
    //                doRemovePeer( peer, it );
    //                netEngine.notifyPeerDisconnected( peer );
    //            }
    //            catch( Exception e )
    //            {
    //                logMessage( "network error ", e );
    //                doRemovePeer( peer, it );
    //                netEngine.notifyException( new NetException( e, peer ) );
    //            }
    //        }
    //
    //        // writable keys
    //
    //        for ( NetPeer peer : connectedPeers )
    //            if ( peer.hashFlushableData() )
    //                setKeyWriteMode( peer );
    //
    //        // do write to channel from peer's buffer
    //
    //        for ( NetPeer peer : getWritablePeers() )
    //        {
    //            try
    //            {
    //                peer.flushOutputBuffer();
    //            }
    //            catch( EOFException e )
    //            {
    //                logMessage( "peer at " + peer.getIPPort() + " disconnected " );
    //                doRemovePeer( peer );
    //                netEngine.notifyPeerDisconnected( peer );
    //            }
    //            catch( Exception e )
    //            {
    //                logMessage( "network error ", e );
    //                doRemovePeer( peer );
    //                netEngine.notifyException( new NetException( e, peer ) );
    //            }
    //        }
    //
    //        // reset interest for keys
    //
    //        resetKeys();
    //        //        }
    //        //        finally
    //        //        {
    //        //            mutex.unlock();
    //        //        }
    //    }

    //    void addPeer( NetPeer peer, SocketChannel socketChannel, int operation ) throws IOException
    //    {
    //        SelectionKey key = null;
    //
    //        try
    //        {
    //            mutex.lock();
    //
    //            socketChannel.configureBlocking( false );
    //            //            key = socketChannel.register( selector.open(), operation ); // register the new SocketChannel with our Selector, indicating we'd like to be notified when there's data waiting to be read/write
    //            key = registerOperation( socketChannel, operation ); // register the new SocketChannel with our Selector, indicating we'd like to be notified when there's data waiting to be read/write
    //
    //            peer.open( socketChannel );
    //            //clientPeer.setDebugLog( debugLog );
    //            peer.setSocketTimeout( socketTimeout );
    //            //clientPeer.setLogLocalPort( false );
    //            peer.setIdentity( identity );
    //            connectedPeers.add( peer );
    //
    //            key.attach( peer );
    //        }
    //        catch( IOException e )
    //        {
    //            try
    //            {
    //                socketChannel.close();
    //            }
    //            catch( IOException e1 )
    //            {
    //                logMessage( "error closing socketchannel", e1 );
    //            }
    //
    //            if ( peer != null )
    //                peer.close();
    //
    //            doCancelKey( key );
    //
    //            throw e;
    //        }
    //        finally
    //        {
    //            mutex.unlock();
    //        }
    //    }

    private void doAddPeer( NetPeer peer, SocketChannel socketChannel, int operation ) throws IOException
    {
        try
        {
            peer.open( socketChannel );
            //            socketChannel.socket().setKeepAlive( true );
            //            socketChannel.configureBlocking( false );
            //            peer.setSocketTimeout( peerSocketTimeout );
            //clientPeer.setDebugLog( debugLog );
            //clientPeer.setLogLocalPort( false );
            peer.setIdentity( identity );

            SelectionKey key = registerOperation( socketChannel, operation ); // register the new SocketChannel with our Selector, indicating we'd like to be notified when there's data waiting to be read/write
            //            SelectionKey key = registerOperation( socketChannel, SelectionKey.OP_READ ); // register the new SocketChannel with our Selector, indicating we'd like to be notified when there's data waiting to be read/write
            //            setKeyWriteMode( key );
            key.attach( peer );

            connectedPeers.add( peer );

            netEngine.notifyPeerConnected( peer );
        }
        catch( IOException e )
        {
            peer.close();
            //   netEngine.notifyPeerConnectionFailed( peer, e );
            throw e;
        }
    }

    //    private void enqueuePeerOutputBuffer( NetPeer peer, NetMessage nm ) throws Exception
    private void enqueuePeerOutputBuffer( NetMessage nm ) throws Exception
    {
        logMessage( "enqueuePeerOutputMessage " + nm );

        int dataSize = NetMessage.integerSize + nm.getByteSize(); // message id + data
        ByteBuffer obb = ByteBuffer.allocate( NetMessage.integerSize + dataSize ); //message size + message id + data
        obb.putInt( dataSize );
        //        peer.sendMessage( nm, obb );
        nm.getPeer().sendMessage( nm, obb );

        //        nm.setPeer( peer );
        netEngine.notifyMessageSent( nm );

        obb.flip();
        //        peer.pendingOutputBuffers.add( obb );
        nm.getPeer().pendingOutputBuffers.add( obb );
    }

    private void doEnqueuePeerOutputMessage( NetMessage nm ) throws Exception
    {
        if ( nm.getPeer() == null )
        {
            //            nm.getByteSize(); // initialise byteSize field to clone it with whole message
            for ( NetPeer peer : connectedPeers )
            {
                NetMessage m = peer.copyMessage( nm, peer );
                //                enqueuePeerOutputBuffer( peer, m );
                enqueuePeerOutputBuffer( m );
            }
        }
        else
            //            enqueuePeerOutputBuffer( nm.getPeer(), nm );
            enqueuePeerOutputBuffer( nm );
    }

    //    private void doRemovePeer( NetPeer peer )
    //    {
    //        cleanupPeer( peer );
    //        connectedPeers.remove( peer );
    //        logConnectedPeers();
    //    }

    //    private void doFlushMessages()
    //    {
    //        logMessage( "flushing pending output messages " + logOutputMessages( pendingOutputMessages ) );
    //        //        commands.addAll( pendingOutputMessages );
    //        //        pendingOutputMessages.clear();
    //        pendingOutputMessages.drainTo( commands );
    //    }

    private void doRemovePeer( SelectionKey key )
    {
        cleanupPeerKey( key );

        NetPeer peer = (NetPeer)key.attachment();
        connectedPeers.remove( peer );
        //        logConnectedPeers();

        // clean peer input messages

        Iterator<NetMessage> it = inputMessages.iterator();
        while ( it.hasNext() )
        {
            NetMessage nm = it.next();
            if ( nm.getPeer() == peer )
                it.remove();
        }
    }

    //    private void doRemovePeer( NetPeer peer, Iterator<NetPeer> it )
    //    {
    //        cleanupPeer( peer );
    //        it.remove();
    //        logConnectedPeers();
    //    }

    //    private void doDisconnectAllPeers()
    //    {
    //        // cleanup peer list
    //
    //        //        try
    //        //        {
    //        //            mutex.lock();
    //
    //        for ( NetPeer peer : connectedPeers )
    //        {
    //            if ( cleanupPeer( peer ) )
    //                netEngine.notifyPeerShutdown( peer );
    //        }
    //
    //        connectedPeers.clear();
    //        //        }
    //        //        finally
    //        //        {
    //        //            mutex.unlock();
    //        //        }
    //    }

    /**
     * cleanup peer list
     */
    private void doDisconnectAllPeers()
    {
        if ( selector == null )
            return;

        for ( SelectionKey key : selector.keys() )
        {
            cleanupPeerKey( key );
            netEngine.notifyPeerShutdown( (NetPeer)key.attachment() );
        }

        inputMessages.clear();
        //        pendingOutputMessages.clear();
        connectedPeers.clear();
    }

    private void doRegisterAcceptOperation( ServerSocketChannel socketChannel )
    {
        //                try
        //                {
        //            mutex.lock();

        try
        {
            if ( selector == null )
                throw new ClosedChannelException();
            socketChannel.register( selector, SelectionKey.OP_ACCEPT );
            netEngine.notifyListenSocketSuccess();
        }
        catch( ClosedChannelException e )
        {
            netEngine.notifyListenSocketFailure( e );
        }

        //        }
        //        finally
        //        {
        //            mutex.unlock();
        //        }
    }

    private void doClose()
    {
        try
        {
            //            mutex.lock();

            doCloseListenSocket();
            doDisconnectAllPeers();
            closeSelector();
        }
        finally
        {
            //            mutex.unlock();
        }
    }

    //    private void doReceiveNetMessages()
    //    {
    //        Iterator<NetPeer> it = connectedPeers.iterator();
    //        while ( it.hasNext() )
    //        {
    //            NetPeer peer = it.next();
    //            try
    //            {
    //                NetMessage nm = peer.receiveNetMessage();
    //                while ( nm != null )
    //                {
    //                    while ( !inputMessages.offer( nm ) )
    //                        ThreadUtils.sleep( 1 );
    //                    nm = peer.receiveNetMessage();
    //                }
    //            }
    //            catch( SocketTimeoutException e )
    //            {
    //            }
    //            catch( EOFException e )
    //            {
    //                logMessage( "peer at " + peer.getIPPort() + " disconnected " );
    //                doRemovePeer( peer, it );
    //                netEngine.notifyPeerDisconnected( peer );
    //            }
    //            catch( NetException e )
    //            {
    //                logMessage( "network error ", e );
    //                doRemovePeer( peer, it );
    //                netEngine.notifyException( e );
    //            }
    //            catch( Exception e )
    //            {
    //                logMessage( "network error ", e );
    //                doRemovePeer( peer, it );
    //                netEngine.notifyException( new NetException( e, peer ) );
    //            }
    //        }
    //        //        it.close();
    //    }

    //    private void doSendOutputMessage( NetMessage nm, NetPeer peer )
    //    {
    //        try
    //        {
    //            peer.sendNetMessage( nm );
    //        }
    //        catch( IOException e )
    //        {
    //            netEngine.notifyException( e );
    //        }
    //    }

    //    private void doSendOutputMessages()
    //    {
    //        NetMessage nm = outputMessages.poll();
    //        while ( nm != null )
    //        {
    //            if ( nm.getPeer() == null )
    //            {
    //                //            if ( connectedPeers.size() == 0 )
    //                //                throw new EOFException( "connection not open/already closed (no peer found)" );
    //
    //                for ( NetPeer peer : connectedPeers )
    //                    doSendOutputMessage( nm, peer );
    //            }
    //            else
    //                doSendOutputMessage( nm, nm.getPeer() );
    //
    //            nm = outputMessages.poll();
    //        }
    //    }

    private void processCommands() throws Exception
    {
        SelectorCommand cmd = commands.poll();
        while ( cmd != null )
        {
            logMessage( "processCommands " + cmd );
            switch ( cmd.commandId )
            {
                case ConnectToServer:
                    doConnectToServer( (String)cmd.data1, (Integer)cmd.data2 );
                    break;

                //                case AddPeer:
                //                    doAddPeer( (NetPeer)cmd.data1, (SocketChannel)cmd.data2, (Integer)cmd.data3 );
                //                    break;

                //                case RemovePeer:
                //                    doRemovePeer( (NetPeer)cmd.data1 );
                //                    break;

                //                case ReceiveMessages:
                //                    doReceiveNetMessages();
                //                    break;
                //
                //                case SendMessages:
                //                    doSendOutputMessages();
                //                    break;

                case OpenListenSocket:
                    doOpenListenSocket( (Integer)cmd.data1 );
                    break;

                case CloseListenSocket:
                    doCloseListenSocket();
                    break;

                case ListenClientConnections:
                    doListen = true;
                    break;

                case SendMessage:
                    NetMessage nm = (NetMessage)cmd.data1;
                    //                    //                    if ( pendingOutputMessages.size() > 0 )
                    //                    if ( !pendingOutputMessages.isEmpty() )
                    //                        throw new NetException( "unprocessed pending output messages " + logOutputMessages( pendingOutputMessages ), nm.getPeer() );
                    doEnqueuePeerOutputMessage( nm );
                    break;

                //                case RegisterAccept:
                //                    doRegisterAcceptOperation( (ServerSocketChannel)cmd.data1 );
                //                    break;

                case FlushMessages:
                    //                    doFlushMessages();
                    doFlushMessages = true;
                    break;

                case DisconnectPeers:
                    doDisconnectAllPeers();
                    break;

                case CloseConnections:
                    doClose();
                    break;

                case Shutdown:
                    bufferThreadActive = false;
                    break;

                default:
                    throw new NetException( "unprocessed selector command" + cmd, null );
            }

            cmd = commands.poll();
        }
    }

    //    private SelectionKey getPeerKey( NetPeer peer )
    //    {
    //        if ( peer.getSocketChannel() != null && selector != null )
    //            return peer.getSocketChannel().keyFor( selector );
    //
    //        return null;
    //    }

    //    private SelectionKey getPeerKey( NetPeer peer )
    //    {
    //        try
    //        {
    //            if ( selector != null )
    //                return peer.getSocketChannel().keyFor( selector );
    //        }
    //        catch( IOException e )
    //        {
    //        }
    //
    //        return null;
    //    }

    private static boolean hasKeyMode( SelectionKey key, int operation )
    {
        int ops = key.interestOps();
        return (ops & operation) != 0;
    }

    private static void setKeyReadMode( SelectionKey key )
    {
        if ( key != null && key.isValid() )
        {
            int ops = key.interestOps();
            ops |= SelectionKey.OP_READ; // Say we want to read
            ops &= ~SelectionKey.OP_WRITE; // Say we don't want to write anymore
            key.interestOps( ops );
        }
    }

    private static void setKeyWriteMode( SelectionKey key )
    {
        if ( key != null && key.isValid() )
        {
            int ops = key.interestOps();
            ops |= SelectionKey.OP_WRITE;
            ops &= ~SelectionKey.OP_READ;
            key.interestOps( ops );
        }
    }

    private static void setKeyNoMode( SelectionKey key )
    {
        if ( key != null && key.isValid() )
        {
            int ops = key.interestOps();
            ops &= ~SelectionKey.OP_WRITE;
            ops &= ~SelectionKey.OP_READ;
            key.interestOps( ops );
        }
    }

    //    private void cancelKey( SelectionKey key )
    //    {
    //        try
    //        {
    //            mutex.lock();
    //            doCancelKey( key );
    //
    //        }
    //        finally
    //        {
    //            mutex.unlock();
    //        }
    //    }

    private void resetKeys() throws ClosedChannelException // List<NetPeer> peers ) throws ClosedChannelException
    {
        if ( selector == null )
            throw new ClosedChannelException();

        //        for ( NetPeer peer : peers )
        //            setKeyNoMode( getPeerKey( peer ) );

        for ( SelectionKey key : selector.keys() )
            setKeyNoMode( key );
    }

    //    private void doCancelKey( SelectionKey key )
    //    {
    //        if ( key != null ) //&& key.isValid() )
    //        {
    //            key.cancel();
    //            try
    //            {
    //                if ( selector != null )
    //                    selector.selectNow(); // to complete key removal from selector's list
    //            }
    //            catch( IOException e )
    //            {
    //                e.printStackTrace();
    //            }
    //        }
    //    }

    private void openSelector() throws IOException
    {
        if ( selector == null )
            selector = Selector.open();
    }

    private void closeSelector()
    {
        try
        {
            //mutex.lock();

            //            bufferThreadActive = false;
            //            bufferThread.interrupt();

            if ( selector != null )
            {
                logMessage( "stopping selector" );
                selector.close();
            }
        }
        catch( IOException e )
        {
            logMessage( "error closing selector", e );
        }
        finally
        {
            selector = null;
            //mutex.unlock();
        }
    }

    //    private void cancelKey( NetPeer peer )
    //    {
    //        //        try
    //        //        {
    //        //            mutex.lock();
    //        doCancelKey( getPeerKey( peer ) );
    //        //        }
    //        //        finally
    //        //        {
    //        //            mutex.unlock();
    //        //        }
    //    }

    //    private void cancelKey( SelectionKey key )
    //    {
    //        try
    //        {
    //            mutex.lock();
    //            doCancelKey( key );
    //
    //        }
    //        finally
    //        {
    //            mutex.unlock();
    //        }
    //    }

    //    private boolean cleanupPeer( NetPeer peer )
    //    {
    //        if ( peer != null )
    //        {
    //            // remove key
    //
    //            //            cancelKey( peer.getSelectorKey( selector ) );
    //            cancelKey( peer );
    //
    //            // close connection
    //
    //            return peer.close();
    //        }
    //
    //        return false;
    //    }

    private static void cleanupPeerKey( SelectionKey key )
    {
        try
        {
            key.channel().close();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        //doCancelKey( key );
        key.cancel();
    }

    private List<SelectionKey> getReadyKeys( int readyOp ) throws IOException
    {
        List<SelectionKey> res = new ArrayList<>();

        if ( selector == null )
            throw new ClosedChannelException();

        // Check for an event one of the registered channels
        //if ( selector.selectNow() > 0 )
        selector.selectNow();

        // Iterate over the set of keys for which events are available
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while ( it.hasNext() )
        {
            SelectionKey key = it.next();

            if ( key.isValid() )
            {
                int readyOps = key.readyOps();
                if ( (readyOps & readyOp) != 0 )
                {
                    it.remove();
                    res.add( key );
                }
            }
        }

        return res;
    }

    //    private List<NetPeer> getReadablePeers( List<NetPeer> peers ) throws IOException
    //    {
    //        List<NetPeer> res = new ArrayList<NetPeer>();
    //
    //        for ( SelectionKey key : getReadyKeys( SelectionKey.OP_READ ) )
    //        {
    //            NetPeer peer = (NetPeer)key.attachment();
    //            if ( peers.contains( peer ) )
    //                res.add( peer );
    //        }
    //
    //        return res;
    //    }

    //    private List<NetPeer> getReadablePeers() throws IOException
    //    {
    //        List<NetPeer> res = new ArrayList<NetPeer>();
    //
    //        for ( SelectionKey key : getReadyKeys( SelectionKey.OP_READ ) )
    //        {
    //            NetPeer peer = (NetPeer)key.attachment();
    //            if ( connectedPeers.contains( peer ) )
    //                res.add( peer );
    //        }
    //
    //        return res;
    //    }

    //    private List<NetPeer> getWritablePeers( List<NetPeer> peers ) throws IOException
    //    {
    //        List<NetPeer> res = new ArrayList<NetPeer>();
    //
    //        for ( SelectionKey key : getReadyKeys( SelectionKey.OP_WRITE ) )
    //        {
    //            NetPeer peer = (NetPeer)key.attachment();
    //            if ( peers.contains( peer ) )
    //                res.add( peer );
    //        }
    //
    //        return res;
    //    }

    //    private List<NetPeer> getWritablePeers() throws IOException
    //    {
    //        List<NetPeer> res = new ArrayList<NetPeer>();
    //
    //        for ( SelectionKey key : getReadyKeys( SelectionKey.OP_WRITE ) )
    //        {
    //            NetPeer peer = (NetPeer)key.attachment();
    //            if ( connectedPeers.contains( peer ) )
    //                res.add( peer );
    //        }
    //
    //        return res;
    //    }

    //    private void setKeyNoMode( NetPeer peer )
    //    {
    //        try
    //        {
    //            mutex.lock();
    //            setKeyNoMode( getPeerKey( peer ) );
    //        }
    //        finally
    //        {
    //            mutex.unlock();
    //        }
    //    }

    //    private void setKeyReadMode( NetPeer peer )
    //    {
    //        //        try
    //        //        {
    //        //            mutex.lock();
    //        setKeyReadMode( getPeerKey( peer ) );
    //        //        }
    //        //        finally
    //        //        {
    //        //            mutex.unlock();
    //        //        }
    //    }

    //    private void setKeyWriteMode( NetPeer peer )
    //    {
    //        //        try
    //        //        {
    //        //            mutex.lock();
    //        setKeyWriteMode( getPeerKey( peer ) );
    //        //        }
    //        //        finally
    //        //        {
    //        //            mutex.unlock();
    //        //        }
    //    }

    private SelectionKey registerOperation( SocketChannel socketChannel, int op ) throws ClosedChannelException
    {
        //        try
        //        {
        //            mutex.lock();

        if ( selector == null || socketChannel == null )
            throw new ClosedChannelException();

        return socketChannel.register( selector, op );
        //        }
        //        finally
        //        {
        //            mutex.unlock();
        //        }
    }

    int getConnectedPeerCount()
    {
        try
        {
            mutex.lock();
            return connectedPeers.size();
        }
        finally
        {
            mutex.unlock();
        }
    }

    //    void addPeer( NetPeer peer, SocketChannel socketChannel, int operation ) throws IOException
    //    {
    //        SelectionKey key = null;
    //
    //        try
    //        {
    //            mutex.lock();
    //
    //            socketChannel.configureBlocking( false );
    //            //            key = socketChannel.register( selector.open(), operation ); // register the new SocketChannel with our Selector, indicating we'd like to be notified when there's data waiting to be read/write
    //            key = registerOperation( socketChannel, operation ); // register the new SocketChannel with our Selector, indicating we'd like to be notified when there's data waiting to be read/write
    //
    //            peer.open( socketChannel );
    //            //clientPeer.setDebugLog( debugLog );
    //            peer.setSocketTimeout( socketTimeout );
    //            //clientPeer.setLogLocalPort( false );
    //            peer.setIdentity( identity );
    //            connectedPeers.add( peer );
    //
    //            key.attach( peer );
    //        }
    //        catch( IOException e )
    //        {
    //            try
    //            {
    //                socketChannel.close();
    //            }
    //            catch( IOException e1 )
    //            {
    //                logMessage( "error closing socketchannel", e1 );
    //            }
    //
    //            if ( peer != null )
    //                peer.close();
    //
    //            doCancelKey( key );
    //
    //            throw e;
    //        }
    //        finally
    //        {
    //            mutex.unlock();
    //        }
    //    }

    //    void addPeer( NetPeer peer, SocketChannel socketChannel, int operation ) throws IOException
    //    {
    //        SelectionKey key = null;
    //
    //        try
    //        {
    //            mutex.lock();
    //
    //            socketChannel.configureBlocking( false );
    //            //            key = socketChannel.register( selector.open(), operation ); // register the new SocketChannel with our Selector, indicating we'd like to be notified when there's data waiting to be read/write
    //            key = registerOperation( socketChannel, operation ); // register the new SocketChannel with our Selector, indicating we'd like to be notified when there's data waiting to be read/write
    //
    //            peer.open( socketChannel );
    //            //clientPeer.setDebugLog( debugLog );
    //            peer.setSocketTimeout( socketTimeout );
    //            //clientPeer.setLogLocalPort( false );
    //            peer.setIdentity( identity );
    //            connectedPeers.add( peer );
    //
    //            key.attach( peer );
    //        }
    //        catch( IOException e )
    //        {
    //            try
    //            {
    //                socketChannel.close();
    //            }
    //            catch( IOException e1 )
    //            {
    //                logMessage( "error closing socketchannel", e1 );
    //            }
    //
    //            if ( peer != null )
    //                peer.close();
    //
    //            doCancelKey( key );
    //
    //            throw e;
    //        }
    //        finally
    //        {
    //            mutex.unlock();
    //        }
    //    }

    //    void addPeer( NetPeer peer, SocketChannel socketChannel, int operation )
    //    {
    //        try
    //        {
    //            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.AddPeer );
    //            cmd.data1 = peer;
    //            cmd.data2 = socketChannel;
    //            cmd.data3 = Integer.valueOf( operation );
    //            commands.put( cmd );
    //        }
    //        catch( InterruptedException e )
    //        {
    //            netEngine.notifyException( e );
    //        }
    //    }

    private void doConnectToServer( String host, int port )
    {
        //        System.out.println( "SelectorThread.doConnectToServer " + Thread.currentThread() );

        SocketChannel socketChannel = null;
        //        long startConnect = System.currentTimeMillis();
        try
        {
            SocketAddress adr = new InetSocketAddress( host, port );
            socketChannel = SocketChannel.open();
            socketChannel.socket().setKeepAlive( true );
            socketChannel.socket().setSoTimeout( peerSocketTimeout );
            //            System.out.println( "connect to server with timeout " + peerSocketTimeout + " (socket to=" + socketChannel.socket().getSoTimeout() + ")" );
            socketChannel.socket().connect( adr, peerSocketTimeout );
            socketChannel.configureBlocking( false );

            NetPeer peer = netEngine.createPeer();
            openSelector();
            doAddPeer( peer, socketChannel, SelectionKey.OP_WRITE );
        }
        catch( IOException e )
        {
            //            System.out.println( "error connecting to server after " + (System.currentTimeMillis() - startConnect) + " ms" );
            if ( socketChannel != null )
            {
                try
                {
                    socketChannel.close();
                }
                catch( IOException e1 )
                {
                }
                socketChannel = null;
            }
            //            logMessage( "error connecting to server at " + host + ":" + port, e );
            //            notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionFailed, e ) );
            netEngine.notifyPeerConnectionFailed( e );

            //            try
            //            {
            //                if ( socketChannel != null )
            //                    socketChannel.close();
            //            }
            //            catch( IOException e1 )
            //            {
            //                logMessage( getIdentity() + " : error closing socketchannel", e1 );
            //            }
        }
    }

    //    void removePeer( NetPeer peer )
    //    {
    //        try
    //        {
    //            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.RemovePeer );
    //            cmd.data1 = peer;
    //            commands.put( cmd );
    //        }
    //        catch( InterruptedException e )
    //        {
    //            netEngine.notifyException( e );
    //        }
    //    }

    /**
     * return a copy of connected peers list
     * @return
     */
    List<NetPeer> getPeers()
    {
        boolean doUnlock = false;
        try
        {
            doUnlock = mutex.lockSelf();
            List<NetPeer> res = new ArrayList<>();
            res.addAll( connectedPeers );
            return res;
        }
        finally
        {
            if ( doUnlock )
                mutex.unlock();
        }
    }

    //    void receiveNetMessages()
    //    {
    //        try
    //        {
    //            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.ReceiveMessages );
    //            commands.put( cmd );
    //        }
    //        catch( InterruptedException e )
    //        {
    //            netEngine.notifyException( e );
    //        }
    //    }
    //
    //    void sendOutputMessages()
    //    {
    //        try
    //        {
    //            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.SendMessages );
    //            commands.put( cmd );
    //        }
    //        catch( InterruptedException e )
    //        {
    //            netEngine.notifyException( e );
    //        }
    //    }

    //    SyncIterator<NetPeer> getPeerIterator()
    //    {
    //        //        return connectedPeers.iterator();
    //        return new SyncIterator<NetPeer>( connectedPeers, mutex );
    //    }

    private void doOpenListenSocket( int port )
    {
        try
        {
            if ( serverSocketChannel == null )
            {
                listenPort = port;
                logMessage( "opening listen socket on port " + listenPort + "..." );
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking( false );
                serverSocketChannel.socket().setSoTimeout( listenSocketTimeout );
                serverSocketChannel.socket().bind( new InetSocketAddress( listenPort ) );

                openSelector();

                // Register the server socket channel, indicating an interest in accepting new connections
                //                serverSocketChannel.register( getSelector(), SelectionKey.OP_ACCEPT );
                doRegisterAcceptOperation( serverSocketChannel );
                //
                //                logMessage( "successfully opened listen socket on port " + serverPort );
                //
                //                MVCModelChange msg = new MVCModelChange( this, this, NetModelChangeId.ListenSocketOpenSucceeded );
                //                notifyObservers( msg );
            }
        }
        catch( IOException e )
        {
            logMessage( "error opening listen socket", e );

            doCloseListenSocket();
            //            MVCModelChange msg = new MVCModelChange( this, this, NetModelChangeId.ListenSocketOpenFailed, e );
            //            notifyObservers( msg );
            netEngine.notifyListenSocketFailure( e );
        }
    }

    private void doCloseListenSocket()
    {
        try
        {
            doListen = false;

            //            mutex.lock();
            if ( serverSocketChannel != null )
            {
                logMessage( "stopping listen socket" );
                serverSocketChannel.close();
                serverSocketChannel = null;
            }
        }
        catch( IOException e )
        {
            logMessage( "error closing listen socket", e );
        }
        //        finally
        //        {
        //            serverSocketChannel = null;
        //            mutex.unlock();
        //        }
    }

    private void doListenClientConnection() throws ClosedChannelException
    {
        //        try
        //        {
        //            mutex.lock();

        //doOpenListenSocket();

        doListen = false;

        if ( serverSocketChannel == null )
            throw new ClosedChannelException();

        //        if ( serverSocketChannel != null )
        {
            try
            {
                //                List<SelectionKey> keys = selector.getReadyKeys( SelectionKey.OP_ACCEPT );
                List<SelectionKey> keys = getAcceptableKeys();
                for ( SelectionKey key : keys )
                    accept( key );
            }
            catch( IOException e )
            {
                logMessage( "error listening server socket", e );
                //notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionFailed, e ) );
                netEngine.notifyListenSocketFailure( e );
            }
        }
        //        }
        //        finally
        //        {
        //            mutex.unlock();
        //        }
    }

    //    private void registerAcceptOperation( ServerSocketChannel socketChannel )
    //    {
    //        try
    //        {
    //            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.RegisterAccept );
    //            cmd.data1 = socketChannel;
    //            commands.put( cmd );
    //        }
    //        catch( InterruptedException e )
    //        {
    //            netEngine.notifyException( e );
    //        }
    //    }

    private List<SelectionKey> getAcceptableKeys() throws IOException
    {
        //        try
        //        {
        //            mutex.lock();

        return getReadyKeys( SelectionKey.OP_ACCEPT );
        //        }
        //        finally
        //        {
        //            mutex.unlock();
        //        }
    }

    private void accept( SelectionKey key )
    {
        SocketChannel socketChannel = null;
        try
        {
            ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
            socketChannel = ssc.accept();
            socketChannel.configureBlocking( false );
            socketChannel.socket().setKeepAlive( true );
            socketChannel.socket().setSoTimeout( peerSocketTimeout );

            NetPeer peer = netEngine.createPeer();
            doAddPeer( peer, socketChannel, SelectionKey.OP_READ );

            //            logMessage( "got client connection from " + peer.getIPPort() );
            //            notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionSucceeded, peer ) );
        }
        catch( IOException e )
        {
            if ( socketChannel != null )
            {
                try
                {
                    socketChannel.close();
                }
                catch( IOException e1 )
                {
                }
                socketChannel = null;
            }

            logMessage( "error accepting client connection", e );
            //            notifyObservers( new MVCModelChange( this, this, NetModelChangeId.PeerConnectionFailed, e ) );
            netEngine.notifyPeerConnectionFailed( e );
        }
    }

    void connectToServer( String host, int port )
    {
        //        System.out.println( "SelectorThread.connectToServer " + Thread.currentThread() );

        try
        {
            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.ConnectToServer );
            if ( !commands.contains( cmd ) )
            {
                cmd.data1 = host;
                cmd.data2 = Integer.valueOf( port );
                commands.put( cmd );
            }
        }
        catch( InterruptedException e )
        {
            netEngine.notifyException( e );
        }
    }

    void enqueueOutputMessage( NetMessage nm )
    {
        try
        {
            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.SendMessage );
            cmd.data1 = nm;

            commands.put( cmd );
            //            if ( autoFlush )
            //                commands.put( cmd );
            //            else
            //                pendingOutputMessages.put( cmd );
        }
        catch( InterruptedException e )
        {
            netEngine.notifyException( e );
        }
    }

    void openListenSocket( int port )
    {
        try
        {
            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.OpenListenSocket );
            if ( !commands.contains( cmd ) )
            {
                cmd.data1 = Integer.valueOf( port );
                commands.put( cmd );
            }
        }
        catch( InterruptedException e )
        {
            netEngine.notifyException( e );
        }
    }

    void closeListenSocket()
    {
        try
        {
            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.CloseListenSocket );
            if ( !commands.contains( cmd ) )
                commands.put( cmd );
        }
        catch( InterruptedException e )
        {
            netEngine.notifyException( e );
        }
    }

    void listenClientConnections()
    {
        try
        {
            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.ListenClientConnections );
            if ( !commands.contains( cmd ) )
                commands.put( cmd );
        }
        catch( InterruptedException e )
        {
            netEngine.notifyException( e );
        }
    }

    void flush()
    {
        try
        {
            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.FlushMessages );
            if ( !commands.contains( cmd ) )
                commands.put( cmd );
        }
        catch( InterruptedException e )
        {
            netEngine.notifyException( e );
        }
    }

    void setAutoFlush( boolean b )
    {
        autoFlush = b;
        if ( autoFlush )
            flush();
    }

    void disconnectPeers()
    {
        try
        {
            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.DisconnectPeers );
            if ( !commands.contains( cmd ) )
                commands.put( cmd );
        }
        catch( InterruptedException e )
        {
            netEngine.notifyException( e );
        }
    }

    void closeConnections()
    {
        try
        {
            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.CloseConnections );
            if ( !commands.contains( cmd ) )
                commands.put( cmd );
        }
        catch( InterruptedException e )
        {
            netEngine.notifyException( e );
        }
    }

    void shutdown()
    {
        try
        {
            SelectorCommand cmd = new SelectorCommand( SelectorCommandId.Shutdown );
            if ( !commands.contains( cmd ) )
                commands.put( cmd );
        }
        catch( InterruptedException e )
        {
            netEngine.notifyException( e );
        }
    }

    void setIdentity( String s )
    {
        identity = s;
    }

    //    private String logOutputMessages( ArrayBlockingQueue<SelectorCommand> messages )
    //    {
    //        StringBuilder sb = new StringBuilder( '[' );
    //        for ( SelectorCommand cmd : messages )
    //        {
    //            if ( cmd.commandId == SelectorCommandId.SendMessage )
    //                sb.append( cmd.data1 );
    //            else
    //                sb.append( cmd.commandId );
    //            sb.append( ',' );
    //        }
    //        sb.append( ']' );
    //        return sb.toString();
    //    }

    private void logMessage( String s )
    {
        if ( NetEngine.doNetLog && doLog )
            NetEngine.logMessageModel.infoMessage( this, identity + " (selector)  : " + s );
    }

    private void logMessage( String s, Exception e )
    {
        //        if ( NetEngine.doNetLog && doLog )
        NetEngine.logMessageModel.errorMessage( this, identity + " (selector)  : " + s, e );
    }

    //    private void logConnectedPeers()
    //    {
    //        logMessage( "connected peers : " + connectedPeers.toString() );
    //    }

    private enum SelectorCommandId
    {
        /**
         * as a client peer, connect to a server
         */
        ConnectToServer,

        /**
         * send a message to client/server
         * in case of not immediate send, store message
         */
        SendMessage,

        /**
         * indeed send pending messages
         */
        FlushMessages,

        /**
         * as a server peer, create listen socket
         */
        OpenListenSocket,

        /**
         * as a server peer, listen to incoming connections once
         */
        ListenClientConnections,

        /**
         * as a server peer, close listen socket
         */
        CloseListenSocket,

        /**
         * as a client/server peer, close all peer connection (but not listen socket in case of server peer)
         */
        DisconnectPeers,

        /**
         * as a client/server peer, close all peer connections (including listen socket in case of server peer)
         */
        CloseConnections,

        /**
         * stop main loop
         */
        Shutdown;
    }

    private class SelectorCommand
    {
        private SelectorCommandId commandId;

        private Object data1;

        private Object data2;

        //        private Object data3;

        public SelectorCommand( SelectorCommandId id )
        {
            commandId = id;
        }

        @Override
        public String toString()
        {
            return commandId.toString();
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            SelectorCommand other = (SelectorCommand)obj;
            if ( !getOuterType().equals( other.getOuterType() ) )
                return false;
            if ( commandId != other.commandId )
                return false;
            return true;
        }

        private SelectorThread getOuterType()
        {
            return SelectorThread.this;
        }
    }

    @Override
    public String toString()
    {
        return identity + " " + connectedPeers.size() + " peers";
    }
}
