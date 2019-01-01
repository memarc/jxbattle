package org.generic.system;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class SystemUtils
{
    public static boolean isMacOSX()
    {
        String osName = System.getProperty( "os.name" );
        return osName.contains( "Mac OS X" );
    }

    public static boolean isLinux()
    {
        String osName = System.getProperty( "os.name" );
        return osName.contains( "Linux" );
    }

    public static boolean isWindows()
    {
        String osName = System.getProperty( "os.name" );
        return osName.contains( "Windows" );
    }

    public static String getEnvVariable( String varName )
    {
        Map<String, String> env = System.getenv();

        //        for ( String envName : env.keySet() )
        //            java.lang.System.out.format( "%s=%s%n", envName, env.get( envName ) );

        return env.get( varName );
    }

    /**
     * @param clazz class belonging to main executable jar
     * @return path of main executable jar
     * @throws URISyntaxException 
     */
    public static Path getExecutableJarPath( Class<?> clazz ) throws URISyntaxException
    {
        return Paths.get( clazz.getProtectionDomain().getCodeSource().getLocation().toURI() );
    }

    public static String getCurrentDirectory()
    {
        return System.getProperty( "user.dir" );
    }
}
