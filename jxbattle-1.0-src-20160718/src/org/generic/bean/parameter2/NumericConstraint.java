package org.generic.bean.parameter2;

public abstract class NumericConstraint<T extends Number>
{
    protected NumericParameter<T> value1;

    protected NumericParameter<T> value2;

    //    protected T value1;
    //    protected T value2;

    public NumericConstraint( NumericParameter<T> v1, NumericParameter<T> v2 )
    //    public NumericPairConstraint( T v1, T v2 )
    {
        value1 = v1;
        value2 = v2;
    }

    public abstract void check();
}
