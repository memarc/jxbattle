package org.generic.gui.componentlist;

import java.util.Iterator;

import org.generic.list.SyncIterator;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.list.SyncListModel;
import org.generic.mvc.model.observer.MVCModel;

public class ModelIterator<M extends MVCModel> implements Iterator<M>
{
    private SyncIterator<MVCController> it;

    public ModelIterator( SyncListModel<MVCController> ctrls )
    {
        it = ctrls.iterator();
    }

    public int index()
    {
        return it.index();
    }

    @SuppressWarnings("unchecked")
    @Override
    public M next()
    {
        return (M)it.next().getModel();
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