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

package jxbattle.gui.base.common;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

/*
  inspired by René Jeschke (rene_jeschke@yahoo.de)

  KeyListener wrapper
  removes unwanted KeyReleased event while holding down key under linux

  P = key pressed
  T = key typed
  R = key released
  x = to inhibit
  
  buggy linux key hold down sequence = P T R P T R P T ... R T R P T R
                                           x     x         x   x        

  correct sequence should be P T P T P T .. T P T R

  example usage :
  
    frame.addKeyListener( KeyListenerWrapper.init( new KeyListener()
    {
        @Override
        public void keyTyped( KeyEvent e )
        {
            System.out.println( "keyTyped " + e.getKeyChar() );
        }

        @Override
        public void keyReleased( KeyEvent e )
        {
            System.out.println( "keyReleased " + e.getKeyCode() );
        }

        @Override
        public void keyPressed( KeyEvent e )
        {
            System.out.println( "keyPressed " + e.getKeyCode() );
        }
    }, false ) );
*/

public final class KeyListenerWrapper implements KeyListener, Runnable
{
    /** The listener to delegate key events to. */
    private final KeyListener wrappedListener;

    /** Our background thread. */
    private final Thread watcher;

    /** Our key event queue. */
    private ArrayBlockingQueue<KeyEvent> keyEvents = new ArrayBlockingQueue<>( 2048 );

    /** Whether to post KeyEvents using invokeLater or not. */
    private final boolean useInvokeLater;

    private final boolean isLinux;

    /**
    * Ctor.
    *
    * @param wrapped The wrapped KeyListener
    * @param invokeLater Whether to post KeyEvents using invokeLater or not.
    */
    private KeyListenerWrapper( final KeyListener wrapped, final boolean invokeLater )
    {
        this.wrappedListener = wrapped;
        this.useInvokeLater = invokeLater;
        this.watcher = new Thread( this );
        this.watcher.setDaemon( true );
        this.isLinux = System.getProperty( "os.name" ).toLowerCase().indexOf( "linux" ) != -1;
    }

    /**
    * Initializes this key listener wrapper (also starts the background thread).
    *
    * @param wrapped The KeyListener to wrap.
    * @param useInvokeLater Whether to post KeyEvents using invokeLater or not.
    * @return this
    */
    public static KeyListenerWrapper init( final KeyListener wrapped, final boolean useInvokeLater )
    {
        final KeyListenerWrapper wrapper = new KeyListenerWrapper( wrapped, useInvokeLater );
        wrapper.watcher.start();
        return wrapper;
    }

    /**
    * Posts a key event.
    *
    * @param e The KeyEvent.
    * @param invokeLater Whether to use invokeLater or not.
    */
    private void postKeyEvent( final KeyEvent e, final boolean invokeLater )
    {
        if ( invokeLater )
        {
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    KeyListenerWrapper.this.postKeyEvent( e, false );
                }
            } );
        }
        else
        {
            switch ( e.getID() )
            {
                case KeyEvent.KEY_PRESSED:
                    this.wrappedListener.keyPressed( e );
                    break;

                case KeyEvent.KEY_RELEASED:
                    this.wrappedListener.keyReleased( e );
                    break;

                case KeyEvent.KEY_TYPED:
                    this.wrappedListener.keyTyped( e );
                    break;

                default:
                    break;
            }
        }
    }

    /**
    * @see KeyListener#keyPressed(KeyEvent)
    */
    @Override
    public void keyPressed( KeyEvent e )
    {
        this.keyEvents.add( e );
    }

    /**
    * @see KeyListener#keyReleased(KeyEvent)
    */
    @Override
    public void keyReleased( KeyEvent e )
    {
        this.keyEvents.add( e );
    }

    /**
    * @see KeyListener#keyTyped(KeyEvent)
    */
    @Override
    public void keyTyped( KeyEvent e )
    {
        this.keyEvents.add( e );
    }

    /**
    * @see Runnable#run()
    */
    @Override
    public void run()
    {
        if ( isLinux )
            runLinux();
        else
            runAny();
    }

    private void runLinux()
    {
        KeyEvent last = null;

        for ( ;; ) /* I could do this all day long ... */
        {
            try
            {
                if ( last == null )
                {
                    last = this.keyEvents.take();
                }

                switch ( last.getID() )
                {
                    case KeyEvent.KEY_PRESSED:
                        //                        if ( isReleasedInhibited )
                        //                        {
                        //                            isReleasedInhibited = false;
                        //                            last = null;
                        //                            break;
                        //                        }

                        //$FALL-THROUGH$
                    case KeyEvent.KEY_TYPED:
                        this.postKeyEvent( last, this.useInvokeLater );
                        last = null;
                        break;

                    case KeyEvent.KEY_RELEASED:
                    {
                        final KeyEvent next = this.keyEvents.poll( 5, TimeUnit.MILLISECONDS );

                        if ( next == null )
                        {
                            this.postKeyEvent( last, this.useInvokeLater );
                            last = null;
                        }
                        else if ( next.getID() == KeyEvent.KEY_PRESSED && next.getKeyCode() == last.getKeyCode() && next.getWhen() == last.getWhen() )
                        {
                            last = next;
                        }
                        else
                        {
                            this.postKeyEvent( last, this.useInvokeLater );
                            last = next;
                        }

                    }
                        break;

                    default:
                        break;
                }
            }
            catch( InterruptedException eaten )
            {
                // *munch*
            }
        }
    }

    private void runAny()
    {

        for ( ;; )
        {
            try
            {
                KeyEvent ev = this.keyEvents.take();
                this.postKeyEvent( ev, this.useInvokeLater );
            }
            catch( InterruptedException eaten )
            {
            }
        }
    }
}