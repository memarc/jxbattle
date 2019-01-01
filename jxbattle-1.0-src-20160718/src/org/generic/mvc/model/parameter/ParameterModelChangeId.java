package org.generic.mvc.model.parameter;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum ParameterModelChangeId implements MVCModelChangeId
{
    /**
     * IntParameter value changed
     */
    IntParameterChanged,

    /**
     * FloatParameter value changed
     */
    FloatParameterChanged,

    /**
     * BoolParameter value changed
     */
    BoolParameterChanged,

    /**
     * BoolParameter value changed
     */
    EnumParameterChanged,

    /**
     * IntMinMaxParameter value changed
     * used to avoid individual change notifications on min or max only
     */
    IntMinMaxParameterChanged,

    /**
     * BoundedIntMinMaxParameter value changed
     * used to avoid individual change notifications on min or max only
     */
    BoundedIntMinMaxParameterChanged,

    //
    ;

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public ParameterModelChangeId[] getValues()
    {
        return values();
    }

    @Override
    public ParameterModelChangeId getValueOf( int val )
    {
        for ( ParameterModelChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for ParameterModelChangeId enum " + val );
    }

    @Override
    public EnumValue getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
