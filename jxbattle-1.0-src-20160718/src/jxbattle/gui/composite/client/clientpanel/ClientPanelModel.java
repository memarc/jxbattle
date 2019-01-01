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

package jxbattle.gui.composite.client.clientpanel;

import jxbattle.model.client.ClientEngine;

import org.generic.gui.logpanel.LogPanelModel;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;

public class ClientPanelModel extends MVCModelImpl
{
    private ClientEngine clientEngine;

    private LogPanelModel logPanelModel;

    public ClientPanelModel( ClientEngine ce )
    {
        clientEngine = ce;
        logPanelModel = new LogPanelModel( ClientEngine.logMessageModel, false );
    }

    ClientEngine getClientEngine()
    {
        return clientEngine;
    }

    LogPanelModel getLogPanelModel()
    {
        return logPanelModel;
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        clientEngine.addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        clientEngine.removeObserver( obs );
    }
}
