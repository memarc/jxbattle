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

package jxbattle.model.common;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugLog
{
    private OutputStream os;

    private String header;

    private SimpleDateFormat simpleFormat = new SimpleDateFormat( "[ HH:mm:ss SSS ] : " );

    private boolean enabled;

    //    private String colorCode;
    //    public static final String ANSI_RESET = "\u001B[0m";
    //    public static final String ANSI_BLACK = "\u001B[30m";
    //    public static final String ANSI_RED = "\u001B[31m";
    //    public static final String ANSI_GREEN = "\u001B[32m";
    //    public static final String ANSI_YELLOW = "\u001B[33m";
    //    public static final String ANSI_BLUE = "\u001B[34m";
    //    public static final String ANSI_PURPLE = "\u001B[35m";
    //    public static final String ANSI_CYAN = "\u001B[36m";
    //    public static final String ANSI_WHITE = "\u001B[37m";

    public DebugLog() // String path ) throws FileNotFoundException
    {
        //   os = new FileOutputStream( new File( path ) );
        os = System.out;
        enabled = false;
        //colorCode = cc;
    }

    public void setHeader( String h )
    {
        header = h;
    }

    public void log( String m )
    {
        if ( enabled && os != null )
        {
            StringBuilder sb = new StringBuilder();

            //sb.append( Ansi.ansi().fg( Ansi.Color.BLUE ) );
            sb.append( simpleFormat.format( new Date() ) );
            sb.append( header );
            sb.append( m );
            sb.append( '\n' );

            //            Ansi a = Ansi.ansi().fg( Ansi.Color.BLUE ).a( sb.toString() );
            //            AnsiConsole.out().print( a );

            try
            {
                os.write( sb.toString().getBytes() );
                os.flush();
            }
            catch( IOException e )
            {
                e.printStackTrace();
                close();
            }
        }
    }

    public void close()
    {
        if ( os != null )
        {
            try
            {
                os.close();
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
            finally
            {
                os = null;
            }
        }
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean e )
    {
        enabled = e;
    }
}
