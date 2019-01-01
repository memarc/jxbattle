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

package jxbattle.bean.common.player;

import jxbattle.common.Consts;
import jxbattle.model.MainModel;

/**
 * information about a player
 */
public class PlayerInfo
{
    //private static int idGen = 0;

    /**
     * player color
     */
    private XBColor playerColor;

    /**
     * player id
     */
    private int playerId = Consts.NullId;

    /**
     * player name
     */
    private String playerName;

    /**
     * current activity
     */
    private PlayerState playerState;

    //public PlayerInfo( XBColor col, int id )
    public PlayerInfo( XBColor col )
    {
        playerColor = col;
        playerState = PlayerState.initialValue();
    }

    public ColorId getColorId()
    {
        return playerColor.getColorId();
    }

    public int getPlayerId()
    {
        return playerId;
    }

    public void setPlayerId( int pid )
    {
        playerId = pid;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setPlayerName( String name )
    {
        playerName = name;
    }

    public XBColor getPlayerColor()
    {
        return playerColor;
    }

    public PlayerState getPlayerState()
    {
        return playerState;
    }

    public void setPlayerState( PlayerState ps )
    {
        playerState = ps;
    }

    @Override
    public String toString()
    {
        if ( MainModel.debug )
            return "player " + playerId + " " + playerState + " " + playerColor + " " + playerName;
        return "player " + playerColor;
    }
}