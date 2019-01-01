package org.generic.bean.parameter2;

public class IntMaxConstraint extends MaxConstraint<Integer>
{
    public IntMaxConstraint( IntParameter v, int maxVal )
    {
        super( v, maxVal );
    }

    @Override
    protected boolean isGreaterThan( Integer v1, Integer v2 )
    {
        return v1 > v2;
    }
}
