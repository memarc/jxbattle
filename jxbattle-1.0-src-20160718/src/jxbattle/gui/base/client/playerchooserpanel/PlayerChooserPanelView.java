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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import jxbattle.model.common.PlayerInfoModel;
import net.miginfocom.swing.MigLayout;

public class PlayerChooserPanelView extends JPanel
{
    private JButton btnChoosePlayer;

    private JButton btnCancel;

    private JScrollPane scrpPlayers;

    private JList<PlayerInfoModel> listPlayers;

    private JPanel pnlColor;

    private JLabel lblName;

    private JTextField tfPlayerName;

    private JLabel lblPleaseEnterA;

    public PlayerChooserPanelView()
    {
        setBorder( new TitledBorder( new LineBorder( new Color( 184, 207, 229 ) ), "Player", TitledBorder.LEADING, TitledBorder.TOP, null, new Color( 51, 51, 51 ) ) );
        setLayout( new MigLayout( "", "[][grow][]", "[][][][][]" ) );
        add( getBtnChoosePlayer(), "cell 0 0 2 1,growx" );
        add( getScrpPlayers(), "cell 2 0 1 3,alignx center,aligny center" );
        add( getBtnCancel(), "cell 0 1 2 1,growx" );
        add( getPnlColor(), "cell 0 2 2 1,grow" );
        add( getLblName(), "cell 0 3,alignx left" );
        add( getTfPlayerName(), "cell 1 3 2 1,growx" );
        add( getLblPleaseEnterAName(), "cell 1 4 2 1" );
    }

    JButton getBtnChoosePlayer()
    {
        if ( btnChoosePlayer == null )
        {
            btnChoosePlayer = new JButton( "Choose player" );
        }
        return btnChoosePlayer;
    }

    JButton getBtnCancel()
    {
        if ( btnCancel == null )
        {
            btnCancel = new JButton( "Cancel" );
        }
        return btnCancel;
    }

    private JScrollPane getScrpPlayers()
    {
        if ( scrpPlayers == null )
        {
            scrpPlayers = new JScrollPane();
            scrpPlayers.setPreferredSize( new Dimension( 200, 120 ) );
            scrpPlayers.setMinimumSize( new Dimension( 10, 120 ) );
            scrpPlayers.setViewportView( getListPlayers() );
        }
        return scrpPlayers;
    }

    JList<PlayerInfoModel> getListPlayers()
    {
        if ( listPlayers == null )
        {
            listPlayers = new JList<>();
            listPlayers.setBackground( Color.LIGHT_GRAY );
        }
        return listPlayers;
    }

    JPanel getPnlColor()
    {
        if ( pnlColor == null )
        {
            pnlColor = new JPanel();
            pnlColor.setMinimumSize( new Dimension( 80, 10 ) );
        }
        return pnlColor;
    }

    private JLabel getLblName()
    {
        if ( lblName == null )
        {
            lblName = new JLabel( "Name" );
        }
        return lblName;
    }

    JTextField getTfPlayerName()
    {
        if ( tfPlayerName == null )
        {
            tfPlayerName = new JTextField();
            tfPlayerName.setColumns( 10 );
        }
        return tfPlayerName;
    }

    JLabel getLblPleaseEnterAName()
    {
        if ( lblPleaseEnterA == null )
        {
            lblPleaseEnterA = new JLabel( "Please enter a name" );
            lblPleaseEnterA.setForeground( Color.RED );
        }
        return lblPleaseEnterA;
    }
}
