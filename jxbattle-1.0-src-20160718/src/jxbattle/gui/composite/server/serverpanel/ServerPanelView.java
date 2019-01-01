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

package jxbattle.gui.composite.server.serverpanel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jxbattle.gui.base.server.gameplayerspanel.GamePlayersPanelView;
import jxbattle.model.MainModel;
import net.miginfocom.swing.MigLayout;

import org.generic.gui.logpanel.LogPanelView;

public class ServerPanelView extends JPanel
{
    private JButton btnRunGame;

    private JButton btnStopGame;

    private GamePlayersPanelView gamePlayersPanelView;

    private JButton btnGameSettings;

    private JButton btnServerSettings;

    private JCheckBox cbStepMode;

    private JButton btnDoStep;

    private JPanel pnlRun;

    private JLabel lblStatus;

    private JLabel label;

    private LogPanelView logPanelView;

    public ServerPanelView()
    {
        setLayout( new MigLayout( "", "[grow][grow]", "[grow][150.00][]" ) );
        add( getPnlRun(), "cell 0 0,grow" );
        add( getGamePlayersPanelView(), "cell 1 0,grow" );
        add( getLogPanelView(), "cell 0 1 2 1,grow" );

        if ( MainModel.debugState )
            add( getLblStatus(), "cell 0 2 2 1" );
    }

    JButton getBtnRunGame()
    {
        if ( btnRunGame == null )
        {
            btnRunGame = new JButton( "Run game" );
        }
        return btnRunGame;
    }

    JButton getBtnStopGame()
    {
        if ( btnStopGame == null )
        {
            btnStopGame = new JButton( "Stop game" );
        }
        return btnStopGame;
    }

    GamePlayersPanelView getGamePlayersPanelView()
    {
        if ( gamePlayersPanelView == null )
        {
            gamePlayersPanelView = new GamePlayersPanelView();
        }
        return gamePlayersPanelView;
    }

    JButton getBtnGameSettings()
    {
        if ( btnGameSettings == null )
        {
            btnGameSettings = new JButton( "Game settings" );
        }
        return btnGameSettings;
    }

    JButton getBtnServerSettings()
    {
        if ( btnServerSettings == null )
        {
            btnServerSettings = new JButton( "Server settings" );
        }
        return btnServerSettings;
    }

    JCheckBox getCbStepMode()
    {
        if ( cbStepMode == null )
        {
            cbStepMode = new JCheckBox( "Step mode" );
        }
        return cbStepMode;
    }

    JButton getBtnDoStep()
    {
        if ( btnDoStep == null )
        {
            btnDoStep = new JButton( "Step" );
        }
        return btnDoStep;
    }

    private JPanel getPnlRun()
    {
        if ( pnlRun == null )
        {
            pnlRun = new JPanel();
            pnlRun.setLayout( new MigLayout( "", "[][]", "[][][][][][]" ) );
            pnlRun.add( getBtnRunGame(), "cell 0 0 2 1,growx" );
            pnlRun.add( getBtnStopGame(), "cell 0 1 2 1,growx" );
            pnlRun.add( getCbStepMode(), "cell 0 2,growx" );
            pnlRun.add( getBtnDoStep(), "cell 1 2,growx" );
            pnlRun.add( getLabel(), "cell 0 3" );
            pnlRun.add( getBtnGameSettings(), "cell 0 4 2 1,growx" );
            pnlRun.add( getBtnServerSettings(), "cell 0 5 2 1,growx" );
        }
        return pnlRun;
    }

    JLabel getLblStatus()
    {
        if ( lblStatus == null )
        {
            lblStatus = new JLabel( "" );
        }
        return lblStatus;
    }

    private JLabel getLabel()
    {
        if ( label == null )
        {
            label = new JLabel( "  " );
        }
        return label;
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
