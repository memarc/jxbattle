package org.generic.bean.parameter2;

public class IntPairParameter
{
    private IntParameter minParameter;

    private IntParameter maxParameter;

    public IntPairParameter( int min, int max )
    {
        minParameter = new IntParameter( min );
        maxParameter = new IntParameter( max );
    }
}
