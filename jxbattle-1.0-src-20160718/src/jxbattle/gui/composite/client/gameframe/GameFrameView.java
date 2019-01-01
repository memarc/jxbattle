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

package jxbattle.gui.composite.client.gameframe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jxbattle.gui.base.client.boardpanel.BoardPanelView;
import net.miginfocom.swing.MigLayout;

class GameFrameView extends JFrame
{
    private JPanel pnlStatus;

    private JButton btnZoomPlus;

    private JButton btnZoomMinus;

    private BoardPanelView boardPanelView;

    private JLabel lbl1;

    private JLabel lblCellXY;

    private JLabel lbl2;

    private JLabel lblTime;

    private JLabel lblLevel;

    private JLabel lbl4;

    private JLabel lblElevation;

    private JLabel lbl5;

    private JLabel lblReserve;

    private JLabel lbl3;

    private JLabel lbl6;

    private JLabel lblOwner;

    private JLabel lblFight;

    private JPanel pnlPlayersStatus;

    private JPanel pnlInfos;

    private JCheckBox cbDrawGrid;

    GameFrameView()
    {
        getContentPane().setLayout( new BorderLayout( 0, 0 ) );
        getContentPane().add( getPnlStatus(), BorderLayout.NORTH );
        getContentPane().add( getBoardPanelView(), BorderLayout.CENTER );
    }

    JPanel getPnlStatus()
    {
        if ( pnlStatus == null )
        {
            pnlStatus = new JPanel();
            pnlStatus.setMinimumSize( new Dimension( 50, 15 ) );
            pnlStatus.setLayout( new MigLayout( "", "[][][grow]", "[][][]" ) );
            pnlStatus.add( getBtnZoomPlus(), "cell 0 0,growx,aligny center" );
            pnlStatus.add( getBtnZoomMinus(), "cell 1 0,growx,aligny center" );
            pnlStatus.add( getPnlPlayersStatus(), "cell 2 0 1 2,grow" );
            pnlStatus.add( getCbDrawGrid(), "cell 0 1 2 1" );
            pnlStatus.add( getPnlInfos(), "cell 0 2 3 1,alignx left,growy" );
        }
        return pnlStatus;
    }

    JButton getBtnZoomPlus()
    {
        if ( btnZoomPlus == null )
        {
            btnZoomPlus = new JButton( "+" );
        }
        return btnZoomPlus;
    }

    JButton getBtnZoomMinus()
    {
        if ( btnZoomMinus == null )
        {
            btnZoomMinus = new JButton( "-" );
        }
        return btnZoomMinus;
    }

    BoardPanelView getBoardPanelView()
    {
        if ( boardPanelView == null )
        {
            boardPanelView = new BoardPanelView();
        }
        return boardPanelView;
    }

    private JLabel getLbl1()
    {
        if ( lbl1 == null )
        {
            lbl1 = new JLabel( "X Y " );
            lbl1.setForeground( Color.GRAY );
        }
        return lbl1;
    }

    private JLabel getLbl2()
    {
        if ( lbl2 == null )
        {
            lbl2 = new JLabel( "Time " );
            lbl2.setForeground( Color.GRAY );
        }
        return lbl2;
    }

    private JLabel getLbl3()
    {
        if ( lbl3 == null )
        {
            lbl3 = new JLabel( "Level" );
            lbl3.setForeground( Color.GRAY );
        }
        return lbl3;
    }

    private JLabel getLbl4()
    {
        if ( lbl4 == null )
        {
            lbl4 = new JLabel( "Elevation " );
            lbl4.setForeground( Color.GRAY );
        }
        return lbl4;
    }

    private JLabel getLbl5()
    {
        if ( lbl5 == null )
        {
            lbl5 = new JLabel( "Reserve " );
            lbl5.setForeground( Color.GRAY );
        }
        return lbl5;
    }

    private JLabel getLbl6()
    {
        if ( lbl6 == null )
        {
            lbl6 = new JLabel( "Owner " );
            lbl6.setForeground( Color.GRAY );
        }
        return lbl6;
    }

    JLabel getLblCellXY()
    {
        if ( lblCellXY == null )
        {
            lblCellXY = new JLabel();
            lblCellXY.setMinimumSize( new Dimension( 50, 15 ) );
        }
        return lblCellXY;
    }

    JLabel getLblTime()
    {
        if ( lblTime == null )
        {
            lblTime = new JLabel();
            lblTime.setMinimumSize( new Dimension( 70, 15 ) );
        }
        return lblTime;
    }

    JLabel getLblLevel()
    {
        if ( lblLevel == null )
        {
            lblLevel = new JLabel();
            lblLevel.setMinimumSize( new Dimension( 80, 15 ) );
        }
        return lblLevel;
    }

    JLabel getLblElevation()
    {
        if ( lblElevation == null )
        {
            lblElevation = new JLabel();
            lblElevation.setMinimumSize( new Dimension( 25, 15 ) );
        }
        return lblElevation;
    }

    JLabel getLblReserve()
    {
        if ( lblReserve == null )
        {
            lblReserve = new JLabel();
            lblReserve.setMinimumSize( new Dimension( 35, 15 ) );
        }
        return lblReserve;
    }

    JLabel getLblOwner()
    {
        if ( lblOwner == null )
        {
            lblOwner = new JLabel();
            lblOwner.setMinimumSize( new Dimension( 30, 15 ) );
        }
        return lblOwner;
    }

    JLabel getLblFight()
    {
        if ( lblFight == null )
        {
            lblFight = new JLabel( "Fight" );
        }
        return lblFight;
    }

    JPanel getPnlPlayersStatus()
    {
        if ( pnlPlayersStatus == null )
        {
            pnlPlayersStatus = new JPanel();
        }
        return pnlPlayersStatus;
    }

    private JPanel getPnlInfos()
    {
        if ( pnlInfos == null )
        {
            pnlInfos = new JPanel();
            pnlInfos.setLayout( new MigLayout( "", "[][][][][][][][][][][][][]", "[]" ) );
            pnlInfos.add( getLbl2(), "cell 0 0" );
            pnlInfos.add( getLblTime(), "cell 1 0" );
            pnlInfos.add( getLbl1(), "cell 2 0" );
            pnlInfos.add( getLblCellXY(), "cell 3 0" );
            pnlInfos.add( getLbl4(), "cell 4 0" );
            pnlInfos.add( getLblElevation(), "cell 5 0" );
            pnlInfos.add( getLbl3(), "cell 6 0" );
            pnlInfos.add( getLblLevel(), "cell 7 0" );
            pnlInfos.add( getLbl5(), "cell 8 0" );
            pnlInfos.add( getLblReserve(), "cell 9 0" );
            pnlInfos.add( getLbl6(), "cell 10 0" );
            pnlInfos.add( getLblOwner(), "cell 11 0" );
            pnlInfos.add( getLblFight(), "cell 12 0" );
        }
        return pnlInfos;
    }

    JCheckBox getCbDrawGrid()
    {
        if ( cbDrawGrid == null )
        {
            cbDrawGrid = new JCheckBox( "Grid" );
        }
        return cbDrawGrid;
    }
}
