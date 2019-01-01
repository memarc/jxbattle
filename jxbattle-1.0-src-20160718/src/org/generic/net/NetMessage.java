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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.Date;

import org.generic.bean.Message;
import org.generic.bean.definedvalue.DefinedInteger;

/**
 * network message between client and server
 */
public abstract class NetMessage extends Message
{
    /**
     * message id
     */
    private NetMessageId messageId;

    /**
     * connection to peer 
     */
    protected NetPeer peer;

    /**
     * serial number
     */
    //    private int serialId;
    //    private static int serialIdGenerator = 0;

    /**
     * request id (synchronous message)
     */
    private static int requestIdGenerator = 0;

    /**
     * this message is a reply to a message with a given request number (used for synchronised message waiting)
     */
    private DefinedInteger requestId;

    /**
     * this message is a reply to a message with a given request number (used for synchronised message waiting)
     */
    private DefinedInteger replyToRequestId;

    /**
     * serialised byte size
     */
    protected int byteSize;

    static String systemCharset = System.getProperty( "file.encoding" );

    public static final int boolSize = 1;

    //public static final int charSize = 2;

    public static final int integerSize = 4;

    public static final int longSize = 8;

    public static final int floatSize = 4;

    public static final int doubleSize = 8;

    /**
     * logging switch for this class
     */
    private final static boolean doLog = false;

    public NetMessage( NetMessageId mid )
    {
        messageId = mid;
        //peer = p;
        //        serialId = serialIdGenerator++;
        requestId = new DefinedInteger();
        replyToRequestId = new DefinedInteger();
        byteSize = -1;
    }

    public NetMessage( NetMessageId mid, NetPeer p )
    {
        this( mid );
        peer = p;
    }

    public NetMessageId getMessageId()
    {
        return messageId;
    }

    //    public NetPeer getPeer()
    //    {
    //        throw new NetException( "NetworkMessage.getPeer() must be overriden" );
    //    }

    public abstract NetPeer getPeer();

    //    public void setPeer( NetPeer p ) throws NetException
    //    {
    //        if ( peer == null )
    //            peer = p;
    //        else if ( peer != p )
    //            throw new NetException( "NetworkMessage.setPeer() : override attempt", p );
    //    }

    //    public int getSerialId()
    //    {
    //        return serialId;
    //    }

    public void generateRequestId() throws NetException
    {
        //        if ( requestId.isDefined() )
        //            throw new NetException( "error overriding request id" );
        //
        //        requestId.setValue( requestIdGenerator++ );
        setRequestId( requestIdGenerator++ );
    }

    public int getRequestId()
    {
        return requestId.getValue();
    }

    public void setRequestId( int id ) throws NetException
    {
        if ( requestId.isDefined() )
            throw new NetException( "error overriding request id", getPeer() );

        requestId.setValue( id );
    }

    public int getReplyToRequestId()
    {
        return replyToRequestId.getValue();
    }

    public void setReplyToRequestId( int id ) throws NetException
    {
        if ( replyToRequestId.isDefined() )
            throw new NetException( "error overriding reply request id", getPeer() );

        replyToRequestId.setValue( id );
    }

    public void copyDataFrom( NetMessage nm ) throws NetException
    {
        //        if ( nm.getDataArray().size() > 0 )
        setDataArray( nm.getDataArray() );
        byteSize = nm.getByteSize();
        requestId.set( nm.requestId );
        replyToRequestId.set( nm.replyToRequestId );
    }

    /**
     * serialised byte size
     * @throws NetException 
     */
    public int getByteSize() throws NetException
    {
        if ( byteSize == -1 )
        {
            byteSize = 0;
            for ( Object o : getDataArray() )
                byteSize += getByteSize( o );

            logMessage( "getByteSize " + byteSize );
        }

        return byteSize;
    }

    public int getByteSize( Object d ) throws NetException
    {
        int res = 0;

        try
        {
            if ( d instanceof Integer )
                res = integerSize;
            else if ( d instanceof String )
            {
                String s = (String)d;

                if ( s.length() > 0 )
                {
                    res = integerSize; // system charset character length
                    //                res += systemCharset.length(); // system charset byte data
                    res += systemCharset.getBytes().length; // system charset byte data
                    res += integerSize; // string character length
                    try
                    {
                        res += s.getBytes( systemCharset ).length; // string byte data
                    }
                    catch( UnsupportedEncodingException e )
                    {
                        e.printStackTrace();
                        throw new NetException( e, getPeer() );
                    }
                }
                else
                    res = integerSize; // "0 byte long" integer value
            }
            else if ( d instanceof ByteBuffer )
            {
                res = integerSize; // buffer length
                ByteBuffer bb = ((ByteBuffer)d);
                res += bb.remaining();
            }
            else if ( d instanceof Boolean )
                res = boolSize;
            else if ( d instanceof Long || d instanceof Date )
                res = longSize;
            else if ( d instanceof Double )
                res = doubleSize;
            //            else if ( d instanceof ArrayList<?> )
            else if ( d instanceof AbstractList )
            {
                res = integerSize; // list size
                res += integerSize; // elements type
                //                for ( Object o : getDataArray() )
                for ( Object o : (AbstractList<?>)d )
                    res += getByteSize( o );
            }
            else
            {
                if ( d == null )
                    throw new NetException( "NetMessage.getByteSize(Object) : unprocessed null value", getPeer() );
                throw new NetException( "NetMessage.getByteSize(Object) : unprocessed type " + d.getClass(), getPeer() );
            }
        }
        finally
        {
            logSize( d, res );
        }

        return res;
    }

    public int getTypedObjectByteSize( Object d ) throws NetException
    {
        int res = integerSize; // ObjectType
        res += getByteSize( d );
        return res;
    }

    //    protected int getNullableByteSize( Object o, Class<?> clazz ) throws NetException
    protected int getNullableByteSize( Object o ) throws NetException
    {
        if ( o == null )
            return boolSize;

        //        return boolSize + getByteSize( clazz.cast( o ) );
        return boolSize + getByteSize( o );
    }

    protected void logMessage( String s )
    {
        if ( NetEngine.doNetLog && doLog )
            NetEngine.logMessageModel.infoMessage( this, "(netmessage)      : " + s );
    }

    protected void logSize( Object o, int size )
    {
        if ( NetEngine.doNetLog && doLog )
            //            NetEngine.logMessageModel.infoMessage( this, getClass().getSimpleName() + " : getByteSize(" + o.getClass().getSimpleName() + ":" + o + ")=" + size );
            System.out.println( getClass().getSimpleName() + " : getByteSize(" + o.getClass().getSimpleName() + ":" + o + ")=" + size );
    }

    @Override
    public String toString()
    {
        return messageId.toString();
    }
}
