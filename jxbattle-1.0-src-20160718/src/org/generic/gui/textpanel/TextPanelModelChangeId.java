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

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum TextPanelModelChangeId implements MVCModelChangeId
{
    /**
     * font changed
     */
    TextPanel_FontChanged,

    /**
     * text content changed
     */
    TextPanel_TextChanged,

    /**
     * text background color changed
     */
    TextPanel_BackgroundColorChanged,

    /**
     * text foreground color changed
     */
    TextPanel_ForegroundColorChanged,

    /**
     * hover cursor coordinates changed
     */
    TextPanel_HoverCursorChanged,

    /**
     * cursor text coordinates changed
     */
    TextPanel_CaretCursorChanged,

    /**
     * cursor pixel coordinates updated
     */
    TextPanel_CaretPixelPositionChanged,

    /**
     * done with selection currently under construction with mouse
     */
    TextPanel_BuildingSelectionCompleted,

    /**
     * currently mouse edited selection has been removed
     */
    TextPanel_BuildingSelectionCleared,

    /**
     * a text selection has been clicked
     */
    TextPanel_SelectionClicked,

    /**
     * highlighted text selection changed
     */
    TextPanel_HighlightSelectionChanged,

    /**
     * a text area has been selected with mouse
     */
    //TextPanel_TextSelection,

    /**
     * a search occurrence area has been added to the model
     */
    TextPanel_SearchOccurrenceAdded,

    /**
     * a text area has been added to the model
     */
    TextPanel_TextSelectionAdded,

    /**
     * a text area has been removed from the model
     */
    TextPanel_TextSelectionRemoved,

    /**
     * cursor under mouse coordinates changed (no button pressed)
     */
    TextPanel_MouseMove,

    /**
     * cursor under mouse coordinates changed (one button is pressed)
     */
    TextPanel_MouseisDragging,

    /**
     * mouse has been clicked
     */
    //TextPanel_MouseClick,

    /**
     * mouse wheel moved
     */
    TextPanel_MouseWheel,

    /**
     * mouse button has been pressed
     */
    TextPanel_MousePressed,

    /**
     * mouse button has been released (no move since mouse button press event)
     */
    TextPanel_MouseRelease,

    /**
     * mouse button has been released (mouse has moved since mouse button press event)
     */
    TextPanel_MouseDragEnd,

    /**
     * keyboard event
     */
    TextPanel_KeyTyped,

    /**
     * size of component has changed
     */
    TextPanel_PixelSizeChanged,

    /**
     * number of visible text lines has changed
     */
    TextPanel_VisibleVerticalCharCountChanged,

    ;

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public TextPanelModelChangeId[] getValues()
    {
        return values();
    }

    @Override
    public TextPanelModelChangeId getValueOf( int val )
    {
        for ( TextPanelModelChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for TextPanelModelChangeId enum " + val );
    }

    @Override
    public TextPanelModelChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}