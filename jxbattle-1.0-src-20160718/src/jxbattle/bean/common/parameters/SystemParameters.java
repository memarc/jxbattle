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

package jxbattle.bean.common.parameters;

import jxbattle.bean.common.net.PortParameter;
import jxbattle.common.Consts;

import org.generic.bean.parameter.BoolParameter;
import org.generic.bean.parameter.IntParameter;
import org.generic.net.PeerInfo;

/**
 * overall non game related parameters
 */
public class SystemParameters
{
    // client only

    private String clientName;

    // common to server and client

    /**
     * socket timeout
     */
    private IntParameter socketTimeout;

    /**
     * sleep time applied before send/receive network messages (0=disable)
     * used to simulate/debug slow network connections 
     */
    private IntParameter networkBias;

    /**
     * write log during game
     */
    private BoolParameter logGameReplay;

    // client

    /**
     * list of previously reached servers 
     */
    //    private ServerHistory serverHistory;

    /**
     * current server
     */
    private PeerInfo serverPeerInfo;

    // server

    /**
     * port number server listens to
     */
    private PortParameter serverListenPort;

    /**
     * interval between update ticks sent to clients
     */
    private IntParameter gameTickInterval;

    /**
     * are orders from server send every update ticks or a multiple of it ?
     * if > 1, useful for slow network connections 
     */
    private IntParameter flushFrequency;

    /**
     * check client state consistency at each synchronisation step
     */
    private BoolParameter checkClientsState;

    public SystemParameters()
    {
        socketTimeout = new IntParameter( 10, 5000, Consts.defaultSocketTimeout );
        networkBias = new IntParameter( 0, 2000 );
        logGameReplay = new BoolParameter( false, false );
        //        serverHistory = new ServerHistory();
        serverPeerInfo = new PeerInfo( "localhost", Consts.defaultServerPort );
        serverListenPort = new PortParameter();
        gameTickInterval = new IntParameter( 1, 2000, Consts.emitFlushInterval );
        flushFrequency = new IntParameter( 1, 20, 2 );
        checkClientsState = new BoolParameter( false, false );
    }

    public String getClientName()
    {
        return clientName;
    }

    public void setClientName( String name )
    {
        clientName = name;
    }

    public IntParameter getSocketTimeout()
    {
        return socketTimeout;
    }

    public IntParameter getNetworkBias()
    {
        return networkBias;
    }

    public BoolParameter getLogGameReplay()
    {
        return logGameReplay;
    }

    //    public ServerHistory getServerHistory()
    //    {
    //        return serverHistory;
    //    }

    public PortParameter getServerListenPort()
    {
        return serverListenPort;
    }

    public IntParameter getGameTickInterval()
    {
        return gameTickInterval;
    }

    public IntParameter getFlushFrequency()
    {
        return flushFrequency;
    }

    public BoolParameter getCheckClientsState()
    {
        return checkClientsState;
    }

    public PeerInfo getServerPeerInfo()
    {
        //        return serverHistory.getCurrentPeer();
        return serverPeerInfo;
    }
}
