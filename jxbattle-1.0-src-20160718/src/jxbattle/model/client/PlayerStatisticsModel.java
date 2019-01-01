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

package jxbattle.model.client;

import jxbattle.bean.common.stats.PlayerStatistics;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public class PlayerStatisticsModel extends MVCModelImpl
{
    // observed beans

    private PlayerStatistics playerStatistics;

    // models

    PlayerStatisticsModel( PlayerStatistics ps )
    {
        setPlayerStatistics( ps );
    }

    PlayerStatistics getPlayerStatistics()
    {
        return playerStatistics;
    }

    private void setPlayerStatistics( PlayerStatistics ps )
    {
        playerStatistics = ps;
    }

    public int getPlayerId()
    {
        return getPlayerStatistics().getPlayerId();
    }

    public double getCoveragePercent()
    {
        return getPlayerStatistics().getCoveragePercent();
    }

    void setCoveragePercent( Object sender, double cp )
    {
        boolean changed = getPlayerStatistics().getCoveragePercent() != cp;
        if ( changed )
        {
            getPlayerStatistics().setCoveragePercent( cp );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.CoveragePercentChanged ) );
        }
    }
}
