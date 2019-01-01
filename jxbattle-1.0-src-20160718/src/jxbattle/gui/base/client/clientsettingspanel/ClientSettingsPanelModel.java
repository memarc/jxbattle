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

package jxbattle.gui.base.client.clientsettingspanel;

import jxbattle.model.client.ClientEngine;
import jxbattle.model.client.automaton.ClientAutomatonStateId;

import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.parameter.IntParameterModel;

public class ClientSettingsPanelModel extends MVCModelImpl
{
    private ClientEngine clientEngine;

    public ClientSettingsPanelModel( ClientEngine ce )
    {
        clientEngine = ce;
    }

    boolean isReadOnly()
    {
        return clientEngine.getCurrentState() != ClientAutomatonStateId.NotConnected;
    }

    IntParameterModel getSocketTimeoutModel()
    {
        return clientEngine.getSystemParametersModel().getSocketTimeoutModel();
    }

    IntParameterModel getNetworkBiasModel()
    {
        return clientEngine.getSystemParametersModel().getNetworkBiasModel();
    }

    boolean getLogGameReplay()
    {
        return clientEngine.getSystemParametersModel().getLogGameReplayModel().getValue();
    }

    void setLogGameReplay( Object sender, boolean s )
    {
        clientEngine.getSystemParametersModel().getLogGameReplayModel().setValue( sender, s );
    }
}
