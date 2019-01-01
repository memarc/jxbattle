package org.generic.bean.parameter2;

import org.generic.mvc.model.MVCModelError;

public abstract class MaxConstraint<T extends Number> extends NumericConstraint<T>
{
    //private T max;

    public MaxConstraint( NumericParameter<T> p, T maxVal )
    {
        super( p, new NumericParameter<T>( maxVal ) );
        //max = maxVal;
    }

    protected abstract boolean isGreaterThan( T v1, T v2 );

    @Override
    public void check()
    {
        // p <= value2
        // value1 <= value2
        // !(value1 > value2)

        if ( isGreaterThan( value1.getValue(), value2.getValue() ) )
            throw new MVCModelError( "error : value > max" );
    }

    @Override
    public String toString()
    {
        return "[value <=" + value2.getValue() + "]";
    }
}
