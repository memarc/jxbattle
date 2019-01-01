package org.generic.bean.definedvalue;

public class DefinedDouble extends DefinedValue<Double>
{
    public DefinedDouble()
    {
    }

    public DefinedDouble( DefinedDouble di )
    {
        if ( di.isDefined() )
            setValue( di.value );
    }

    public DefinedDouble( double v )
    {
        setValue( v );
    }

    public void setValue( double v )
    {
        setValue( Double.valueOf( v ) );
    }

    public boolean equals( double v )
    {
        if ( !isDefined() )
            return false;

        return getValue().longValue() == v;
    }

    public void add( double v )
    {
        setValue( value + v );
    }

    public void sub( double v )
    {
        setValue( value - v );
    }

    public void mul( double v )
    {
        setValue( value * v );
    }

    public void div( double v )
    {
        setValue( value / v );
    }

    // Object interface

    @Override
    public DefinedDouble clone()
    {
        //        DefinedDouble res = new DefinedDouble();
        //        if ( isDefined() )
        //            res.setValue( value );
        //        return res;
        return new DefinedDouble( this );
    }
}
