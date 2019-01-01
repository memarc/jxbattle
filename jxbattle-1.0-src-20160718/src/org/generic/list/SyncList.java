package org.generic.list;

import java.util.List;

public class SyncList<T> extends AbstractSyncList<T>
{
    public SyncList()
    {
    }

    @Override
    public SyncIterator<T> iterator()
    {
        return new SyncIterator<>( this );
    }

    //    public void addTuple( T e1, T e2 )
    //    {
    //        add( e1 );
    //        add( e2 );
    //    }

    public void addUnique( List<T> c )
    {
        for ( T t : c )
            addUnique( t );
    }
}
