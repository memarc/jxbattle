package org.generic.gui.closeabletabbedpane;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Iterator;

import org.generic.mvc.model.observer.MVCModelImpl;

//public class CloseableTabbedPaneModel<M extends MVCModel, V extends JComponent, C extends MVCController<M, V>> implements Iterable<Component>
public class CloseableTabbedPaneUIModel extends MVCModelImpl implements Iterable<Component>
{
    private CloseableTabbedPaneView view;

    private int hoveredTabIndex;

    private CloseableTabbedPaneModel componentsModel;

    private Component hoveredTabComponent;

    private boolean hoveredIsClosable;

    private final int closeWidth = 8, closeHeight = 8;

    private int closeX = 0, closeY = 0, meX = 0, meY = 0;

    private Rectangle rectangle;

    public CloseableTabbedPaneUIModel( CloseableTabbedPaneView v )
    {
        view = v;
        rectangle = new Rectangle( 0, 0, closeWidth, closeHeight );
    }

    void setComponentsModel( CloseableTabbedPaneModel ctpm )
    {
        componentsModel = ctpm;
    }

    private boolean mouseOverTab()
    {
        hoveredTabIndex = -1;
        hoveredIsClosable = false;
        hoveredTabComponent = null;
        int tabCount = view.getTabCount();
        for ( int j = 0; j < tabCount; j++ )
            if ( view.getBoundsAt( j ).contains( meX, meY ) )
            {
                hoveredTabIndex = j;
                //                hoveredIsClosable = isTabCloseable( j );
                hoveredTabComponent = view.getComponent( j );
                hoveredIsClosable = componentsModel.isTabCloseable( hoveredTabComponent );
                closeX = view.getBoundsAt( j ).x + view.getBoundsAt( j ).width - closeWidth - 5;
                closeY = view.getBoundsAt( j ).y + 5;
                return true;
            }
        return false;
    }

    private void controlCursor()
    {
        if ( view.getTabCount() > 0 )
            if ( isCloseUnderMouse( meX, meY ) && hoveredIsClosable )
            {
                view.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
                if ( hoveredTabIndex > -1 )
                    view.setToolTipTextAt( hoveredTabIndex, "Close " + view.getTitleAt( hoveredTabIndex ) );
            }
            else
            {
                view.setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
                if ( hoveredTabIndex > -1 )
                    view.setToolTipTextAt( hoveredTabIndex, null );
            }
    }

    /**
     * @return true if tabpane should be repainted
     */
    boolean mouseMoved( int mx, int my )
    {
        meX = mx;
        meY = my;
        if ( mouseOverTab() )
        {
            controlCursor();
            return true;
        }

        return false;
    }

    private boolean isUnderMouse( int x, int y )
    {
        if ( Math.abs( x - meX ) < closeWidth && Math.abs( y - meY ) < closeHeight )
            return true;
        return false;
    }

    private boolean isCloseUnderMouse( int x, int y )
    {
        rectangle.x = closeX;
        rectangle.y = closeY;
        return rectangle.contains( x, y );
    }

    void mouseExited()
    {
        hoveredTabIndex = -1;
        hoveredIsClosable = false;
    }

    boolean canClose()
    {
        if ( isCloseUnderMouse( meX, meY ) )
        {
            //hoveredIsClosable = isTabCloseable( tabbedPane.getSelectedComponent() );
            //            if ( hoveredIsClosable && hoveredTabIndex >= 0 && componentsModel.tabAboutToClose( hoveredTabIndex ) )
            //            if ( hoveredIsClosable && hoveredTabIndex >= 0 )
            //            {
            //                view.removeTabAt( hoveredTabIndex );
            //                //                    closableTabComponents.remove( tabbedPane.getSelectedComponent() );
            //                componentsModel.removeTab( selectedTabComponent );
            //            }
            return hoveredIsClosable && hoveredTabIndex >= 0;
            //selectedTab = tabbedPane.getSelectedIndex();
        }

        return false;
    }

    int getHoveredTabIndex()
    {
        return hoveredTabIndex;
    }

    void paint( Graphics g )
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

        if ( mouseOverTab() )
            if ( hoveredIsClosable )
                drawClose( g, closeX, closeY );
    }

    private void drawClose( Graphics g, int x, int y )
    {
        if ( view != null && view.getTabCount() > 0 )
        {
            Graphics2D g2 = (Graphics2D)g;
            drawColored( g2, isUnderMouse( x, y ) ? Color.RED : Color.WHITE, x, y );
        }
    }

    private void drawColored( Graphics2D g2, Color color, int x, int y )
    {
        g2.setStroke( new BasicStroke( 5, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND ) );
        g2.setColor( Color.BLACK );
        g2.drawLine( x, y, x + closeWidth, y + closeHeight );
        g2.drawLine( x + closeWidth, y, x, y + closeHeight );
        g2.setColor( color );
        g2.setStroke( new BasicStroke( 3, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND ) );
        g2.drawLine( x, y, x + closeWidth, y + closeHeight );
        g2.drawLine( x + closeWidth, y, x, y + closeHeight );
    }

    // Iterator<Component> interface

    @Override
    public Iterator<Component> iterator()
    {
        return new TabComponentIterator();
    }

    private class TabComponentIterator implements Iterator<Component>
    {
        private int componentCount;

        private int componentIndex;

        public TabComponentIterator()
        {
            componentCount = view.getTabCount();
            componentIndex = 0;
        }

        @Override
        public boolean hasNext()
        {
            return componentIndex < componentCount;
        }

        @Override
        public Component next()
        {
            Component res = view.getComponent( componentIndex );
            componentIndex++;
            return res;
        }

        @Override
        public void remove()
        {
        }
    }
}
