package org.generic.bean.parameter2;

public class IntMinConstraint extends MinConstraint<Integer>
{
    public IntMinConstraint( int minVal, IntParameter v )
    {
        super( minVal, v );
    }

    @Override
    protected boolean isLessThan( Integer v1, Integer v2 )
    {
        return v1 < v2;
    }
}
