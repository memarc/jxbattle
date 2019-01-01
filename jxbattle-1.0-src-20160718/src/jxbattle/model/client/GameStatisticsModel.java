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
import jxbattle.common.Consts;

import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.list.SyncListModel;
import org.generic.mvc.model.observer.MVCModelImpl;

public class GameStatisticsModel extends MVCModelImpl
{
    // observed beans

    // models

    private SyncListModel<PlayerStatisticsModel> playerStatistics;

    public GameStatisticsModel()
    {
        playerStatistics = new SyncListModel<>();
    }

    void initialiseStatistics( Object sender )
    {
        clear();

        //for ( int playerId = 0; playerId < playerCount; playerId++ )
        for ( int playerId = 0; playerId < Consts.playerColors.length; playerId++ )
            playerStatistics.add( sender, new PlayerStatisticsModel( new PlayerStatistics( playerId ) ) );
    }

    private void clear()
    {
        playerStatistics.clear( this );
    }

    public PlayerStatisticsModel getPlayerStatisticsModel( int playerId )
    {
        //        SyncIterator<PlayerStatisticsModel> it = playerStatistics.iterator();
        //        try
        //        {
        //            while ( it.hasNext() )
        //            {
        //                PlayerStatisticsModel ps = it.next();
        //                if ( ps.getPlayerId() == playerId )
        //                    return ps;
        //            }
        //
        //            throw new MVCModelError( "no player with id " + playerId );
        //        }
        //        finally
        //        {
        //            it.close();
        //        }

        for ( PlayerStatisticsModel psm : playerStatistics )
            if ( psm.getPlayerId() == playerId )
                return psm;

        throw new MVCModelError( "no player with id " + playerId );
    }
}
