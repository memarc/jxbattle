package org.generic.bean.cursor2d;

import org.generic.bean.definedvalue.DefinedInteger;

public class Cursor2d
{
    private DefinedInteger x;

    private DefinedInteger y;

    public Cursor2d()
    {
        x = new DefinedInteger();
        y = new DefinedInteger();
    }

    public Cursor2d( Cursor2d c )
    {
        //        x = c.x.clone();
        //        y = c.y.clone();
        x = new DefinedInteger( c.x );
        y = new DefinedInteger( c.y );
    }

    public Cursor2d( int x, int y )
    {
        this.x = new DefinedInteger( x );
        this.y = new DefinedInteger( y );
    }

    public DefinedInteger getX()
    {
        return x;
    }

    public DefinedInteger getY()
    {
        return y;
    }

    public void toRight()
    {
        x.setValue( x.getValue() + 1 );
    }

    public void toLeft()
    {
        x.setValue( x.getValue() - 1 );
    }

    public void set( int x, int y )
    {
        this.x.setValue( x );
        this.y.setValue( y );
    }

    public void set( Cursor2d c )
    {
        this.x.set( c.x );
        this.y.set( c.y );
    }

    public boolean equals( int xo, int yo )
    {
        return x.equals( xo ) && y.equals( yo );
    }

    public boolean equals( Cursor2d c )
    {
        if ( isDefined() && c.isDefined() )
            return x.getValue().intValue() == c.x.getValue().intValue() && y.getValue().intValue() == c.y.getValue().intValue();
        return false;
    }

    public boolean isDefined()
    {
        return x.isDefined() && y.isDefined();
    }

    public void undefine()
    {
        x.undefine();
        y.undefine();
    }

    public boolean isLowerThan( Cursor2d c )
    {
        if ( isDefined() && c.isDefined() )
        {
            int y1 = y.getValue().intValue();
            int y2 = c.y.getValue().intValue();
            //if ( y.getValue() == c.y.getValue() )
            if ( y1 == y2 )
                return x.getValue().intValue() < c.x.getValue().intValue();

            //            return y.getValue() < c.y.getValue();
            return y1 < y2;
        }

        return false;
    }

    public boolean isLowerOrEqualThan( Cursor2d c )
    {
        if ( isDefined() && c.isDefined() )
        {
            int y1 = y.getValue().intValue();
            int y2 = c.y.getValue().intValue();
            //if ( y.getValue() == c.y.getValue() )
            if ( y1 == y2 )
            {
                return x.getValue().intValue() <= c.x.getValue().intValue();
                //                int x1 = x.getValue();
                //                int x2 = c.x.getValue();
                //                return x1 <= x2;
            }

            //            return y.getValue() <= c.y.getValue();
            return y1 <= y2;
        }

        return false;
    }

    public boolean isGreaterThan( Cursor2d c )
    {
        if ( y.getValue() > c.y.getValue() )
            return true;

        return x.getValue() > c.x.getValue();
    }

    public Cursor2d getClosest( Cursor2d c1, Cursor2d c2 )
    {
        int dy1 = Math.abs( y.getValue() - c1.y.getValue() );
        int dy2 = Math.abs( y.getValue() - c2.y.getValue() );

        if ( dy1 > dy2 )
            return c2;
        else if ( dy1 < dy2 )
            return c1;

        int dx1 = Math.abs( x.getValue() - c1.x.getValue() );
        int dx2 = Math.abs( x.getValue() - c2.x.getValue() );
        if ( dx1 <= dx2 )
            return c1;
        return c2;
    }

    @Override
    public Cursor2d clone()
    {
        //        Cursor2d res = new Cursor2d();
        //        res.x = x.clone();
        //        res.y = y.clone();
        //        return res;
        return new Cursor2d( this );
    }

    @Override
    public String toString()
    {
        return x.toString() + " " + y.toString();
    }
}
