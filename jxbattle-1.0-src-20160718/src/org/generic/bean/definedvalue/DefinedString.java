package org.generic.bean.definedvalue;

public class DefinedString extends DefinedValue<String>
{
    public DefinedString()
    {
    }

    public DefinedString( DefinedString di )
    {
        if ( di.isDefined() )
            setValue( di.value );
    }

    public DefinedString( String s )
    {
        setValue( s );
    }

    public boolean equals( String s )
    {
        if ( !isDefined() )
            return false;

        return getValue().equals( s );
    }

    @Override
    public DefinedString clone()
    {
        //        DefinedString res = new DefinedString();
        //        if ( isDefined() )
        //            res.setValue( value );
        //        return res;
        return new DefinedString( this );
    }
}
