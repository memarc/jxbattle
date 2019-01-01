package org.generic.gui.textpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import org.generic.bean.cursor2d.Cursor2d;

public class TextPanelUIModel
{
    private TextPanelView view;

    private TextPanelModel model;

    private Font font;

    private int charPixelWidth;

    private int charPixelHeight;

    private int linePixelHeight;

    private FontMetrics fontMetrics;

    private Point tooltipLocation;

    private int visibleHorizontalCharCount; // visible panel width in char units

    private int visibleVerticalCharCount; // visible panel height in char units

    private char[] drawChars;

    //    private Rectangle visibleRect;

    public TextPanelUIModel( TextPanelView v )
    {
        view = v;
        view.setPreferredSize( computePanelSize() );
        view.setToolTipText( "" );
        charPixelWidth = -1;
        charPixelHeight = -1;
        linePixelHeight = -1;
        visibleHorizontalCharCount = -1;
        visibleVerticalCharCount = -1;
        drawChars = new char[ 1 ];
    }

    void setAppModel( TextPanelModel m )
    {
        model = m;
    }

    void updateSize()
    {
        updateLinePixelHeight();

        Dimension ps = computePanelSize();
        if ( !view.getSize().equals( ps ) )
        {
            view.setMinimumSize( ps );
            view.setPreferredSize( ps );
            view.setSize( ps );
        }
    }

    Font getFont()
    {
        return font;
    }

    void setFont( Font f )
    {
        boolean changed = font != f;
        if ( changed )
        {
            font = f;
            fontMetrics = view.getFontMetrics( font );
            //            charPixelWidth = fontMetrics.getMaxAdvance();
            //            charPixelHeight = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent();
            charPixelWidth = fontMetrics.charWidth( 'A' );
            charPixelHeight = fontMetrics.getHeight();
            updateLinePixelHeight();
        }
    }

    private void updateLinePixelHeight()
    {
        if ( model == null )
            linePixelHeight = charPixelHeight;
        else
            linePixelHeight = Math.max( charPixelHeight, model.getLinePixelHeight() );
    }

    Cursor2d mouseToText( int mx, int my )
    {
        int tx = mx / charPixelWidth;
        //        int ty = my / charPixelHeight;
        int ty = my / linePixelHeight;
        return new Cursor2d( tx, ty );
    }

    Cursor2d textToMouse( int tx, int ty )
    {
        int mx = tx * charPixelWidth;
        //        int my = ty * charPixelHeight;
        int my = ty * linePixelHeight;
        return new Cursor2d( mx, my );
    }

    //    private Dimension getCurrentPanelSize()
    //    {
    //        if ( model != null )
    //        {
    //            int max = Integer.MIN_VALUE;
    //            for ( int ty = 0; ty < model.getText().size(); ty++ )
    //            {
    //                int l = model.getText().get( ty ).length();
    //                if ( l > max )
    //                    max = l;
    //            }
    //
    //            return new Dimension( max * charPixelWidth, model.getText().size() * charPixelHeight );
    //        }
    //
    //        return new Dimension( 1, 1 );
    //    }

    Dimension computePanelSize()
    {
        if ( model != null )
            //            return new Dimension( model.getMaxLineWidth() * charPixelWidth, model.getLineCount() * charPixelHeight );
            return new Dimension( model.getMaxLineWidth() * charPixelWidth, model.getLineCount() * linePixelHeight );

        return new Dimension( 1, 1 );
    }

    //    private void drawString( Graphics g, int tx, int ty, String s, ColorInfo ci )
    //    {
    //        int px = tx * charPixelWidth;
    //        //        int py = ty * charPixelHeight;
    //        int py = ty * linePixelHeight;
    //
    //        g.setColor( ci.getBackgroundColor() );
    //        //        g.fillRect( px, py, charPixelWidth, charPixelHeight );
    //        g.fillRect( px, py, charPixelWidth, linePixelHeight );
    //
    //        g.setColor( ci.getForegroundColor() );
    //        g.drawString( s, px, py + fontMetrics.getAscent() );
    //    }

    private void drawChar( Graphics g, int tx, int ty, char c, ColorInfo ci )
    {
        int px = tx * charPixelWidth;
        //        int py = ty * charPixelHeight;
        int py = ty * linePixelHeight;

        g.setColor( ci.getBackgroundColor() );
        //        g.fillRect( px, py, charPixelWidth, charPixelHeight );
        g.fillRect( px, py, charPixelWidth, linePixelHeight );

        g.setColor( ci.getForegroundColor() );
        drawChars[ 0 ] = c;
        g.drawChars( drawChars, 0, 1, px, py + fontMetrics.getAscent() );
    }

    //    void paintComponent( Graphics g )
    //    {
    //        //System.out.println( "TextPanelView.doPaint" );
    //        //        System.out.println( "panel size " + getSize() );
    //        //        System.out.println( "clip " + g.getClipBounds() );
    //        //System.out.println( Utils.getTimeStamp() + " TextPanelView.doPaint cursor " + Utils.objectIdentity( model.getHoverCursor() ) + " " + model.getHoverCursor() );
    //
    //        Dimension ps = getCurrentPanelSize();
    //        view.setPreferredSize( ps );
    //        view.revalidate();
    //
    //        g.setColor( model.getTextBackgroupColor() );
    //        g.fillRect( 0, 0, (int)ps.getWidth(), (int)ps.getHeight() );
    //
    //        g.setFont( font );
    //        // int max = Integer.MIN_VALUE;
    //        //        for ( int ty = 0; ty < model.getText().size(); ty++ )
    //        for ( int ty = 0; ty < model.getLineCount(); ty++ )
    //        {
    //            //            String s = model.getText().get( ty );
    //            String s = model.getLine( ty );
    //            //            if ( s.length() > max )
    //            //                max = s.length();
    //
    //            //                try
    //            //                {
    //            for ( int tx = 0; tx < s.length(); tx++ )
    //            {
    //                char c = s.charAt( tx );
    //                //if ( tx == model.getCursorX() && ty == model.getCursorY() )
    //                ColorInfo col = model.getCharColor( tx, ty );
    //                //System.out.println( "paint " + tx + " " + ty + " " + col );
    //                //                        if ( model.getHoverCursor().getPosition().equals( tx, ty ) )
    //                //                            drawString( g, model.getFont(), tx, ty, String.valueOf( c ), model.getHoverCursor().getColorInfo() );
    //                //                        else if ( model.getTextCursor().getPosition().equals( tx, ty ) )
    //                //                            drawString( g, model.getFont(), tx, ty, String.valueOf( c ), model.getTextCursor().getColorInfo() );
    //                //                        else
    //                //                            drawString( g, model.getFont(), tx, ty, String.valueOf( c ), model.getTextColorInfo() );
    //                //drawString( g, uiModel.getFont(), tx, ty, String.valueOf( c ), col );
    //                drawString( g, tx, ty, String.valueOf( c ), col );
    //            }
    //            //                }
    //            //                catch( Exception e )
    //            //                {
    //            //                    Global.logMessageModel.guiMessage( this, "Error rendering cursor", e );
    //            //                }
    //        }
    //
    //        //currentSize = new Dimension( max * charWidth, model.getText().size() * charHeight );
    //        //view.setCurrentSize( currentSize );
    //        //setPreferredSize( currentSize );
    //    }

    //    private void updateDimension()
    //    {
    //        //        System.out.println();
    //        //        System.out.println( System.currentTimeMillis() + " updateDimension " );
    //        //        System.out.println( "size " + view.getSize() );
    //        //        System.out.println( "pref " + view.getPreferredSize() );
    //        //        System.out.println( "max  " + view.getMaximumSize() );
    //
    //        Dimension ps = getCurrentPanelSize();
    //        //view.setPreferredSize( ps );
    //        if ( !view.getSize().equals( ps ) )
    //        {
    //            view.setPreferredSize( ps );
    //            view.setSize( ps );
    //            //        System.out.println( System.currentTimeMillis() + " updateDimension " + " " + ps.width + "x" + ps.height + " " + model.getText() );
    //            //            System.out.println( "to   " + ps );
    //            //            System.out.println( "size " + view.getSize() );
    //            //            System.out.println( "pref " + view.getPreferredSize() );
    //            //            System.out.println( "max  " + view.getMaximumSize() );
    //        }
    //    }

    void doPaintComponent( Graphics g )
    {
        if ( model != null )
        {
            //            updateDimension();

            //            view.revalidate();
            //            view.invalidate();
            //            view.validate();

            Rectangle clipRect = g.getClipBounds();
            updatePanelVisibleSize( clipRect );

            g.setColor( model.getTextBackgroundColor() );
            g.fillRect( clipRect.x, clipRect.y, clipRect.width, clipRect.height );

            Cursor2d start = mouseToText( clipRect.x, clipRect.y );
            Cursor2d end = mouseToText( clipRect.x + clipRect.width, clipRect.y + clipRect.height );
            int x1 = start.getX().getValue();
            int y1 = start.getY().getValue();
            int x2 = end.getX().getValue();
            int y2 = end.getY().getValue();

            g.setFont( font );
            for ( int ty = y1; ty <= y2 && ty < model.getLineCount(); ty++ )
            {
                //            String s = model.getText().get( ty );
                String s = model.getLine( ty );
                int l = s.length();
                //            if ( s.length() > max )
                //                max = s.length();

                //                try
                //                {
                for ( int tx = x1; tx <= x2 && tx < l; tx++ )
                {
                    char c = s.charAt( tx );
                    //if ( tx == model.getCursorX() && ty == model.getCursorY() )
                    ColorInfo col = model.getCharColor( tx, ty );
                    //System.out.println( "paint " + tx + " " + ty + " " + col );
                    //                        if ( model.getHoverCursor().getPosition().equals( tx, ty ) )
                    //                            drawString( g, model.getFont(), tx, ty, String.valueOf( c ), model.getHoverCursor().getColorInfo() );
                    //                        else if ( model.getTextCursor().getPosition().equals( tx, ty ) )
                    //                            drawString( g, model.getFont(), tx, ty, String.valueOf( c ), model.getTextCursor().getColorInfo() );
                    //                        else
                    //                            drawString( g, model.getFont(), tx, ty, String.valueOf( c ), model.getTextColorInfo() );
                    //drawString( g, uiModel.getFont(), tx, ty, String.valueOf( c ), col );
                    //drawString( g, tx, ty, String.valueOf( c ), col );
                    drawChar( g, tx, ty, c, col );
                }
                //                }
                //                catch( Exception e )
                //                {
                //                    Global.logMessageModel.guiMessage( this, "Error rendering cursor", e );
                //                }
            }

            //currentSize = new Dimension( max * charWidth, model.getText().size() * charHeight );
            //view.setCurrentSize( currentSize );
            //setPreferredSize( currentSize );

            //        Rectangle c = getCaretRectangle();
            //        if ( c != null )
            //        {

            //        Rectangle vr = view.getVisibleRect();
            //        if ( vr != null )
            //            visibleRect = vr;
            //        System.out.println( "[paint] visible " + vr );
            //        if ( vr.width == 0 )
            //            System.out.println( "azeazeff" );
            //            int mc = c.x + c.width / 2;
            //            int mv = vr.x + vr.width / 2;
            //            System.out.println( "[paint] mc " + mc + " mv " + mv );
            //        }
        }
    }

    //    Rectangle getVisibleRect()
    //    {
    //        return visibleRect;
    //    }

    //    Rectangle getCaretRectangle()
    //    {
    //        TextCursor cc = model.getCaretCursor();
    //        if ( cc.isPositionDefined() )
    //        {
    //            Cursor2d c = textToMouse( cc.getPosition().getX().getValue(), cc.getPosition().getY().getValue() );
    //            Rectangle res = new Rectangle( c.getX().getValue(), c.getY().getValue(), getCharPixelWidth(), getCharPixelHeight() );
    //            return res;
    //        }
    //
    //        return null;
    //    }

    private void updatePanelVisibleSize( Rectangle r )
    {
        visibleHorizontalCharCount = r.width / charPixelWidth;
        visibleVerticalCharCount = r.height / charPixelHeight;

        //        System.out.println( "clip " + r );
        //        System.out.println( "size " + r.width + " pix" );
        //        System.out.println( "size " + visibleCharWidth + " chars" );
    }

    public int getVisibleHorizontalChars()
    {
        return visibleHorizontalCharCount;
    }

    int computeVisibleHorizontalCharCount( int pixelWidth )
    {
        if ( charPixelWidth == -1 )
            return -1;
        return pixelWidth / charPixelWidth;
    }

    public int getVisibleVerticalChars()
    {
        return visibleVerticalCharCount;
    }

    int getCharPixelWidth()
    {
        return charPixelWidth;
    }

    int getCharPixelHeight()
    {
        return charPixelHeight;
    }

    String getToolTipText( int mx, int my )
    {
        if ( model.isEnableTooltips() )
        {
            Cursor2d tc = mouseToText( mx, my );
            TextSelection ts = model.getSelectionAt( tc.getX().getValue(), tc.getY().getValue() );
            if ( ts != null )
            {
                //                tooltipLocation = new Point( mx, my - charPixelHeight );
                tooltipLocation = new Point( mx, my - linePixelHeight );
                String res = ts.getTooltipText();
                //            if ( ApplicationSettings.getInstance().isDebugMode() )
                //                res += " " + ts.getStartIndex() + "->" + ts.getEndIndex();
                return res;
            }
        }

        //        tooltipLocation = new Point( -10000, -10000 );
        tooltipLocation = null;
        return null;
    }

    Point getToolTipLocation( int mx, int my )
    {
        return tooltipLocation;
    }

    Color getBackgroundColor()
    {
        return model.getTextBackgroundColor();
    }
}
