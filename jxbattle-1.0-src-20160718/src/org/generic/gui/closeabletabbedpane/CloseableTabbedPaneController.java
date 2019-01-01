package org.generic.gui.closeabletabbedpane;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.generic.gui.closeabletabbedpane.CloseableTabbedPaneModel.TabComponentIterator;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class CloseableTabbedPaneController implements MVCController<CloseableTabbedPaneModel, CloseableTabbedPaneView>, MVCModelObserver, MouseListener, MouseMotionListener
{
    private CloseableTabbedPaneView view;

    private CloseableTabbedPaneModel model;

    private CloseableTabbedPaneUIModel uiModel;

    public CloseableTabbedPaneController( CloseableTabbedPaneView v )
    {
        view = v;
        init();
    }

    private void init()
    {
        view.addMouseMotionListener( this );
        view.addMouseListener( this );

        uiModel = new CloseableTabbedPaneUIModel( view );
        view.setUIModel( uiModel );
        createHandlers();
    }

    private void createHandlers()
    {
        view.addChangeListener( new ChangeListener()
        {
            @Override
            public void stateChanged( ChangeEvent e )
            {
                model.selectTab( CloseableTabbedPaneController.this, view.getSelectedIndex() );
            }
        } );
    }

    //        private boolean isTabCloseable( Component comp )
    //        {
    //            return closableTabs.contains( comp );
    //        }

    //private void addTab( Pair<String, Component> data )
    private void addTab( String title, Component component )
    {
        view.addTab( title + "  ", component );
    }

    private void removeTab( Component component )
    {
        view.remove( component );
    }

    private void selectTab( Component component )
    {
        view.setSelectedComponent( component );
    }

    protected boolean tabAboutToClose( int tabIndex )
    {
        return true;
    }

    // MouseMotionListener interface

    public void mouseMoved( MouseEvent me )
    {
        if ( uiModel.mouseMoved( me.getX(), me.getY() ) )
            view.repaint();
    }

    // MouseListener interface

    public void mouseEntered( MouseEvent me )
    {
    }

    public void mouseExited( MouseEvent me )
    {
        uiModel.mouseExited();
        view.repaint();
    }

    public void mousePressed( MouseEvent me )
    {
    }

    public void mouseClicked( MouseEvent me )
    {
    }

    public void mouseDragged( MouseEvent me )
    {
    }

    public void mouseReleased( MouseEvent me )
    {
        if ( me.getButton() == MouseEvent.BUTTON1 ) // LMB
        {
            int i = uiModel.getHoveredTabIndex();
            if ( tabAboutToClose( i ) )
                if ( uiModel.canClose() )
                    model.removeTab( this, i );
        }
        else if ( me.getButton() == MouseEvent.BUTTON2 ) // middle MB
        {
            int i = uiModel.getHoveredTabIndex();
            if ( model.isTabCloseable( i ) )
                model.removeTab( this, i );
        }
    }

    // MVCController interface

    private void updateUI()
    {
        view.revalidate();
        view.repaint();
    }

    private void modelToUI_edt()
    {
        view.removeAll();

        TabComponentIterator it = model.iterator();
        while ( it.hasNext() )
        {
            Component c = it.next();
            view.add( it.getTitle(), c );
        }

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
    public CloseableTabbedPaneView getView()
    {
        return view;
    }

    @Override
    public CloseableTabbedPaneModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( CloseableTabbedPaneModel m )
    {
        unsubscribeModel();
        model = m;
        uiModel.setComponentsModel( model );
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
        if ( change.getChangeId() instanceof CloseableTabbedPaneChangeId )
            switch ( (CloseableTabbedPaneChangeId)change.getChangeId() )
            {
                case ComponentAdded:
                    String title = (String)change.getDataArray().get( 0 );
                    Component component = (Component)change.getDataArray().get( 1 );
                    addTab( title, component );
                    break;

                case ComponentRemoved:
                    removeTab( (Component)change.getData() );
                    break;

                case ComponentSelected:
                    if ( change.getSender() != this )
                        selectTab( (Component)change.getData() );
                    break;

                default:
                    break;
            }
    }

    @Override
    public void modelChanged( final MVCModelChange change )
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
