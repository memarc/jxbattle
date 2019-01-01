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

package jxbattle.model.server;

import jxbattle.bean.common.player.ColorId;
import jxbattle.bean.server.GameParametersProfile;
import jxbattle.common.Consts;
import jxbattle.common.net.XBPeer;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.game.GameModel;
import jxbattle.model.common.parameters.SystemParametersModel;
import jxbattle.model.common.parameters.game.GameParametersModel;

import org.generic.bean.parameter.IntParameter;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.list.SyncListModel;
import org.generic.mvc.model.logmessage.LogMessageModel;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.mvc.model.parameter.IntParameterModel;

public class ServerModel extends MVCModelImpl
{
    private XBServerNetEngine serverNetEngine;

    private IntParameterModel playerCountModel;

    /**
     * clients 
     */
    private SyncListModel<ClientInfoModel> clientInfosModel;

    private GameModel gameModel;

    /**
     * game profiles
     */
    private GameParametersProfilesModel gameParametersProfilesModel;

    private SystemParametersModel systemParametersModel;

    public static final LogMessageModel logMessageModel = new LogMessageModel();

    ServerModel( SystemParametersModel spm, GameParametersProfilesModel gppm )
    {
        clientInfosModel = new SyncListModel<>();
        gameModel = new GameModel();
        gameParametersProfilesModel = gppm;
        systemParametersModel = spm;
        serverNetEngine = new XBServerNetEngine( this );
        serverNetEngine.setIdentity( "XBserver" );
    }

    ClientInfoModel getClientInfoModelFromPeer( XBPeer clientPeer )
    {
        for ( ClientInfoModel cim : clientInfosModel )
            if ( cim.getClientPeer() == clientPeer )
                return cim;

        throw new MVCModelError( "no client info matching peer " + clientPeer.toString() ); // TODO arrive parfois en cours de config
    }

    public SyncListModel<ClientInfoModel> getClientInfosModel()
    {
        return clientInfosModel;
    }

    PlayerInfoModel isPlayerAvailable( ColorId colorId )
    {
        for ( ClientInfoModel cim : clientInfosModel )
            if ( cim.isClientPlayerChosen() )
                if ( cim.getPlayerColor().getColorId().value == colorId.value )
                    return null;

        return gameModel.getPlayersModel().getPlayerInfoModel( colorId );
    }

    public XBServerNetEngine getServerSideConnectionModel()
    {
        return serverNetEngine;
    }

    public IntParameterModel getPlayerCountModel()
    {
        if ( playerCountModel == null )
            playerCountModel = new IntParameterModel( new IntParameter( 1, Consts.playerColors.length ) );

        return playerCountModel;
    }

    public GameModel getGameModel()
    {
        return gameModel;
    }

    public GameParametersProfilesModel getGameParametersProfilesModel()
    {
        return gameParametersProfilesModel;
    }

    void addGameProfile( Object sender, GameParametersProfile profile )
    {
        gameParametersProfilesModel.add( sender, new GameParametersProfileModel( profile ) );
    }

    /**
     * get GameParametersModel from current profile
     */
    private GameParametersModel getCurrentGameParametersModel()
    {
        //        return gameParametersProfilesModel.getCurrentProfileModel().getGameParametersModel();
        return gameParametersProfilesModel.getCurrent().getGameParametersModel();
    }

    public GameParametersProfileModel getCurrentGameParametersProfileModel()
    {
        //        return gameParametersProfilesModel.getCurrentProfileModel();
        return gameParametersProfilesModel.getCurrent();
    }

    void setGameParametersFromCurrentProfile()
    {
        getGameModel().setGameParameters( this, getCurrentGameParametersModel().getGameParameters() );
    }

    public SystemParametersModel getSystemParametersModel()
    {
        return systemParametersModel;
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        serverNetEngine.addObserver( obs );
        clientInfosModel.addObserver( obs );
        gameModel.addObserver( obs );
        gameParametersProfilesModel.addObserver( obs );
        systemParametersModel.addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        serverNetEngine.removeObserver( obs );
        clientInfosModel.removeObserver( obs );
        gameModel.removeObserver( obs );
        gameParametersProfilesModel.removeObserver( obs );
        systemParametersModel.removeObserver( obs );
    }
}
