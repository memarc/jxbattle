package org.generic.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.generic.system.SystemUtils;

public class PathString
{
    private static String fileSep;

    //    private String path;
    private String stringValue;

    private List<PathElement> pathElements;

    private boolean isAbsolute;

    public PathString( String path )
    {
        parsePathElements( path );
    }

    public PathString( File path )
    {
        parsePathElements( path.getAbsolutePath() );
    }

    public PathString( PathString ps )
    {
        pathElements = new ArrayList<>();
        for ( PathElement pe : ps.pathElements )
            pathElements.add( new PathElement( pe ) );
        stringValue = ps.stringValue;
        isAbsolute = ps.isAbsolute;
    }

    //    private List<PathElement> getPathElements()
    //    {
    //        if ( pathElements == null )
    //        {
    //            pathElements = new ArrayList();
    //            String[] pes = path.split( fileSep );
    //            for ( String pe : pes )
    //                if ( pe.length() > 0 )
    //                    pathElements.add( new PathElement( pe ) );
    //        }
    //        return pathElements;
    //    }

    private static String getFileSeparator()
    {
        if ( fileSep == null )
        {
            if ( SystemUtils.isLinux() || SystemUtils.isMacOSX() )
                fileSep = "/";
            else
                fileSep = "\\\\";
        }
        return fileSep;
    }

    private void parsePathElements( String path )
    {
        pathElements = new ArrayList<>();
        String[] pes = path.split( getFileSeparator() );
        for ( String pe : pes )
            if ( pe.length() > 0 )
                pathElements.add( new PathElement( pe ) );

        stringValue = path;
        isAbsolute = stringValue.charAt( 0 ) == getFileSeparator().charAt( 0 );
    }

    public void appendPathElement( String p )
    {
        pathElements.add( new PathElement( p ) );
        stringValue = null;
    }

    public boolean exists()
    {
        return new File( toString() ).exists();
    }

    public PathElement getLastPathElement()
    {
        if ( pathElements != null )
        {
            int n = pathElements.size();
            if ( n > 0 )
                return pathElements.get( n - 1 );
        }

        return null;
    }

    //    private void setLastPathElement( String pe )
    //    {
    //        List<PathElement> pes = getPathElements();
    //
    //        if ( pes != null && pes.size() > 0 )
    //        {
    //            //            pes.remove( pes.size() - 1 );
    //            //            pes.add( pe );
    //            pes.get( pes.size() - 1 ).name = pe;
    //        }
    //    }

    public void removeFileExtension()
    {
        PathElement pe = getLastPathElement();
        pe.removeFileExtension();
        stringValue = null;
    }

    public void removeFileExtension( String ext )
    {
        PathElement pe = getLastPathElement();
        pe.removeFileExtension( ext );
        stringValue = null;
    }

    public void removeLastPathElement()
    {
        if ( pathElements != null && pathElements.size() > 0 )
        {
            pathElements.remove( pathElements.size() - 1 );
            stringValue = null;
        }
    }

    public void replaceIllegalChars( char substitute )
    {
        for ( PathElement pe : pathElements )
            pe.replaceIllegalChars( substitute );
        stringValue = null;
    }

    //    private boolean isAbsolute()
    //    {
    //        //        return pathElements.get( 0 ).name.charAt( 0 ) == fileSep.charAt( 0 );
    //        return stringValue.charAt( 0 ) == fileSep.charAt( 0 );
    //    }

    public File toFile()
    {
        return new File( toString() );
    }

    /**
     * return path optionally excluding last element if it's a file
     */
    public String getLastDir()
    {
        for ( int i = pathElements.size() - 1; i >= 0; i-- )
        {
            String p = toString( i );
            if ( new File( p ).isDirectory() )
                return p;
        }

        return ".";
    }

    /**
     * @return last path element if indeed is a file
     */
    public String getFilename()
    {
        int n = pathElements.size();
        if ( n > 0 )
        {
            //            String p = pathElements.get( n - 1 ).name;
            String p = pathElements.get( n - 1 ).name.toString();
            if ( new File( p ).isFile() )
                return p;
        }

        return null;
    }

    public boolean isDirectory()
    {
        return new File( toString() ).isDirectory();
    }

    private void addFileExtension( String ext )
    {
        PathElement pe = getLastPathElement();
        pe.addFileExtension( ext );
        stringValue = null;
    }

    private void setFileExtension( String ext )
    {
        PathElement pe = getLastPathElement();
        pe.setFileExtension( ext );
        stringValue = null;
    }

    private String toString( int npe )
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for ( int i = 0; i < npe; i++ )
        {
            if ( !first || isAbsolute )
                sb.append( getFileSeparator() );
            first = false;
            sb.append( pathElements.get( i ).name );
        }

        return sb.toString();
    }

    @Override
    public String toString()
    {
        if ( stringValue == null )
        {
            StringBuilder sb = new StringBuilder();
            boolean first = true;

            for ( PathElement pe : pathElements )
            {
                if ( !first || isAbsolute )
                    sb.append( getFileSeparator() );
                first = false;
                sb.append( pe.name );
            }

            stringValue = sb.toString();
        }

        return stringValue;
    }

    public class PathElement
    {
        private StringBuilder name;

        public PathElement( String n )
        {
            //            name = n;
            name = new StringBuilder( n );
        }

        public PathElement( PathElement pe )
        {
            //        name = pe.name;
            name = new StringBuilder( pe.name );
        }

        public String getExtension()
        {
            int indexExt = name.lastIndexOf( "." );
            if ( indexExt != -1 )
                return name.substring( indexExt + 1 );
            return null;
        }

        //        private String makeExtension( String ext )
        //        {
        //            if ( ext.charAt( 0 ) != '.' )
        //                return "." + ext;
        //
        //            return ext;
        //        }

        //        private int extensionPos( String ext )
        //        {
        //            String tmp = makeExtension( ext );
        //            int extPos = name.indexOf( tmp );
        //            if ( extPos != name.length() - tmp.length() )
        //                return -1;
        //
        //            return extPos;
        //        }

        public void removeFileExtension()
        {
            //            int indexExt = name.lastIndexOf( '.' );
            int indexExt = name.lastIndexOf( "." );
            if ( indexExt != -1 )
                //                name = name.substring( 0, indexExt );
                name.setLength( indexExt );
        }

        public void removeFileExtension( String ext )
        {
            //            String tmp = makeExtension( ext );
            //            int indexExt = name.lastIndexOf( tmp );
            //            int indexExt = extensionPos( ext );
            //            if ( indexExt != -1 )
            //                if ( name.substring( indexExt ).equals( ext ) )
            int indexExt = name.indexOf( ext );
            if ( indexExt != -1 )
            {
                name.setLength( indexExt );
                if ( name.charAt( indexExt - 1 ) == '.' )
                    name.setLength( indexExt - 1 );
            }
        }

        private void removeEndChar( char c )
        {
            int l = name.length();
            if ( l > 0 )
                if ( name.charAt( l - 1 ) == c )
                    name.setLength( l - 1 );
        }

        public void addFileExtension( String ext )
        {
            //            String tmp = removeEndChar( name, '.' );

            if ( ext.charAt( 0 ) == '.' )
                removeEndChar( '.' );
            else if ( name.charAt( name.length() - 1 ) != '.' )
                name.append( '.' );
            name.append( ext );
        }

        /**
         * replace current extension, if any
         */
        public void setFileExtension( String ext )
        {
            //            String tmp = makeExtension( ext );
            //            int extPos = name.indexOf( tmp );
            //            if ( extPos != name.length() - tmp.length() )
            //            {
            //                String p2 = removeEndChar( path, '.' );
            //                name = p2.concat( tmp );
            //            }
            removeFileExtension();
            addFileExtension( ext );
        }

        public void replaceIllegalChars( char substitute )
        {
            //            String tmp = name;
            //            for ( char c : illegals )
            //                tmp = StringUtils.replaceChar( tmp, c, substitute ).toString();
            //            name = tmp;
            name = PathString.replaceIllegalChars( name, substitute );
        }

        @Override
        public String toString()
        {
            return name.toString();
        }
    }

    //private static char[] illegalChars = new char[] { ' ', '/', '\\', '?', '%', '*', ':', '|', '"', '<', '>' };
    private static String illegalChars2 = " /\\?%*:|\"<>'";

    public static StringBuilder replaceIllegalChars( CharSequence s, char substitute )
    {
        //        StringBuilder tmp = new StringBuilder( s );
        //        for ( char c : illegalChars )
        //            tmp = StringUtils.replaceChar( tmp, c, substitute ).toString();
        //        return tmp.toString();

        StringBuilder res = new StringBuilder();
        int l = s.length();
        for ( int i = 0; i < l; i++ )
        {
            char c = s.charAt( i );
            if ( illegalChars2.indexOf( c ) == -1 )
                res.append( c );
            else
                res.append( substitute );
        }
        return res;
    }

    public static String removeAnyFileExtension( String path )
    {
        PathString ps = new PathString( path );
        ps.removeFileExtension();
        return ps.toString();
    }

    public static String removeFileExtension( String path, String ext )
    {
        PathString ps = new PathString( path );
        //PathElement pe = ps.getLastPathElement();
        ps.removeFileExtension( ext );
        return ps.toString();
    }

    //    private static String removeEndChar( String s, char c )
    //    {
    //        if ( s.charAt( s.length() - 1 ) == c )
    //            return s.substring( 0, s.length() - 1 );
    //        return s;
    //    }

    //    public static String removeEndSlash( String s )
    //    {
    //        return removeEndChar( s, '/' );
    //    }

    public static String addFileExtension( String path, String ext )
    {
        PathString ps = new PathString( path );
        //        PathElement pe = ps.getLastPathElement();
        ps.addFileExtension( ext );
        return ps.toString();
    }

    public static String setFileExtension( String path, String ext )
    {
        //        String tmp;
        //        if ( ext.charAt( 0 ) != '.' )
        //            tmp = "." + ext;
        //        else
        //            tmp = ext;
        //
        //        int extPos = path.indexOf( tmp );
        //        if ( extPos != path.length() - tmp.length() )
        //        {
        //            String p2 = removeEndChar( path, '.' );
        //            return p2.concat( tmp );
        //        }
        //
        //        return path;

        PathString ps = new PathString( path );
        //        PathElement pe = ps.getLastPathElement();
        ps.setFileExtension( ext );
        return ps.toString();
    }

    public static String removeLastPathElement( String path )
    {
        PathString ps = new PathString( path );
        ps.removeLastPathElement();
        return ps.toString();
    }

    /**
     * remove last path element if it's not a directory
     */
    public static String removeFilename( String path )
    {
        PathString ps = new PathString( path );
        return ps.getLastDir();
    }
}
