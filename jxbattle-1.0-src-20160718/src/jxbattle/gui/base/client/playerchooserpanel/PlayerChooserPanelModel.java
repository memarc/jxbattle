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

package jxbattle.gui.base.client.playerchooserpanel;

import jxbattle.model.client.ClientEngine;

import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;

public class PlayerChooserPanelModel extends MVCModelImpl
{
    private ClientEngine clientEngine;

    public PlayerChooserPanelModel( ClientEngine ce )
    {
        clientEngine = ce;
    }

    ClientEngine getClientEngine()
    {
        return clientEngine;
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        clientEngine.addObserver( obs );
        clientEngine.getGameModel().getPlayersModel().addObserver( obs );
        clientEngine.getClientSideConnectionModel().addObserver( obs );
        clientEngine.getSystemParametersModel().addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        clientEngine.removeObserver( obs );
        clientEngine.getGameModel().getPlayersModel().removeObserver( obs );
        clientEngine.getClientSideConnectionModel().removeObserver( obs );
        clientEngine.getSystemParametersModel().removeObserver( obs );
    }
}
