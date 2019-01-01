package org.generic.gui.doublecomponentlist;

import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.generic.gui.componentlist.JComponentListController;
import org.generic.gui.componentlist.JComponentListModel;
import org.generic.gui.componentlist.JComponentListView;
import org.generic.gui.componentlist.ViewIterator;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.list.ListModelChangeId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

//public class DoubleComponentListController<M1 extends MVCModel, V1 extends JComponent, M2 extends MVCModel, V2 extends JComponent> implements MVCController<DoubleComponentListModel<M1, V1, M2, V2>, JComponent>, MVCModelObserver
public class DoubleComponentListController implements MVCController<DoubleComponentListModel, JComponent>, MVCModelObserver
{
    //    private DoubleComponentListModel<M1, V1, M2, V2> model;
    private DoubleComponentListModel model;

    //    private JComponentListController<M1, V1, MVCController<M1, V1>> componentListController1;
    private JComponentListController componentListController1;

    //    private JComponentListController<M2, V2, MVCController<M2, V2>> componentListController2;
    private JComponentListController componentListController2;

    private JComponentListView view1;

    private JComponentListView view2;

    private boolean scrollbarUpdate = false;

    public DoubleComponentListController( JComponentListView v1, JComponentListView v2 )
    {
        view1 = v1;
        view2 = v2;
        init();
    }

    private void init()
    {
        //        view = new DoubleComponentListView();
        //        componentListController1 = new JComponentListController<>( view.getComponentListView1() );
        //        componentListController2 = new JComponentListController<>( view.getComponentListView2() );
        componentListController1 = new JComponentListController( view1 );
        componentListController2 = new JComponentListController( view2 );
        createHandlers();
    }

    private void createHandlers()
    {
        componentListController2.getView().addComponentListener( new ComponentAdapter()
        {
            //            @Override
            //            public void componentShown( ComponentEvent e )
            //            {
            //            }

            @Override
            public void componentResized( ComponentEvent e )
            {
                updateHeights();
            }
        } );

        componentListController1.getView().getScrollPane().getVerticalScrollBar().addAdjustmentListener( new AdjustmentListener()
        {
            @Override
            public void adjustmentValueChanged( AdjustmentEvent e )
            {
                if ( model.isSyncLinesHeight() )
                {
                    BoundedRangeModel brm2 = componentListController2.getView().getScrollPane().getVerticalScrollBar().getModel();
                    brm2.setValue( e.getValue() );
                }
                else
                {
                    if ( !scrollbarUpdate )
                    {
                        scrollbarUpdate = true;
                        //                        System.out.println( "\nsync1\n" + System.currentTimeMillis() );
                        synchroniseScrollbars( componentListController1.getView().getScrollPane(), componentListController2.getView().getScrollPane(), e.getValue() );
                        scrollbarUpdate = false;
                    }
                }
            }
        } );

        componentListController2.getView().getScrollPane().getVerticalScrollBar().addAdjustmentListener( new AdjustmentListener()
        {
            @Override
            public void adjustmentValueChanged( AdjustmentEvent e )
            {
                if ( model.isSyncLinesHeight() )
                {
                    BoundedRangeModel brm1 = componentListController1.getView().getScrollPane().getVerticalScrollBar().getModel();
                    brm1.setValue( e.getValue() );
                }
                else
                {
                    if ( !scrollbarUpdate )
                    {
                        scrollbarUpdate = true;
                        //                        System.out.println( "\nsync2\n" + System.currentTimeMillis() );
                        synchroniseScrollbars( componentListController2.getView().getScrollPane(), componentListController1.getView().getScrollPane(), e.getValue() );
                        scrollbarUpdate = false;
                    }
                }
            }
        } );
    }

    private void synchroniseScrollbars( JScrollPane source, JScrollPane dest, int val )
    {
        BoundedRangeModel brm1 = source.getVerticalScrollBar().getModel();
        //        System.out.println( "source " + brm1.toString() );
        int maxVal1 = brm1.getMaximum() - brm1.getExtent();
        float p = (float)val / maxVal1;
        //        System.out.println( p );

        BoundedRangeModel brm2 = dest.getVerticalScrollBar().getModel();
        //        System.out.println( "dest1 " + brm2.toString() );
        int maxVal2 = brm2.getMaximum() - brm2.getExtent();
        brm2.setValue( (int)(p * maxVal2) );
        //        System.out.println( "dest2 " + brm2.toString() );
    }

    //    public JComponentListController<M1, V1, MVCController<M1, V1>> getComponentListController1()
    //    {
    //        return componentListController1;
    //    }
    //
    //    public JComponentListController<M2, V2, MVCController<M2, V2>> getComponentListController2()
    //    {
    //        return componentListController2;
    //    }

    // MVCController interface

    private void updateUI()
    {
        //        view.revalidate();
        //        view.repaint();
        view1.revalidate();
        view2.revalidate();
        view1.repaint();
        view2.repaint();
    }

    private void updateHeights()
    {
        if ( SwingUtilities.isEventDispatchThread() )
            updateHeights_edt();
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    updateHeights_edt();
                }
            } );
    }

    private void updateCompHeights( JComponentListModel clm, int max )
    {
        ViewIterator v1it = clm.getViewIterator();
        while ( v1it.hasNext() )
        {
            JComponent v1 = (JComponent)v1it.next();
            Dimension d = v1.getSize();
            Dimension p = v1.getPreferredSize();
            //            Dimension m = v1.getMaximumSize();
            d.height = max;
            p.height = max;
            //            m.height = max;
            v1.setSize( d );
            v1.setPreferredSize( p );
            //            v1.setMaximumSize( m );
        }
    }

    private void updateHeights_edt()
    {
        if ( model.isSyncLinesHeight() )
        {
            int max = model.getComponentsMaxLineHeight();
            //            System.out.println( max );
            if ( max > 0 )
            {
                updateCompHeights( model.getComponentListModel1(), max );
                updateCompHeights( model.getComponentListModel2(), max );
            }
        }

        updateUI();
    }

    private void modelToUI_edt()
    {
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
    public JComponent getView()
    {
        //        return view;
        return null;
    }

    @Override
    //    public DoubleComponentListModel<M1, V1, M2, V2> getModel()
    public DoubleComponentListModel getModel()
    {
        return model;
    }

    @Override
    //    public void setModel( DoubleComponentListModel<M1, V1, M2, V2> m )
    public void setModel( DoubleComponentListModel m )
    {
        unsubscribeModel();
        model = m;
        componentListController1.setModel( model.getComponentListModel1() );
        componentListController2.setModel( model.getComponentListModel2() );
        subscribeModel();

        modelToUI();
    }

    @Override
    public void close()
    {
        model.close();
        unsubscribeModel();
    }

    // MVCModelObserver interface

    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof ListModelChangeId )
            switch ( (ListModelChangeId)change.getChangeId() )
            {
                case List_ListCleared:
                case List_AddElement:
                case List_RemoveElement:
                    updateHeights();
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
