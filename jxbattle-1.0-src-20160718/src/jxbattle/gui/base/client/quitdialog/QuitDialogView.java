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

package jxbattle.gui.base.client.quitdialog;

import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

class QuitDialogView extends JDialog
{
    private JLabel lblQuestion;

    private JButton btnWatch;

    private JButton btnAbort;

    private JButton btnCancel;

    public QuitDialogView( Window parentWindow )
    {
        super( SwingUtilities.getWindowAncestor( parentWindow ) ); // contournement du bug dialog non modal

        setModal( true );
        setModalityType( ModalityType.APPLICATION_MODAL );

        setSize( 500, 200 );

        getContentPane().setLayout( new MigLayout( "", "[grow]", "[grow][]" ) );
        getContentPane().add( getLblQuestion(), "cell 0 0,alignx center,growy" );
        getContentPane().add( getBtnWatch(), "flowx,cell 0 1,growx" );
        getContentPane().add( getBtnAbort(), "cell 0 1,growx" );
        getContentPane().add( getBtnCancel(), "cell 0 1,growx" );
    }

    public JLabel getLblQuestion()
    {
        if ( lblQuestion == null )
        {
            lblQuestion = new JLabel( "What do you want to do ?" );
        }
        return lblQuestion;
    }

    public JButton getBtnWatch()
    {
        if ( btnWatch == null )
        {
            btnWatch = new JButton( "Continue watch game" );
        }
        return btnWatch;
    }

    public JButton getBtnAbort()
    {
        if ( btnAbort == null )
        {
            btnAbort = new JButton( "Quit and close" );
        }
        return btnAbort;
    }

    public JButton getBtnCancel()
    {
        if ( btnCancel == null )
        {
            btnCancel = new JButton( "Cancel" );
        }
        return btnCancel;
    }
}
