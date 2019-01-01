package org.generic.bean.parameter2;

import org.generic.mvc.model.MVCModelError;

public abstract class MinConstraint<T extends Number> extends NumericConstraint<T>
{
    public MinConstraint( T minVal, NumericParameter<T> p )
    {
        super( new NumericParameter<T>( minVal ), p );
    }

    protected abstract boolean isLessThan( T v1, T v2 );

    @Override
    public void check()
    {
        // p >= value1
        // value2 >= value1
        // value1 < value2

        if ( isLessThan( value1.getValue(), value2.getValue() ) )
            throw new MVCModelError( "error : value > max" );
    }

    @Override
    public String toString()
    {
        return "[value <=" + value2.getValue() + "]";
    }
}
