package org.generic.gui;

import java.awt.event.InputEvent;

public class KeyInfo
{
    private int keyCode;

    private boolean shift;

    private boolean ctrl;

    private boolean pressedOrReleased;

    private long timestamp;

    public KeyInfo( int code, int modif )
    {
        this( code, modif, true );
    }

    public KeyInfo( int code, int modif, boolean pressed )
    {
        keyCode = code;
        shift = (modif & InputEvent.SHIFT_DOWN_MASK) != 0;
        ctrl = (modif & InputEvent.CTRL_DOWN_MASK) != 0;

        pressedOrReleased = pressed;
        //System.out.println( "KeyInfo() " + keyCode + " " + (shift ? "shift ON" : "shift OFF") );

        timestamp = System.currentTimeMillis();
    }

    public int getKeyCode()
    {
        return keyCode;
    }

    public boolean isShift()
    {
        return shift;
    }

    public boolean isCtrl()
    {
        return ctrl;
    }

    public boolean isPressedOrReleased()
    {
        return pressedOrReleased;
    }

    public void setPressedOrReleased( boolean b )
    {
        pressedOrReleased = b;
    }

    public long getTimestamp()
    {
        return timestamp;
    }
}
