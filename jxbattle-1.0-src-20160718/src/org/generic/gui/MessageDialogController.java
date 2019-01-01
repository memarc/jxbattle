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

package org.generic.gui;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.generic.bean.TextMessage;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.logmessage.LogMessageModel;
import org.generic.mvc.model.logmessage.LogMessageModelChangeId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class MessageDialogController implements MVCController<LogMessageModel, JComponent>, MVCModelObserver
{
    private Component parentComponent;

    private LogMessageModel model;

    private Window windowAncestor;

    public MessageDialogController( Component parent )
    {
        parentComponent = parent;
    }

    private Window getWindowAncestor()
    {
        if ( windowAncestor == null && parentComponent != null )
            windowAncestor = SwingUtilities.getWindowAncestor( parentComponent );

        //        System.out.println( "MessageDialogController windowAncestor " + windowAncestor );
        return windowAncestor;
    }

    //    private void displayErrorMessage( MVCModelChange change )
    //    {
    //        TextMessage m = (TextMessage)change.getChangedObject();
    //        //        System.out.println( m.toString() );
    //        //        m.getException().printStackTrace();
    //        JOptionPane.showMessageDialog( parentComponent, m.getMessage() + " :\n" + m.getException(), "Error", JOptionPane.ERROR_MESSAGE );
    //    }

    private void displayInfoMessage( MVCModelChange change )
    {
        TextMessage m = (TextMessage)change.getData();
        //System.out.println( m.toString() );
        if ( m.getException() == null )
            JOptionPane.showMessageDialog( getWindowAncestor(), m.getMessage(), "Message", JOptionPane.PLAIN_MESSAGE );
        else
            JOptionPane.showMessageDialog( getWindowAncestor(), m.getMessage() + " :\n" + m.getException(), "Error", JOptionPane.ERROR_MESSAGE );
    }

    // MVCController interface

    @Override
    public JComponent getView()
    {
        return null;
    }

    @Override
    public LogMessageModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( LogMessageModel m )
    {
        unsubscribeModel();

        model = m;
        subscribeModel();
    }

    private void processModelChange( MVCModelChange change )
    {
        switch ( (LogMessageModelChangeId)change.getChangeId() )
        {
        //            case ErrorMessage:
        //                displayErrorMessage( change );
        //                break;

            case ErrorMessage:
            case GuiMessage:
                displayInfoMessage( change );
                break;

            default:
                break;
        }
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        // called from code that is potentially not in the EDT...

        //        if ( SwingUtilities.isEventDispatchThread() )
        //            processModelChange( change );
        //        else
        {
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    processModelChange( change );
                }
            } );
        }
    }

    @Override
    public void subscribeModel()
    {
        if ( model != null )
            model.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        if ( model != null )
            model.removeObserver( this );
    }

    private void close_edt()
    {
        unsubscribeModel();
    }

    @Override
    public void close()
    {
        if ( SwingUtilities.isEventDispatchThread() )
            close_edt();
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    close_edt();
                }
            } );
    }
}
