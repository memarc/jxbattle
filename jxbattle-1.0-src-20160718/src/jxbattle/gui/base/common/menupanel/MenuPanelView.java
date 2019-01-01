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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jxbattle.common.Consts;
import net.miginfocom.swing.MigLayout;

public class MenuPanelView extends JPanel
{
    private JButton btnPlayGame;

    private JButton btnCreateServer;

    private JButton btnWebSite;

    private JLabel lblJxbattle;

    private JLabel label;

    private JButton btnReplayGame;

    public MenuPanelView()
    {
        setLayout( new MigLayout( "", "[131px,grow]", "[][25px][25px][][][grow][]" ) );
        add( getLabel(), "cell 0 0" );
        add( getBtnPlayGame(), "cell 0 1,alignx center,aligny center" );
        add( getBtnCreateServer(), "cell 0 2,alignx center,aligny center" );
        add( getBtnReplayGame(), "cell 0 3,alignx center" );
        add( getBtnWebSite(), "cell 0 4,alignx center" );
        add( getLblJxbattle(), "cell 0 6,alignx right" );
    }

    JButton getBtnPlayGame()
    {
        if ( btnPlayGame == null )
        {
            btnPlayGame = new JButton( "Play game" );
        }
        return btnPlayGame;
    }

    JButton getBtnCreateServer()
    {
        if ( btnCreateServer == null )
        {
            btnCreateServer = new JButton( "Create server" );
        }
        return btnCreateServer;
    }

    JButton getBtnWebSite()
    {
        if ( btnWebSite == null )
        {
            btnWebSite = new JButton( "Web site/online doc" );
        }
        return btnWebSite;
    }

    private JLabel getLblJxbattle()
    {
        if ( lblJxbattle == null )
        {
            lblJxbattle = new JLabel( "jXBattle " + Consts.applicationVersion );
        }
        return lblJxbattle;
    }

    private JLabel getLabel()
    {
        if ( label == null )
        {
            label = new JLabel( " " );
        }
        return label;
    }

    JButton getBtnReplayGame()
    {
        if ( btnReplayGame == null )
        {
            btnReplayGame = new JButton( "Replay game" );
        }
        return btnReplayGame;
    }
}
