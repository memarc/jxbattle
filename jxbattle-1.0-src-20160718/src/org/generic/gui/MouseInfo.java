package org.generic.gui;

import org.generic.bean.cursor2d.Cursor2d;

public class MouseInfo
{
    private Cursor2d pixelPosition;

    private boolean leftButton;

    private boolean rightButton;

    public MouseInfo()
    {
        pixelPosition = new Cursor2d();
    }

    public MouseInfo( MouseInfo mi )
    {
        pixelPosition = mi.pixelPosition.clone();
        leftButton = mi.leftButton;
        rightButton = mi.rightButton;
    }

    public Cursor2d getPixelPosition()
    {
        return pixelPosition;
    }

    public void setPixelPosition( int mx, int my )
    {
        pixelPosition.set( mx, my );
    }

    public void unsetPosition()
    {
        pixelPosition.undefine();
    }

    public boolean isLeftButton()
    {
        return leftButton;
    }

    public void setLeftButton( boolean b )
    {
        leftButton = b;
    }

    public boolean isRightButton()
    {
        return rightButton;
    }

    public void setRightButton( boolean b )
    {
        rightButton = b;
    }

    // Object interface

    @Override
    public MouseInfo clone()
    {
        //        MouseInfo res = new MouseInfo();
        //        res.pixelPosition = pixelPosition.clone();
        //        res.leftButton = leftButton;
        //        res.rightButton = rightButton;
        //        return res;
        return new MouseInfo( this );
    }
}
