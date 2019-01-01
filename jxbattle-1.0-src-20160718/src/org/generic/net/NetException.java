package org.generic.net;

public class NetException extends Exception
{
    private Exception originalException;

    private NetPeer peer;

    public NetException( Exception e, NetPeer p )
    {
        originalException = e;
        peer = p;
    }

    public NetException( String s, NetPeer p )
    {
        originalException = new Exception( s );
        peer = p;
    }

    public Exception getOriginalException()
    {
        return originalException;
    }

    public NetPeer getPeer()
    {
        return peer;
    }
}
