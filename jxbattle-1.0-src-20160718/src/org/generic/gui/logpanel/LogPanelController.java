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

package org.generic.gui.logpanel;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.generic.bean.TextMessage;
import org.generic.gui.GuiUtils;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.logmessage.LogMessageModelChangeId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class LogPanelController implements MVCController<LogPanelModel, LogPanelView>, MVCModelObserver
{
    private LogPanelView view;

    //    private LogMessageModel model;
    private LogPanelModel model;

    public LogPanelController( LogPanelView v )
    {
        view = v;
        init();
    }

    private void init()
    {
        createHandlers();
    }

    private void createHandlers()
    {
        view.addAncestorListener( new AncestorListener()
        {
            @Override
            public void ancestorRemoved( AncestorEvent event )
            {
            }

            @Override
            public void ancestorMoved( AncestorEvent event )
            {
                flushMessages(); // flush stored message while view was not realized
            }

            @Override
            public void ancestorAdded( AncestorEvent event )
            {
                flushMessages(); // flush stored message while view was not realized
            }
        } );
    }

    private void flushMessages()
    {
        if ( model.hasMessageBuffer() )
        {
            JTextArea ta = view.getTextArea();
            String m;
            //            while ( (m = messageBuffer.poll()) != null )
            while ( (m = model.getBufferisedMessage()) != null )
                GuiUtils.appendTextareaLine( ta, m );
        }
    }

    public void clearMessages()
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                Document m = view.getTextArea().getDocument();
                try
                {
                    m.remove( 0, m.getLength() );
                }
                catch( BadLocationException e )
                {
                }
            }
        } );
    }

    private void updateUI()
    {
        view.revalidate();
        view.repaint();
    }

    // MVCController interface

    @Override
    public LogPanelView getView()
    {
        return view;
    }

    @Override
    public LogPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( LogPanelModel m )
    {
        unsubscribeModel();

        //        model = null;
        //        if ( m instanceof LogMessageModel )
        //        {
        //            model = (LogMessageModel)m;
        model = m;
        subscribeModel();
        //        }
    }

    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof LogMessageModelChangeId )
            switch ( (LogMessageModelChangeId)change.getChangeId() )
            {
                case ErrorMessage:
                case InfoMessage:
                    TextMessage m = (TextMessage)change.getData();
                    boolean display = view != null;
                    if ( model.hasMessageBuffer() )
                    {
                        boolean valid = !view.isValid(); // isValid() : if used in an applet and applet not displayed yet, we get blocked
                        boolean showing = view.isShowing();
                        display = display && showing && valid;
                    }
                    //                    if ( view != null && view.isValid() && view.isShowing() )
                    if ( display )
                    {
                        JTextArea ta = view.getTextArea();
                        GuiUtils.appendTextareaLine( ta, m.toString() );
                        updateUI();
                    }
                    else if ( model.hasMessageBuffer() )
                    {
                        // store message if view not yet displayed
                        boolean added = false;
                        while ( !added )
                            //                            added = messageBuffer.offer( m.toString() );
                            added = model.bufferiseMessage( m.toString() );
                    }
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

        if ( SwingUtilities.isEventDispatchThread() )
            processModelChange( change );
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    processModelChange( change );
                }
            } );
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
        view = null;
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
