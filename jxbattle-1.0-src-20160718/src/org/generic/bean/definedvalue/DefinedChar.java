package org.generic.bean.definedvalue;

public class DefinedChar extends DefinedValue<Character>
{
    public DefinedChar()
    {
    }

    public DefinedChar( DefinedChar di )
    {
        if ( di.isDefined() )
            setValue( di.value );
    }

    public DefinedChar( char v )
    {
        setValue( v );
    }

    public void setValue( char v )
    {
        setValue( Character.valueOf( v ) );
    }

    public boolean equals( char v )
    {
        if ( !isDefined() )
            return false;

        return getValue().charValue() == v;
    }

    // Object interface

    @Override
    public DefinedChar clone()
    {
        //        DefinedChar res = new DefinedChar();
        //        if ( isDefined() )
        //            res.setValue( value );
        //        return res;
        return new DefinedChar( this );
    }
}
