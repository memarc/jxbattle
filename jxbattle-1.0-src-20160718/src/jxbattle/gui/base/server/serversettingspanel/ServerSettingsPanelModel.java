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

package jxbattle.gui.base.server.serversettingspanel;

import jxbattle.model.MainModel;
import jxbattle.model.common.parameters.SystemParametersModel;
import jxbattle.model.server.ServerEngine;

import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;

public class ServerSettingsPanelModel extends MVCModelImpl
{
    private SystemParametersModel systemParametersModel;

    private ServerEngine serverEngine;

    public ServerSettingsPanelModel( SystemParametersModel spm, ServerEngine se )
    {
        systemParametersModel = spm;
        serverEngine = se;
    }

    public SystemParametersModel getSystemParametersModel()
    {
        return systemParametersModel;
    }

    //    public boolean isSettingUp()
    //    {
    //        return serverEngine.isSettingUp();
    //    }

    boolean isReadOnly()
    {
        return !serverEngine.isSettingUp();
    }

    void saveConfig()
    {
        //serverEngine.saveConfig();
        MainModel.getInstance().saveServerConfig();
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        serverEngine.addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        serverEngine.removeObserver( obs );
    }
}
