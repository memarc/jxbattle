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

package jxbattle.gui.base.client.clientsettingspanel;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import net.miginfocom.swing.MigLayout;

public class ClientSettingsPanelView extends JPanel
{
    private JCheckBox cbLogGame;

    private JLabel lblNetworkBias;

    private JSpinner spinNetworkBias;

    private JLabel lblSocketTimeout;

    private JSpinner spinSocketTimeout;

    public ClientSettingsPanelView()
    {
        setLayout( new MigLayout( "", "[][grow]", "[][][]" ) );
        add( getCbLogGame(), "cell 0 0,alignx left" );
        add( getLblSocketTimeout(), "cell 0 1" );
        add( getSpinSocketTimeout(), "cell 1 1,growx" );
        add( getLblNetworkBias(), "cell 0 2,alignx left" );
        add( getSpinNetworkBias(), "cell 1 2,growx" );
    }

    public JCheckBox getCbLogGame()
    {
        if ( cbLogGame == null )
        {
            cbLogGame = new JCheckBox( "Log game to file" );
        }
        return cbLogGame;
    }

    public JLabel getLblNetworkBias()
    {
        if ( lblNetworkBias == null )
        {
            lblNetworkBias = new JLabel( "Network bias (ms)" );
        }
        return lblNetworkBias;
    }

    public JSpinner getSpinNetworkBias()
    {
        if ( spinNetworkBias == null )
        {
            spinNetworkBias = new JSpinner();
        }
        return spinNetworkBias;
    }

    public JLabel getLblSocketTimeout()
    {
        if ( lblSocketTimeout == null )
        {
            lblSocketTimeout = new JLabel( "Socket timeout" );
        }
        return lblSocketTimeout;
    }

    public JSpinner getSpinSocketTimeout()
    {
        if ( spinSocketTimeout == null )
        {
            spinSocketTimeout = new JSpinner();
        }
        return spinSocketTimeout;
    }
}
