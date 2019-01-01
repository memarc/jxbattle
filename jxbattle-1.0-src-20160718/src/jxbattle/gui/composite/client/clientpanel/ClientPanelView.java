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

package jxbattle.gui.composite.client.clientpanel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jxbattle.gui.base.client.connectiontoserverpanel.ConnectionToServerPanelView;
import jxbattle.gui.base.client.playerchooserpanel.PlayerChooserPanelView;
import jxbattle.model.MainModel;
import net.miginfocom.swing.MigLayout;

import org.generic.gui.logpanel.LogPanelView;

public class ClientPanelView extends JPanel
{
    private ConnectionToServerPanelView serverConnectionView;

    private PlayerChooserPanelView playerChooserPanelView;

    private JLabel lblStatus;

    private JButton btnClientSettings;

    private JButton btnGameSettings;

    private LogPanelView logPanelView;

    public ClientPanelView()
    {
        setLayout( new MigLayout( "", "[grow][grow]", "[grow][][75.00:150][]" ) );
        add( getServerConnectionView(), "cell 0 0,grow" );
        add( getPlayerChooserPanelView(), "cell 1 0,grow" );
        add( getBtnGameSettings(), "cell 0 1,alignx right" );
        add( getBtnClientSettings(), "cell 1 1" );
        add( getLogPanelView(), "cell 0 2 2 1,grow" );

        if ( MainModel.debugState )
            add( getLblStatus(), "cell 0 3" );
    }

    ConnectionToServerPanelView getServerConnectionView()
    {
        if ( serverConnectionView == null )
        {
            serverConnectionView = new ConnectionToServerPanelView();
        }
        return serverConnectionView;
    }

    PlayerChooserPanelView getPlayerChooserPanelView()
    {
        if ( playerChooserPanelView == null )
        {
            playerChooserPanelView = new PlayerChooserPanelView();
        }
        return playerChooserPanelView;
    }

    JLabel getLblStatus()
    {
        if ( lblStatus == null )
        {
            lblStatus = new JLabel( "" );
        }
        return lblStatus;
    }

    JButton getBtnClientSettings()
    {
        if ( btnClientSettings == null )
        {
            btnClientSettings = new JButton( "Client settings" );
        }
        return btnClientSettings;
    }

    JButton getBtnGameSettings()
    {
        if ( btnGameSettings == null )
        {
            btnGameSettings = new JButton( "Game settings" );
        }
        return btnGameSettings;
    }

    LogPanelView getLogPanelView()
    {
        if ( logPanelView == null )
        {
            logPanelView = new LogPanelView();
        }
        return logPanelView;
    }
}
