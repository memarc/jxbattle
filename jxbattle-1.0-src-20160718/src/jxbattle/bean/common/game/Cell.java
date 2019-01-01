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
import java.util.Iterator;
import java.util.List;

import jxbattle.bean.common.game.UserCommand.UserCommandCode;

/**
 * board cell information
 */
public class Cell
{
    /**
     * cell current state
     */
    public CellState cellState;

    /**
     * cell X coordinate (in grid system ie. nth horizontal cell, not pixel position)
     */
    public int gridX;

    /**
     * cell Y coordinate (in grid system ie. nth vertical cell, not pixel position)
     */
    public int gridY;

    /**
     * cell pixel X coordinate relative to board panel origin (top left = 0, 0) 
     */
    //public int pixelX;

    /**
     * cell pixel Y coordinate relative to board panel origin (top left = 0, 0)
     */
    //public int pixelY;

    /**
     * pending user commands (parachute, managed commands, ...)
     */
    public List<UserCommand> commands;

    /**
     * managed commands 
     */

    //public UserCommandCode managedCommand;

    Cell( int maxNeighbours )
    {
        cellState = new CellState( maxNeighbours );
        commands = new ArrayList<>();
    }

    public UserCommand hasCommand( UserCommandCode code, int playerId )
    {
        for ( UserCommand uc : commands )
            if ( uc.code == code && uc.playerId == playerId )
                return uc;

        return null;
    }

    public UserCommandCode hasManagedCommand()
    {
        for ( UserCommand uc : commands )
            switch ( uc.code )
            {
                case MANAGED_BUILD_BASE:
                case MANAGED_SCUTTLE_BASE:
                case MANAGED_DIG_GROUND:
                case MANAGED_FILL_GROUND:
                case MANAGED_TROOPS_PARA:
                case MANAGED_TROOPS_GUN:
                    return uc.code;

                default:
                    break;
            }

        return null;
    }

    public UserCommand hasMarchCommand()
    {
        for ( UserCommand uc : commands )
            switch ( uc.code )
            {
                case SET_MARCH:
                case SWAP_MARCH:
                case STOP_MARCH:
                    return uc;

                default:
                    break;
            }

        return null;
    }

    public void cancelMarch( int playerId, boolean all )
    {
        Iterator<UserCommand> it = commands.iterator();
        while ( it.hasNext() )
        {
            UserCommand uc = it.next();
            if ( uc.playerId == playerId )
                switch ( uc.code )
                {
                    case SET_MARCH:
                    case SWAP_MARCH:
                        it.remove();
                        break;

                    case STOP_MARCH:
                        if ( all )
                            it.remove();
                        break;

                    default:
                        break;
                }
        }
    }

    public void cancelCommand( UserCommandCode code, int playerId )
    {
        Iterator<UserCommand> it = commands.iterator();
        while ( it.hasNext() )
        {
            UserCommand uc = it.next();
            if ( uc.code == code && uc.playerId == playerId )
            {
                it.remove();
                break;
            }
        }
    }

    // Object interface

    @Override
    public String toString()
    {
        return gridX + "," + gridY + " " + cellState.toString();
    }
}
