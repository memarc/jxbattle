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

package jxbattle;

import jxbattle.model.MainModel;

import org.generic.mvc.model.MVCModelError;

public class JxbattleMain
{
    private MainModel mainModel;

    public JxbattleMain()
    {
        mainModel = MainModel.getInstance();
    }

    public static void main( String[] args )
    {
        try
        {
            int count = 1;
            if ( args.length > 0 )
                count = MainModel.fastPlay ? Integer.valueOf( args[ 0 ] ).intValue() : 1;
            new JxbattleMain().run( count );
        }
        catch( MVCModelError e )
        {
            e.printStackTrace();
        }
    }

    //    private void lockFileInit()
    //    {
    //        if ( MainModel.fastPlay )
    //        {
    //            Runtime.getRuntime().addShutdownHook( new Thread()
    //            {
    //                @Override
    //                public void run()
    //                {
    //                    mainModel.getLockFile().delete();
    //                }
    //            } );
    //        }
    //    }

    //    private void run2( int playerCount )
    //    {
    //        Random mt1 = new MTRandom( 1433162549514L );
    //        Random mt2 = new MTRandom( 1433162549514L );
    //
    //        try ( FileOutputStream fos = new FileOutputStream( new File( "/tmp/rand" ) ) )
    //        {
    //            for ( int i = 0; i < 100000; i++ )
    //            {
    //                String bs = String.valueOf( mt1.nextInt() );
    //                fos.write( "rand1 ".getBytes() );
    //                fos.write( bs.getBytes() );
    //                fos.write( '\n' );
    //                fos.write( "rand2 ".getBytes() );
    //                bs = String.valueOf( mt2.nextInt() );
    //                fos.write( bs.getBytes() );
    //                fos.write( '\n' );
    //            }
    //        }
    //        catch( Exception e )
    //        {
    //            e.printStackTrace();
    //        }
    //    }

    private void run( int playerCount )
    {
        //        if ( MainModel.debug )
        //            MainModel.fastPlay = JOptionPane.showConfirmDialog( null, "fast play ?", "", JOptionPane.OK_CANCEL_OPTION ) == JOptionPane.OK_OPTION;

        //        lockFileInit();

        mainModel.setPlayerCount( playerCount );

        // run main model

        mainModel.run( this );
    }

}
