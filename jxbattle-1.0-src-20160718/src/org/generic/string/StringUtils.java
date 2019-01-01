package org.generic.string;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generic.NumericUtils;

public class StringUtils
{
    private static String lineSeparator;

    public static String rightPadString( CharSequence cs, char chr, int maxLen )
    {
        StringBuilder sb = new StringBuilder( cs );

        while ( sb.length() < maxLen )
            sb.append( chr );

        return sb.toString();
    }

    public static String leftPadString( CharSequence cs, char chr, int maxLen )
    {
        StringBuilder sb = new StringBuilder( cs );

        while ( sb.length() < maxLen )
            sb.insert( 0, chr );

        return sb.toString();
    }

    public static String toEllipsis( CharSequence cs, int maxLen, boolean fillToLength )
    {
        if ( cs == null )
            return null;

        StringBuilder sb = new StringBuilder( cs );

        char ellipsisChar;
        if ( sb.length() > maxLen - 3 )
        {
            sb.setLength( maxLen - 3 );
            ellipsisChar = '.';
        }
        else
        {
            if ( fillToLength )
                ellipsisChar = ' ';
            else
                return sb.toString();
        }

        int i = sb.length();
        for ( ; i < maxLen - 3; i++ )
            sb.append( ' ' );
        for ( ; i < maxLen; i++ )
            sb.append( ellipsisChar );

        return sb.toString();
    }

    public static String toFixedSize( CharSequence cs, int size, boolean alignLeft )
    {
        if ( cs.length() > size )
            return toEllipsis( cs, size, true );

        if ( alignLeft )
            return rightPadString( cs, ' ', size );

        return leftPadString( cs, ' ', size );
    }

    public static boolean equalsNotNull( String s1, String s2 )
    {
        //        if ( s1 == null )
        //            return s2 == null;
        //
        //        if ( s2 == null )
        //            return false;
        //
        //        return s1.equals( s2 );
        return NumericUtils.equalsNotNull( s1, s2 );
    }

    //    public static void trimChar( StringBuilder sb, char c )
    //    {
    //        for ( int i = 0; i < sb.length(); )
    //            if ( sb.charAt( i ) == c )
    //                sb.delete( i, i + 1 );
    //            else
    //                i++;
    //    }

    //    public static StringBuilder trimChar( StringBuilder sb, char c )
    //    {
    //        //        StringBuilder res = new StringBuilder();
    //        //
    //        //        for ( int i = 0; i < sb.length(); )
    //        //            if ( sb.charAt( i ) != c )
    //        //                res.append( c );
    //        //
    //        //        return res;
    //
    //        return trimChar( sb, c );
    //    }

    public static StringBuilder trimChar( CharSequence cs, char c )
    {
        StringBuilder res = new StringBuilder();

        for ( int i = 0; i < cs.length(); i++ )
        {
            char c2 = cs.charAt( i );
            if ( c2 != c )
                res.append( c2 );
        }

        return res;
    }

    public static StringBuilder replaceChar( CharSequence cs, char c1, char c2 )
    {
        StringBuilder res = new StringBuilder();

        for ( int i = 0; i < cs.length(); i++ )
        {
            char c = cs.charAt( i );
            if ( c == c1 )
                res.append( c2 );
            else
                res.append( c );
        }

        return res;
    }

    private static Pattern intPattern = Pattern.compile( "[-+]?\\d+" );

    public static boolean isInteger( String s )
    {
        Matcher m = intPattern.matcher( s );
        return m.matches();
    }

    public static String getLineSeparator()
    {
        if ( lineSeparator == null )
            lineSeparator = System.getProperty( "line.separator" );

        return lineSeparator;
    }

    //    public static void main( String[] args )
    //    {
    //        StringBuilder sb = new StringBuilder( " a b " );
    //        trimChar( sb, '-' );
    //        System.out.println( '>' + sb.toString() + '<' );
    //    }

    public static String joinStringList( List<String> l, char c )
    {
        StringBuilder res = new StringBuilder();

        boolean first = true;
        for ( String s : l )
        {
            if ( !first )
                res.append( c );
            res.append( s );
            first = false;
        }

        return res.toString();
    }

    public static List<String> removeDuplicates( List<String> l )
    {
        List<String> res = new ArrayList<>();
        for ( String s : l )
            if ( !res.contains( s ) )
                res.add( s );
        return res;
    }

    public static byte[] sha512Digest( String s ) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance( "SHA-512" );
        md.update( s.getBytes() );
        byte[] hash = md.digest();
        return hash;
    }

    private static final String HEX = "0123456789ABCDEF";

    public static String bufToHexString( byte[] buf )
    {
        StringBuffer str = new StringBuffer();

        for ( int i = 0; i < buf.length; i++ )
        {
            byte v = buf[ i ];
            str.append( HEX.charAt( v >>> 4 & 0x0F ) );
            str.append( HEX.charAt( v & 0x0F ) );
        }

        return str.toString();
    }
}
