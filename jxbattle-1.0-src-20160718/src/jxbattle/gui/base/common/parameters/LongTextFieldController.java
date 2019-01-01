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

package jxbattle.gui.base.common.parameters;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import jxbattle.model.common.parameters.LongParameterModel;

import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class LongTextFieldController implements MVCController<LongParameterModel, JTextField>, MVCModelObserver
{
    private JTextField view;

    private LongParameterModel model;

    public LongTextFieldController( JTextField v )
    {
        view = v;
        init();
    }

    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }

    private void init()
    {
        view.setDocument( new PositiveNumericDocument() );

        // select text on focus gain

        view.addFocusListener( new FocusAdapter()
        {
            @Override
            public void focusGained( FocusEvent e )
            {
                if ( e.getSource() instanceof JTextComponent )
                {
                    final JTextComponent textComponent = ((JTextComponent)e.getSource());
                    SwingUtilities.invokeLater( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            textComponent.selectAll();
                        }
                    } );
                }
            }
        } );

        // update model when text field value changes

        view.getDocument().addDocumentListener( new DocumentListener()
        {
            @Override
            public void removeUpdate( DocumentEvent e )
            {
                onRemoveUpdate( LongTextFieldController.this );
            }

            @Override
            public void insertUpdate( DocumentEvent e )
            {
                onInsertUpdate( LongTextFieldController.this );
            }

            @Override
            public void changedUpdate( DocumentEvent e )
            {
            }
        } );
    }

    private void onInsertUpdate( Object sender )
    {
        model.setValue( sender, Long.parseLong( view.getText() ) );
    }

    private void onRemoveUpdate( Object sender )
    {
        String text = view.getText();
        if ( text.length() > 0 )
            model.setValue( sender, Long.parseLong( text ) );
    }

    private void modelToUI_edt()
    {
        view.setText( String.valueOf( model.getValue() ) );
    }

    private void modelToUI()
    {
        // always update UI later to avoid "Attempt to mutate in notification" IllegalStateException

        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                modelToUI_edt();
            }
        } );
    }

    // MVCController interface

    @Override
    public JTextField getView()
    {
        return view;
    }

    @Override
    public LongParameterModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( LongParameterModel m )
    {
        unsubscribeModel();
        //        model = null;
        //
        //        if ( m instanceof LongParameterModel )
        //        {
        //            model = (LongParameterModel)m;
        model = m;
        subscribeModel();
        //        }

        modelToUI();
    }

    // ModelObserver

    @Override
    public void modelChanged( MVCModelChange change )
    {
        if ( change.getSender() != this )
            modelToUI();
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

    private static class PositiveNumericDocument extends PlainDocument
    {
        private Pattern digitsOnlyPattern = Pattern.compile( "\\d*" );

        @Override
        public void insertString( int offs, String str, AttributeSet a ) throws BadLocationException
        {
            if ( str != null && digitsOnlyPattern.matcher( str ).matches() )
                super.insertString( offs, str, a );
        }
    }
}
