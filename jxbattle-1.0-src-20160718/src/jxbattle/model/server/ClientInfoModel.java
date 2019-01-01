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

import jxbattle.bean.common.game.GameState;
import jxbattle.bean.common.player.PlayerState;
import jxbattle.bean.common.player.XBColor;
import jxbattle.bean.common.stats.PlayerStatistics;
import jxbattle.bean.server.ClientInfo;
import jxbattle.common.net.XBPeer;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public class ClientInfoModel extends MVCModelImpl
{
    // observed beans

    private ClientInfo clientInfo;

    //private PlayerInfo playerInfo; // player played by this client

    /**
     * used to check all clients board are in the same state during game
     */
    private GameState gameState;

    // models

    private PlayerInfoModel playerInfoModel; // player played by this client

    ClientInfoModel( ClientInfo ci )
    {
        setClientInfo( ci );
    }

    private ClientInfo getClientInfo()
    {
        return clientInfo;
    }

    private void setClientInfo( ClientInfo ci )
    {
        clientInfo = ci;
    }

    public XBPeer getClientPeer()
    {
        return getClientInfo().getClientPeer();
    }

    void unsetClientPeer()
    {
        getClientInfo().setClientPeer( null );
    }

    //    public ClientPeerStateId getClientPeerStateId()
    //    {
    //        return clientInfo.getClientPeerState();
    //    }
    //    void setClientPeerStateId( Object sender, ClientPeerStateId state )
    //    {
    //        boolean changed = clientInfo.getClientPeerState() != state;
    //        if ( changed )
    //        {
    //            clientInfo.setClientPeerState( state );
    //            notifyObservers( new MVCModelChange( sender, MVCModelChangeId.ClientStateChanged, clientInfo ) );
    //        }
    //    }

    public PlayerState getPlayerState()
    {
        return playerInfoModel.getPlayerState();
    }

    void setPlayerState( Object sender, PlayerState ps )
    {
        playerInfoModel.setPlayerState( sender, ps );
    }

    public XBColor getPlayerColor()
    {
        return playerInfoModel.getPlayerColor();
    }

    public String getClientName()
    {
        return getClientInfo().getClientName();
    }

    void setClientName( Object sender, String name )
    {
        getClientInfo().setClientName( name );
        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.ClientNameChanged, getClientInfo() ) );
    }

    //    public boolean isClientNameOk()
    //    {
    //        String name = clientInfo.getClientName();
    //        return name != null && name.length() > 0;
    //    }

    public boolean isConnected()
    {
        XBPeer cp = getClientInfo().getClientPeer();
        if ( cp == null )
            return false;

        return cp.isConnected();
    }

    //    public int getPlayerId()
    //    {
    //        if ( playerInfoModel == null )
    //            return -1;
    //
    //        return playerInfoModel.getPlayerId();
    //    }

    public void setPlayerId( Object sender, int pid )
    {
        playerInfoModel.setPlayerId( sender, pid );
    }

    public void setPlayer( Object sender, PlayerInfoModel pim )
    {
        if ( playerInfoModel != null )
            playerInfoModel.removeObservers( getObservers() );

        if ( pim == null )
        {
            if ( playerInfoModel != null )
            {
                playerInfoModel.resetPlayer( sender );
                playerInfoModel = null;
            }
        }
        else
        {
            playerInfoModel = pim;
            playerInfoModel.setPlayerName( sender, getClientInfo().getClientName() );
            playerInfoModel.addObservers( getObservers() );
        }
    }

    //    public PlayerInfoModel getPlayer()
    //    {
    //        return playerInfoModel;
    //    }

    //    void resetPlayer( Object sender )
    //    {
    //        if ( playerInfoModel != null )
    //        {
    //            playerInfoModel.setPlayerStateId( sender, PlayerStateId.initialValue() );
    //            playerInfoModel.setPlayerName( sender, null );
    //        }
    //    }

    public boolean isClientPlayerChosen()
    {
        //        if ( playerInfoModel == null )
        //            return false;
        //    
        //        return playerInfoModel.isPlayerSelected();
        return playerInfoModel != null;
    }

    public GameState getGameState()
    {
        return gameState;
    }

    void setGameState( Object sender, GameState gs )
    {
        gameState = gs;
        if ( gameState != null )
            setPlayerState( sender, gameState.getCurrentPlayerState() );
    }

    PlayerStatistics getPlayerStatistics( int playerId )
    {
        for ( PlayerStatistics ps : gameState.getPlayerStatistics() )
        {
            if ( ps.getPlayerId() == playerId )
                return ps;
        }

        throw new MVCModelError( "no player with id " + playerId );
    }

    @Override
    public String toString()
    {
        if ( playerInfoModel != null && playerInfoModel.isPlayerSelected() )
            return playerInfoModel.toString();
        return "no player selected";
    }
}
