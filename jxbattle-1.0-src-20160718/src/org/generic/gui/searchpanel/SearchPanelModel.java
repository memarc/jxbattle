package org.generic.gui.searchpanel;

import java.util.ArrayList;
import java.util.List;

import org.generic.NumericUtils;
import org.generic.bean.Pair;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.string.StringUtils;

public class SearchPanelModel extends MVCModelImpl implements MVCModelObserver
{
    private String searchQuery; // searched occurrence, ie. needle

    //    private String targetText; // searched text, ie. haystack
    //private List<String> targetTexts; // searched texts, ie. haystacks
    private List<SearchableText> targets; // searched texts, ie. haystacks

    //    private List<Integer> searchOccurrences;
    //    private List<SearchOccurrence> searchOccurrences;

    private int currentSearchOccurrenceIndex;

    private boolean allowGotoPrevious;

    private boolean allowGotoNext;

    private boolean allowGotoFirst;

    private boolean allowGotoLast;

    private boolean caseSensitive;

    private boolean enable;

    private boolean immediateSearch;

    public SearchPanelModel()
    {
        resetSearch();
        caseSensitive = false;
        //        targetTexts = new ArrayList<String>();
        targets = new ArrayList<>();
    }

    private void resetSearch()
    {
        currentSearchOccurrenceIndex = -1;
        allowGotoPrevious = false;
        allowGotoNext = false;
        allowGotoFirst = false;
        allowGotoLast = false;
    }

    private void dispatchSearchQuery()
    {
        for ( SearchableText st : targets )
            st.resetSearchResults();

        for ( SearchableText st : targets )
            st.setSearchQuery( searchQuery, caseSensitive );
    }

    public boolean getCaseSensitive()
    {
        return caseSensitive;
    }

    public void setCaseSensitive( Object sender, boolean b )
    {
        boolean changed = !NumericUtils.booleanIdentity( caseSensitive, b );
        if ( changed )
        {
            caseSensitive = b;

            resetSearch();
            //getSearchOcurrences( sender );
            if ( immediateSearch )
                dispatchSearchQuery();

            MVCModelChange change = new MVCModelChange( sender, this, SearchPanelModelChangeId.CaseSensitivityChanged );
            notifyObservers( change );
        }
    }

    boolean getImmediateSearch()
    {
        return immediateSearch;
    }

    public void setImmediateSearch( Object sender, boolean b )
    {
        boolean changed = !NumericUtils.booleanIdentity( immediateSearch, b );
        if ( changed )
        {
            immediateSearch = b;

            MVCModelChange change = new MVCModelChange( sender, this, SearchPanelModelChangeId.ImmediateSearchChanged );
            notifyObservers( change );
        }
    }

    public void runSearch( Object sender )
    {
        resetSearch();
        dispatchSearchQuery();
        firstOccurrence( sender );
    }

    public void setSearchQuery( Object sender, String s )
    {
        boolean changed = !StringUtils.equalsNotNull( searchQuery, s );
        if ( changed )
        {
            resetSearch();
            //            getSearchOcurrences( sender );

            searchQuery = s;

            if ( immediateSearch )
                dispatchSearchQuery();

            firstOccurrence( sender );

            //            MVCModelChange change = new MVCModelChange( sender, this, SearchPanelModelChangeId.SearchResultsChanged, search );
            //            notifyObservers( change );
        }
    }

    //    public List<Integer> getSearchOcurrences( Object sender )
    //    {
    //        if ( searchOccurrences == null )
    //        {
    //            searchOccurrences = new ArrayList<Integer>();
    //            int start = 0;
    //            int found = -1;
    //
    //            if ( targetText != null && search != null && search.length() > 0 )
    //            {
    //                String needle = caseSensitive ? search : search.toLowerCase();
    //                String hayStack = caseSensitive ? targetText : targetText.toLowerCase();
    //
    //                do
    //                {
    //                    found = hayStack.indexOf( needle, start );
    //                    if ( found != -1 )
    //                    {
    //                        searchOccurrences.add( Integer.valueOf( found ) );
    //                        start = found + 1;
    //                    }
    //                }
    //                while ( found != -1 );
    //            }
    //
    //            if ( searchOccurrences.size() > 0 )
    //                setCurrentOccurrenceIndex( sender, 0 );
    //            else
    //                setCurrentOccurrenceIndex( sender, -1 );
    //        }
    //
    //        return searchOccurrences;
    //    }

    //    public List<SearchOccurrence> getSearchOcurrences( Object sender )
    //    {
    //        if ( searchOccurrences == null )
    //        {
    //            searchOccurrences = new ArrayList<SearchOccurrence>();
    //            int start = 0;
    //            int found = -1;
    //
    //            if ( search != null && search.length() > 0 )
    //            {
    //                String needle = caseSensitive ? search : search.toLowerCase();
    //
    //                int ti = 0;
    //                for ( String targetText : targetTexts )
    //                {
    //                    String hayStack = caseSensitive ? targetText : targetText.toLowerCase();
    //
    //                    do
    //                    {
    //                        found = hayStack.indexOf( needle, start );
    //                        if ( found != -1 )
    //                        {
    //                            searchOccurrences.add( new SearchOccurrence( found, ti ) );
    //                            start = found + 1;
    //                        }
    //                    }
    //                    while ( found != -1 );
    //
    //                    ti++;
    //                }
    //            }
    //
    //            if ( searchOccurrences.size() > 0 )
    //                setCurrentOccurrenceIndex( sender, 0 );
    //            else
    //                setCurrentOccurrenceIndex( sender, -1 );
    //        }
    //
    //        return searchOccurrences;
    //    }

    String getSearchQuery()
    {
        return searchQuery;
    }

    public int getSearchQueryLength()
    {
        if ( searchQuery == null )
            return 0;
        return searchQuery.length();
    }

    boolean hasSearchQuery()
    {
        return searchQuery != null && searchQuery.length() != 0;
    }

    //    boolean isSearchOccurrenceFound()
    //    {
    //        //        if ( hasSearchQuery() )
    //        return getCurrentSearchOccurrenceIndex() >= 0;
    //
    //        //        return false;
    //    }

    boolean isSearchNotFound()
    {
        if ( hasSearchQuery() )
            return getCurrentSearchOccurrenceIndex() >= 0;

        return true;
    }

    private Pair<Integer, Integer> parseOccurrenceIndex()
    {
        int occurrenceInTargetIndex = getCurrentSearchOccurrenceIndex();
        if ( occurrenceInTargetIndex != -1 )
        {
            int targetIndex = 0;
            for ( SearchableText st : targets )
            {
                int n = st.getOccurrenceCount();
                if ( occurrenceInTargetIndex < n )
                    return new Pair<>( targetIndex, occurrenceInTargetIndex );
                occurrenceInTargetIndex -= n;
                targetIndex++;
            }
        }

        return null;
    }

    private Pair<Integer, Integer> getFirstOccurrenceIndex()
    {
        if ( getCurrentSearchOccurrenceIndex() != -1 )
        {
            int targetIndex = 0;
            for ( SearchableText st : targets )
            {
                int n = st.getOccurrenceCount();
                if ( n > 0 )
                    return new Pair<>( targetIndex, 0 );
                targetIndex++;
            }
        }

        return null;
    }

    private Pair<Integer, Integer> getLastOccurrenceIndex()
    {
        if ( getCurrentSearchOccurrenceIndex() != -1 )
        {
            int targetIndex = targets.size() - 1;
            while ( targetIndex >= 0 )
            {
                int n = targets.get( targetIndex ).getOccurrenceCount();
                if ( n > 0 )
                    return new Pair<>( targetIndex, n - 1 );
                targetIndex--;
            }
        }

        return null;
    }

    private boolean isFirstOccurrenceIndex( Pair<Integer, Integer> ind )
    {
        Pair<Integer, Integer> first = getFirstOccurrenceIndex();
        if ( first != null )
            return first.getLeft() == ind.getLeft() && first.getRight() == ind.getRight();
        return false;
    }

    private boolean isLastOccurrenceIndex( Pair<Integer, Integer> ind )
    {
        Pair<Integer, Integer> first = getLastOccurrenceIndex();
        if ( first != null )
            return first.getLeft() == ind.getLeft() && first.getRight() == ind.getRight();
        return false;
    }

    public int getCurrentSearchOccurrenceIndex()
    {
        if ( currentSearchOccurrenceIndex < 0 )
        {
            for ( SearchableText st : targets )
                if ( st.getOccurrenceCount() > 0 )
                {
                    currentSearchOccurrenceIndex = 0;
                    break;
                }
        }

        return currentSearchOccurrenceIndex;
    }

    void setCurrentOccurrenceIndex( Object sender, int index )
    {
        currentSearchOccurrenceIndex = index;

        if ( currentSearchOccurrenceIndex < 0 )
            currentSearchOccurrenceIndex = -1;
        else if ( currentSearchOccurrenceIndex >= getTotalSearchOccurrenceCount() )
            currentSearchOccurrenceIndex = -1;

        //        MVCModelChange change = new MVCModelChange( sender, this, SearchPanelModelChangeId.CurrentOccurrenceChanged, currentSearchOccurrenceIndex );
        //        notifyObservers( change );

        Pair<Integer, Integer> p = parseOccurrenceIndex();
        if ( p != null )
        {
            setAllowGotoPrevious( sender, !isFirstOccurrenceIndex( p ) );
            setAllowGotoNext( sender, !isLastOccurrenceIndex( p ) );
            setAllowGotoFirst( sender, !isFirstOccurrenceIndex( p ) );
            setAllowGotoLast( sender, !isLastOccurrenceIndex( p ) );

            for ( int t = 0; t < targets.size(); t++ )
            {
                if ( t == p.getLeft() )
                    targets.get( t ).setCurrentOccurrenceIndex( sender, p.getRight() );
                else
                    targets.get( t ).setCurrentOccurrenceIndex( sender, -1 );
            }
        }
        else
        {
            setAllowGotoPrevious( sender, false );
            setAllowGotoNext( sender, false );
        }
    }

    void firstOccurrence( Object sender )
    {
        setCurrentOccurrenceIndex( sender, 0 );
    }

    void previousOccurrence( Object sender )
    {
        setCurrentOccurrenceIndex( sender, currentSearchOccurrenceIndex - 1 );
    }

    void nextOccurrence( Object sender )
    {
        setCurrentOccurrenceIndex( sender, currentSearchOccurrenceIndex + 1 );
    }

    void lastOccurrence( Object sender )
    {
        setCurrentOccurrenceIndex( sender, getTotalSearchOccurrenceCount() - 1 );
    }

    //    public int getCurrentSearchOccurrencePosition()
    //    {
    //        Pair<Integer, Integer> p = parseOccurrenceIndex();
    //        if ( p != null )
    //        {
    //            //        if ( currentSearchOccurrenceIndex >= 0 && currentSearchOccurrenceIndex < searchOccurrences.size() )
    //            //            return searchOccurrences.get( currentSearchOccurrenceIndex ).position;
    //
    //            return targets.get( p.getLeft() ).getSearchOccurrences().get( p.getRight() ).getPosition();
    //        }
    //
    //        return -1;
    //    }

    boolean getAllowGotoPrevious()
    {
        //        if ( hasSearch() )
        return allowGotoPrevious;

        //        return false;
    }

    private void setAllowGotoPrevious( Object sender, boolean b )
    {
        boolean changed = !NumericUtils.booleanIdentity( allowGotoPrevious, b );
        if ( changed )
        {
            allowGotoPrevious = b;
            MVCModelChange change = new MVCModelChange( sender, this, SearchPanelModelChangeId.CanGotoPreviousChanged, allowGotoPrevious );
            notifyObservers( change );
        }
    }

    boolean getAllowGotoNext()
    {
        //            if ( hasSearch() )
        return allowGotoNext;

        //            return false;
    }

    private void setAllowGotoNext( Object sender, boolean b )
    {
        boolean changed = !NumericUtils.booleanIdentity( allowGotoNext, b );
        if ( changed )
        {
            allowGotoNext = b;
            MVCModelChange change = new MVCModelChange( sender, this, SearchPanelModelChangeId.CanGotoNextChanged, allowGotoNext );
            notifyObservers( change );
        }
    }

    boolean getAllowGotoFirst()
    {
        return allowGotoFirst;
    }

    private void setAllowGotoFirst( Object sender, boolean b )
    {
        boolean changed = !NumericUtils.booleanIdentity( allowGotoFirst, b );
        if ( changed )
        {
            allowGotoFirst = b;
            MVCModelChange change = new MVCModelChange( sender, this, SearchPanelModelChangeId.CanGotoFirstChanged, allowGotoFirst );
            notifyObservers( change );
        }
    }

    boolean getAllowGotoLast()
    {
        return allowGotoLast;
    }

    private void setAllowGotoLast( Object sender, boolean b )
    {
        boolean changed = !NumericUtils.booleanIdentity( allowGotoLast, b );
        if ( changed )
        {
            allowGotoLast = b;
            MVCModelChange change = new MVCModelChange( sender, this, SearchPanelModelChangeId.CanGotoLastChanged, allowGotoLast );
            notifyObservers( change );
        }
    }

    public boolean getEnable()
    {
        return enable;
    }

    public void setEnable( Object sender, boolean b )
    {
        boolean changed = !NumericUtils.booleanIdentity( enable, b );
        if ( changed )
        {
            enable = b;
            MVCModelChange change = new MVCModelChange( sender, this, SearchPanelModelChangeId.EnableSearchChanged, enable );
            notifyObservers( change );
        }
    }

    //    public void setTargetText( Object sender, String text )
    //    {
    //        targetText = text;
    //        resetSearch();
    //        getSearchOcurrences( sender );
    //    }

    //    public void clearAllTargetTexts( Object sender )
    //    {
    //        targetTexts.clear();
    //        resetSearch();
    //        getSearchOcurrences( sender );
    //    }
    //
    //    public void addTargetText( Object sender, String text )
    //    {
    //        targetTexts.add( text );
    //        resetSearch();
    //        getSearchOcurrences( sender );
    //    }

    public void clearAllSearchableTexts()
    {
        resetSearch();

        for ( SearchableText st : targets )
            //        {
            //            st.removeObserver( this );
            //            st.removeObservers( getObservers() );
            removeRelatedModel( st );
        //        }

        targets.clear();
    }

    public void addSearchableText( SearchableText st )
    {
        resetSearch();

        targets.add( st );
        //        st.addObserver( this );
        //        st.addObservers( getObservers() );
        addRelatedModel( st );

        if ( immediateSearch )
            dispatchSearchQuery();
    }

    //    boolean isFirstSearchOccurrence()
    //    {
    //        int tot = getTotalSearchOccurrenceCount();
    //        if ( tot > 0 )
    //            return currentSearchOccurrenceIndex == 0;
    //        return false;
    //    }

    int getTotalSearchOccurrenceCount()
    {
        int res = 0;
        for ( SearchableText st : targets )
            res += st.getOccurrenceCount();
        return res;
    }

    //    boolean isLastSearchOccurrence()
    //    {
    //        int tot = getTotalSearchOccurrenceCount();
    //        if ( tot > 0 )
    //            return currentSearchOccurrenceIndex == tot - 1;
    //        return false;
    //    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof SearchPanelModelChangeId )
            switch ( (SearchPanelModelChangeId)change.getChangeId() )
            {
                case SearchResultsChanged:
                    resetSearch();
                    setCurrentOccurrenceIndex( this, getCurrentSearchOccurrenceIndex() );
                    break;

                default:
                    break;
            }
    }

    @Override
    public void subscribeModel()
    {
        for ( SearchableText st : targets )
        {
            st.addObserver( this );
            //            st.addObservers( getObservers() );
        }
    }

    @Override
    public void unsubscribeModel()
    {
        clearAllSearchableTexts();
    }

    @Override
    public void close()
    {
        unsubscribeModel();
    }
}
