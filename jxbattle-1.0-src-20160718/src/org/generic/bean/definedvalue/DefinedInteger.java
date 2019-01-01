package org.generic.bean.definedvalue;

public class DefinedInteger extends DefinedValue<Integer>
{
    public DefinedInteger()
    {
    }

    public DefinedInteger( DefinedInteger di )
    {
        if ( di.isDefined() )
            setValue( di.value );
    }

    public DefinedInteger( int v )
    {
        setValue( v );
    }

    public void setValue( int v )
    {
        setValue( Integer.valueOf( v ) );
    }

    public boolean equals( int v )
    {
        if ( !isDefined() )
            return false;

        return getValue().intValue() == v;
    }

    // Object interface

    @Override
    public DefinedInteger clone()
    {
        //        DefinedInteger res = new DefinedInteger();
        //        if ( isDefined() )
        //            res.setValue( value );
        //        return res;
        return new DefinedInteger( this );
    }
}
