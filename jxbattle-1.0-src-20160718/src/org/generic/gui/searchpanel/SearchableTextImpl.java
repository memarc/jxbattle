package org.generic.gui.searchpanel;

import java.util.ArrayList;
import java.util.List;

import org.generic.bean.Pair;
import org.generic.bean.cursor2d.Cursor2d;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public abstract class SearchableTextImpl extends MVCModelImpl implements SearchableText
{
    private List<SearchOccurrence> searchOccurrences;

    private String searchQuery;

    private boolean caseSensitive;

    private int currentSearchOccurrenceIndex;

    public SearchableTextImpl()
    {
        searchOccurrences = null;
        currentSearchOccurrenceIndex = -1;
    }

    @Override
    public void notifySearchableTextChanged()
    {
        MVCModelChange change = new MVCModelChange( this, this, SearchPanelModelChangeId.SearchableTextChanged );
        notifyObservers( change );
    }

    @Override
    public int getOccurrenceCount()
    {
        if ( searchOccurrences == null )
            computeSearchResults();

        return searchOccurrences.size();
    }

    @Override
    public String getSearchQuery()
    {
        return searchQuery;
    }

    @Override
    public void setSearchQuery( String search, boolean cs )
    {
        searchQuery = search;
        caseSensitive = cs;
        computeSearchResults();
    }

    //    protected void updateSearchResults()
    //    {
    //        searchOccurrences = new ArrayList<SearchOccurrence>();
    //
    //        if ( searchQuery != null && searchQuery.length() > 0 )
    //        {
    //            String needle = caseSensitive ? searchQuery : searchQuery.toLowerCase();
    //            String hayStack = caseSensitive ? getSearchableText() : getSearchableText().toLowerCase();
    //
    //            //            int ti = 0;
    //            int start = 0;
    //            int found = -1;
    //            do
    //            {
    //                found = hayStack.indexOf( needle, start );
    //                if ( found != -1 )
    //                {
    //                    //                    searchOccurrences.add( new SearchOccurrence( found, ti ) );
    //                    searchOccurrences.add( new SearchOccurrence( found ) );
    //                    start = found + 1;
    //                }
    //            }
    //            while ( found != -1 );
    //
    //            //            ti++;
    //        }
    //
    //        MVCModelChange change = new MVCModelChange( this, this, SearchPanelModelChangeId.SearchResultsChanged );
    //        notifyObservers( change );
    //    }

    @Override
    public void resetSearchResults()
    {
        searchOccurrences = new ArrayList<SearchOccurrence>();
    }

    private void computeSearchResults()
    {
        resetSearchResults();

        if ( searchQuery != null && searchQuery.length() > 0 )
        {
            String needle = caseSensitive ? searchQuery : searchQuery.toLowerCase();
            int ln = needle.length();

            for ( int l = 0; l < getSearchableLineCount(); l++ )
            {
                String hayStack = caseSensitive ? getSearchableLine( l ) : getSearchableLine( l ).toLowerCase();

                int start = 0;
                int found;
                do
                {
                    //                    found = hayStack.indexOf( needle, start );
                    Pair<Integer, Integer> ind = findNeedle( hayStack, needle, start, caseSensitive );
                    found = ind.getLeft();
                    if ( found != -1 )
                    {
                        //                        searchOccurrences.add( new SearchOccurrence( found ) );
                        searchOccurrences.add( new SearchOccurrence( found, l, ind.getRight() ) );
                        start = found + ln;
                    }
                }
                while ( found != -1 );
            }
        }

        MVCModelChange change = new MVCModelChange( this, this, SearchPanelModelChangeId.SearchResultsChanged );
        notifyObservers( change );
    }

    public Pair<Integer, Integer> findNeedle( String hayStack, String needle, int startPosition, boolean caseSensitive )
    {
        return new Pair<Integer, Integer>( hayStack.indexOf( needle, startPosition ), needle.length() );
    }

    public List<SearchOccurrence> getSearchOccurrences()
    {
        return searchOccurrences;
    }

    @Override
    public void setCurrentOccurrenceIndex( Object sender, int index )
    {
        currentSearchOccurrenceIndex = index;
        MVCModelChange change = new MVCModelChange( sender, this, SearchPanelModelChangeId.CurrentOccurrenceChanged, index );
        notifyObservers( change );
    }

    protected int getCurrentOccurrenceIndex()
    {
        return currentSearchOccurrenceIndex;
    }

    @Override
    public Cursor2d getCurrentSearchOccurrencePosition()
    {
        if ( currentSearchOccurrenceIndex == -1 )
            return new Cursor2d();

        return searchOccurrences.get( currentSearchOccurrenceIndex ).toCursor2d();
    }
}
