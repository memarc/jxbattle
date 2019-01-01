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

package jxbattle.gui.base.server.serversettingspanel;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import net.miginfocom.swing.MigLayout;

public class ServerSettingsPanelView extends JPanel
{
    private JLabel lblListenPort;

    private JSpinner spinListenPort;

    private JLabel lblListenSocketTimeout;

    private JSpinner spinSocketTimeout;

    private JLabel lblGameUpdateTick;

    private JSpinner spinGameTick;

    private JCheckBox cbCheckClientsState;

    private JLabel lblFlushFrequency;

    private JSpinner spinFlushFrequency;

    private JLabel lblBiasNetworkAgent;

    private JSpinner spinNetworkBias;

    public ServerSettingsPanelView()
    {
        setLayout( new MigLayout( "", "[grow][grow][grow]", "[][][][][][]" ) );
        add( getLblListenPort(), "cell 0 0,alignx left" );
        add( getSpinListenPort(), "cell 1 0 2 1,growx" );
        add( getLblListenSocketTimeout(), "cell 0 1,alignx left" );
        add( getSpinSocketTimeout(), "cell 1 1 2 1,growx" );
        add( getLblGameUpdateTick(), "cell 0 2,alignx left" );
        add( getSpinGameTick(), "cell 1 2 2 1,growx" );
        add( getCbCheckClientsState(), "cell 0 3 3 1" );
        add( getLblFlushFrequency(), "cell 0 4" );
        add( getSpinFlushFrequency(), "cell 1 4 2 1,growx" );
        add( getLblBiasNetworkAgent(), "cell 0 5" );
        add( getSpinNetworkBias(), "cell 1 5 2 1,growx" );
    }

    public JLabel getLblListenPort()
    {
        if ( lblListenPort == null )
        {
            lblListenPort = new JLabel( "Listen port" );
        }
        return lblListenPort;
    }

    public JSpinner getSpinListenPort()
    {
        if ( spinListenPort == null )
        {
            spinListenPort = new JSpinner();
        }
        return spinListenPort;
    }

    public JLabel getLblListenSocketTimeout()
    {
        if ( lblListenSocketTimeout == null )
        {
            lblListenSocketTimeout = new JLabel( "Socket timeout (ms)" );
        }
        return lblListenSocketTimeout;
    }

    public JSpinner getSpinSocketTimeout()
    {
        if ( spinSocketTimeout == null )
        {
            spinSocketTimeout = new JSpinner();
        }
        return spinSocketTimeout;
    }

    public JLabel getLblGameUpdateTick()
    {
        if ( lblGameUpdateTick == null )
        {
            lblGameUpdateTick = new JLabel( "Game update tick (ms)" );
        }
        return lblGameUpdateTick;
    }

    public JSpinner getSpinGameTick()
    {
        if ( spinGameTick == null )
        {
            spinGameTick = new JSpinner();
        }
        return spinGameTick;
    }

    public JCheckBox getCbCheckClientsState()
    {
        if ( cbCheckClientsState == null )
        {
            cbCheckClientsState = new JCheckBox( "Check clients state" );
        }
        return cbCheckClientsState;
    }

    public JLabel getLblFlushFrequency()
    {
        if ( lblFlushFrequency == null )
        {
            lblFlushFrequency = new JLabel( "Flush frequency" );
        }
        return lblFlushFrequency;
    }

    public JSpinner getSpinFlushFrequency()
    {
        if ( spinFlushFrequency == null )
        {
            spinFlushFrequency = new JSpinner();
        }
        return spinFlushFrequency;
    }

    public JLabel getLblBiasNetworkAgent()
    {
        if ( lblBiasNetworkAgent == null )
        {
            lblBiasNetworkAgent = new JLabel( "Network bias (ms)" );
        }
        return lblBiasNetworkAgent;
    }

    public JSpinner getSpinNetworkBias()
    {
        if ( spinNetworkBias == null )
        {
            spinNetworkBias = new JSpinner();
        }
        return spinNetworkBias;
    }
}
