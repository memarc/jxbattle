package org.generic.net;

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

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.generic.EnumValue;
import org.generic.bean.definedvalue.DefinedInteger;
import org.generic.bean.definedvalue.IDefinedValue;
import org.generic.bean.parameter.BoolParameter;
import org.generic.bean.parameter.EnumParameter;
import org.generic.bean.parameter.IntParameter;
import org.generic.bean.parameter.LongParameter;

public abstract class NetPeer
{
    //    private ChannelUtil channelUtil;

    /**
     * currently used byte buffer for net message deserialisation (receiveXXX methods)
     */
    private ByteBuffer inputByteBuffer;

    /**
     * currently used byte buffer for net message serialisation (sendXXX methods)
     */
    private ByteBuffer outputByteBuffer;

    /**
     * pending input buffer
     */
    ByteBuffer pendingInputBuffer;

    /**
     * pending output buffers to be written to socket channel
     */
    List<ByteBuffer> pendingOutputBuffers;

    //private DebugLog debugLog;

    /**
     * bytebuffer only aimed at receiving next message length
     */
    ByteBuffer intBuffer = ByteBuffer.allocate( 4 );

    /**
     * identity string used for logging
     */
    private String identity;

    /**
     * in toString(), log local port rather than peer port 
     */
    private boolean logLocalPort = false;

    private SocketChannel socketChannel;

    /**
     * logging switch for this class
     */
    private final static boolean doLog = false;

    /**
     * system string charset 
     */
    //private String systemCharset;

    public NetPeer()
    {
        //        channelUtil = null;
        //        inputMessages = new ArrayList<NetMessage>();
        pendingInputBuffer = null;
        pendingOutputBuffers = new ArrayList<>();
        //        lock = new Mutex();
        //        systemCharset = System.getProperty( "file.encoding" );
    }

    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }

    //    public void open( SocketChannel channel )
    //    {
    //        if ( channelUtil == null )
    //            channelUtil = new ChannelUtil( channel );
    //        else
    //            throw new IllegalStateException( "ChannelUtil already initialised" );
    //    }

    public void open( SocketChannel channel )
    {
        socketChannel = channel;
    }

    //    public boolean close()
    //    {
    //        boolean wasOpen = channelUtil != null;
    //
    //        if ( wasOpen )
    //        {
    //            channelUtil.close();
    //            channelUtil = null;
    //        }
    //
    //        //        inputMessages.clear();
    //
    //        return wasOpen;
    //    }

    public boolean close()
    {
        if ( socketChannel != null )
        {
            try
            {
                socketChannel.close();
            }
            catch( IOException e )
            {
            }
            finally
            {
                socketChannel = null;
            }
            return true;
        }

        return false;
    }

    //    private ChannelUtil getChannelUtil() throws IOException
    //    {
    //        if ( !isConnected() )
    //            throw new IOException( "connection not open/already closed" );
    //
    //        return channelUtil;
    //    }

    //    SelectionKey getSelectorKey( Selector selector )
    //    {
    //        if ( channelUtil != null )
    //            return channelUtil.getSelectorKey( selector );
    //
    //        return null;
    //    }

    //    SocketChannel getSocketChannel()
    //    {
    //        if ( channelUtil != null )
    //            return channelUtil.getSocketChannel();
    //
    //        return null;
    //    }

    SocketChannel getSocketChannel() throws IOException
    {
        if ( !isConnected() )
            throw new IOException( "connection not open/already closed" );

        return socketChannel;
    }

    //    boolean hashFlushableData()
    //    {
    //        if ( channelUtil != null )
    //            return channelUtil.hasFlushableData();
    //
    //        return false;
    //    }

    //    boolean hasWritableSpace()
    //    {
    //        if ( channelUtil != null )
    //            return channelUtil.hasWritableSpace();
    //
    //        return false;
    //    }

    //    public void setDebugLog( DebugLog dl )
    //    {
    //        debugLog = dl;
    //        channelUtil.setDebugLog( dl );
    //    }

    //    public boolean isConnected()
    //    {
    //        if ( channelUtil == null )
    //            return false;
    //
    //        return channelUtil.isConnected();
    //    }

    public boolean isConnected()
    {
        if ( socketChannel == null )
            return false;

        return socketChannel.isConnected();
    }

    //    public String getIPPort()
    //    {
    //        try
    //        {
    //            return getChannelUtil().getIP() + ":" + getChannelUtil().getPort();
    //        }
    //        catch( IOException e )
    //        {
    //            return "<not connected>";
    //        }
    //    }

    private String getIP() throws IOException
    {
        return getSocketChannel().socket().getInetAddress().getHostAddress();
    }

    private int getPort() throws IOException
    {
        return getSocketChannel().socket().getPort();
    }

    public String getIPPort()
    {
        try
        {
            return getIP() + ":" + getPort();
        }
        catch( IOException e )
        {
            return "<not connected>";
        }
    }

    //    public int getPort() throws IOException
    //    {
    //        return getChannelUtil().getPort();
    //    }
    //
    //    public int getLocalPort() throws IOException
    //    {
    //        return getChannelUtil().getLocalPort();
    //    }

    //    public void setNetworkBias( int bias ) throws IOException
    //    {
    //        getChannelUtil().setNetworkBias( bias );
    //    }

    //    void readInputBuffer() throws EOFException, IOException
    //    {
    //        getChannelUtil().readInputBuffer();
    //    }

    //    void flushOutputBuffer() throws EOFException, IOException
    //    {
    //        //        try
    //        //        {
    //        //            lock.lock();
    //
    //        getChannelUtil().flushOutputBuffer();
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    //    void processPendingInputs() throws IOException
    //    {
    //        getChannelUtil().processPendingInputs();
    //    }
    //
    //    void processPendingOutputs() throws IOException
    //    {
    //        getChannelUtil().processPendingOutputs();
    //    }

    //    public void setSocketTimeout( int timeout ) throws IOException
    //    {
    //        getChannelUtil().setSocketTimeout( timeout );
    //    }

    void setSocketTimeout( int timeout ) throws IOException
    {
        getSocketChannel().socket().setSoTimeout( timeout );
    }

    //    protected long receiveLong( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        //        return getChannelUtil().receiveInt( waitData );
    //        long res = getChannelUtil().receiveLong( waitData );
    //        logMessage( "received long " + res );
    //        return res;
    //    }
    //
    //    protected void sendLong( long l ) throws EOFException, IOException
    //    {
    //        getChannelUtil().sendLong( l );
    //        //        if ( autoFlush )
    //        //            getChannelUtil().flushOutputBuffer();
    //    }

    //    protected Boolean receiveBoolean( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        //        return Boolean.valueOf( getChannelUtil().receiveInt( waitData ) == 1 );
    //        Boolean res = getChannelUtil().receiveBoolean( waitData );
    //        logMessage( "received bool " + res );
    //        return res;
    //    }

    //    protected int receiveInt( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        //        return getChannelUtil().receiveInt( waitData );
    //        int res = getChannelUtil().receiveInt( waitData );
    //        logMessage( "received int " + res );
    //        return res;
    //    }

    //    protected EnumValue receiveEnum( Class<? extends EnumValue> cls ) throws NetException
    //    {
    //        try
    //        {
    //            EnumValue inst = cls.newInstance();
    //            return inst.getValueOf( receiveInt() );
    //            //            Method m = cls.getMethod( "getValueOf", new Class[] { int.class } ); // getValueOf : static method not declared in EnumValue interface
    //            //            return (EnumValue)m.invoke( null, receiveInt() );
    //        }
    //        catch( Exception e )
    //        {
    //            throw new NetException( e, this );
    //        }
    //    }

    protected void sendEnum( EnumValue e )
    {
        outputByteBuffer.putInt( e.getOrdinal() );
    }

    protected void receiveEnumParameter( EnumParameter<? extends EnumValue> param ) //, Class<? extends EnumValue> clss )
    {
        //        param.setValue( receiveEnum( clss ) );
        param.setValue( receiveInt() );
    }

    protected int receiveInt()
    {
        return inputByteBuffer.getInt();
    }

    protected Integer receiveInteger()
    {
        return Integer.valueOf( receiveInt() );
    }

    protected void sendInt( int i )
    {
        outputByteBuffer.putInt( i );
    }

    protected DefinedInteger receiveDefinedInt()
    {
        DefinedInteger res = new DefinedInteger();
        boolean defined = receiveBool();
        if ( defined )
            res.setValue( receiveInt() );
        logMessage( "received defined int " + res );
        return res;
    }

    protected void sendDefinedInt( IDefinedValue<Integer> i )
    {
        logMessage( "send defined int " + i );
        sendBool( i.isDefined() );

        if ( i.isDefined() )
            sendInt( i.getValue() );

        //        if ( autoFlush )
        //            getChannelUtil().flushOutputBuffer();
    }

    protected void receiveIntParameter( IntParameter param )
    {
        param.setValue( receiveInt() );
    }

    protected long receiveLong()
    {
        return inputByteBuffer.getLong();
    }

    protected void sendLong( long l )
    {
        outputByteBuffer.putLong( l );
    }

    protected void receiveLongParameter( LongParameter param )
    {
        param.setValue( receiveLong() );
    }

    protected double receiveDouble()
    {
        return inputByteBuffer.getDouble();
    }

    protected void sendDouble( double d )
    {
        outputByteBuffer.putDouble( d );
    }

    protected Float receiveFloat()
    {
        return Float.valueOf( inputByteBuffer.getFloat() );
    }

    protected void sendFloat( float f )
    {
        outputByteBuffer.putFloat( f );
    }

    protected boolean receiveBool()
    {
        return inputByteBuffer.get() == 1;
    }

    protected Boolean receiveBoolean()
    {
        return Boolean.valueOf( receiveBool() );
    }

    protected void sendBoolean( Boolean b )
    {
        sendBool( b.booleanValue() );
    }

    protected void sendBool( boolean b )
    {
        outputByteBuffer.put( (byte)(b ? 1 : 0) );
    }

    protected void receiveBoolParameter( BoolParameter param )
    {
        param.setValue( receiveBool() );
    }

    private byte[] receiveBytes()
    {
        int len = inputByteBuffer.getInt();
        return receiveBytes( len );
    }

    private byte[] receiveBytes( int len )
    {
        byte[] bs = new byte[ len ];
        inputByteBuffer.get( bs );
        return bs;
    }

    private void sendBytes( byte[] bs )
    {
        outputByteBuffer.putInt( bs.length );
        outputByteBuffer.put( bs );
    }

    //    protected String receiveString() throws NetException
    //    {
    //        int s = inputByteBuffer.getInt();
    //        switch ( s )
    //        {
    //            case -1:
    //                return null;
    //            case 0:
    //                return "";
    //            default:
    //
    //                // charset
    //                String charset = new String( receiveBytes( s ) );
    //
    //                // string
    //                try
    //                {
    //                    return new String( receiveBytes(), charset );
    //                }
    //                catch( UnsupportedEncodingException e )
    //                {
    //                    e.printStackTrace();
    //                    throw new NetException( e, this );
    //                }
    //        }
    //    }

    protected String receiveString() throws NetException
    {
        //        boolean defined = receiveBool();
        //        if ( defined )
        //        {
        int s = inputByteBuffer.getInt();
        switch ( s )
        {
            case 0:
                return "";

            default:
                // charset
                String charset = new String( receiveBytes( s ) );

                // string
                try
                {
                    return new String( receiveBytes(), charset );
                }
                catch( UnsupportedEncodingException e )
                {
                    e.printStackTrace();
                    throw new NetException( e, this );
                }
        }
        //        }
        //        return null;
    }

    //    protected void sendString( String s ) throws NetException
    //    {
    //        if ( s == null )
    //            sendInt( -1 );
    //        else if ( s.length() == 0 )
    //            sendInt( 0 );
    //        else
    //        {
    //            sendBytes( NetMessage.systemCharset.getBytes() );
    //            try
    //            {
    //                sendBytes( s.getBytes( NetMessage.systemCharset ) );
    //            }
    //            catch( UnsupportedEncodingException e )
    //            {
    //                e.printStackTrace();
    //                throw new NetException( e, this );
    //            }
    //        }
    //    }

    protected void sendString( String s ) throws NetException
    {
        //        if ( s == null )
        //            sendBool( false );
        //        else
        //        {
        //            sendBool( true );

        if ( s.length() == 0 )
            sendInt( 0 );
        else
        {
            sendBytes( NetMessage.systemCharset.getBytes() );
            try
            {
                sendBytes( s.getBytes( NetMessage.systemCharset ) );
            }
            catch( UnsupportedEncodingException e )
            {
                e.printStackTrace();
                throw new NetException( e, this );
            }
        }
        //        }
    }

    protected Date receiveDate()
    {
        return new Date( receiveLong() );
    }

    protected void sendDate( Date d )
    {
        sendLong( d.getTime() );
    }

    protected Object receiveNullableObject( Class<?> clazz ) throws NetException
    {
        boolean isNull = receiveBool();
        if ( isNull )
            return null;

        return clazz.cast( receiveObject( clazz ) );
    }

    //    protected Object receiveNullableObject() throws NetException
    //    {
    //        boolean isNull = receiveBool();
    //        if ( isNull )
    //            return null;
    //
    //        return receiveObject();
    //    }

    protected void sendNullableObject( Object o ) throws NetException
    {
        if ( o == null )
            sendBool( true ); // is null
        else
        {
            sendBool( false ); // is not null
            sendObject( o );
        }
    }

    protected Object receiveObject( Class<?> clazz ) throws NetException
    {
        if ( clazz == String.class )
            return receiveString();
        else if ( clazz == java.util.Date.class )
            return receiveDate();

        throw new NetException( "NetPeer.receiveObject : unhandled " + clazz, this );
    }

    protected void sendObject( Object o ) throws NetException
    {
        if ( o instanceof String )
            sendString( (String)o );
        else if ( o instanceof java.util.Date )
            sendDate( (java.util.Date)o );
        else
            throw new NetException( "NetPeer.sendObject : unhandled class " + o.getClass(), this );
    }

    protected Object receiveTypedObject() throws NetException
    {
        int type = receiveInt();
        ObjectType ot = ObjectType.getValueOfS( type );
        switch ( ot )
        {
            case STRING:
                return receiveString();

            case DATE:
                return receiveDate();

            case LIST:
                int count = receiveInt();
                ArrayList<Object> res = new ArrayList<>();
                if ( count > 0 )
                {
                    int lot = receiveInt();
                    ObjectType listObjType = ObjectType.getValueOfS( lot );

                    switch ( listObjType )
                    {
                        case STRING:
                            for ( int i = count; i > 0; i-- )
                                res.add( receiveString() );
                            break;

                        case DATE:
                            for ( int i = count; i > 0; i-- )
                                res.add( receiveDate() );
                            break;

                        default:
                            throw new NetException( "NetPeer.receiveTypedObject : unhandled object type", this );
                    }
                    //                    for ( int i = count; i > 0; i-- )
                    //                        res.add( receiveTypedObject() );
                }
                return res;

            default:
                throw new NetException( "NetPeer.receiveTypedObject : unhandled object type", this );
        }
    }

    protected void sendTypedObject( Object o ) throws NetException
    {
        if ( o instanceof String )
        {
            sendEnum( ObjectType.STRING );
            sendString( (String)o );
        }
        else if ( o instanceof java.util.Date )
        {
            sendEnum( ObjectType.DATE );
            sendDate( (java.util.Date)o );
        }
        else if ( o instanceof java.util.List )
        {
            sendEnum( ObjectType.LIST );
            List l = (List)o;
            int n = l.size();
            sendInt( n );
            if ( n > 0 )
            {
                Object lo = l.get( 0 );
                if ( lo instanceof String )
                    sendEnum( ObjectType.STRING );
                else if ( lo instanceof java.util.Date )
                    sendEnum( ObjectType.DATE );
                else
                    throw new NetException( "NetPeer.sendTypedObject : unhandled class " + lo.getClass(), this );
            }
            for ( Object lo : l )
                sendObject( lo );
        }
        else
            throw new NetException( "NetPeer.sendTypedObject : unhandled class " + o.getClass(), this );
    }

    protected ByteBuffer receiveByteBuffer()
    {
        return ByteBuffer.wrap( receiveBytes() );
    }

    protected void sendByteBuffer( ByteBuffer bb )
    {
        //bb.flip();
        sendInt( bb.remaining() );
        outputByteBuffer.put( bb );
    }

    /**
     * flush output buffer at each send
     */
    //protected boolean autoFlush = false;

    //    protected long receiveLong( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        //        return getChannelUtil().receiveInt( waitData );
    //        long res = getChannelUtil().receiveLong( waitData );
    //        logMessage( "received long " + res );
    //        return res;
    //    }
    //
    //    protected void sendLong( long l ) throws EOFException, IOException
    //    {
    //        getChannelUtil().sendLong( l );
    //        //        if ( autoFlush )
    //        //            getChannelUtil().flushOutputBuffer();
    //    }

    //    protected Boolean receiveBoolean( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        //        return Boolean.valueOf( getChannelUtil().receiveInt( waitData ) == 1 );
    //        Boolean res = getChannelUtil().receiveBoolean( waitData );
    //        logMessage( "received bool " + res );
    //        return res;
    //    }

    //    protected int receiveInt( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        //        return getChannelUtil().receiveInt( waitData );
    //        int res = getChannelUtil().receiveInt( waitData );
    //        logMessage( "received int " + res );
    //        return res;
    //    }

    boolean readByteBuffer( ByteBuffer bb ) throws SocketTimeoutException, EOFException, IOException
    {
        try
        {
            int n = socketChannel.read( bb );
            switch ( n )
            {
                case 0:
                    throw new SocketTimeoutException();

                case -1:
                    throw new EOFException();

                default:
                    return bb.remaining() == 0;
            }
        }
        catch( IOException e )
        {
            String em = e.getMessage();
            if ( em != null && (em.equals( "Connection reset by peer" ) || em.equals( "Connexion ré-initialisée par le correspondant" )) )
                throw new EOFException();

            throw e;
        }
    }

    boolean writeByteBuffer( ByteBuffer bb ) throws IOException
    {
        //return outputBuffer.putByteBuffer( bb );
        socketChannel.write( bb );
        boolean res = bb.remaining() == 0;
        if ( res )
            socketChannel.socket().getOutputStream().flush();
        return res;
    }

    int tryReceiveInt() throws SocketTimeoutException, EOFException, IOException
    {
        intBuffer.clear();
        //        if ( channelUtil.readByteBuffer( intBuffer ) )
        if ( readByteBuffer( intBuffer ) )
        {
            intBuffer.flip();
            int res = intBuffer.getInt();
            logMessage( "received int " + res );
            return res;
        }

        throw new IOException( "incomplete int received" );
    }

    //    boolean readByteBuffer( ByteBuffer bb ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        return channelUtil.readByteBuffer( bb );
    //    }

    //    protected long receiveLong( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        long res = getChannelUtil().receiveLong( waitData );
    //        logMessage( "received long " + res );
    //        return res;
    //    }
    //
    //    protected double receiveDouble( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        double res = getChannelUtil().receiveDouble( waitData );
    //        logMessage( "received double " + res );
    //        return res;
    //    }
    //
    //    protected Integer receiveInteger( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        //        return Integer.valueOf( getChannelUtil().receiveInt( waitData ) );
    //        return Integer.valueOf( receiveInt( waitData ) );
    //    }
    //
    //    protected DefinedInteger receiveDefinedInt( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        DefinedInteger res = new DefinedInteger();
    //        boolean defined = receiveBoolean( waitData );
    //        if ( defined )
    //            res.setValue( receiveInt( true ) );
    //        logMessage( "received defined int " + res );
    //        return res;
    //    }

    //    protected String receiveString( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        return getChannelUtil().receiveString( waitData );
    //    }

    //    protected String receiveString( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        int len = receiveInt( waitData );
    //        switch ( len )
    //        {
    //            case -1:
    //                return null;
    //
    //            case 0:
    //                return "";
    //
    //            default:
    //                // charset
    //
    //                String charset = getChannelUtil().receiveString( null, true );
    //                logMessage( "received charset " + charset );
    //
    //                //  string
    //
    //                String res = getChannelUtil().receiveString( charset, true );
    //                //                ByteBuffer bb = getChannelUtil().receiveByteBuffer( true );
    //                //                String res = new String( bb.array(), charset );
    //                logMessage( "received string " + res );
    //                return res;
    //        }
    //    }

    //    protected ByteBuffer receiveByteBuffer( boolean waitData ) throws SocketTimeoutException, EOFException, IOException
    //    {
    //        return getChannelUtil().receiveByteBuffer( waitData );
    //    }
    //
    //    protected void sendBoolean( boolean b ) throws EOFException, IOException
    //    {
    //        logMessage( "send bool " + b );
    //        getChannelUtil().sendBoolean( b );
    //        //        if ( autoFlush )
    //        //            getChannelUtil().flushOutputBuffer();
    //    }
    //
    //    protected void sendInt( int i ) throws EOFException, IOException
    //    {
    //        logMessage( "send int " + i );
    //        getChannelUtil().sendInt( i );
    //        //        if ( autoFlush )
    //        //            getChannelUtil().flushOutputBuffer();
    //    }
    //
    //    protected void sendLong( long l ) throws EOFException, IOException
    //    {
    //        logMessage( "send long " + l );
    //        getChannelUtil().sendLong( l );
    //        //        if ( autoFlush )
    //        //            getChannelUtil().flushOutputBuffer();
    //    }
    //
    //    protected void sendDouble( double d ) throws EOFException, IOException
    //    {
    //        logMessage( "send double " + d );
    //        getChannelUtil().sendDouble( d );
    //        //        if ( autoFlush )
    //        //            getChannelUtil().flushOutputBuffer();
    //    }

    //    protected void sendDefinedInt( IDefinedValue<Integer> i ) throws IOException
    //    {
    //        logMessage( "send defined int " + i );
    //        getChannelUtil().sendBoolean( i.isDefined() );
    //
    //        if ( i.isDefined() )
    //            getChannelUtil().sendInt( i.getValue() );
    //
    //        //        if ( autoFlush )
    //        //            getChannelUtil().flushOutputBuffer();
    //    }
    //
    //    protected void sendEnum( EnumValue v ) throws IOException
    //    {
    //        logMessage( "send enum " + v );
    //        getChannelUtil().sendInt( v.getOrdinal() );
    //        //        if ( autoFlush )
    //        //            getChannelUtil().flushOutputBuffer();
    //    }

    //    protected void sendString( String s ) throws EOFException, IOException
    //    {
    //        logMessage( "send string " + s );
    //
    //        if ( s == null )
    //            sendInt( -1 );
    //        else
    //        {
    //            int len = s.getBytes( systemCharset ).length;
    //            sendInt( len );
    //
    //            if ( len > 0 )
    //            {
    //                // send charset
    //
    //                getChannelUtil().sendString( systemCharset, null );
    //
    //                // send string
    //
    //                getChannelUtil().sendString( s, systemCharset );
    //            }
    //        }
    //    }

    //    boolean writeByteBuffer( ByteBuffer bb ) throws IOException
    //    {
    //        return channelUtil.writeByteBuffer( bb );
    //    }

    //    protected void sendByteBuffer( ByteBuffer b ) throws EOFException, IOException
    //    {
    //        getChannelUtil().sendByteBuffer( b );
    //        //        if ( autoFlush )
    //        //            getChannelUtil().flushOutputBuffer();
    //    }

    //    NetMessage hasInputMessage()
    //    {
    //        if ( inputMessages.size() > 0 )
    //            return inputMessages.remove( 0 );
    //
    //        return null;
    //    }

    protected void sendNetMessageId( NetMessageId id )
    {
        sendEnum( id );
    }

    protected abstract NetMessageId receiveNetMessageId() throws NetException;

    //    protected abstract NetMessage createMessage( NetMessageId mid ) throws NetException;
    protected abstract NetMessage createMessage( NetMessageId mid, NetPeer peer ) throws NetException;

    //    NetMessage copyMessage( NetMessage nm ) throws NetException
    //    {
    //        NetMessage res = createMessage( nm.getMessageId(), null );
    //        res.copyDataFrom( nm );
    //        return res;
    //    }

    NetMessage copyMessage( NetMessage nm, NetPeer peer ) throws NetException
    {
        NetMessage res = createMessage( nm.getMessageId(), peer );
        res.copyDataFrom( nm );
        return res;
    }

    private NetMessage doCreateMessage( NetMessageId networkMessageId ) throws NetException
    {
        //        NetMessage res = createMessage( networkMessageId );
        //        res.setPeer( this );
        //        return res;
        return createMessage( networkMessageId, this );
    }

    protected abstract void receiveNetMessage( NetMessage nm ) throws NetException;

    NetMessage receiveNetMessage( ByteBuffer bb ) throws NetException
    {
        inputByteBuffer = bb;
        NetMessageId networkMessageId = receiveNetMessageId();

        logMessage( "received NetMessageId " + networkMessageId );

        NetMessage nm = doCreateMessage( networkMessageId );

        receiveNetMessage( nm );
        return nm;
    }

    //    void receiveNetMessages() throws Exception
    //    {
    //        while ( true )
    //        {
    //            NetMessage nm = receiveNetMessage();
    //            if ( nm != null )
    //            {
    //                inputMessages.add( nm );
    //                logMessage( "received message " + nm.toString() );
    //            }
    //        }
    //    }

    protected abstract void sendNetMessage( NetMessage nm ) throws NetException;

    void sendMessage( NetMessage nm, ByteBuffer bb ) throws NetException
    {
        outputByteBuffer = bb;
        sendNetMessageId( nm.getMessageId() );
        sendNetMessage( nm );
    }

    //    void addOutputMessage( NetMessage nm ) throws IOException
    //    {
    //        //            NetworkPeer peer = nm.getPeer();
    //        //            if ( peer == null || peer != this )
    //        //                throw new NetworkException( "invalid message peer " + (peer == null ? "<null>" : peer), this );
    //
    //        logMessage( "enqueued output message " + nm );
    //        sendNetMessage( nm );
    //    }

    public String getIdentity()
    {
        return identity;
    }

    void setIdentity( String s )
    {
        identity = s;
        //        channelUtil.setIdentity( identity );
    }

    //    protected abstract boolean canLog();

    protected void logMessage( String s )
    {
        if ( NetEngine.doNetLog && doLog )
            //        if ( canLog() )
            NetEngine.logMessageModel.infoMessage( this, identity + " (netpeer)      : " + s );
    }

    public void setLogLocalPort( boolean localPort )
    {
        logLocalPort = localPort;
    }

    @Override
    public String toString()
    {
        return identity + " " + getIPPort();

        //        if ( !isConnected() )
        //            return identity + " <not connected>";

        //        try
        //        {
        //            if ( logLocalPort )
        //                //return identity+ " "+getChannelUtil().toStringLocalPort();
        //                return identity + " " + getChannelUtil().toStringLocalRemotePort();

        //            return identity + " " + getChannelUtil().toString();
        //        }
        //        catch( IOException e )
        //        {
        //            return identity + " <error>";
        //        }
    }

    //    public void setDebugLog( DebugLog dl )
    //    {
    //        debugLog = dl;
    //        channelUtil.setDebugLog( dl );
    //    }
}
