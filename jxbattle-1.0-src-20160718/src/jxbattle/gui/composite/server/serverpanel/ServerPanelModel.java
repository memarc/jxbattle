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

package jxbattle.gui.composite.server.serverpanel;

import jxbattle.model.server.GameParametersProfilesModel;
import jxbattle.model.server.ServerEngine;
import jxbattle.model.server.ServerModel;

import org.generic.gui.logpanel.LogPanelModel;
import org.generic.mvc.model.observer.MVCModelImpl;

public class ServerPanelModel extends MVCModelImpl
{
    private ServerEngine serverEngine;

    private LogPanelModel logPanelModel;

    public ServerPanelModel( ServerEngine se )
    {
        serverEngine = se;
        logPanelModel = new LogPanelModel( ServerModel.logMessageModel, false );

        addRelatedModel( serverEngine );
    }

    LogPanelModel getLogPanelModel()
    {
        return logPanelModel;
    }

    ServerModel getServerModel()
    {
        return serverEngine.getServerModel();
    }

    ServerEngine getServeEngine()
    {
        return serverEngine;
    }

    GameParametersProfilesModel getGameParametersProfilesModel()
    {
        return serverEngine.getGameParametersProfilesModel();
    }
}
