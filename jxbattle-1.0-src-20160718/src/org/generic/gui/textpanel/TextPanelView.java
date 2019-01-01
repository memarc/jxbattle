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

package org.generic.gui.textpanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import org.generic.gui.GuiUtils;

public class TextPanelView extends JPanel
{
    private TextPanelUIModel uiModel;

    //private Dimension currentSize;

    public TextPanelView()
    {
        //currentSize = new Dimension( 0, 0 );
        //setPreferredSize( getCurrentSize() );
        //        setPreferredSize( new Dimension( 10, 10 ) );
    }

    //    void setCurrentSize( Dimension s )
    //    {
    //        currentSize = s;
    //    }

    //    private void setViewFont( Font f )
    //    {
    //        fontMetrics = getFontMetrics( f );
    //        charWidth = fontMetrics.getMaxAdvance();
    //        charHeight = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent();
    //    }

    //    void setModel( TextPanelModel m )
    //    {
    //        //System.out.println( "TextPanelView.setModel" );
    //        model = m;
    //        //setViewFont( model.getFont() );
    //        //getCurrentSize();
    //    }

    //    private void doPaint( Graphics g )
    //    {
    //        //System.out.println( "TextPanelView.doPaint" );
    //        //        System.out.println( "panel size " + getSize() );
    //        //        System.out.println( "clip " + g.getClipBounds() );
    //        //System.out.println( Utils.getTimeStamp() + " TextPanelView.doPaint cursor " + Utils.objectIdentity( model.getHoverCursor() ) + " " + model.getHoverCursor() );
    //
    //        setPreferredSize( getCurrentSize() );
    //        revalidate();
    //
    //        if ( uiModel != null )
    //        {
    //            int max = Integer.MIN_VALUE;
    //            for ( int ty = 0; ty < uiModel.getText().size(); ty++ )
    //            {
    //                String s = uiModel.getText().get( ty );
    //                if ( s.length() > max )
    //                    max = s.length();
    //
    //                //                try
    //                //                {
    //                for ( int tx = 0; tx < s.length(); tx++ )
    //                {
    //                    char c = s.charAt( tx );
    //                    //if ( tx == model.getCursorX() && ty == model.getCursorY() )
    //                    ColorInfo col = uiModel.getCharColor( tx, ty );
    //                    //System.out.println( "paint " + tx + " " + ty + " " + col );
    //                    //                        if ( model.getHoverCursor().getPosition().equals( tx, ty ) )
    //                    //                            drawString( g, model.getFont(), tx, ty, String.valueOf( c ), model.getHoverCursor().getColorInfo() );
    //                    //                        else if ( model.getTextCursor().getPosition().equals( tx, ty ) )
    //                    //                            drawString( g, model.getFont(), tx, ty, String.valueOf( c ), model.getTextCursor().getColorInfo() );
    //                    //                        else
    //                    //                            drawString( g, model.getFont(), tx, ty, String.valueOf( c ), model.getTextColorInfo() );
    //                    //drawString( g, uiModel.getFont(), tx, ty, String.valueOf( c ), col );
    //                    uiModel.drawString( g, tx, ty, String.valueOf( c ), col );
    //                }
    //                //                }
    //                //                catch( Exception e )
    //                //                {
    //                //                    Global.logMessageModel.guiMessage( this, "Error rendering cursor", e );
    //                //                }
    //            }
    //
    //            //currentSize = new Dimension( max * charWidth, model.getText().size() * charHeight );
    //            //view.setCurrentSize( currentSize );
    //            //setPreferredSize( currentSize );
    //        }
    //    }

    //    private Dimension getCurrentSize()
    //    {
    //        if ( uiModel != null )
    //        {
    //            //            int max = Integer.MIN_VALUE;
    //            //            for ( int ty = 0; ty < model.getText().size(); ty++ )
    //            //            {
    //            //                int l = model.getText().get( ty ).length();
    //            //                if ( l > max )
    //            //                    max = l;
    //            //            }
    //            //
    //            //            currentSize = new Dimension( max * charWidth, model.getText().size() * charHeight );
    //            return uiModel.getCurrentPanelSize();
    //        }
    //
    //        //System.out.println( "TextPanelView.getCurrentSize " + currentSize );
    //
    //        //return currentSize;
    //        return new Dimension( 0, 0 );
    //    }

    void setUiModel( TextPanelUIModel m )
    {
        uiModel = m;
    }

    @Override
    protected void paintComponent( Graphics g )
    {
        super.paintComponent( g );

        if ( uiModel != null )
        {
            //            Dimension ps = uiModel.getCurrentPanelSize();
            //            //            setPreferredSize( ps );
            //            //            revalidate();
            //            if ( !getSize().equals( ps ) )
            //                setSize( ps );

            uiModel.doPaintComponent( g );
        }
    }

    //    @Override
    //    public void repaint( int x, int y, int width, int height )
    //    {
    //        //        if ( uiModel != null )
    //        //            uiModel.repaint( x, y, width, height );
    //    }

    // Scrollable interface

    //    
    //    public Dimension getPreferredScrollableViewportSize()
    //    {
    //        //System.out.println( "getPreferredScrollableViewportSize " + getCurrentSize() );
    //        return getCurrentSize();
    //    }
    //
    //    
    //    public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction )
    //    {
    //        return 20;
    //    }
    //
    //    
    //    public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction )
    //    {
    //        return 20;
    //    }
    //
    //    
    //    public boolean getScrollableTracksViewportWidth()
    //    {
    //        return true;
    //    }
    //
    //    
    //    public boolean getScrollableTracksViewportHeight()
    //    {
    //        return true;
    //    }

    // JComponent interface

    @Override
    public String getToolTipText( MouseEvent e )
    {
        //        TextSelection ts = uiModel.getSelectionUnderMouse( e.getX(), e.getY() );
        //        if ( ts != null )
        //            return ts.getTooltipText();
        //        return "tooltip";
        return uiModel.getToolTipText( e.getX(), e.getY() );
    }

    @Override
    public Point getToolTipLocation( MouseEvent e )
    {
        return uiModel.getToolTipLocation( e.getX(), e.getY() );
    }

    // Component interface

    @Override
    public Color getBackground()
    {
        if ( uiModel == null )
            return GuiUtils.defaultSwingBackgroundColor;

        return uiModel.getBackgroundColor();
    }
}
