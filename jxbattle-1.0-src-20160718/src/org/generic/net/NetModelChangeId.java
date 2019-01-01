package org.generic.net;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum NetModelChangeId implements MVCModelChangeId
{
    /**
     * connection to peer succeeded
     */
    PeerConnectionSucceeded,

    /**
     * connection to peer failed
     */
    PeerConnectionFailed,

    /**
     * connection to peer has been shut down
     */
    ConnectionShutdown,

    /**
     * opening of listen socket succeeded
     */
    ListenSocketOpenSucceeded,

    /**
     * a network message has been received
     */
    NetMessageReceived,

    /**
     * connection closed by peer
     */
    PeerDisconnected,

    /**
     * a net message has been sent
     */
    MessageSent,

    /**
    * general network error message
    */
    NetError,

    /**
    * opening of listen socket failed
    */
    ListenSocketOpenFailed,

    ;

    // MVCModelChangeId(EnumValue) interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public NetModelChangeId[] getValues()
    {
        return values();
    }

    @Override
    public NetModelChangeId getValueOf( int val )
    {
        for ( NetModelChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for NetModelChangeId enum " + val );
    }

    @Override
    public NetModelChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
