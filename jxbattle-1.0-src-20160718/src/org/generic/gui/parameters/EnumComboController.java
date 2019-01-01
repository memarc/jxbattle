package org.generic.gui.parameters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import org.generic.EnumValue;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.mvc.model.parameter.EnumParameterModel;

public class EnumComboController<T extends EnumValue> implements MVCController<EnumParameterModel<T>, JComboBox<T>>, MVCModelObserver
{
    private JComboBox<T> view;

    private EnumParameterModel<T> model;

    public EnumComboController( JComboBox<T> v )
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
        createHandlers();
    }

    private void createHandlers()
    {
        view.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                model.setValue( EnumComboController.this, (T)view.getSelectedItem() );
            }
        } );
    }

    private void updateUI()
    {
        view.revalidate();
        view.updateUI();
    }

    private void modelToUI_edt()
    {
        for ( EnumValue tgp : model.getEnumParameter().getValue().getValues() )
            view.addItem( (T)tgp );

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

    // MVCController

    @Override
    public JComboBox<T> getView()
    {
        return view;
    }

    @Override
    public EnumParameterModel<T> getModel()
    {
        return model;
    }

    @Override
    public void setModel( EnumParameterModel<T> m )
    {
        unsubscribeModel();
        model = m;
        subscribeModel();

        modelToUI();
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

    // ModelObserver

    //    @SuppressWarnings("unused")
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

}
