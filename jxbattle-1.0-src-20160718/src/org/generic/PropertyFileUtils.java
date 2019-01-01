package org.generic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

/**
 * environment "property" file manager
 * @author fgrand
 */
public class PropertyFileUtils
{
    //    /**
    //     * load a property file
    //     */
    //    public static void loadPropertyFile( String filename, Properties defaultProperties, ClassLoader classLoader, boolean log ) throws IOException
    //    {
    //        try
    //        {
    //            // unit test execution in IDE context
    //
    //            if ( log )
    //                System.out.println( "trying src/test/resources/" + filename );
    //            defaultProperties.load( new BufferedReader( new InputStreamReader( new FileInputStream( "src/test/resources/" + filename ) ) ) );
    //        }
    //        catch( FileNotFoundException e )
    //        {
    //            try
    //            {
    //                // application execution in IDE context
    //
    //                if ( log )
    //                    System.out.println( "trying src/main/resources/" + filename );
    //                defaultProperties.load( new BufferedReader( new InputStreamReader( new FileInputStream( "src/main/resources/" + filename ) ) ) );
    //            }
    //            catch( FileNotFoundException e2 )
    //            {
    //                // application execution outside IDE -> classpath
    //
    //                if ( log )
    //                    System.out.println( "trying /" + filename + " in classpath (" + System.getProperty( "java.class.path" ) + ")" );
    //
    //                //InputStream is = getClass().getResourceAsStream( "/"+filename );
    //                //InputStream is = ClassLoader.class.getResourceAsStream( "/"+filename );
    //
    //                Class<?> clc = classLoader == null ? ClassLoader.class : (Class<?>)classLoader.getClass();
    //                InputStream is = clc.getResourceAsStream( "/" + filename );
    //                if ( is == null )
    //                    throw new IOException( "loading " + filename + " failed" );
    //
    //                defaultProperties.load( is );
    //            }
    //        }
    //    }

    private static void loadPropertyFileFromClasspath( String filename, Properties defaultProperties, ClassLoader classLoader, boolean log ) throws IOException
    {
        // application execution outside IDE -> classpath

        if ( log )
            System.out.println( "trying /" + filename + " in classpath (" + System.getProperty( "java.class.path" ) + ")" );

        //InputStream is = getClass().getResourceAsStream( "/"+filename );
        //InputStream is = ClassLoader.class.getResourceAsStream( "/"+filename );

        Class<?> clc = classLoader == null ? ClassLoader.class : (Class<?>)classLoader.getClass();
        try ( InputStream is = clc.getResourceAsStream( "/" + filename ) )
        {
            if ( is == null )
                throw new IOException( "loading " + filename + " failed" );

            defaultProperties.load( is );
            //        BufferedInputStream fis = (BufferedInputStream)is;
            //        FileChannel fc = fis.
            //        System.out.println( "filechannel " + fc.toString() );
            //        FileDescriptor fd = fis.getFD();
            //        System.out.println( "filedesc " + fd.toString() );

            URL url = clc.getResource( "/" + filename );

            if ( log )
                System.out.println( filename + " loaded from classpath (" + url + ")" );
        }
    }

    private static void loadPropertyFileFromMainResources( String filename, Properties defaultProperties, boolean log ) throws FileNotFoundException, IOException
    {
        // application execution in IDE context

        if ( log )
            System.out.println( "trying src/main/resources/" + filename );

        //        defaultProperties.load( new BufferedReader( new InputStreamReader( new FileInputStream( "src/main/resources/" + filename ) ) ) );
        try ( BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream( "src/main/resources/" + filename ) ) ) )
        {
            defaultProperties.load( br );
            System.out.println( filename + " loaded from main resources" );
        }
    }

    private static void loadPropertyFileFromTestResources( String filename, Properties defaultProperties, boolean log ) throws FileNotFoundException, IOException
    {
        // unit test execution in IDE context

        if ( log )
            System.out.println( "trying src/test/resources/" + filename );
        //        defaultProperties.load( new BufferedReader( new InputStreamReader( new FileInputStream( "src/test/resources/" + filename ) ) ) );
        try ( BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream( "src/test/resources/" + filename ) ) ) )
        {
            defaultProperties.load( br );
            System.out.println( filename + " loaded from test resources" );
        }
    }

    /**
     * load a property file
     */
    public static void loadPropertyFile( String filename, Properties defaultProperties, ClassLoader classLoader, boolean log ) throws IOException
    {
        System.out.println( "current dir " + System.getProperty( "user.dir" ) );

        try
        {
            loadPropertyFileFromClasspath( filename, defaultProperties, classLoader, log );
        }
        catch( IOException e )
        {
            try
            {
                loadPropertyFileFromMainResources( filename, defaultProperties, log );
            }
            catch( IOException e2 )
            {
                loadPropertyFileFromTestResources( filename, defaultProperties, log );
            }
        }
    }

    /**
     * loads environment value from a property file
     * @return loaded value in a Properties instance
     * @throws IOException
     */
    public static void loadEnvironmentProperties( Properties defaultProperties, ClassLoader classLoader ) throws IOException
    {
        loadPropertyFile( "environment.properties", defaultProperties, classLoader, true );

        for ( Object key : defaultProperties.keySet() )
            System.out.println( key + "=" + defaultProperties.get( key ) );
    }
}
