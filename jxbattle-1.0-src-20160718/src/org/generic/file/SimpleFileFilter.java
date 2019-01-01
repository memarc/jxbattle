package org.generic.file;

import java.io.File;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileFilter;

public class SimpleFileFilter extends FileFilter
{
    private Pattern pattern;

    private String description;

    private String[] extensions;

    public SimpleFileFilter( String desc, String[] exts )
    {
        description = desc;
        extensions = exts;

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for ( String ext : extensions )
        {
            if ( !first )
                sb.append( '|' );
            first = false;
            sb.append( "^..*." );
            sb.append( ext );
            sb.append( "$" );
        }

        //        pattern = Pattern.compile( "^..*.csv$|^..*.CSV$" );
        pattern = Pattern.compile( sb.toString() );
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    public String[] getExtensions()
    {
        return extensions;
    }

    @Override
    public boolean accept( File f )
    {
        if ( f.isDirectory() )
            return true;

        if ( !f.isFile() )
            return false;

        return pattern.matcher( f.getName() ).matches();
    }
}
