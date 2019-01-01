package org.generic.bean.parameter;

/**
 * min-max value with lower limit for min and upper limit for max
 */
public class BoundedIntMinMaxParameter
{
    private IntMinMax bounds;

    private IntParameter minParameter;

    private IntParameter maxParameter;

    public BoundedIntMinMaxParameter( int boundMin, int boundMax )
    {
        bounds = new IntMinMax( boundMin, boundMax );
        minParameter = new IntParameter( boundMin, boundMax, boundMin );
        maxParameter = new IntParameter( boundMin, boundMax, boundMax );
    }

    public BoundedIntMinMaxParameter( BoundedIntMinMaxParameter p )
    {
        bounds = new IntMinMax( p.bounds );
        minParameter = new IntParameter( p.minParameter );
        maxParameter = new IntParameter( p.maxParameter );
    }

    //    public boolean isValid( int v )
    //    {
    //        try
    //        {
    //            bounds.checkValue( v );
    //            return true;
    //        }
    //        catch( MVCModelError e )
    //        {
    //            return false;
    //        }
    //    }

    public int getMinValue()
    {
        return minParameter.getValue();
    }

    public void setMinValue( int v )
    {
        bounds.checkValue( v );
        minParameter.setValue( v );
    }

    public int getMaxValue()
    {
        return maxParameter.getValue();
    }

    public void setMaxValue( int v )
    {
        bounds.checkValue( v );
        maxParameter.setValue( v );
    }

    public IntParameter getMinParameter()
    {
        return minParameter;
    }

    public IntParameter getMaxParameter()
    {
        return maxParameter;
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
        BoundedIntMinMaxParameter other = (BoundedIntMinMaxParameter)obj;
        if ( bounds == null )
        {
            if ( other.bounds != null )
                return false;
        }
        else if ( !bounds.equals( other.bounds ) )
            return false;
        if ( maxParameter == null )
        {
            if ( other.maxParameter != null )
                return false;
        }
        else if ( !maxParameter.equals( other.maxParameter ) )
            return false;
        if ( minParameter == null )
        {
            if ( other.minParameter != null )
                return false;
        }
        else if ( !minParameter.equals( other.minParameter ) )
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bounds == null) ? 0 : bounds.hashCode());
        result = prime * result + ((maxParameter == null) ? 0 : maxParameter.hashCode());
        result = prime * result + ((minParameter == null) ? 0 : minParameter.hashCode());
        return result;
    }

    @Override
    public String toString()
    {
        return "BoundedIntMinMaxParameter [minParameter=" + minParameter + ", maxParameter=" + maxParameter + "]";
    }
}
