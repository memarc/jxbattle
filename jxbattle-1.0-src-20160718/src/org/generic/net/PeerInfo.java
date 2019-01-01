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

/**
 * peer information (host/port)
 */
public class PeerInfo
{
    /**
     * game server hostname (used in client, not in server)
     */
    private String host;

    /**
     * listen/connection port number
     */
    private int port;

    /**
     * socket channel
     */
    //private SocketChannel socketChannel;

    //    public PeerInfo()
    //    {
    //        host = "";
    //        port = -1;
    //    }

    public PeerInfo( int p )
    {
        port = p;
    }

    public PeerInfo( String h, int p )
    {
        host = h;
        port = p;
    }

    public int getPort()
    {
        return port;
    }

    public String getHost()
    {
        return host;
    }

    public void setPort( int p )
    {
        port = p;
    }

    public void setHost( String h )
    {
        host = h;
    }

    //    public SocketChannel getSocketChannel()
    //    {
    //        return socketChannel;
    //    }
    //
    //    public void setSocketChannel( SocketChannel channel )
    //    {
    //        socketChannel = channel;
    //    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        return result;
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
        PeerInfo other = (PeerInfo)obj;
        if ( host == null )
        {
            if ( other.host != null )
                return false;
        }
        else if ( !host.equals( other.host ) )
            return false;

        if ( port != other.port )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if ( host == null )
            sb.append( "<none>" );
        else
            sb.append( host );
        sb.append( ':' );
        sb.append( port );
        return sb.toString();
    }
}
