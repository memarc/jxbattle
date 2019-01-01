package org.generic.bean.definedvalue;

public class DefinedBoolean extends DefinedValue<Boolean>
{
    public DefinedBoolean()
    {
    }

    public DefinedBoolean( DefinedBoolean di )
    {
        if ( di.isDefined() )
            setValue( di.value );
    }

    public DefinedBoolean( boolean v )
    {
        setValue( v );
    }

    public void setValue( boolean v )
    {
        setValue( Boolean.valueOf( v ) );
    }

    public boolean equals( boolean v )
    {
        if ( !isDefined() )
            return false;

        return getValue().booleanValue() == v;
    }

    // Object interface

    @Override
    public DefinedBoolean clone()
    {
        //        DefinedBoolean res = new DefinedBoolean();
        //        if ( isDefined() )
        //            res.setValue( value );
        //        return res;
        return new DefinedBoolean( this );
    }
}
