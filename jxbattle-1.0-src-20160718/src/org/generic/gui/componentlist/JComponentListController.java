package org.generic.gui.componentlist;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.generic.gui.GuiUtils;
import org.generic.gui.MouseInfo;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.list.ListModelChangeId;
import org.generic.mvc.model.observer.MVCModel;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class JComponentListController<M extends MVCModel, V extends JComponent, C extends MVCController<M, V>> implements MVCController<JComponentListModel<M, V, C>, JComponentListView>, MVCModelObserver
{
    private JComponentListView view;

    private JComponentListModel<M, V, C> model;

    public JComponentListController( JComponentListView v )
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
        view.getListPanel().addMouseListener( new MouseAdapter()
        {
            @SuppressWarnings("unchecked")
            @Override
            public void mouseClicked( MouseEvent e )
            {
                //                System.out.println( "SequencePanelView.getListPanel().mouseClicked() x " + e.getX() + " y " + e.getY() );
                //                System.out.println( "JComponentListController mouseClicked" );

                if ( model != null )
                {
                    V v = (V)view.getListPanel().getComponentAt( e.getX(), e.getY() );
                    //                    V v = (V)view.getListPanel().getComponentAt( e.getXOnScreen(), e.getYOnScreen() );
                    if ( v == view || v == view.getListPanel() )
                        v = null;
                    if ( e.getClickCount() == 1 )
                    {
                        MouseInfo mi = null;
                        if ( v != null )
                        {
                            int x = e.getX() - v.getX();
                            int y = e.getY() - v.getY();
                            mi = new MouseInfo();
                            mi.setPixelPosition( x, y );
                            mi.setLeftButton( e.getButton() == MouseEvent.BUTTON1 );
                            mi.setRightButton( e.getButton() == MouseEvent.BUTTON3 );
                        }
                        model.notifySimpleClick( JComponentListController.this, v, mi );
                    }
                    else
                        model.notifyDoubleClick( JComponentListController.this, v );
                }
            }
        } );

        view.addComponentListener( new ComponentAdapter()
        {
            @Override
            public void componentResized( ComponentEvent e )
            {
                model.notifyResize( JComponentListController.this, view.getSize() );
            }
        } );

        //        view.addKeyListener( new KeyListener()
        //        {
        //            @Override
        //            public void keyTyped( KeyEvent e )
        //            {
        //                System.out.println( "JComponentListController.keyTyped" );
        //            }
        //
        //            @Override
        //            public void keyPressed( KeyEvent e )
        //            {
        //                System.out.println( "JComponentListController.keyPressed" );
        //            }
        //
        //            @Override
        //            public void keyReleased( KeyEvent e )
        //            {
        //                System.out.println( "JComponentListController.keyReleased" );
        //            }
        //        } );

        //        view.getListPanel().addMouseWheelListener( new MouseWheelListener()
        //        {
        //            @Override
        //            public void mouseWheelMoved( MouseWheelEvent e )
        //            {
        //                System.out.println( e );
        //            }
        //        } );
    }

    //    private void updateViewLayout()
    //    {
    //        StringBuilder sb = new StringBuilder();
    //
    //        int n = model.size();
    //        if ( n > 0 )
    //        {
    //            sb.append( '0' );
    //            for ( int i = 0; i < n; i++ )
    //            {
    //                sb.append( '[' );
    //                sb.append( ']' );
    //                sb.append( '0' );
    //            }
    //            MigLayout ml = (MigLayout)view.getListPanel().getLayout();
    //            ml.setRowConstraints( sb.toString() );
    //        }
    //    }

    private void addComponent( C comp )
    {
        int i = model.indexOf( comp );
        //        view.getListPanel().add( comp.getView(), "cell 0 " + String.valueOf( model.size() ) );
        view.getListPanel().add( comp.getView(), "cell 0 " + String.valueOf( i ) );
        //controllers.add( comp );

        //   updateViewLayout();

        updateUI();
    }

    private void removeComponent( C comp )
    {
        view.getListPanel().remove( comp.getView() );
        model.setCurrent( JComponentListController.this, null );
        updateUI();
    }

    private void closeComponents()
    {
        if ( model != null )
        {
            //            SyncIterator<C> it = model.iterator();
            //            try
            //            {
            //                while ( it.hasNext() )
            //                    it.next().close();
            //            }
            //            finally
            //            {
            //                it.close();
            //            }

            for ( C c : model )
                c.close();
        }
    }

    private void clearComponents()
    {
        view.getListPanel().removeAll();
        updateUI();
    }

    private void highlightSelectedComponent()
    {
        ViewIterator<V> it = model.getViewIterator();
        while ( it.hasNext() )
            it.next().setBorder( null );

        if ( model.getBorderOnCurrent() )
            if ( model.getCurrent() != null )
                ((JComponent)model.getCurrent().getView()).setBorder( new LineBorder( Color.BLACK ) );

        updateUI();
    }

    private void setHorizontalUnitScroll( int v )
    {
        view.getScrollPane().getHorizontalScrollBar().setUnitIncrement( v );
    }

    private void setHorizontalBlockScroll( int v )
    {
        view.getScrollPane().getHorizontalScrollBar().setBlockIncrement( v );
    }

    private void setVerticalUnitScroll( int v )
    {
        view.getScrollPane().getVerticalScrollBar().setUnitIncrement( v );
    }

    private void setVerticalBlockScroll( int v )
    {
        view.getScrollPane().getVerticalScrollBar().setBlockIncrement( v );
    }

    public void scrollRectToVisible( Rectangle vr, boolean center )
    {
        //        view.getScrollPane().getViewport().scrollRectToVisible( vr );
        //view.getListPanel().scrollRectToVisible( vr );
        GuiUtils.scrollToRectangle( view.getListPanel(), vr, center );
        updateUI();
    }

    // MVCController interface

    private void updateUI()
    {
        //        ViewIterator<V> it = model.getViewIterator();
        //        while ( it.hasNext() )
        //        {
        //            V v = it.next();
        //            //            Dimension d = v.getPreferredSize();
        //            //            v.paintImmediately( new Rectangle( 0, 0, d.width, d.height ) );
        //            //v.invalidate();
        //            v.validate();
        //            v.repaint();
        //        }

        //view.invalidate();
        //        view.getScrollPane().validate();
        //        view.getListPanel().validate();
        //        view.validate();

        view.revalidate();
        view.repaint();
    }

    private void updateBackgroundColor()
    {
        view.getListPanel().setBackground( model.getBackgroundColor() );
    }

    private void modelToUI_edt()
    {
        clearComponents();
        for ( C c : model )
            addComponent( c );

        setHorizontalUnitScroll( model.getHorizontalUnitScroll() );
        setHorizontalBlockScroll( model.getHorizontalBlockScroll() );
        setVerticalUnitScroll( model.getVerticalUnitScroll() );
        setVerticalBlockScroll( model.getVerticalBlockScroll() );
        updateBackgroundColor();

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
    public JComponentListView getView()
    {
        return view;
    }

    @Override
    public JComponentListModel<M, V, C> getModel()
    {
        return model;
    }

    @Override
    public void setModel( JComponentListModel<M, V, C> m )
    {
        unsubscribeModel();
        model = m;
        subscribeModel();

        modelToUI();
    }

    // MVCModelObserver interface

    @SuppressWarnings("unchecked")
    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof ListModelChangeId && model.isControllerListModel( change.getSourceModel() ) )
            switch ( (ListModelChangeId)change.getChangeId() )
            {
                case List_AddElement:
                    addComponent( (C)change.getData() );
                    break;

                case List_RemoveElement:
                    removeComponent( (C)change.getData() );
                    break;

                case List_PreListClear:
                    closeComponents();
                    break;

                case List_ListCleared:
                    clearComponents();
                    break;

                default:
                    break;
            }
        else if ( change.getChangeId() instanceof ComponentListChangeId )
            switch ( (ComponentListChangeId)change.getChangeId() )
            {
                case CurrentComponentChanged:
                    highlightSelectedComponent();
                    break;

                case BorderOnCurrentChanged:
                    highlightSelectedComponent();
                    break;

                case HorizontalUnitScrollChanged:
                    setHorizontalUnitScroll( (Integer)change.getData() );
                    break;

                case HorizontalBlockScrollChanged:
                    setHorizontalBlockScroll( (Integer)change.getData() );
                    break;

                case VerticalUnitScrollChanged:
                    setVerticalUnitScroll( (Integer)change.getData() );
                    break;

                case VerticalBlockScrollChanged:
                    setVerticalBlockScroll( (Integer)change.getData() );
                    break;

                case BackgroundColorChanged:
                    updateBackgroundColor();
                    break;

                default:
                    break;
            }
    }

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        processModelChange( change );
        //        SwingUtilities.invokeLater( new Runnable()
        //        {
        //            @Override
        //            public void run()
        //            {
        //                processModelChange( change );
        //            }
        //        } );
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

    @Override
    public void close()
    {
        closeComponents();
        unsubscribeModel();
    }
}
