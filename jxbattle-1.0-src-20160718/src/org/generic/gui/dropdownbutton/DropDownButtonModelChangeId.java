package org.generic.gui.dropdownbutton;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum DropDownButtonModelChangeId implements MVCModelChangeId
{
    ItemSelected,

    //
    ;

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public DropDownButtonModelChangeId[] getValues()
    {
        return values();
    }

    @Override
    public DropDownButtonModelChangeId getValueOf( int val )
    {
        for ( DropDownButtonModelChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for DropDownButtonModelChangeId enum " + val );
    }

    @Override
    public DropDownButtonModelChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
