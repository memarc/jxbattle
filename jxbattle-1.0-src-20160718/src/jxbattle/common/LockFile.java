/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package jxbattle.common;

import java.io.File;
import java.io.IOException;

public class LockFile
{
    private final String filename = "jxbattle-" + Consts.applicationVersion + ".lock";

    private File file;

    private boolean acquired;

    public LockFile()
    {
        acquired = false;
        file = new File( System.getProperty( "java.io.tmpdir" ), filename );
    }

    public boolean acquire()
    {
        if ( acquired )
            return true;

        if ( !file.exists() )
        {
            try
            {
                file.createNewFile();
                acquired = true;
                return true;
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
        }

        return false;
    }

    public void delete()
    {
        file.delete();
        acquired = false;
    }

    //    public void write( int i ) throws IOException
    //    {
    //        Writer fw = null;
    //
    //        try
    //        {
    //            fw = new FileWriter( file );
    //            fw.write( i );
    //        }
    //        finally
    //        {
    //            if ( fw != null )
    //                fw.close();
    //        }
    //    }
    //
    //    public int read() throws IOException
    //    {
    //        Reader fr = null;
    //
    //        try
    //        {
    //            fr = new FileReader( file );
    //
    //            return fr.read();
    //        }
    //        finally
    //        {
    //            if ( fr != null )
    //                fr.close();
    //        }
    //    }
}
