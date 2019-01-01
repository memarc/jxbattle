package org.generic.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.generic.string.StringUtils;

public class FileUtils
{
    private static String pathSeparator;

    private static byte[] readStreamToBytes( InputStream in, int length ) throws IOException
    {
        byte[] buffer = new byte[ length ];
        int offset = 0;
        for ( ;; )
        {
            int remain = length - offset;
            if ( remain <= 0 )
                break;

            int numRead = in.read( buffer, offset, remain );
            if ( numRead == -1 )
                throw new IOException( "Reached EOF, read " + offset + " expecting " + length );

            offset += numRead;
        }
        return buffer;
    }

    private static void readStreamToBytes( InputStream in, byte[] buffer, int start, int length ) throws IOException
    {
        int offset = 0;
        for ( ;; )
        {
            int remain = length - offset;
            if ( remain <= 0 )
                break;

            int numRead = in.read( buffer, offset + start, remain );
            if ( numRead == -1 )
                throw new IOException( "Reached EOF, read " + offset + " expecting " + length );

            offset += numRead;
        }
    }

    public static void readFileToBytes( String path, byte[] buf, int startIndex ) throws IOException
    {
        File file = new File( path );

        long fileLength = file.length();
        if ( fileLength > Integer.MAX_VALUE )
            throw new IOException( "File '" + file.getName() + "' too big" );

        InputStream in = new FileInputStream( file );
        try
        {
            readStreamToBytes( in, buf, startIndex, (int)fileLength );
        }
        finally
        {
            in.close();
        }
    }

    public static byte[] readFileToBytes( String path ) throws IOException
    {
        File file = new File( path );

        long fileLength = file.length();
        if ( fileLength > Integer.MAX_VALUE )
            throw new IOException( "File '" + file.getName() + "' too big" );

        InputStream in = new FileInputStream( file );
        try
        {
            return readStreamToBytes( in, (int)fileLength );
        }
        finally
        {
            in.close();
        }
    }

    public static String readFileToString( String path ) throws IOException
    {
        return new String( readFileToBytes( path ) );
    }

    public static void writeBytesToFile( byte[] bs, String filename ) throws IOException
    {
        //        FileOutputStream fos = null;
        //        try
        //        {
        //            fos = new FileOutputStream( new File( filename ) );
        //            fos.write( bs );
        //        }
        //        finally
        //        {
        //            if ( fos != null )
        //                fos.close();
        //        }

        try ( FileOutputStream fos = new FileOutputStream( new File( filename ) ) )
        {
            fos.write( bs );
            fos.flush();
        }
    }

    public static void writeStringToFile( String s, String filename ) throws IOException
    {
        writeBytesToFile( s.getBytes(), filename );
    }

    public static void writeStringsToFile( List<String> strings, String filename ) throws IOException
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream( new File( filename ) );

            for ( String s : strings )
            {
                fos.write( s.getBytes() );
                fos.write( StringUtils.getLineSeparator().getBytes() );
            }
        }
        finally
        {
            if ( fos != null )
                fos.close();
        }
    }

    public static String getPathSeparator()
    {
        if ( pathSeparator == null )
            pathSeparator = System.getProperty( "file.separator" );

        return pathSeparator;
    }
}
