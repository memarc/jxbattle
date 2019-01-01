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

package jxbattle.gui.composite.client.clientsettingsdialog;

import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import jxbattle.gui.base.client.clientsettingspanel.ClientSettingsPanelView;
import net.miginfocom.swing.MigLayout;

class ClientSettingsDialogView extends JDialog
{
    private JButton btnClose;

    private ClientSettingsPanelView clientSettingsPanelView;

    public ClientSettingsDialogView( Window parentWindow )
    {
        super( SwingUtilities.getWindowAncestor( parentWindow ) ); // contournement du bug dialog non modal

        setModal( true );
        setModalityType( ModalityType.APPLICATION_MODAL );

        setSize( 350, 250 );

        getContentPane().setLayout( new MigLayout( "", "[grow]", "[grow][]" ) );
        getContentPane().add( getClientSettingsPanelView(), "cell 0 0,grow" );
        getContentPane().add( getBtnClose(), "cell 0 1,alignx right" );
    }

    public JButton getBtnClose()
    {
        if ( btnClose == null )
        {
            btnClose = new JButton( "Close" );
        }
        return btnClose;
    }

    public ClientSettingsPanelView getClientSettingsPanelView()
    {
        if ( clientSettingsPanelView == null )
        {
            clientSettingsPanelView = new ClientSettingsPanelView();
        }
        return clientSettingsPanelView;
    }
}
