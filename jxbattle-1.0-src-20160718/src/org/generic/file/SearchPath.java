package org.generic.file;

import java.util.ArrayList;
import java.util.List;

public class SearchPath
{
    private List<PathString> list;

    public SearchPath()
    {
        list = new ArrayList<>();
    }

    public PathString findPath( String path ) throws Exception
    {
        for ( PathString ps : list )
        {
            PathString tmp = new PathString( ps );
            tmp.appendPathElement( path );
            if ( tmp.exists() )
                return tmp;
        }

        throw new Exception( "cannot find file/dir " + path + " in search path " + toString() );
    }

    public void appendPath( String p )
    {
        if ( p != null )
            list.add( new PathString( p ) );
    }

    // Object interface

    @Override
    public String toString()
    {
        return list.toString();
    }
}
