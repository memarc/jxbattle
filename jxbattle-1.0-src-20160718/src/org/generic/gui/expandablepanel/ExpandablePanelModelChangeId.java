package org.generic.gui.expandablepanel;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum ExpandablePanelModelChangeId implements MVCModelChangeId
{
    ButtonTextChanged,

    ExpansionChanged,

    //
    ;

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public ExpandablePanelModelChangeId[] getValues()
    {
        return values();
    }

    @Override
    public ExpandablePanelModelChangeId getValueOf( int val )
    {
        for ( ExpandablePanelModelChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for ExpandablePanelModelChangeId enum " + val );
    }

    @Override
    public ExpandablePanelModelChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
