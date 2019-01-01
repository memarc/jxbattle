package org.generic.mvc.model.list;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum ListModelChangeId implements MVCModelChangeId
{
    /**
     * default list messages
     * @see SyncListModel<T>.getAddElementChangeId(), SyncListModel<T>.getRemoveElementChangeId(), SyncListModel<T>.getListClearedChangeId()
     */

    /**
     * element has been added to list
     */
    List_AddElement,

    /**
     * element has been removed from list
     */
    List_RemoveElement,

    /**
     * current element in list changed
     */
    List_CurrentChanged,

    /**
     * list is about to be cleared
     */
    List_PreListClear,

    /**
     * list has been cleared
     */
    List_ListCleared;

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public ListModelChangeId[] getValues()
    {
        return values();
    }

    @Override
    public ListModelChangeId getValueOf( int val )
    {
        for ( ListModelChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for ListModelChangeId enum " + val );
    }

    @Override
    public ListModelChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
