package org.generic;

/**
 * interface for generic enums 
 * @author fgrand
 */
public interface EnumValue
{
    public int getOrdinal();

    public EnumValue getValueOf( int val );

    public EnumValue getValueOf( String s );

    public boolean equals( EnumValue v );

    public EnumValue[] getValues();

}
