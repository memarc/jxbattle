package org.generic.gui.componentlist;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;

import org.generic.gui.GuiUtils;
import org.generic.gui.MouseInfo;
import org.generic.list.SyncIterator;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.list.SyncListModel;
import org.generic.mvc.model.observer.MVCModel;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;

//public class JComponentListModel<M extends MVCModel, V extends JComponent, C extends MVCController<M, V>> extends SyncListModel<C>
public class JComponentListModel<M extends MVCModel, V extends JComponent, C extends MVCController<M, V>> extends MVCModelImpl implements Iterable<C>
{
    //    private SyncListModel<MVCController<M, V>> controllers;
    private SyncListModel<C> controllers;

    private C current;

    private int horizontalUnitScroll;

    private int horizontalBlockScroll;

    private int verticalUnitScroll;

    private int verticalBlockScroll;

    /**
     * display border on currently selected component
     */
    private boolean borderOnCurrent;

    private Color backgroundColor;

    private Dimension currentSize;

    public JComponentListModel()
    {
        controllers = new SyncListModel<C>()
        {
            @Override
            protected void addElementObserver( C element, MVCModelObserver obs )
            {
                element.getModel().addObserver( obs );
            }

            @Override
            protected void removeElementObserver( C element, MVCModelObserver obs )
            {
                element.getModel().removeObserver( obs );
            }
        };

        borderOnCurrent = true;
        verticalUnitScroll = 1;
        verticalBlockScroll = 10;
        backgroundColor = GuiUtils.defaultSwingBackgroundColor;
        currentSize = new Dimension();

        addRelatedModel( controllers );
    }

    private C getControllerFromView( V v )
    {
        for ( C res : this )
            if ( res.getView() == v )
                return res;
        return null;
    }

    public V getViewFromModel( M m )
    {
        for ( C res : this )
            if ( res.getModel() == m )
                return res.getView();
        return null;
    }

    public boolean isCurrent( C c )
    {
        return current == c;
    }

    public C getCurrent()
    {
        return current;
    }

    void setCurrent( V v )
    {
        setCurrent( this, getControllerFromView( v ) );
    }

    void notifySimpleClick( Object sender, V v, MouseInfo mi )
    {
        MVCModelChange change = new MVCModelChange( sender, this, ComponentListChangeId.ComponentSimpleClicked );
        change.addData( getControllerFromView( v ) );
        change.addData( mi );
        notifyObservers( change );
    }

    void notifyDoubleClick( Object sender, V v )
    {
        notifyObservers( new MVCModelChange( sender, this, ComponentListChangeId.ComponentDoubleClicked, getControllerFromView( v ) ) );
    }

    void notifyResize( Object sender, Dimension dim )
    {
        currentSize.setSize( dim );
        notifyObservers( new MVCModelChange( sender, this, ComponentListChangeId.Resize, currentSize ) );
    }

    public Dimension getCurrentSize()
    {
        return currentSize;
    }

    public void setCurrent( Object sender, C curr )
    {
        boolean change = current != curr;
        if ( change )
        {
            //            notifyObservers( new MVCModelChange( sender, this, ComponentListChangeId.PreComponentChange, current ) );
            current = curr;
            notifyObservers( new MVCModelChange( sender, this, ComponentListChangeId.CurrentComponentChanged, current ) );
        }
    }

    public void setCurrent( Object sender, int index )
    {
        //        setCurrent( sender, super.get( index ) );
        setCurrent( sender, controllers.get( index ) );
    }

    //    public SyncIterator<C> getControllerIterator()
    //    {
    //        return iterator();
    //    }

    public void add( Object sender, C c )
    {
        controllers.add( sender, c );
    }

    public void remove( Object sender, C c )
    {
        controllers.remove( sender, c );
    }

    public int indexOf( C c )
    {
        return controllers.indexOf( c );
    }

    @Override
    public SyncIterator<C> iterator()
    {
        return controllers.iterator();
    }

    public int size()
    {
        return controllers.size();
    }

    public void clear( Object sender )
    {
        SyncIterator<C> it = iterator();
        while ( it.hasNext() )
        {
            C apc = it.next();
            apc.close();
        }

        controllers.clear( sender );
    }

    public boolean getBorderOnCurrent()
    {
        return borderOnCurrent;
    }

    public void setBorderOnCurrent( Object sender, boolean b )
    {
        borderOnCurrent = b;
        notifyObservers( new MVCModelChange( sender, this, ComponentListChangeId.BorderOnCurrentChanged, Boolean.valueOf( borderOnCurrent ) ) );
    }

    Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor( Object sender, Color c )
    {
        if ( backgroundColor != c )
        {
            backgroundColor = c;
            notifyObservers( new MVCModelChange( sender, this, ComponentListChangeId.BackgroundColorChanged ) );
        }
    }

    //    public void removeElementFromModel( M m )
    //    {
    //        ModelIterator<M> it = getModelIterator();
    //        while ( it.hasNext() )
    //        {
    //            M e = it.next();
    //            if ( e == m )
    //            {
    //                it.remove();
    //                break;
    //            }
    //        }
    //    }

    int getHorizontalUnitScroll()
    {
        return horizontalUnitScroll;
    }

    public void setHorizontalUnitScroll( Object sender, int v )
    {
        boolean change = horizontalUnitScroll != v;
        if ( change )
        {
            horizontalUnitScroll = v;
            notifyObservers( new MVCModelChange( sender, this, ComponentListChangeId.HorizontalUnitScrollChanged, horizontalUnitScroll ) );
        }
    }

    int getHorizontalBlockScroll()
    {
        return horizontalBlockScroll;
    }

    public void setHorizontalBlockScroll( Object sender, int v )
    {
        boolean change = horizontalBlockScroll != v;
        if ( change )
        {
            horizontalBlockScroll = v;
            notifyObservers( new MVCModelChange( sender, this, ComponentListChangeId.HorizontalBlockScrollChanged, horizontalBlockScroll ) );
        }
    }

    int getVerticalUnitScroll()
    {
        return verticalUnitScroll;
    }

    public void setVerticalUnitScroll( Object sender, int v )
    {
        boolean change = verticalUnitScroll != v;
        if ( change )
        {
            verticalUnitScroll = v;
            notifyObservers( new MVCModelChange( sender, this, ComponentListChangeId.VerticalUnitScrollChanged, verticalUnitScroll ) );
        }
    }

    int getVerticalBlockScroll()
    {
        return verticalBlockScroll;
    }

    public void setVerticalBlockScroll( Object sender, int v )
    {
        boolean change = verticalBlockScroll != v;
        if ( change )
        {
            verticalBlockScroll = v;
            notifyObservers( new MVCModelChange( sender, this, ComponentListChangeId.VerticalBlockScrollChanged, verticalBlockScroll ) );
        }
    }

    //    @Override
    //    protected void addElementObserver( C element, MVCModelObserver obs )
    //    {
    //        element.getModel().addObserver( obs );
    //    }
    //
    //    @Override
    //    protected void removeElementObserver( C element, MVCModelObserver obs )
    //    {
    //        element.getModel().removeObserver( obs );
    //    }

    @SuppressWarnings("unchecked")
    public ModelIterator<M> getModelIterator()
    {
        //        return new ModelIterator<M>( (SyncListModel<MVCController>)this );
        return new ModelIterator( controllers );
    }

    //    public class ModelIterator<M2 extends MVCModel> implements Iterator<M>
    //    {
    //        private SyncIterator<C> it;
    //
    //        public ModelIterator( SyncListModel<C> controllers )
    //        {
    //            it = controllers.iterator();
    //        }
    //
    //        public int index()
    //        {
    //            return it.index();
    //        }
    //
    //        @SuppressWarnings("unchecked")
    //        @Override
    //        public M next()
    //        {
    //            return (M)it.next().getModel();
    //        }
    //
    //        @Override
    //        public boolean hasNext()
    //        {
    //            return it.hasNext();
    //        }
    //
    //        @Override
    //        public void remove()
    //        {
    //            it.remove();
    //        }
    //    }

    @SuppressWarnings("unchecked")
    public ViewIterator<V> getViewIterator()
    {
        //        return new ViewIterator<V>( (SyncListModel<MVCController>)controllers );
        return new ViewIterator( controllers );
    }

    boolean isControllerListModel( MVCModel m )
    {
        return controllers == m;
    }

    //    public class ViewIterator implements Iterator<V>
    //    {
    //        private SyncIterator<C> it;
    //
    //        public ViewIterator( SyncListModel<C> ctrls )
    //        {
    //            it = ctrls.iterator();
    //        }
    //
    //        @SuppressWarnings("unchecked")
    //        @Override
    //        public V next()
    //        {
    //            return (V)it.next().getView();
    //        }
    //
    //        @Override
    //        public boolean hasNext()
    //        {
    //            return it.hasNext();
    //        }
    //
    //        @Override
    //        public void remove()
    //        {
    //            it.remove();
    //        }
    //    }
}
