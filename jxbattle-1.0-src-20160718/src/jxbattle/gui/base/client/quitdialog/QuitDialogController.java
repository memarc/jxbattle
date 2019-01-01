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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.generic.gui.GuiUtils;

public class QuitDialogController
{
    private QuitDialogView view;

    public enum QuitDialogCode
    {
        Cancel, Watch, Abort
    }

    private QuitDialogCode status;

    public QuitDialogController( Window parentWindow )
    {
        view = new QuitDialogView( parentWindow );
        init( parentWindow );
    }

    private void init( Window parentWindow )
    {
        GuiUtils.centerWindow( view, parentWindow );
        createHandlers();
    }

    public QuitDialogCode run( boolean allowWatch )
    {
        view.getBtnWatch().setEnabled( allowWatch );
        view.setVisible( true );
        return status;
    }

    private void createHandlers()
    {
        view.getBtnCancel().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                cancel();
            }
        } );

        view.getBtnWatch().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                watch();
            }
        } );

        view.getBtnAbort().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                abort();
            }
        } );

        view.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent w )
            {
                // inhibit close
            }
        } );
    }

    private void cancel()
    {
        status = QuitDialogCode.Cancel;
        close();
    }

    private void watch()
    {
        status = QuitDialogCode.Watch;
        close();
    }

    private void abort()
    {
        status = QuitDialogCode.Abort;
        close();
    }

    public void close()
    {
        if ( view != null )
        {
            view.dispose();
            view = null;
        }
    }
}
