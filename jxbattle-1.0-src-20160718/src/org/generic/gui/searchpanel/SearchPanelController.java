package org.generic.gui.searchpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.generic.gui.GuiUtils;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class SearchPanelController implements MVCController<SearchPanelModel, SearchPanelView>, MVCModelObserver
{
    private SearchPanelView view;

    private SearchPanelModel model;

    public SearchPanelController( SearchPanelView v )
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
        view.getBtnRunSearch().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                model.runSearch( SearchPanelController.this );
            }
        } );

        view.getCbImmediateSearch().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                model.setImmediateSearch( SearchPanelController.this, view.getCbImmediateSearch().isSelected() );
            }
        } );

        view.getBtnPrevious().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                model.previousOccurrence( SearchPanelController.this );
            }
        } );

        view.getBtnNext().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                model.nextOccurrence( SearchPanelController.this );
            }
        } );

        view.getBtnFirst().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                model.firstOccurrence( SearchPanelController.this );
            }
        } );

        view.getBtnLast().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                model.lastOccurrence( SearchPanelController.this );
            }
        } );

        final Document doc = view.getTxtSearch().getDocument();
        doc.addDocumentListener( new DocumentListener()
        {
            @Override
            public void removeUpdate( DocumentEvent e )
            {
                setSearch( doc );
            }

            @Override
            public void insertUpdate( DocumentEvent e )
            {
                setSearch( doc );
            }

            @Override
            public void changedUpdate( DocumentEvent e )
            {
            }
        } );

        view.getTxtSearch().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                model.runSearch( SearchPanelController.this );
            }
        } );
    }

    private void setSearch( Document doc )
    {
        try
        {
            //            model.setSearch( SearchPanelController.this, doc.getText( 0, doc.getLength() ) );
            model.setSearchQuery( SearchPanelController.this, doc.getText( 0, doc.getLength() ) );
            updateRunSearchState();
        }
        catch( BadLocationException e )
        {
        }
    }

    private void updateRunSearchState()
    {
        view.getBtnRunSearch().setEnabled( model.getEnable() && model.hasSearchQuery() );
    }

    private void updateUIState()
    {
        if ( model == null )
        {
            // "not found" label

            //        view.getLblNotFound().setVisible( model == null ? false : !model.isSearchNotFound() );
            JLabel ol = view.getLblOccurrences();
            ol.setVisible( false );

            //            // previous/...
            //
            //            view.getBtnPrevious().setEnabled( false );
            //            view.getBtnNext().setEnabled( false );
            //
            //            view.getBtnFirst().setEnabled( false );
            //            view.getBtnLast().setEnabled( false );
            GuiUtils.setRecursiveEnable( view, false );
        }
        else
        {
            // immediate search

            view.getCbImmediateSearch().setSelected( model.getImmediateSearch() );

            // overall enable

            boolean b = model.getEnable();
            view.setEnabled( b );
            view.getLblFind().setEnabled( b );
            view.getTxtSearch().setEnabled( b );
            view.getCbImmediateSearch().setEnabled( b );
            view.enablePanel( b );
            updateRunSearchState();

            JLabel ol = view.getLblOccurrences();
            ol.setEnabled( b );
            if ( b )
            {
                if ( !model.isSearchNotFound() )
                {
                    ol.setVisible( true );
                    ol.setForeground( Color.RED );
                    ol.setText( "Not found" );
                }
                else
                {
                    int n = model.getTotalSearchOccurrenceCount();
                    if ( n > 0 )
                    {
                        ol.setVisible( true );
                        ol.setForeground( Color.BLACK );
                        ol.setText( n + " occurrence(s)" );
                    }
                }
            }
            else
                ol.setText( null );

            // previous/...

            view.getBtnPrevious().setEnabled( b && model.getAllowGotoPrevious() );
            view.getBtnNext().setEnabled( b && model.getAllowGotoNext() );

            view.getBtnFirst().setEnabled( b && model.getAllowGotoFirst() );
            view.getBtnLast().setEnabled( b && model.getAllowGotoLast() );
        }
    }

    public void setPanelComponent( Component c )
    {
        view.setPanelComponent( c );
        updateUIState();
    }

    // MVCController interface

    private void updateUI()
    {
        view.revalidate();
        view.repaint();
    }

    private void modelToUI_edt()
    {
        //        view.getTxtSearch().setText( model.getSearchQuery() );
        GuiUtils.setTextField( view.getTxtSearch(), model.getSearchQuery() );

        updateUIState();
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

    @Override
    public SearchPanelView getView()
    {
        return view;
    }

    @Override
    public SearchPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( SearchPanelModel m )
    {
        unsubscribeModel();
        model = m;
        subscribeModel();

        modelToUI();
    }

    @Override
    public void close()
    {
        unsubscribeModel();
    }

    // MVCModelObserver interface

    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof SearchPanelModelChangeId )
            switch ( (SearchPanelModelChangeId)change.getChangeId() )
            {
                case SearchResultsChanged:
                case CurrentOccurrenceChanged:
                case CanGotoPreviousChanged:
                case CanGotoNextChanged:
                case CanGotoFirstChanged:
                case CanGotoLastChanged:
                case EnableSearchChanged:
                    updateUIState();
                    break;

                case ImmediateSearchChanged:
                case SearchableTextChanged:
                    model.runSearch( SearchPanelController.this );
                    break;

                default:
                    break;
            }
    }

    @Override
    public void modelChanged( final MVCModelChange change )
    {
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
}
