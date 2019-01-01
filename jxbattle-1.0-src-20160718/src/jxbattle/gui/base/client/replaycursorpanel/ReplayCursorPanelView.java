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

package jxbattle.gui.base.client.replaycursorpanel;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import net.miginfocom.swing.MigLayout;

public class ReplayCursorPanelView extends JPanel
{
    private JPanel pnlStatus;

    private JButton btnPrevious;

    private JButton btnNext;

    //    private BoardPanelView boardPanelView;

    private JButton btnStart;

    private JButton btnBigPrevious;

    private JButton btnBigNext;

    private JButton btnEnd;

    private JSpinner spinStepSize;

    private JLabel lblStepSize;

    public ReplayCursorPanelView()
    {
        setLayout( new BorderLayout( 0, 0 ) );
        add( getPnlStatus(), BorderLayout.NORTH );
    }

    private JPanel getPnlStatus()
    {
        if ( pnlStatus == null )
        {
            pnlStatus = new JPanel();
            pnlStatus.setLayout( new MigLayout( "", "[49px][54px][44px][44px][54px][49px][][grow]", "[25px]" ) );
            pnlStatus.add( getBtnStart(), "cell 0 0,alignx right,aligny center" );
            pnlStatus.add( getBtnBigPrevious(), "cell 1 0,alignx left,aligny center" );
            pnlStatus.add( getBtnPrevious(), "cell 2 0,alignx left,aligny center" );
            pnlStatus.add( getBtnNext(), "cell 3 0,alignx left,aligny center" );
            pnlStatus.add( getBtnBigNext(), "cell 4 0,alignx left,aligny center" );
            pnlStatus.add( getBtnEnd(), "cell 5 0,alignx left,aligny center" );
            pnlStatus.add( getLblStepSize(), "cell 6 0,alignx left,aligny center" );
            pnlStatus.add( getSpinStepSize(), "cell 7 0,growx,aligny center" );
        }
        return pnlStatus;
    }

    JButton getBtnPrevious()
    {
        if ( btnPrevious == null )
        {
            btnPrevious = new JButton( "<" );
        }
        return btnPrevious;
    }

    JButton getBtnNext()
    {
        if ( btnNext == null )
        {
            btnNext = new JButton( ">" );
        }
        return btnNext;
    }

    JButton getBtnStart()
    {
        if ( btnStart == null )
        {
            btnStart = new JButton( "|<" );
        }
        return btnStart;
    }

    JButton getBtnBigPrevious()
    {
        if ( btnBigPrevious == null )
        {
            btnBigPrevious = new JButton( "<<" );
        }
        return btnBigPrevious;
    }

    JButton getBtnBigNext()
    {
        if ( btnBigNext == null )
        {
            btnBigNext = new JButton( ">>" );
        }
        return btnBigNext;
    }

    JButton getBtnEnd()
    {
        if ( btnEnd == null )
        {
            btnEnd = new JButton( ">|" );
        }
        return btnEnd;
    }

    JSpinner getSpinStepSize()
    {
        if ( spinStepSize == null )
        {
            spinStepSize = new JSpinner();
        }
        return spinStepSize;
    }

    private JLabel getLblStepSize()
    {
        if ( lblStepSize == null )
        {
            lblStepSize = new JLabel( "Step size" );
        }
        return lblStepSize;
    }
}
