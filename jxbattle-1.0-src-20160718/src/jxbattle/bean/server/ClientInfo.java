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

package jxbattle.bean.server;

import jxbattle.common.net.XBPeer;

public class ClientInfo
{
    private String clientName;

    //private ClientPeerStateId clientPeerState;

    /**
     * connection to the client
     */
    private XBPeer clientPeer;

    public ClientInfo( XBPeer peer )
    {
        clientPeer = peer;
        //clientPeerState = ClientPeerStateId.initialValue();
    }

    public XBPeer getClientPeer()
    {
        return clientPeer;
    }

    public void setClientPeer( XBPeer peer )
    {
        clientPeer = peer;
    }

    public String getClientName()
    {
        return clientName;
    }

    public void setClientName( String name )
    {
        clientName = name;
    }

    //    public ClientPeerStateId getClientPeerState()
    //    {
    //        return clientPeerState;
    //    }
    //    public void setClientPeerState( ClientPeerStateId state )
    //    {
    //        clientPeerState = state;
    //    }
}