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

package jxbattle.gui.composite.client.replayframe;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jxbattle.gui.base.client.boardpanel.BoardPanelView;
import jxbattle.gui.base.client.replaycursorpanel.ReplayCursorPanelView;
import net.miginfocom.swing.MigLayout;

class ReplayFrameView extends JFrame
{
    private JLabel lblStatus;

    private JPanel pnlStatus;

    private BoardPanelView boardPanelView;

    private JButton btnRunEngine;

    private JCheckBox cbVisualDebug;

    private JLabel lblClientplayer;

    private JLabel lblPlayer;

    private ReplayCursorPanelView replayCursorPanelView;

    ReplayFrameView()
    {
        getContentPane().setLayout( new BorderLayout( 0, 0 ) );
        getContentPane().add( getPnlStatus(), BorderLayout.NORTH );
        getContentPane().add( getBoardPanelView(), BorderLayout.CENTER );
    }

    JLabel getLblStatus()
    {
        return lblStatus;
    }

    private JPanel getPnlStatus()
    {
        if ( pnlStatus == null )
        {
            pnlStatus = new JPanel();
            pnlStatus.setLayout( new MigLayout( "", "[49px][54px][44px][44px,grow]", "[25px,grow][][]" ) );
            pnlStatus.add( getReplayCursorPanelView(), "cell 0 0 4 1,grow" );
            pnlStatus.add( getCbVisualDebug(), "cell 0 1" );
            pnlStatus.add( getBtnEngineStep(), "cell 1 1,alignx left,aligny center" );
            pnlStatus.add( getLblPlayer(), "cell 2 1,alignx right" );
            pnlStatus.add( getLblClientplayer(), "cell 3 1,alignx left" );
            lblStatus = new JLabel( "" );
            pnlStatus.add( this.lblStatus, "cell 0 2 4 1,alignx left,aligny center" );
        }
        return pnlStatus;
    }

    BoardPanelView getBoardPanelView()
    {
        if ( boardPanelView == null )
        {
            boardPanelView = new BoardPanelView();
        }
        return boardPanelView;
    }

    JButton getBtnEngineStep()
    {
        if ( btnRunEngine == null )
        {
            btnRunEngine = new JButton( "Engine step" );
        }
        return btnRunEngine;
    }

    JCheckBox getCbVisualDebug()
    {
        if ( cbVisualDebug == null )
        {
            cbVisualDebug = new JCheckBox( "debug" );
        }
        return cbVisualDebug;
    }

    JLabel getLblClientplayer()
    {
        if ( lblClientplayer == null )
        {
            lblClientplayer = new JLabel( "" );
        }
        return lblClientplayer;
    }

    private JLabel getLblPlayer()
    {
        if ( lblPlayer == null )
        {
            lblPlayer = new JLabel( "player :" );
        }
        return lblPlayer;
    }

    ReplayCursorPanelView getReplayCursorPanelView()
    {
        if ( replayCursorPanelView == null )
        {
            replayCursorPanelView = new ReplayCursorPanelView();
        }
        return replayCursorPanelView;
    }
}
