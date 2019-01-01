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

package jxbattle.gui.base.common.menupanel;

import jxbattle.model.MainModel;
import jxbattle.model.client.ClientEngine;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public class MenuPanelModel extends MVCModelImpl
{
    private MainModel mainModel;

    public MenuPanelModel( MainModel mm )
    {
        mainModel = mm;
        initRelatedModels();
    }

    private void initRelatedModels()
    {
        addRelatedModel( mainModel );
    }

    void openClient( Object sender )
    {
        ClientEngine ce = mainModel.createClientEngine();
        //        ce.setEngineActive( sender, true );
        ce.setEngineActive( sender );
    }

    void openServer( Object sender )
    {
        mainModel.createServerEngine( sender );
    }

    void browseWebSite( Object sender )
    {
        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.BrowseWebSite ) );
    }

    //    // MVCModel interface
    //
    //    @Override
    //    public void addObserver( MVCModelObserver obs )
    //    {
    //        mainModel.addObserver( obs );
    //    }
    //
    //    @Override
    //    public void removeObserver( MVCModelObserver obs )
    //    {
    //        mainModel.removeObserver( obs );
    //    }
}
