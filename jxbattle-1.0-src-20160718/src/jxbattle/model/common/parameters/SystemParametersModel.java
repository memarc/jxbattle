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

import jxbattle.bean.common.parameters.SystemParameters;
import jxbattle.model.common.PeerInfoModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.parameter.BoolParameterModel;
import org.generic.mvc.model.parameter.IntParameterModel;
import org.generic.string.StringUtils;

public class SystemParametersModel extends MVCModelImpl
{
    // observed beans

    private SystemParameters systemParameters;

    // models

    private IntParameterModel socketTimeoutModel;

    private IntParameterModel networkBiasModel;

    private BoolParameterModel logGameReplayModel;

    private ServerHistoryModel serverHistoryModel;

    private IntParameterModel serverListenPortModel;

    private IntParameterModel gameTickIntervalModel;

    private IntParameterModel flushFrequencyModel;

    private BoolParameterModel checkClientsStateModel;

    private PeerInfoModel serverSocketModel;

    public SystemParametersModel( SystemParameters sp )
    {
        setSystemParameters( sp );
    }

    public SystemParameters getSystemParameters()
    {
        return systemParameters;
    }

    public void setSystemParameters( SystemParameters sp )
    {
        systemParameters = sp;
    }

    public String getClientName()
    {
        return systemParameters.getClientName();
    }

    public void setClientName( Object sender, String name )
    {
        boolean changed = !StringUtils.equalsNotNull( getClientName(), name );
        if ( changed )
        {
            getSystemParameters().setClientName( name );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.ClientNameChanged, name ) );
        }
    }

    public IntParameterModel getSocketTimeoutModel()
    {
        if ( socketTimeoutModel == null )
            socketTimeoutModel = new IntParameterModel( getSystemParameters().getSocketTimeout() );
        return socketTimeoutModel;
    }

    public IntParameterModel getNetworkBiasModel()
    {
        if ( networkBiasModel == null )
            networkBiasModel = new IntParameterModel( getSystemParameters().getNetworkBias() );
        return networkBiasModel;
    }

    public BoolParameterModel getLogGameReplayModel()
    {
        if ( logGameReplayModel == null )
            logGameReplayModel = new BoolParameterModel( getSystemParameters().getLogGameReplay() );
        return logGameReplayModel;
    }

    public ServerHistoryModel getServerHistoryModel()
    {
        if ( serverHistoryModel == null )
            //            serverHistoryModel = new ServerHistoryModel( getSystemParameters().getServerHistory() );
            serverHistoryModel = new ServerHistoryModel();
        return serverHistoryModel;
    }

    public IntParameterModel getServerListenPortModel()
    {
        if ( serverListenPortModel == null )
            serverListenPortModel = new IntParameterModel( getSystemParameters().getServerListenPort() );
        return serverListenPortModel;
    }

    public IntParameterModel getGameTickIntervalModel()
    {
        if ( gameTickIntervalModel == null )
            gameTickIntervalModel = new IntParameterModel( getSystemParameters().getGameTickInterval() );
        return gameTickIntervalModel;
    }

    public IntParameterModel getFlushFrequencyModel()
    {
        if ( flushFrequencyModel == null )
            flushFrequencyModel = new IntParameterModel( getSystemParameters().getFlushFrequency() );
        return flushFrequencyModel;
    }

    public BoolParameterModel getCheckClientsStateModel()
    {
        if ( checkClientsStateModel == null )
            checkClientsStateModel = new BoolParameterModel( getSystemParameters().getCheckClientsState() );
        return checkClientsStateModel;
    }

    public PeerInfoModel getServerSocketModel()
    {
        if ( serverSocketModel == null )
            serverSocketModel = new PeerInfoModel( getSystemParameters().getServerPeerInfo() );
        return serverSocketModel;
    }
}
