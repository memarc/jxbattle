package org.generic.bean.parameter2;

public abstract class LessThanConstraint<T extends Number> extends NumericConstraint<T>
{
    public LessThanConstraint( NumericParameter<T> v1, NumericParameter<T> v2 )
    {
        super( v1, v2 );
    }
}
