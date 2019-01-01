package org.generic.bean.definedvalue;

public class DefinedValue<T> implements IDefinedValue<T>
{
    private boolean defined;

    protected T value;

    public DefinedValue()
    {
        undefine();
    }

    public DefinedValue( DefinedValue<T> dv )
    {
        //        defined = dv.defined;
        //        value = dv.value;
        if ( dv.defined )
            setValue( dv.value );
    }

    @Override
    public boolean isDefined()
    {
        return defined;
    }

    @Override
    public void undefine()
    {
        defined = false;
        //        String s = LogUtils.stackTraceToString( Thread.currentThread() );
        //        int i1 = s.indexOf( "includesCoordinates" );
        //        int i2 = s.indexOf( "paintComponent" );
        //        if ( i1 == -1 && i2 == -1 )
        //            System.out.println( "\n\n" + System.currentTimeMillis() + "\n" + s ); 
    }

    @Override
    public T getValue()
    {
        if ( !defined )
            throw new Error( "error getting undefined value" );

        return value;
    }

    public void setValue( T val )
    {
        value = val;
        defined = true;
    }

    public void set( DefinedValue<T> val )
    {
        defined = val.defined;
        value = val.value;
    }

    public boolean equals( DefinedValue<T> other )
    {
        if ( defined && other.defined )
            return value.equals( other.value );

        return false;
    }

    //    public boolean equals( T other )
    //    {
    //        if ( defined )
    //            return value.equals( other );
    //
    //        return false;
    //    }

    // Object interface

    @Override
    public String toString()
    {
        if ( defined )
            return value.toString();
        return "<undefined>";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (defined ? 1231 : 1237);
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
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
        DefinedValue<?> other = (DefinedValue<?>)obj;
        if ( defined != other.defined )
            return false;
        if ( value == null )
        {
            if ( other.value != null )
                return false;
        }
        else if ( !value.equals( other.value ) )
            return false;
        return true;
    }

    //    @Override
    //    public DefinedValue<T> clone()
    //    {
    //        DefinedValue<T> res = new DefinedValue<T>();
    //        if ( isDefined() )
    //        {
    //            res.defined = defined;
    //            res.value = value;
    //        }
    //        return res;
    //    }

}
