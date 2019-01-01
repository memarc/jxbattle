package org.generic.gui.searchpanel;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum SearchPanelModelChangeId implements MVCModelChangeId
{
    /**
     * searchable text changed
     */
    SearchableTextChanged,

    /**
     * list of search occurrences changed
     */
    SearchResultsChanged,

    /**
     * current occurrence changed
     */
    CurrentOccurrenceChanged,

    /**
     * caret position changed
     */
    //    CaretPositionChanged,

    /**
     * "goto previous/next occurrence" buttons state
     */
    CanGotoPreviousChanged, CanGotoNextChanged,

    /**
     * "goto first/last occurrence" buttons state
     */
    CanGotoFirstChanged, CanGotoLastChanged,

    /**
     * search sensitivity to case changed 
     */
    CaseSensitivityChanged,

    /**
     * panel enabling changed
     */
    EnableSearchChanged,

    /**
     * "immediate search" mode changed
     */
    ImmediateSearchChanged,

    //
    ;

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public SearchPanelModelChangeId[] getValues()
    {
        return values();
    }

    @Override
    public SearchPanelModelChangeId getValueOf( int val )
    {
        for ( SearchPanelModelChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for SearchPanelModelChangeId enum " + val );
    }

    @Override
    public SearchPanelModelChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
