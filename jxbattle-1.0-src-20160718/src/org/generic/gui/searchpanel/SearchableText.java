package org.generic.gui.searchpanel;

import java.util.List;

import org.generic.bean.cursor2d.Cursor2d;
import org.generic.mvc.model.observer.MVCModel;

public interface SearchableText extends MVCModel
{
    public void resetSearchResults();

    public void notifySearchableTextChanged();

    //    public String getSearchableText();
    public int getSearchableLineCount();

    public String getSearchableLine( int l );

    public String getSearchQuery();

    public void setSearchQuery( String search, boolean caseSensitive );

    public List<SearchOccurrence> getSearchOccurrences();

    public int getOccurrenceCount();

    public void setCurrentOccurrenceIndex( Object sender, int index );

    public Cursor2d getCurrentSearchOccurrencePosition();
}
