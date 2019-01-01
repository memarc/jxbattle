package org.generic.bean.cursor1d;

import org.generic.bean.definedvalue.DefinedInteger;

public class Interval1dImpl implements Interval1d
{
    private DefinedInteger start;

    private DefinedInteger end;

    public Interval1dImpl()
    {
        start = new DefinedInteger();
        end = new DefinedInteger();
    }

    public Interval1dImpl( int s, int e )
    {
        start = new DefinedInteger( s );
        end = new DefinedInteger( e );
    }

    public Interval1dImpl( Interval1d i )
    {
        this();
        if ( i.isDefined() )
        {
            setStartPos( i.getStartPos() );
            setEndPos( i.getEndPos() );
        }
    }

    @Override
    public int getStartPos()
    {
        return start.getValue();
    }

    @Override
    public void setStartPos( int p )
    {
        start.setValue( p );
    }

    @Override
    public int getEndPos()
    {
        return end.getValue();
    }

    @Override
    public void setEndPos( int p )
    {
        end.setValue( p );
    }

    @Override
    public int length()
    {
        return end.getValue() - start.getValue();
    }

    @Override
    public boolean includesPos( int p )
    {
        return getStartPos() <= p && p <= getEndPos();
    }

    @Override
    public Interval1d intersection( Interval1d i )
    {
        if ( isDefined() && i.isDefined() )
        {
            int s1 = getStartPos();
            int e1 = getEndPos();

            int s2 = i.getStartPos();
            int e2 = i.getEndPos();

            boolean empty = s1 > e2 || s2 > e1;
            if ( !empty )
            {
                boolean b1 = s2 <= s1;
                int is = b1 ? s1 : s2;

                boolean b2 = e2 <= e1;
                int ie = b2 ? e2 : e1;

                return new Interval1dImpl( is, ie );
            }
        }
        return new Interval1dImpl();
    }

    @Override
    public boolean hasIntersection( Interval1d i )
    {
        if ( isDefined() && i.isDefined() )
        {
            int s1 = getStartPos();
            int e1 = getEndPos();

            int s2 = i.getStartPos();
            int e2 = i.getEndPos();

            boolean empty = s1 > e2 || s2 > e1;
            return !empty;
        }
        return false;
    }

    @Override
    public boolean equals( Interval1d i )
    {
        if ( isDefined() && i.isDefined() )
            return getStartPos() == i.getStartPos() && getEndPos() == i.getEndPos();

        return false;
    }

    @Override
    public boolean equals( int s, int e )
    {
        if ( isDefined() )
            return getStartPos() == s && getEndPos() == e;

        return false;
    }

    @Override
    public Interval1d shift( int i )
    {
        if ( isDefined() )
        {
            int s = getStartPos() + i;
            int e = getEndPos() + i;
            return new Interval1dImpl( s, e );
        }
        return new Interval1dImpl();
    }

    @Override
    public boolean isDefined()
    {
        return start.isDefined() && end.isDefined();
    }

    @Override
    public void undefine()
    {
        start.undefine();
        end.undefine();
    }

    @Override
    public String toString()
    {
        return start.toString() + "->" + end.toString();
    }

    @Override
    public Interval1d clone()
    {
        return new Interval1dImpl( this );
    }
}
