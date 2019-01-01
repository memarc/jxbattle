package org.generic.mvc.model.list;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.generic.list.AbstractSyncList;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModel;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelChangeId;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;

public class SyncListModel<T> extends AbstractSyncList<T> implements MVCModel
{
    private MVCModelImpl modelImpl;

    private T current;

    private boolean autoCurrent;

    /**
     * comparator to use to keep list sorted
     */
    private Comparator<? super T> sortComparator;

    /**
     * also call addObserverToElements() if addObserver() called
     */
    private boolean autoSubscribeToElements = true;

    public SyncListModel()
    {
        modelImpl = new MVCModelImpl();
    }

    private void defaultSort()
    {
        if ( sortComparator != null )
            Collections.sort( this, sortComparator );
        //            Collections.sort( list, sortComparator );
    }

    private void completeAdd( Object sender, T e )
    {
        // if auto subscribe activated, add observers of the list to the observers of the added element
        if ( autoSubscribeToElements )
            for ( MVCModelObserver o : modelImpl.getObservers() )
                addElementObserver( e, o );

        notifyObservers( new MVCModelChange( sender, this, getAddElementChangeId(), e ) );

        if ( autoCurrent && current == null )
            setCurrent( sender, e );
    }

    protected boolean doAdd( Object sender, T e )
    {
        // add element
        add( e );

        // auto sort
        defaultSort();

        completeAdd( sender, e );

        return true;
    }

    /**
     * @return true if element was added
     */
    protected boolean doAddUnique( Object sender, T e )
    {
        // add element
        if ( addUnique( e ) )
        {
            // auto sort
            defaultSort();

            completeAdd( sender, e );

            return true;
        }

        return false;
    }

    protected boolean doRemove( Object sender, T e )
    {
        int ci = indexOf( e );

        boolean b = remove( e );

        //        if ( autoSubscribeToElements && e instanceof MVCModel )
        if ( b )
        {
            // if auto subscribe activated, remove observers of the list from the observers of the removed element
            if ( autoSubscribeToElements )
            {
                //                SyncIterator<MVCModelObserver> it = modelImpl.getObservers().iterator();
                //                while ( it.hasNext() )
                //                    removeElementObserver( e, it.next() );
                //                it.close();

                for ( MVCModelObserver o : modelImpl.getObservers() )
                    removeElementObserver( e, o );
            }

            notifyObservers( new MVCModelChange( sender, this, getRemoveElementChangeId(), e ) );

            if ( current == e )
            {
                T c = null;

                if ( autoCurrent )
                {
                    int s = size();

                    if ( s > 0 )
                    {
                        if ( ci == s - 1 )
                            ci--;
                        c = get( ci );
                    }
                }
                setCurrent( sender, c );
            }
        }

        return b;
    }

    public boolean add( Object sender, T e )
    {
        return doAdd( sender, e );
    }

    public boolean addUnique( Object sender, T e )
    {
        return doAddUnique( sender, e );
    }

    public void clear( Object sender )
    {
        notifyObservers( new MVCModelChange( sender, this, getPreListClearChangeId(), this ) );
        //        list.clear();
        super.clear();
        //        current = null;
        notifyObservers( new MVCModelChange( sender, this, getListClearedChangeId(), this ) );

        setCurrent( sender, null );
    }

    public boolean remove( Object sender, T e )
    {
        return doRemove( sender, e );
    }

    public boolean removeCurrent( Object sender )
    {
        if ( current != null )
            return doRemove( sender, current );
        return false;
    }

    /**
     * keep list sorted, use provided comparator 
     */
    public void keepSorted( Comparator<? super T> comp )
    {
        sortComparator = comp;
    }

    protected MVCModelChangeId getCurrentElementChangeId()
    {
        return ListModelChangeId.List_CurrentChanged;
    }

    protected MVCModelChangeId getAddElementChangeId()
    {
        return ListModelChangeId.List_AddElement;
    }

    protected MVCModelChangeId getRemoveElementChangeId()
    {
        return ListModelChangeId.List_RemoveElement;
    }

    protected MVCModelChangeId getListClearedChangeId()
    {
        return ListModelChangeId.List_ListCleared;
    }

    protected MVCModelChangeId getPreListClearChangeId()
    {
        return ListModelChangeId.List_PreListClear;
    }

    protected void addElementObserver( T element, MVCModelObserver obs )
    {
        if ( element instanceof MVCModel )
            ((MVCModel)element).addObserver( obs );
    }

    protected void removeElementObserver( T element, MVCModelObserver obs )
    {
        if ( element instanceof MVCModel )
            ((MVCModel)element).removeObserver( obs );
    }

    private void addObserverToElements( MVCModelObserver obs )
    {
        Iterator<T> it = iterator();
        try
        {
            while ( it.hasNext() )
                addElementObserver( it.next(), obs );
        }
        finally
        {
            //it.close();
        }
    }

    private void removeObserverToElements( MVCModelObserver obs )
    {
        Iterator<T> it = iterator();

        try
        {
            while ( it.hasNext() )
                removeElementObserver( it.next(), obs );
        }
        finally
        {
            //            it.close();
        }
    }

    public void setAutoCurrent( boolean b )
    {
        autoCurrent = b;
    }

    public T getCurrent()
    {
        return current;
    }

    public boolean setCurrent( Object sender, T c )
    {
        boolean changed = current != c;
        if ( changed )
        {
            if ( c != null && !super.contains( c ) )
                throw new MVCModelError( "invalid current element : not in list" );

            current = c;
            notifyObservers( new MVCModelChange( sender, this, getCurrentElementChangeId(), current ) );
        }
        return changed;
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        modelImpl.addObserver( obs );

        if ( autoSubscribeToElements )
            addObserverToElements( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        modelImpl.removeObserver( obs );

        if ( autoSubscribeToElements )
            removeObserverToElements( obs );
    }

    @Override
    public void addObservers( List<MVCModelObserver> observers )
    {
        modelImpl.addObservers( observers );
    }

    @Override
    public void removeObservers( List<MVCModelObserver> observers )
    {
        modelImpl.removeObservers( observers );
    }

    @Override
    public void removeAllObservers()
    {
        modelImpl.removeAllObservers();
    }

    @Override
    public void notifyObservers( MVCModelChange change )
    {
        modelImpl.notifyObservers( change );
    }

    @Override
    public boolean enableMVCLog( MVCModelChange change )
    {
        return false;
    }

    //    @Override
    //    public void moveObserversTo( MVCModel model )
    //    {
    //        modelImpl.moveObserversTo( model );
    //    }
}
