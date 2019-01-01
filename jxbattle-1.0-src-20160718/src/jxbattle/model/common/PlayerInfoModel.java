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

package jxbattle.model.common;

import jxbattle.bean.common.player.ColorId;
import jxbattle.bean.common.player.PlayerInfo;
import jxbattle.bean.common.player.PlayerState;
import jxbattle.bean.common.player.XBColor;
import jxbattle.common.Consts;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.string.StringUtils;

public class PlayerInfoModel extends MVCModelImpl
{
    // observed beans

    private PlayerInfo playerInfo; // player color/state

    // models

    public PlayerInfoModel( PlayerInfo pi )
    {
        setPlayerInfo( pi );
    }

    public PlayerInfo getPlayerInfo()
    {
        return playerInfo;
    }

    private void setPlayerInfo( PlayerInfo pi )
    {
        playerInfo = pi;
    }

    public String getPlayerName()
    {
        return getPlayerInfo().getPlayerName();
    }

    public void setPlayerName( Object sender, String name )
    {
        boolean changed = !StringUtils.equalsNotNull( getPlayerInfo().getPlayerName(), name );
        if ( changed )
        {
            getPlayerInfo().setPlayerName( name );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.PlayerNameChanged, getPlayerInfo() ) );
        }
    }

    public void setPlayerId( Object sender, int id )
    {
        boolean changed = getPlayerInfo().getPlayerId() != id;
        if ( changed )
        {
            getPlayerInfo().setPlayerId( id );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.PlayerIdChanged, getPlayerInfo() ) );
        }
    }

    public XBColor getPlayerColor()
    {
        return getPlayerInfo().getPlayerColor();
    }

    public int getPlayerId()
    {
        return getPlayerInfo().getPlayerId();
    }

    public ColorId getColorId()
    {
        return getPlayerInfo().getColorId();
    }

    //    public void setPlayerId( Object sender, int id )
    //    {
    //        boolean changed = playerInfo.getPlayerId() != id;
    //        if ( changed )
    //        {
    //            playerInfo.setPlayerId( id );
    //            notifyObservers( new MVCModelChange( sender, MVCModelChangeId.PlayerIdChanged, playerInfo ) );
    //        }
    //    }

    public PlayerState getPlayerState()
    {
        return getPlayerInfo().getPlayerState();
    }

    public void setPlayerState( Object sender, PlayerState st )
    {
        boolean changed = getPlayerInfo().getPlayerState() != st;
        if ( changed )
        {
            getPlayerInfo().setPlayerState( st );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.PlayerStateChanged, getPlayerInfo() ) );
        }
    }

    public boolean isPlayerSelected()
    {
        return getPlayerInfo().getPlayerName() != null;
        //return playerInfo.getPlayerId() != Consts.NullId;
    }

    public void resetPlayer( Object sender )
    {
        setPlayerState( sender, PlayerState.initialValue() );
        setPlayerName( sender, null );
        setPlayerId( sender, Consts.NullId );
    }

    @Override
    public String toString() // TODO a virer
    {
        return playerInfo.toString();
    }
}
