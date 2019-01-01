package org.generic.bean.definedvalue;

public class DefinedLong extends DefinedValue<Long>
{
    public DefinedLong()
    {
    }

    public DefinedLong( DefinedLong di )
    {
        if ( di.isDefined() )
            setValue( di.value );
    }

    public DefinedLong( long v )
    {
        setValue( v );
    }

    public void setValue( long v )
    {
        setValue( Long.valueOf( v ) );
    }

    public boolean equals( long v )
    {
        if ( !isDefined() )
            return false;

        return getValue().longValue() == v;
    }

    // Object interface

    @Override
    public DefinedLong clone()
    {
        //        DefinedLong res = new DefinedLong();
        //        if ( isDefined() )
        //            res.setValue( value );
        //        return res;
        return new DefinedLong( this );
    }
}
