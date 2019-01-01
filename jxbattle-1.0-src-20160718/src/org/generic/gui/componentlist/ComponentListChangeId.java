package org.generic.gui.componentlist;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum ComponentListChangeId implements MVCModelChangeId
{
    /**
     * horizontal/vertical unit/block increment value changed
     */
    HorizontalUnitScrollChanged, HorizontalBlockScrollChanged, VerticalUnitScrollChanged, VerticalBlockScrollChanged,

    /**
     * current component will change (sent before CurrentComponentChanged) 
     */
    //    PreComponentChange,

    /**
     * currently selected component has changed
     */
    CurrentComponentChanged,

    /**
     * a component has been simple clicked
     */
    ComponentSimpleClicked,

    /**
     * a component has been double clicked
     */
    ComponentDoubleClicked,

    /**
     * list component has been resized
     */
    Resize,

    /**
     * "display border on currently selected component" setting has changed
     */
    BorderOnCurrentChanged,

    /**
     * background color has changed
     */
    BackgroundColorChanged,

    ;

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public ComponentListChangeId[] getValues()
    {
        return values();
    }

    @Override
    public ComponentListChangeId getValueOf( int val )
    {
        for ( ComponentListChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for ComponentListChangeId enum " + val );
    }

    @Override
    public ComponentListChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
