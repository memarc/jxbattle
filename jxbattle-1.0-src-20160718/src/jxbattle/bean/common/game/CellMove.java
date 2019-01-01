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

/**
 * move between 2 cells.
 * a cell has an array of moves to its neighbours 
 */
public class CellMove
{
    /**
     * cell the move is pointing to
     */
    public Cell targetCell;

    /**
     * direction of target cell. For square topology, 0=up, 1=right, 2=down, 3=left
     */
    public int dir;

    /**
     * reverse direction from target cell
     */
    public int revDir;

    /**
     * move vector is set or not
     */
    public boolean isSet;

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if ( isSet )
            switch ( dir )
            {
                case 0:
                    sb.append( "up" );
                    break;

                case 1:
                    sb.append( "right" );
                    break;

                case 2:
                    sb.append( "down" );
                    break;

                case 3:
                    sb.append( "left" );
                    break;

                default:
                    break;
            }
        return sb.toString();
    }

    //    /**
    //     * march command set or not
    //     */
    //    public boolean hasMarch;

}
