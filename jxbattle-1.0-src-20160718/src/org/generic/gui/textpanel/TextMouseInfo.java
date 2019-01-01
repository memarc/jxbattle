package org.generic.gui.textpanel;

import org.generic.bean.cursor2d.Cursor2d;
import org.generic.gui.MouseInfo;

public class TextMouseInfo extends MouseInfo
{
    private Cursor2d charPosition;

    public TextMouseInfo()
    {
        charPosition = new Cursor2d();
    }

    public TextMouseInfo( TextMouseInfo tmi )
    {
        super( tmi );
        charPosition = tmi.charPosition.clone();
    }

    public Cursor2d getCharPosition()
    {
        return charPosition;
    }

    public void setCharPosition( Cursor2d c )
    {
        charPosition = c;
    }

    @Override
    public void unsetPosition()
    {
        super.unsetPosition();
        charPosition.undefine();
    }

    // Object interface

    @Override
    public TextMouseInfo clone()
    {
        //        TextMouseInfo res = new TextMouseInfo( this );
        //        res.charPosition = charPosition.clone();
        //        return res;
        return new TextMouseInfo( this );
    }
}
