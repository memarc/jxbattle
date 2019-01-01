package org.generic.gui.dropdownbutton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.list.ListModelChangeId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class DropDownButtonController implements MVCController<DropDownButtonModel, DropDownButtonView>, MVCModelObserver, ActionListener
{
    private DropDownButtonView view;

    private DropDownButtonModel model;

    private JPopupMenu popupMenu;

    public DropDownButtonController( DropDownButtonView v )
    {
        view = v;
        init();
    }

    private void init()
    {
        view.setText( " " );
        createHandlers();
    }

    private void createHandlers()
    {
        view.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                getPopupMenu().show( view, 1, DropDownButtonView.ArrowDim - 1 );
            }
        } );
    }

    private void setButtonText( String text )
    {
        view.setText( text + "     " );
    }

    // ActionListener interface

    @Override
    public void actionPerformed( ActionEvent e )
    {
        model.notifyItemSelection( DropDownButtonController.this, e.getActionCommand() );
    }

    private JPopupMenu getPopupMenu()
    {
        if ( popupMenu == null )
        {
            popupMenu = new JPopupMenu();
            for ( String item : model )
            {
                JMenuItem mi = new JMenuItem( item );
                mi.addActionListener( DropDownButtonController.this );
                popupMenu.add( mi );
            }
        }

        return popupMenu;
    }

    public void setPopupMenu( JPopupMenu pm )
    {
        popupMenu = pm;
    }

    private void updateUI()
    {
        view.revalidate();
        view.repaint();
    }

    private void modelToUI_edt()
    {
        setButtonText( model.getButtonText() );

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

    // MVCController interface

    @Override
    public DropDownButtonView getView()
    {
        return view;
    }

    @Override
    public DropDownButtonModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( DropDownButtonModel m )
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
        if ( change.getChangeId() instanceof ListModelChangeId )
            switch ( (ListModelChangeId)change.getChangeId() )
            {
                case List_AddElement:
                case List_RemoveElement:
                case List_ListCleared:
                    popupMenu = null;
                    break;

                default:
                    break;
            }
    }

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
}
