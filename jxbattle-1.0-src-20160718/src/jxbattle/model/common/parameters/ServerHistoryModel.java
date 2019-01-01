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

package jxbattle.model.common.parameters;

import java.util.ArrayList;
import java.util.List;

import jxbattle.model.common.PeerInfoModel;

import org.generic.list.SyncIterator;
import org.generic.mvc.model.list.SyncListModel;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.net.PeerInfo;

public class ServerHistoryModel /* extends SyncListModel<PeerInfoModel> */implements Iterable<PeerInfoModel>
{
    // observed beans

    //    private ServerHistory serverHistory;

    // models

    //private PortParameterModel portParameterModel;

    //    private PeerInfoModel currentPeerInfoModel;

    private SyncListModel<PeerInfoModel> peerInfoModels;

    //    ServerHistoryModel( ServerHistory sh )
    //    {
    //        peerInfoModels = new SyncListModel<PeerInfoModel>();
    //        setServerHistory( this, sh );
    //    }

    ServerHistoryModel()
    {
        peerInfoModels = new SyncListModel<PeerInfoModel>();
    }

    //    private ServerHistory getServerHistory()
    //    {
    //        return serverHistory;
    //    }

    //    private void setServerHistory( Object sender, ServerHistory sh )
    //    {
    //        serverHistory = sh;
    //        peerInfoModels.clear( sender );
    //        for ( PeerInfo peerInfo : serverHistory )
    //            peerInfoModels.add( sender, new PeerInfoModel( peerInfo ) );
    //    }

    public void addUnique( Object sender, PeerInfo pi )
    {
        peerInfoModels.addUnique( sender, new PeerInfoModel( pi ) );
    }

    public void removeFirstFromHistory( Object sender )
    {
        if ( peerInfoModels.size() > 0 )
            peerInfoModels.remove( sender, peerInfoModels.get( 0 ) );
    }

    public List<PeerInfo> getPeerInfos()
    {
        List<PeerInfo> res = new ArrayList<PeerInfo>();

        for ( PeerInfoModel pim : peerInfoModels )
            res.add( pim.getPeerInfo() );

        return res;
    }

    //    public void addCurrentToHistory( Object sender )
    //    {
    //        PeerInfoModel pi = getCurrentPeerInfoModel();
    //        //if ( !contains( pi ) )
    //        //add( sender, pi );
    //        addUnique( sender, pi );
    //    }

    //    public PeerInfoModel getCurrentPeerInfoModel()
    //    {
    //        if ( currentPeerInfoModel == null )
    //            currentPeerInfoModel = new PeerInfoModel( getServerHistory().getCurrentPeer() );
    //        return currentPeerInfoModel;
    //    }

    //    public PortParameterModel getPortParameterModel()
    //    {
    //        //        if ( portParameterModel == null )
    //        //            portParameterModel = getCurrentPeerInfoModel().getPortParameterModel();
    //        //
    //        //        return portParameterModel;
    //
    //        return getCurrentPeerInfoModel().getPortParameterModel();
    //    }

    //    @Override
    //    public void addObserver( MVCModelObserver obs )
    //    {
    //        getCurrentPeerInfoModel().addObserver( obs );
    //        super.addObserver( obs );
    //    }
    //
    //    @Override
    //    public void removeObserver( MVCModelObserver obs )
    //    {
    //        getCurrentPeerInfoModel().removeObserver( obs );
    //        super.removeObserver( obs );
    //    }

    public SyncIterator<PeerInfoModel> iterator()
    {
        return peerInfoModels.iterator();
    }

    public void addObserver( MVCModelObserver obs )
    {
        peerInfoModels.addObserver( obs );
    }

    public void removeObserver( MVCModelObserver obs )
    {
        peerInfoModels.removeObserver( obs );
    }
}
