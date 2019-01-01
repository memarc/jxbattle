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

package jxbattle.gui.base.client.clientstatuspanel;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

class ClientStatusPanelView extends JPanel
{
    private JLabel lblPlayerName;

    private JLabel lblPlayerStatus;

    private JLabel lblOccupyPercent;

    public ClientStatusPanelView()
    {
        setBackground( Color.LIGHT_GRAY );
        setBorder( new LineBorder( Color.GRAY, 2, true ) );
        setLayout( new MigLayout( "", "[]0[]", "[]0[]" ) );
        add( getLblPlayerName(), "cell 0 0 2 1,alignx center" );
        add( getLblPlayerStatus(), "cell 0 1,growx" );
        add( getLblOccupyPercent(), "cell 1 1" );
    }

    public void setClientBorder( Color col )
    {
        setBorder( new LineBorder( col, 3, true ) );
    }

    public JLabel getLblPlayerName()
    {
        if ( lblPlayerName == null )
        {
            lblPlayerName = new JLabel();
            lblPlayerName.setBorder( null );
        }
        return lblPlayerName;
    }

    public JLabel getLblPlayerStatus()
    {
        if ( lblPlayerStatus == null )
        {
            lblPlayerStatus = new JLabel();
            lblPlayerStatus.setHorizontalAlignment( SwingConstants.CENTER );
            lblPlayerStatus.setBorder( null );
        }
        return lblPlayerStatus;
    }

    public JLabel getLblOccupyPercent()
    {
        if ( lblOccupyPercent == null )
        {
            lblOccupyPercent = new JLabel();
        }
        return lblOccupyPercent;
    }
}
