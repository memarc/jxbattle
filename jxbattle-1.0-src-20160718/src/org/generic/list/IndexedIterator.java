package org.generic.list;

import java.util.Iterator;
import java.util.List;

//public class IndexedIterator<L extends List<T>, T> implements Iterator<T>
public class IndexedIterator<T> implements Iterator<T>
{
    //protected L list;

    private int lastRetIndex = -1;

    private int nextIndex = 0; // next iterated object index

    private Iterator<T> iterator;

    private boolean first = false;

    //public IndexedIterator( L l )
    public IndexedIterator( List<T> l )
    {
        //list = l;
        //iterator = l.iterator();
        this( l.iterator() );
    }

    public IndexedIterator( Iterator<T> it )
    {
        //list = l;
        iterator = it;
    }

    /* (non-Javadoc)
     * @see org.generic.list.IIndexIterator#hasNext()
     */
    @Override
    public boolean hasNext()
    {
        while ( iterator.hasNext() && !isElementIterable( nextIndex ) )
        {
            nextIndex++;
            iterator.next();
        }

        return iterator.hasNext();
    }

    /* (non-Javadoc)
     * @see org.generic.list.IIndexIterator#next()
     */
    @Override
    public T next()
    {
        nextIndex++;
        lastRetIndex++;
        first = lastRetIndex == 0;
        return iterator.next();
    }

    /* (non-Javadoc)
     * @see org.generic.list.IIndexIterator#remove()
     */
    @Override
    public void remove()
    {
        if ( nextIndex == 0 )
            throw new IndexOutOfBoundsException();
        nextIndex--;
        iterator.remove();
    }

    /**
     * @return true if list element is iterable
     */
    protected boolean isElementIterable( int i )
    {
        return true;
    }

    /**
     * @return currently iterated (not necessarily returned) object
     */
    //    public int getIterationIndex()
    //    {
    //        return nextIndex - 1;
    //    }

    public boolean isFirst()
    {
        return first;
    }

    public int index()
    {
        return lastRetIndex;
    }
}
