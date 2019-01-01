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

package jxbattle.bean.common.game;

import java.util.ArrayList;
import java.util.List;

import jxbattle.bean.common.player.PlayerState;
import jxbattle.bean.common.stats.PlayerStatistics;

/**
 * current game state (board state, players stats)
 */
public class GameState
{
    private PlayerState currentPlayerState;

    private BoardState boardState;

    private List<PlayerStatistics> playerStatistics;

    public GameState()
    {
        playerStatistics = new ArrayList<PlayerStatistics>();
    }

    public BoardState getBoardState()
    {
        return boardState;
    }

    public void setBoardState( BoardState bs )
    {
        boardState = bs;
    }

    public List<PlayerStatistics> getPlayerStatistics()
    {
        return playerStatistics;
    }

    public PlayerState getCurrentPlayerState()
    {
        return currentPlayerState;
    }

    public void setCurrentPlayerState( PlayerState ps )
    {
        currentPlayerState = ps;
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
        GameState other = (GameState)obj;
        if ( boardState == null )
        {
            if ( other.boardState != null )
                return false;
        }
        else if ( !boardState.equals( other.boardState ) )
            return false;
        if ( playerStatistics == null )
        {
            if ( other.playerStatistics != null )
                return false;
        }
        else if ( !playerStatistics.equals( other.playerStatistics ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for ( PlayerStatistics ps : playerStatistics )
        {
            sb.append( " id(" );
            sb.append( ps.getPlayerId() );
            sb.append( ")=" );
            sb.append( ps.getCoveragePercent() );
        }

        return sb.toString();

    }
}
