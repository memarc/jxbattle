package org.generic.bean.parameter2;

import java.util.ArrayList;
import java.util.List;

public class NumericParameter<T extends Number>
{
    protected T value;

    private T defaultValue;

    private List<NumericConstraint<T>> constraints;

    public NumericParameter( T dflt )
    {
        value = defaultValue = dflt;
    }

    public NumericParameter( NumericParameter<T> src )
    {
        value = src.value;
        defaultValue = src.defaultValue;
        constraints = src.constraints;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue( T val )
    {
        checkConstraints( val );
        value = val;
    }

    protected void addConstraint( NumericConstraint<T> c )
    {
        if ( constraints == null )
            constraints = new ArrayList<NumericConstraint<T>>();
        constraints.add( c );

        checkConstraints( defaultValue );
        checkConstraints( value );
    }

    private void checkConstraints( T val )
    {
        if ( constraints == null )
            return;

        for ( NumericConstraint<T> c : constraints )
            c.check();
    }

    public T getDefaultValue()
    {
        return defaultValue;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        NumericParameter<?> other = (NumericParameter<?>)obj;
        if ( value == null )
        {
            if ( other.value != null )
                return false;
        }
        else if ( !value.equals( other.value ) )
            return false;
        return true;
    }

    //    @Override
    //    public boolean equals( Object obj )
    //    {
    //        if ( this == obj )
    //            return true;
    //        if ( !super.equals( obj ) )
    //            return false;
    //        if ( getClass() != obj.getClass() )
    //            return false;
    //        IntParameter other = (IntParameter)obj;
    //        if ( defaultValue != other.defaultValue )
    //            return false;
    //        if ( value != other.value )
    //            return false;
    //        return true;
    //    }

    @Override
    public String toString()
    {
        return String.valueOf( value ) + " " + constraints.toString();
    }
}
