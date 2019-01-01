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

package jxbattle.bean.common.stats;

/**
 * stats for one player
 */
public class PlayerStatistics
{
    /**
     * player identification
     */
    private int playerId;

    /**
     * % of cells occupied by player
     */
    private double coveragePercent;

    public PlayerStatistics( int pid )
    {
        playerId = pid;
    }

    public double getCoveragePercent()
    {
        return coveragePercent;
    }

    public void setCoveragePercent( double percent )
    {
        coveragePercent = percent;
    }

    public int getPlayerId()
    {
        return playerId;
    }

    public void setPlayerId( int id )
    {
        playerId = id;
    }

    private static boolean equals( double d1, double d2 )
    {
        return Math.abs( d1 - d2 ) < 1e-6;
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
        PlayerStatistics other = (PlayerStatistics)obj;
        if ( !equals( coveragePercent, other.coveragePercent ) )
            return false;
        if ( playerId != other.playerId )
            return false;
        return true;
    }
}
