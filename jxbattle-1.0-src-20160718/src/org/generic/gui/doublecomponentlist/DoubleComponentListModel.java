package org.generic.gui.doublecomponentlist;

import javax.swing.JComponent;

import org.generic.gui.componentlist.JComponentListModel;
import org.generic.gui.componentlist.ViewIterator;
import org.generic.mvc.model.observer.MVCModelImpl;

//public class DoubleComponentListModel<M1 extends MVCModel, V1 extends JComponent, M2 extends MVCModel, V2 extends JComponent> extends MVCModelImpl
public class DoubleComponentListModel extends MVCModelImpl
{
    //    private JComponentListModel<M1, V1, MVCController<M1, V1>> componentListModel1;
    private JComponentListModel componentListModel1;

    //    private JComponentListModel<M2, V2, MVCController<M2, V2>> componentListModel2;
    private JComponentListModel componentListModel2;

    private boolean syncLinesHeight = true;

    //    public DoubleComponentListModel( JComponentListModel<M1, V1, MVCController<M1, V1>> clm1, JComponentListModel<M2, V2, MVCController<M2, V2>> clm2 )
    public DoubleComponentListModel( JComponentListModel clm1, JComponentListModel clm2 )
    {
        componentListModel1 = clm1;
        componentListModel2 = clm2;

        addRelatedModel( componentListModel1 );
        addRelatedModel( componentListModel2 );
    }

    //    public DoubleCompoListModel()
    //    {
    //        componentListModel1 = new JComponentListModel<M1, V1, MVCController<M1, V1>>();
    //        componentListModel2 = new JComponentListModel<M2, V2, MVCController<M2, V2>>();
    //    }

    //    public JComponentListModel<M1, V1, MVCController<M1, V1>> getComponentListModel1()
    public JComponentListModel getComponentListModel1()
    {
        return componentListModel1;
    }

    //    public JComponentListModel<M2, V2, MVCController<M2, V2>> getComponentListModel2()
    public JComponentListModel getComponentListModel2()
    {
        return componentListModel2;
    }

    boolean isSyncLinesHeight()
    {
        return syncLinesHeight;
    }

    private int getComponentMaxLineHeight( JComponentListModel<?, ?, ?> clm )
    {
        int v1max = Integer.MIN_VALUE;
        ViewIterator v1it = clm.getViewIterator();
        while ( v1it.hasNext() )
        {
            JComponent v1 = (JComponent)v1it.next();
            v1max = Math.max( v1max, v1.getSize().height );
        }

        return v1max;
    }

    public int getComponentsMaxLineHeight()
    {
        return Math.max( getComponentMaxLineHeight( componentListModel1 ), getComponentMaxLineHeight( componentListModel2 ) );
    }

    void close()
    {
        removeRelatedModel( componentListModel1 );
        removeRelatedModel( componentListModel2 );
    }
}
