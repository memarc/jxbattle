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

package jxbattle.gui.base.server.gameplayerspanel;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;

import jxbattle.model.server.ClientInfoModel;
import net.miginfocom.swing.MigLayout;

public class GamePlayersPanelView extends JPanel
{
    private int listMinYSize = 120;

    private JSpinner spinPlayerCount;

    private JScrollPane scrpPlayers;

    private JList<ClientInfoModel> listPlayers;

    public GamePlayersPanelView()
    {
        setBorder( new TitledBorder( null, "Players", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
        setLayout( new MigLayout( "", "[grow]", "[][grow]" ) );
        add( getSpinPlayerCount(), "cell 0 0,alignx left,aligny top" );
        add( getScrpPlayers(), "cell 0 1,grow" );
    }

    public JSpinner getSpinPlayerCount()
    {
        if ( spinPlayerCount == null )
        {
            spinPlayerCount = new JSpinner();
            spinPlayerCount.setMinimumSize( new Dimension( 100, 20 ) );
        }
        return spinPlayerCount;
    }

    public JScrollPane getScrpPlayers()
    {
        if ( scrpPlayers == null )
        {
            scrpPlayers = new JScrollPane();
            scrpPlayers.setMinimumSize( new Dimension( 10, listMinYSize ) );
            scrpPlayers.setViewportView( getListPlayers() );
        }
        return scrpPlayers;
    }

    public JList<ClientInfoModel> getListPlayers()
    {
        if ( listPlayers == null )
        {
            listPlayers = new JList<>();
            listPlayers.setBackground( Color.LIGHT_GRAY );
            listPlayers.setBorder( null );
        }
        return listPlayers;
    }
}
