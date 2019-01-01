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

package jxbattle.gui.base.client.clientstatuspanel;

import jxbattle.model.client.PlayerStatisticsModel;
import jxbattle.model.common.PlayerInfoModel;

import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;

public class ClientStatusPanelModel extends MVCModelImpl
{
    private PlayerInfoModel playerInfoModel;

    private PlayerStatisticsModel playerStatisticsModel;

    private boolean isClientPlayer; // true if this status panel is the client panel one

    public ClientStatusPanelModel( PlayerInfoModel pim, PlayerStatisticsModel psm, boolean isClient )
    {
        playerInfoModel = pim;
        playerStatisticsModel = psm;
        isClientPlayer = isClient;
    }

    public PlayerInfoModel getPlayerInfoModel()
    {
        return playerInfoModel;
    }

    public PlayerStatisticsModel getPlayerStatisticsModel()
    {
        return playerStatisticsModel;
    }

    public boolean isClientPlayer()
    {
        return isClientPlayer;
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        playerInfoModel.addObserver( obs );
        playerStatisticsModel.addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        playerInfoModel.removeObserver( obs );
        playerStatisticsModel.removeObserver( obs );
    }
}
