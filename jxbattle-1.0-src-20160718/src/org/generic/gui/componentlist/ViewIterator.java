package org.generic.gui.componentlist;

import java.util.Iterator;

import org.generic.list.SyncIterator;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.list.SyncListModel;

public class ViewIterator<V> implements Iterator<V>
{
    private SyncIterator<MVCController> it;

    public ViewIterator( SyncListModel<MVCController> ctrls )
    {
        //super( ctrls.g );
        //it = new IndexedIterator<MVCController>( ctrls );
        it = ctrls.iterator();
    }

    //    public void close()
    //    {
    //        it.close();
    //    }

    @SuppressWarnings("unchecked")
    @Override
    public V next()
    {
        return (V)it.next().getView();
    }

    @Override
    public boolean hasNext()
    {
        return it.hasNext();
    }

    @Override
    public void remove()
    {
        it.remove();
    }
}
