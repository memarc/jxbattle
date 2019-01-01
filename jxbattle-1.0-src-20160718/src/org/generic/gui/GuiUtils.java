package org.generic.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.generic.string.StringUtils;

public class GuiUtils
{
    public static final Color defaultSwingBackgroundColor = new Color( 238, 238, 238 );

    private static Font fixedNormalFont;

    private static Font fixedBoldFont;

    public static Font getFixedNormalFont()
    {
        if ( fixedNormalFont == null )
            fixedNormalFont = new Font( Font.MONOSPACED, Font.PLAIN, 14 );
        return fixedNormalFont;
    }

    public static Font getFixedBoldFont()
    {
        if ( fixedBoldFont == null )
            fixedBoldFont = new Font( Font.MONOSPACED, Font.BOLD, 14 );
        return fixedBoldFont;
    }

    public static void appendTextareaLine( final JTextArea ta, String s )
    {
        final StringBuilder sb = new StringBuilder( s );
        sb.append( '\n' );

        if ( SwingUtilities.isEventDispatchThread() )
        {
            ta.append( sb.toString() );
            ta.setCaretPosition( ta.getText().length() );
        }
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    ta.append( sb.toString() );
                    ta.setCaretPosition( ta.getText().length() );
                }
            } );
    }

    public static boolean yesNoDialog( Component parent, String m )
    {
        return JOptionPane.showConfirmDialog( parent, m, "Please confirm", JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION;
    }

    private static void setRecursiveEnable_edt( Component comp, boolean b )
    {
        if ( comp != null )
            comp.setEnabled( b );

        if ( comp instanceof Container )
        {
            Container cont = (Container)comp;
            for ( Component c : cont.getComponents() )
            {
                setRecursiveEnable_edt( c, b );
            }
        }
    }

    public static void setRecursiveEnable( final Component comp, final boolean b )
    {
        if ( SwingUtilities.isEventDispatchThread() )
            setRecursiveEnable_edt( comp, b );
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    setRecursiveEnable_edt( comp, b );
                }
            } );
    }

    public static void centerWindow( Window win, Container parent )
    {
        Dimension dim = win.getSize();
        Dimension parentDim = parent.getSize();
        Point parentLocation = parent.getLocation();

        int xPos = parentDim.width / 2 - dim.width / 2;
        int yPos = parentDim.height / 2 - dim.height / 2;
        if ( xPos < 0 )
            xPos = 0;
        if ( yPos < 0 )
            yPos = 0;

        win.setLocation( parentLocation.x + xPos, parentLocation.y + yPos );
    }

    public static void centerWindowOnScreen( Window win )
    {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

        Dimension dim = win.getSize();

        int xPos = screenDim.width / 2 - dim.width / 2;
        int yPos = screenDim.height / 2 - dim.height / 2;
        if ( xPos < 0 )
            xPos = 0;
        if ( yPos < 0 )
            yPos = 0;

        win.setLocation( xPos, yPos );
    }

    public static String getColorLabel( String s, Color col, Color background )
    {
        // <html> sdfsdf <font color="#990000">Red</font>dfgdfg </html>

        StringBuilder sb = new StringBuilder( "<html><p bgcolor=\"#" );
        sb.append( String.format( "%02x", Integer.valueOf( background.getRed() ) ) );
        sb.append( String.format( "%02x", Integer.valueOf( background.getGreen() ) ) );
        sb.append( String.format( "%02x", Integer.valueOf( background.getBlue() ) ) );
        sb.append( "\">" );

        sb.append( "<font color=\"#" );
        sb.append( String.format( "%02x", Integer.valueOf( col.getRed() ) ) );
        sb.append( String.format( "%02x", Integer.valueOf( col.getGreen() ) ) );
        sb.append( String.format( "%02x", Integer.valueOf( col.getBlue() ) ) );
        sb.append( "\">" );

        sb.append( s );

        sb.append( " </font>" );

        sb.append( "</p></html>" );

        return sb.toString();
    }

    public static MouseEvent propageMouseEventToParent( JComponent c, MouseEvent e )
    {
        Container p = c.getParent();

        // convert event to parent coordinate system
        MouseEvent ne = SwingUtilities.convertMouseEvent( c, e, p );

        // propagate mouse event to parent component
        p.dispatchEvent( ne );

        return ne;
    }

    public static void setTextField( JTextField tf, int v )
    {
        String s = String.valueOf( v );
        setTextField( tf, s );
    }

    public static void setTextField( JTextField tf, String s )
    {
        if ( !StringUtils.equalsNotNull( tf.getText(), s ) )
            tf.setText( s );
    }

    public static void setTextArea( JTextArea ta, String s )
    {
        if ( !StringUtils.equalsNotNull( ta.getText(), s ) )
            ta.setText( s );
    }

    public static void scrollToRectangle( JComponent c, Rectangle r, boolean center )
    {
        Rectangle r2 = new Rectangle( r );

        if ( center )
        {
            Dimension s = c.getSize();
            int d = Math.min( s.width, s.height ) >> 2;
            r2.width += d;
            r2.height += d;
            d = d >> 1;
            r2.x -= d;
            r2.y -= d;
            //            r2.x = Math.max( r2.x - d, 0 );
            //            r2.y = Math.max( r2.y - d, 0 );
            //
            //            if ( r2.x + r2.width > s.width )
            //                r2.width = s.width - r2.x;
            //            if ( r2.y + r2.height > s.height )
            //                r2.height = s.height - r2.y;
        }

        c.scrollRectToVisible( r2 );
    }
}
