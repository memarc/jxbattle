package org.generic.gui.tristatebutton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class TristateButtonController implements MVCController<TristateButtonModel, JButton>, MVCModelObserver
{
    private JButton view;

    private TristateButtonModel model;

    public TristateButtonController( JButton v )
    {
        view = v;
        createHandlers();
    }

    private void createHandlers()
    {
        view.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                model.nextState( TristateButtonController.this );
            }
        } );
    }

    private void updateUI()
    {
        view.revalidate();
        view.repaint();
    }

    private void modelToUI_edt()
    {
        view.setText( model.getButtonText() );
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

    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof TristateButtonModelChangeId )
            switch ( (TristateButtonModelChangeId)change.getChangeId() )
            {
                case TristateButtonStateChanged:
                    modelToUI_edt();
                    break;
            }
    }

    // MVCController interface

    @Override
    public JButton getView()
    {
        return view;
    }

    @Override
    public TristateButtonModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( TristateButtonModel m )
    {
        unsubscribeModel();
        model = m;
        subscribeModel();

        modelToUI();
    }

    @Override
    public void close()
    {
        view = null;
        unsubscribeModel();
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
        model.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        if ( model != null )
            model.removeObserver( this );
    }
}
