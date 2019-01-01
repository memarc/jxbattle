package org.generic.bean.definedvalue;

public class DefinedUInt extends DefinedValue<Integer>
{
    public DefinedUInt()
    {
    }

    public DefinedUInt( DefinedUInt di )
    {
        if ( di.isDefined() )
            setValue( di.value );
    }

    public DefinedUInt( int v )
    {
        setValue( v );
    }

    public void setValue( int v )
    {
        if ( v < 0 )
            throw new Error( "invalid negative value" );

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
    public DefinedUInt clone()
    {
        //        DefinedUInt res = new DefinedUInt();
        //        if ( isDefined() )
        //            res.setValue( value );
        //        return res;
        return new DefinedUInt( this );
    }
}
