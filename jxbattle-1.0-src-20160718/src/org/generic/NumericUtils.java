package org.generic;

import java.math.BigDecimal;

public class NumericUtils
{
    public static boolean booleanIdentity( boolean a, boolean b )
    {
        return (a && b) || (!a && !b);
    }

    //    public static double roundTwoDecimals( double d )
    //    {
    //        DecimalFormat twoDForm = new DecimalFormat( "#.##" );
    //        return Double.valueOf( twoDForm.format( d ) );
    //    }

    public static double roundDouble( double d, int decimalPlaces )
    {
        try
        {
            BigDecimal bd = new BigDecimal( d );
            bd = bd.setScale( decimalPlaces, BigDecimal.ROUND_UP );
            return bd.doubleValue();
        }
        catch( NumberFormatException e )
        {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean equalsNotNull( Object o1, Object o2 )
    {
        if ( o1 == null )
            return o2 == null;

        if ( o2 == null )
            return false;

        return o1.equals( o2 );
    }
}
