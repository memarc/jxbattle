package org.generic.gui.closeabletabbedpane;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

public class CloseableTabbedPane extends JTabbedPane
{
    private TabCloseUI closeUI = new TabCloseUI( this );

    private List<Component> closeableTabComponents = new ArrayList<Component>();

    public CloseableTabbedPane( int flag )
    {
        super( flag );
    }

    public void paint( Graphics g )
    {
        super.paint( g );
        closeUI.paint( g );
    }

    public void addCloseableTab( String title, Component component, boolean allowClose )
    {
        if ( allowClose )
            closeableTabComponents.add( component );
        super.addTab( title + "  ", component );
    }

    public String getTabTitleAt( int index )
    {
        return super.getTitleAt( index ).trim();
    }

    private class TabCloseUI implements MouseListener, MouseMotionListener
    {
        private CloseableTabbedPane tabbedPane;

        private int closeX = 0, closeY = 0, meX = 0, meY = 0;

        private int selectedTabIndex;

        private Component selectedTabComponent;

        private boolean selectedIsClosable;

        private final int width = 8, height = 8;

        private Rectangle rectangle = new Rectangle( 0, 0, width, height );

        //        private TabCloseUI()
        //        {
        //        }

        public TabCloseUI( CloseableTabbedPane pane )
        {
            tabbedPane = pane;
            tabbedPane.addMouseMotionListener( this );
            tabbedPane.addMouseListener( this );
        }

        //        private boolean isTabCloseable( Component comp )
        //        {
        //            return closableTabs.contains( comp );
        //        }

        private boolean isTabCloseable( int i )
        {
            return closeableTabComponents.contains( tabbedPane.getComponent( i ) );
        }

        private void controlCursor()
        {
            if ( tabbedPane.getTabCount() > 0 )
                if ( closeUnderMouse( meX, meY ) && selectedIsClosable )
                {
                    tabbedPane.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
                    if ( selectedTabIndex > -1 )
                        tabbedPane.setToolTipTextAt( selectedTabIndex, "Close " + tabbedPane.getTitleAt( selectedTabIndex ) );
                }
                else
                {
                    tabbedPane.setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
                    if ( selectedTabIndex > -1 )
                        tabbedPane.setToolTipTextAt( selectedTabIndex, null );
                }
        }

        private boolean closeUnderMouse( int x, int y )
        {
            rectangle.x = closeX;
            rectangle.y = closeY;
            return rectangle.contains( x, y );
        }

        public void paint( Graphics g )
        {
            //            int tabCount = tabbedPane.getTabCount();
            //            for ( int j = 0; j < tabCount; j++ )
            //                if ( tabbedPane.getComponent( j ).isShowing() )
            //                    if ( isTabCloseable( j ) )
            //                    {
            //                        int x = tabbedPane.getBoundsAt( j ).x + tabbedPane.getBoundsAt( j ).width - width - 5;
            //                        int y = tabbedPane.getBoundsAt( j ).y + 5;
            //                        drawClose( g, x, y );
            //                        break;
            //                    }

            if ( mouseOverTab( meX, meY ) )
                if ( selectedIsClosable )
                    drawClose( g, closeX, closeY );
        }

        private boolean isUnderMouse( int x, int y )
        {
            if ( Math.abs( x - meX ) < width && Math.abs( y - meY ) < height )
                return true;
            return false;
        }

        private void drawClose( Graphics g, int x, int y )
        {
            if ( tabbedPane != null && tabbedPane.getTabCount() > 0 )
            {
                Graphics2D g2 = (Graphics2D)g;
                drawColored( g2, isUnderMouse( x, y ) ? Color.RED : Color.WHITE, x, y );
            }
        }

        private void drawColored( Graphics2D g2, Color color, int x, int y )
        {
            g2.setStroke( new BasicStroke( 5, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND ) );
            g2.setColor( Color.BLACK );
            g2.drawLine( x, y, x + width, y + height );
            g2.drawLine( x + width, y, x, y + height );
            g2.setColor( color );
            g2.setStroke( new BasicStroke( 3, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND ) );
            g2.drawLine( x, y, x + width, y + height );
            g2.drawLine( x + width, y, x, y + height );
        }

        private boolean mouseOverTab( int x, int y )
        {
            selectedTabIndex = -1;
            selectedIsClosable = false;
            selectedTabComponent = null;
            int tabCount = tabbedPane.getTabCount();
            for ( int j = 0; j < tabCount; j++ )
                if ( tabbedPane.getBoundsAt( j ).contains( x, y ) )
                {
                    selectedTabIndex = j;
                    selectedIsClosable = isTabCloseable( j );
                    selectedTabComponent = tabbedPane.getComponent( j );
                    closeX = tabbedPane.getBoundsAt( j ).x + tabbedPane.getBoundsAt( j ).width - width - 5;
                    closeY = tabbedPane.getBoundsAt( j ).y + 5;
                    return true;
                }
            return false;
        }

        // MouseMotionListener interface

        public void mouseMoved( MouseEvent me )
        {
            meX = me.getX();
            meY = me.getY();
            if ( mouseOverTab( meX, meY ) )
            {
                controlCursor();
                tabbedPane.repaint();
            }
        }

        // MouseListener interface

        public void mouseEntered( MouseEvent me )
        {
        }

        public void mouseExited( MouseEvent me )
        {
            selectedTabIndex = -1;
            selectedIsClosable = false;
            tabbedPane.repaint();
        }

        public void mousePressed( MouseEvent me )
        {
        }

        public void mouseClicked( MouseEvent me )
        {
        }

        public void mouseDragged( MouseEvent me )
        {
        }

        public void mouseReleased( MouseEvent me )
        {
            if ( closeUnderMouse( me.getX(), me.getY() ) )
            {
                //selectedIsClosable = isTabCloseable( tabbedPane.getSelectedComponent() );
                if ( selectedIsClosable && selectedTabIndex >= 0 && tabAboutToClose( selectedTabIndex ) )
                {
                    tabbedPane.removeTabAt( selectedTabIndex );
                    //                    closableTabComponents.remove( tabbedPane.getSelectedComponent() );
                    closeableTabComponents.remove( selectedTabComponent );
                }
                //selectedTab = tabbedPane.getSelectedIndex();
            }
        }
    }

    public boolean tabAboutToClose( int tabIndex )
    {
        return true;
    }
}
