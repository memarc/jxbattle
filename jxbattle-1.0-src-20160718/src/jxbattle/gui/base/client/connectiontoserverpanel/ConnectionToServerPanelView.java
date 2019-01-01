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

package jxbattle.gui.base.client.connectiontoserverpanel;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

public class ConnectionToServerPanelView extends JPanel
{
    private JSpinner spinServerPort;

    private JButton btnConnect;

    private JButton btnDisconnect;

    private JComboBox<ServerComboBoxItem> cmbServers;

    //    private AutocompleteComboBoxView<ServerComboBoxItem> cmbServers;

    private JButton btnRemoveServer;

    public ConnectionToServerPanelView()
    {
        setBorder( new TitledBorder( null, "Server", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
        setLayout( new MigLayout( "", "[][grow][grow]", "[][][][][]" ) );
        add( getBtnRemoveServer(), "cell 0 0,alignx right" );
        add( getCmbServers(), "cell 1 0 2 1,growx" );
        add( getSpinServerPort(), "cell 2 1,growx" );
        add( getBtnConnect(), "cell 2 3,growx" );
        add( getBtnDisconnect(), "cell 2 4,growx" );
    }

    JSpinner getSpinServerPort()
    {
        if ( spinServerPort == null )
        {
            spinServerPort = new JSpinner();
        }
        return spinServerPort;
    }

    JButton getBtnConnect()
    {
        if ( btnConnect == null )
        {
            btnConnect = new JButton( "Connect" );
        }
        return btnConnect;
    }

    JButton getBtnDisconnect()
    {
        if ( btnDisconnect == null )
        {
            btnDisconnect = new JButton( "Disconnect" );
        }
        return btnDisconnect;
    }

    JComboBox<ServerComboBoxItem> getCmbServers()
    {
        if ( cmbServers == null )
        {
            cmbServers = new JComboBox<>();
            cmbServers.setEditable( true );
        }
        return cmbServers;
    }

    //    AutocompleteComboBoxView<ServerComboBoxItem> getCmbServers()
    //    {
    //        if ( cmbServers == null )
    //        {
    //            cmbServers = new AutocompleteComboBoxView<ServerComboBoxItem>();
    //            cmbServers.setEditable( true );
    //        }
    //        return cmbServers;
    //    }

    JButton getBtnRemoveServer()
    {
        if ( btnRemoveServer == null )
        {
            btnRemoveServer = new JButton( "x" );
            btnRemoveServer.setToolTipText( "remove server from list" );
        }
        return btnRemoveServer;
    }
}
