package org.generic.list;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractSyncList<T> extends CopyOnWriteArrayList<T>
{
    //    protected CopyOnWriteArrayList<T> list;
    //
    //    public AbstractSyncList()
    //    {
    //        list = new CopyOnWriteArrayList<T>();
    //    }

    public boolean addUnique( T e )
    {
        return addIfAbsent( e );
    }

    Iterator<T> unsyncIterator()
    {
        return super.iterator();
    }

    @Override
    public SyncIterator<T> iterator()
    {
        return new SyncIterator<>( this );
    }

    // Object interface

    @Override
    public boolean equals( Object obj )
    {
        return this == obj;
    }
}
