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

package jxbattle.gui.base.client.connectiontoserverpanel;

import jxbattle.model.client.ClientEngine;
import jxbattle.model.common.PortParameterModel;
import jxbattle.model.common.parameters.ServerHistoryModel;
import jxbattle.model.common.parameters.SystemParametersModel;

import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.net.PeerInfo;

public class ConnectionToServerPanelModel extends MVCModelImpl
{
    private ClientEngine clientEngine;

    //    private ServerHistoryModel serverHistoryModel; // list of all server hosts/ports in combo
    private SystemParametersModel systemParametersModel;

    public ConnectionToServerPanelModel( ClientEngine ce )
    {
        clientEngine = ce;
        //        serverHistoryModel = clientEngine.getSystemParametersModel().getServerHistoryModel();
        systemParametersModel = ce.getSystemParametersModel();
    }

    PortParameterModel getPortModel()
    {
        //        return serverHistoryModel.getPortParameterModel();
        return systemParametersModel.getServerSocketModel().getPortParameterModel();
    }

    //    void setPort( Object sender, int port )
    //    {
    //        //serverHistoryModel.getCurrentPeerInfoModel().setPort( sender, port );
    //        systemParametersModel.getServerSocketModel().setPort( sender, port );
    //    }

    String getHost()
    {
        //        return serverHistoryModel.getCurrentPeerInfoModel().getHost();
        return systemParametersModel.getServerSocketModel().getHost();
    }

    //    void setHost( Object sender, String host )
    //    {
    //        //        serverHistoryModel.getCurrentPeerInfoModel().setHost( sender, host );
    //        systemParametersModel.getServerSocketModel().setHost( sender, host );
    //    }

    ServerHistoryModel getServerHistoryModel()
    {
        //        return serverHistoryModel;
        return systemParametersModel.getServerHistoryModel();
    }

    void addCurrentToHistory( Object sender )
    {
        systemParametersModel.getServerHistoryModel().addUnique( sender, systemParametersModel.getServerSocketModel().getPeerInfo() );
    }

    void connectServer( Object sender, PeerInfo pi )
    {
        clientEngine.connectServer( sender, pi );
    }

    void disconnectServer()
    {
        clientEngine.disconnectServer();
    }

    boolean isConnected()
    {
        return clientEngine.getClientSideConnectionModel().isConnected();
    }

    boolean isClientNameOk()
    {
        return clientEngine.isClientNameOk();
    }

    boolean isGaming()
    {
        return clientEngine.isGaming();
    }

    boolean isGameEnded()
    {
        return clientEngine.isGameEnded();
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        //        serverHistoryModel.addObserver( obs );
        systemParametersModel.getServerHistoryModel().addObserver( obs );
        clientEngine.addObserver( obs );
        clientEngine.getClientSideConnectionModel().addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        //        serverHistoryModel.removeObserver( obs );
        systemParametersModel.getServerHistoryModel().removeObserver( obs );
        clientEngine.removeObserver( obs );
        clientEngine.getClientSideConnectionModel().removeObserver( obs );
    }
}
