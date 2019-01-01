package org.generic.gui.closeabletabbedpane;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum CloseableTabbedPaneChangeId implements MVCModelChangeId
{
    //    CurrentComponentChanged,

    ComponentAdded, ComponentRemoved, ComponentSelected,

    //
    ;

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public CloseableTabbedPaneChangeId[] getValues()
    {
        return values();
    }

    @Override
    public CloseableTabbedPaneChangeId getValueOf( int val )
    {
        for ( CloseableTabbedPaneChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for CloseableTabbedPaneChangeId enum " + val );
    }

    @Override
    public CloseableTabbedPaneChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
