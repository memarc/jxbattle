package org.generic.list;

import java.util.Iterator;

public class SyncIterator<T> implements Iterator<T>
{
    protected AbstractSyncList<T> list;

    private Iterator<T> iterator;

    private T lastRet;

    private int lastRetIndex = -1;

    protected int nextIndex = 0; // next iterated object index

    private boolean first = false;

    //private String creatorThreadStacktrace; // debug

    public SyncIterator( AbstractSyncList<T> l )
    {
        list = l;
        iterator = l.unsyncIterator();
        //creatorThreadStacktrace = LogUtils.stackTraceToString( Thread.currentThread() );
    }

    @Override
    public boolean hasNext()
    {
        while ( iterator.hasNext() && nextIndex < list.size() && !isElementIterable( nextIndex ) )
        {
            nextIndex++;
            iterator.next();
        }

        return iterator.hasNext();
    }

    /**
     * @return true if list element is iterable
     */
    protected boolean isElementIterable( int i )
    {
        return true;
    }

    @Override
    public T next()
    {
        nextIndex++;
        lastRetIndex++;
        first = lastRetIndex == 0;
        lastRet = iterator.next();
        return lastRet;
    }

    public T lastReturned()
    {
        return lastRet;
    }

    /**
     * @return index of last returned element
     */
    public int index()
    {
        return lastRetIndex;
    }

    /**
     * @return true only after first call to next() 
     */
    public boolean isFirst()
    {
        return first;
    }

    @Override
    public void remove()
    {
        list.remove( lastRet );
    }
}
