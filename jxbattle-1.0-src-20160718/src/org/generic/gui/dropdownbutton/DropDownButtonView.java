package org.generic.gui.dropdownbutton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.JButton;

public class DropDownButtonView extends JButton
{
    static int ArrowDim = 25;

    private static int LeftArrowInnerMargin = 3;

    private static int RightArrowInnerMargin = 4;

    private static int TopArrowInnerMargin = 7;

    private static int BottomArrowInnerMargin = 8;

    public DropDownButtonView()
    {
        setFocusPainted( false );
    }

    protected void paintComponent( Graphics g )
    {
        super.paintComponent( g );

        // vertical separator

        Dimension dim = getSize();
        g.setColor( Color.GRAY );
        int x1 = dim.width - ArrowDim;
        g.drawLine( x1, 0, x1, dim.height );

        // arrow

        g.setColor( Color.BLACK );
        Polygon p = new Polygon();
        p.addPoint( x1 + LeftArrowInnerMargin, TopArrowInnerMargin );
        p.addPoint( dim.width - RightArrowInnerMargin, TopArrowInnerMargin );
        p.addPoint( x1 + ArrowDim / 2, dim.height - BottomArrowInnerMargin );
        g.fillPolygon( p );
    }
}
