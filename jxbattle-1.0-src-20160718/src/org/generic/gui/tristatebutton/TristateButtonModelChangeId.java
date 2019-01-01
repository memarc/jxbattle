package org.generic.gui.tristatebutton;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum TristateButtonModelChangeId implements MVCModelChangeId
{
    TristateButtonStateChanged,

    //    TristateButtonClick,

    ;

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public TristateButtonModelChangeId[] getValues()
    {
        return values();
    }

    @Override
    public TristateButtonModelChangeId getValueOf( int val )
    {
        for ( TristateButtonModelChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for TristateButtonModelChangeId enum " + val );
    }

    @Override
    public TristateButtonModelChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
