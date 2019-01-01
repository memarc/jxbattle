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

package jxbattle.gui.composite.mainframe;

import jxbattle.gui.composite.mainpanel.MainPanelModel;
import jxbattle.model.MainModel;

import org.generic.mvc.model.observer.MVCModelImpl;

public class MainFrameModel extends MVCModelImpl
{
    private MainPanelModel mainPanelModel;

    private MainModel mainModel;

    public MainFrameModel( MainModel mm )
    {
        mainModel = mm;
        mainPanelModel = new MainPanelModel( mm );
    }

    MainPanelModel getMainPanelModel()
    {
        return mainPanelModel;
    }

    void shutdown()
    {
        mainModel.shutdown();
    }
}
