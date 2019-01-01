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

package jxbattle.gui.composite.mainpanel;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import jxbattle.gui.base.common.menupanel.MenuPanelView;

public class MainPanelView extends JPanel
{
    private JTabbedPane tabbedPane;

    private MenuPanelView menuPanelView;

    public MainPanelView()
    {
        setLayout( new BorderLayout( 0, 0 ) );
        add( getTabbedPane() );
    }

    public JTabbedPane getTabbedPane()
    {
        if ( tabbedPane == null )
        {
            tabbedPane = new JTabbedPane( SwingConstants.TOP );
            tabbedPane.addTab( "Menu", null, getMenuPanelView(), null );
        }
        return tabbedPane;
    }

    public MenuPanelView getMenuPanelView()
    {
        if ( menuPanelView == null )
        {
            menuPanelView = new MenuPanelView();
        }
        return menuPanelView;
    }
}
