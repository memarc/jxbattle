package org.generic.net;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;

public enum ObjectType implements EnumValue
{
    STRING, DATE, LIST;

    // EnumValue interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public ObjectType[] getValues()
    {
        return values();
    }

    public static ObjectType getValueOfS( int val )
    {
        for ( ObjectType im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for ObjectType enum " + val );
    }

    @Override
    public ObjectType getValueOf( int val )
    {
        for ( ObjectType im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for ObjectType enum " + val );
    }

    @Override
    public ObjectType getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
