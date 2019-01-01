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

package org.generic.gui.parameters;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class IntSpinnerController implements MVCController<IntSpinnerModel, JSpinner>, MVCModelObserver
{
    private JSpinner view;

    //    private IntParameterModel model;
    //
    private SpinnerNumberModel uiModel;

    private IntSpinnerModel model;

    /**
     * do uiModel min/max initialisation
     */
    private boolean initUIModel;

    /**
     * allow model to be updated from UI
     */
    private boolean updateModel;

    public IntSpinnerController( JSpinner v )
    {
        view = v;
        initUIModel = true;
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
        view.setValue( Integer.MIN_VALUE );

        // manage mouse wheel

        view.addMouseWheelListener( new MouseWheelListener()
        {
            @Override
            public void mouseWheelMoved( MouseWheelEvent e )
            {
                onMouseWheel( IntSpinnerController.this, e );
            }
        } );

        // select text on focus gain

        JFormattedTextField spinnerTF = ((JSpinner.DefaultEditor)view.getEditor()).getTextField();
        spinnerTF.addFocusListener( new FocusAdapter()
        {
            @Override
            public void focusGained( final FocusEvent e )
            {
                if ( e.getSource() instanceof JTextComponent )
                {
                    SwingUtilities.invokeLater( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            final JTextComponent textComponent = ((JTextComponent)e.getSource());
                            textComponent.selectAll();
                        }
                    } );
                }
            }
        } );

        // select text on mouse click

        spinnerTF.addMouseListener( new MouseAdapter()
        {
            @Override
            public void mouseClicked( MouseEvent e )
            {
                if ( e.getSource() instanceof JTextComponent )
                {
                    final JTextComponent textComponent = ((JTextComponent)e.getSource());
                    textComponent.selectAll();
                }
            }
        } );

        // update model when spinner value changes

        uiModel = (SpinnerNumberModel)view.getModel();
        uiModel.addChangeListener( new ChangeListener()
        {
            @Override
            public void stateChanged( ChangeEvent e )
            {
                uiToModel( IntSpinnerController.this );
            }
        } );

        setNumberFilter();

        //        JFormattedTextField spinnerTF = ((JSpinner.DefaultEditor)view.getEditor()).getTextField();
        //        spinnerTF.setInputVerifier( new InputVerifier()
        //        //view.getEditor().setInputVerifier( new InputVerifier()
        //        //view.setInputVerifier( new InputVerifier()
        //                {
        //                    @Override
        //                    public boolean verify( JComponent input )
        //                    {
        //                        return false;
        //                    }
        //
        //                    @Override
        //                    public boolean shouldYieldFocus( JComponent input )
        //                    {
        //                        return super.shouldYieldFocus( input );
        //                    }
        //                } );
    }

    private void onMouseWheel( Object sender, MouseWheelEvent e )
    {
        if ( view.isEnabled() )
            model.setDelta( sender, -e.getWheelRotation() );
    }

    private static class NumberDocumentFilter extends DocumentFilter
    {
        // number only document filter for spinner editor

        @Override
        public void insertString( FilterBypass fb, int offset, String string, AttributeSet attr ) throws BadLocationException
        {
            if ( filterText( string ) )
                super.insertString( fb, offset, string, attr );
        }

        //        @Override
        //        public void remove( FilterBypass fb, int offset, int length ) throws BadLocationException
        //        {
        //            super.remove( fb, offset, length );
        //        }

        @Override
        public void replace( FilterBypass fb, int offset, int length, String text, AttributeSet attrs ) throws BadLocationException
        {
            if ( filterText( text ) )
                super.replace( fb, offset, length, text, attrs );
        }

        private static boolean filterText( String text )
        {
            for ( int i = text.length() - 1; i >= 0; i-- )
            {
                char c = text.charAt( i );
                boolean ok = Character.isDigit( c ) || c == 160; // 160 = non breakable space
                if ( !ok )
                    return false;
            }

            return true;
        }
    }

    /**
     * set character filtering
     */
    private void setNumberFilter()
    {
        JSpinner.NumberEditor jsEditor = (JSpinner.NumberEditor)view.getEditor();
        final Document jsDoc = jsEditor.getTextField().getDocument();
        if ( jsDoc instanceof PlainDocument )
        {
            AbstractDocument doc = new PlainDocument()
            {
                @Override
                public void setDocumentFilter( DocumentFilter filter )
                {
                    if ( filter instanceof NumberDocumentFilter )
                        super.setDocumentFilter( filter );
                }
            };
            doc.setDocumentFilter( new NumberDocumentFilter() );
            jsEditor.getTextField().setDocument( doc );
        }
    }

    //    private void setNumberFilter3()
    //    {
    //        JSpinner.NumberEditor jsEditor = (JSpinner.NumberEditor)view.getEditor();
    //        Document jsDoc = jsEditor.getTextField().getDocument();
    //        if ( jsDoc instanceof PlainDocument )
    //        {
    //            PlainDocument doc = (PlainDocument)jsDoc;
    //            doc.setDocumentFilter( new NumberDocumentFilter() );
    //        }
    //    }

    //    private static void filterKey( KeyEvent e )
    //    {
    //        int c = e.getKeyCode();
    //        switch ( c )
    //        {
    //            case KeyEvent.VK_0:
    //            case KeyEvent.VK_1:
    //            case KeyEvent.VK_2:
    //            case KeyEvent.VK_3:
    //            case KeyEvent.VK_4:
    //            case KeyEvent.VK_5:
    //            case KeyEvent.VK_6:
    //            case KeyEvent.VK_7:
    //            case KeyEvent.VK_8:
    //            case KeyEvent.VK_9:
    //            case KeyEvent.VK_BACK_SPACE:
    //            case KeyEvent.VK_DELETE:
    //            case KeyEvent.VK_ENTER:
    //            case KeyEvent.VK_LEFT:
    //            case KeyEvent.VK_RIGHT:
    //            case KeyEvent.VK_END:
    //            case KeyEvent.VK_HOME:
    //                break;
    //
    //            default:
    //                e.consume();
    //                break;
    //        }
    //    }

    //    private void setNumberFilter2()
    //    {
    //        JFormattedTextField tf = ((JSpinner.DefaultEditor)view.getEditor()).getTextField();
    //        tf.addKeyListener( new KeyListener()
    //        {
    //            @Override
    //            public void keyTyped( KeyEvent e )
    //            {
    //                char c = e.getKeyChar();
    //                boolean ok = Character.isDigit( c ) || c == 160 || c == '\n' || c == '\b'; // 160 = non breakable space
    //                if ( !ok )
    //                    e.consume();
    //            }
    //
    //            @Override
    //            public void keyReleased( KeyEvent e )
    //            {
    //                filterKey( e );
    //            }
    //
    //            @Override
    //            public void keyPressed( KeyEvent e )
    //            {
    //                filterKey( e );
    //            }
    //        } );
    //    }

    //    private void setNumberFilter2()
    //    {
    //        JFormattedTextField spinnerTF = ((JSpinner.DefaultEditor)view.getEditor()).getTextField();
    //        spinnerTF.setFormatterFactory( new AbstractFormatterFactory()
    //        {
    //            @Override
    //            public AbstractFormatter getFormatter( JFormattedTextField tf )
    //            {
    //                try
    //                {
    //                    return new MaskFormatter( "######" );
    //                }
    //                catch( ParseException e )
    //                {
    //                    return null;
    //                }
    //            }
    //        } );
    //    }

    //    private void initUIModel2()
    //    {
    //        updateModel = false;
    //
    //        uiModel = new SpinnerNumberModel( model.getValue(), model.getMin(), model.getMax(), 1 );
    //
    //        // update model when spinner value changes
    //
    //        uiModel.addChangeListener( new ChangeListener()
    //        {
    //            @Override
    //            public void stateChanged( ChangeEvent e )
    //            {
    //                uiToModel();
    //            }
    //        } );
    //
    //        view.setModel( uiModel );
    //
    //        // setNumberFilter(); // must be AFTER setting model
    //    }

    private void initUIModel()
    {
        if ( initUIModel )
        {
            updateModel = false;
            if ( model == null )
            {
                uiModel.setMinimum( 0 );
                uiModel.setMaximum( 0 );
            }
            else
            {
                uiModel.setMinimum( Integer.valueOf( model.getDisplayedMinValue() ) ); // triggers property change -> call uiToModel()
                uiModel.setMaximum( Integer.valueOf( model.getDisplayedMaxValue() ) ); // triggers property change -> call uiToModel()
            }
            //setNumberFilter();
            initUIModel = false;
        }
    }

    private void updateUI()
    {
        view.revalidate();
        view.updateUI();
    }

    private void modelToUI_edt()
    {
        initUIModel();

        updateModel = false;
        if ( model == null )
        {
            uiModel.setValue( 0 );
            view.setEnabled( false );
        }
        else
        {
            view.setEnabled( true );
            uiModel.setValue( Integer.valueOf( model.getValue() ) );
        }
        updateModel = true;

        updateUI();
    }

    private void modelToUI()
    {
        if ( SwingUtilities.isEventDispatchThread() )
            modelToUI_edt();
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    modelToUI_edt();
                }
            } );
    }

    private void uiToModel( Object sender )
    {
        if ( updateModel )
            model.setValue( sender, getSpinnerValue() );
    }

    private int getSpinnerValue()
    {
        return ((Number)view.getValue()).intValue();
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

    // MVCController

    @Override
    public JSpinner getView()
    {
        return view;
    }

    @Override
    public IntSpinnerModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( IntSpinnerModel m )
    {
        unsubscribeModel();
        model = m;
        subscribeModel();
        initUIModel = true;

        modelToUI();
    }

    // ModelObserver

    @Override
    public void modelChanged( MVCModelChange change )
    {
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
}
