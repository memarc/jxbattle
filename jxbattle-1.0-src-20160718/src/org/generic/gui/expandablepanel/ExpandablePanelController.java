package org.generic.gui.expandablepanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class ExpandablePanelController implements MVCController<ExpandablePanelModel, ExpandablePanelView>, MVCModelObserver
{
    private ExpandablePanelView view;

    private ExpandablePanelModel model;

    //    private Dimension viewPrefSize;

    public ExpandablePanelController( ExpandablePanelView v )
    {
        view = v;
        //        viewPrefSize = new Dimension( 200, 40 );
        init();
    }

    private void init()
    {
        //        view.setController( this );
        //view.setViewPreferredSize( viewPrefSize );
        //        updateDim();
        createHandlers();
    }

    private void createHandlers()
    {
        view.getBtnToggle().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                model.setExpanded( ExpandablePanelController.this, !model.isExpanded() );
            }
        } );
    }

    private void setButtonText()
    {
        view.getBtnToggle().setText( (model.isExpanded() ? "[-] " : "[+] ") + model.getButtonText() );
    }

    //    private void updateDim()
    //    {
    //        //        view.getPanel().invalidate();
    //        //        view.getPanel().revalidate();
    //        //        view.validate();
    //
    //        int ph = 0;
    //        if ( model != null )
    //            ph += model.isExpanded() ? view.getPanel().getHeight() : 0;
    //        else
    //            ph += view.getPanel().getHeight();
    //        System.out.println( view.getPanel().getHeight() );
    //
    //        //Dimension ps = new Dimension( 200, 40 + ph );
    //        viewPrefSize.height = 40 + ph;
    //
    //        //        view.setMinimumSize( ps );
    //        //        view.setPreferredSize( ps );
    //        //        view.setSize( ps );
    //
    //        if ( model != null )
    //            System.out.println( model.isExpanded() );
    //        System.out.println( viewPrefSize );
    //        System.out.println( view.getSize() );
    //    }

    private Dimension computeViewSize()
    {
        Dimension res = new Dimension();
        res.width = Math.max( view.getBtnToggle().getWidth(), view.getPanel().getWidth() + 10 );
        res.height = model == null ? 1 : 42 + (model.isExpanded() ? view.getPanel().getHeight() : 0);
        //System.out.println( "getViewSize " + res );
        return res;
    }

    private void updateViewSize()
    {
        Dimension ps = computeViewSize();
        if ( !view.getSize().equals( ps ) )
        {
            view.setMinimumSize( ps );
            view.setPreferredSize( ps );
            view.setSize( ps );
        }
    }

    private void expandView()
    {
        view.getPanel().setVisible( model.isExpanded() );

        //        updateDim();
        if ( view.getParent() != null )
            view.getParent().invalidate();
        //        view.getPanel().invalidate();
        //        view.invalidate();

        updateUI();
    }

    private void updateUI()
    {
        //        view.getPanel().invalidate();
        view.revalidate();
        view.repaint();
    }

    private void modelToUI_edt()
    {
        setButtonText();
        expandView();
        updateViewSize();

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

    //    Dimension getViewSize()
    //    {
    //        Dimension dim = new Dimension();
    //        dim.width = 2000;
    //        dim.height = 40 + (model.isExpanded() ? view.getPanel().getHeight() : 0);
    //        System.out.println( "getViewSize " + dim );
    //        //if ( view.getParent() != null )
    //        view.revalidate();
    //        return dim;
    //    }

    //    int getViewHeight()
    //    {
    //        return 40 + (model.isExpanded() ? view.getPanel().getHeight() : 0);
    //    }

    // MVCController interface

    @Override
    public ExpandablePanelView getView()
    {
        return view;
    }

    @Override
    public ExpandablePanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( ExpandablePanelModel m )
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
        if ( change.getChangeId() instanceof ExpandablePanelModelChangeId )
            switch ( (ExpandablePanelModelChangeId)change.getChangeId() )
            {
                case ExpansionChanged:
                    setButtonText();
                    expandView();
                    updateViewSize();
                    break;

                case ButtonTextChanged:
                    setButtonText();
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
