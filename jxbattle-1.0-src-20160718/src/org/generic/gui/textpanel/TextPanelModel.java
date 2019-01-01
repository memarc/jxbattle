package org.generic.gui.textpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generic.bean.Pair;
import org.generic.bean.cursor2d.Cursor2d;
import org.generic.bean.cursor2d.Interval2dHelper;
import org.generic.gui.GuiUtils;
import org.generic.gui.KeyInfo;
import org.generic.gui.searchpanel.SearchOccurrence;
import org.generic.gui.searchpanel.SearchableTextImpl;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.string.StringUtils;

public class TextPanelModel extends SearchableTextImpl
{
    /**
     * exposed values
     */

    //private static Font defaultFont;

    private String text;

    /**
     * line vertical pixel size
     */
    private int linePixelHeight;

    /**
     * text lines
     */
    //    private List<String> text;
    private String[] lines;

    /**
     * text dimensions : number of lines
     */
    private int lineCount;

    /**
     * text dimensions : number of characters in longest line
     */
    private int maxLineWidth;

    /**
     * position of mouse in text (unit=character) 
     */
    private TextCursor hoverCursor;

    private boolean enableHoverCursor;

    /**
     * position of text cursor (caret)
     */
    private TextCursor caretCursor;

    private Rectangle caretPixelRectangle; // caret cursor pixel coordinates on  view

    private boolean enableCaretCursor; // caret position can be modified with mouse

    private boolean visibleCaretCursor; // caret is displayed

    /**
     * default text color
     */
    private ColorInfo defaultTextColorInfo;

    /**
     * specific character colors;
     */
    private List<ColorInfo> charColors;

    /**
     * default search occurrence color
     */
    private ColorInfo defaultSearchOccurrenceColorInfo;

    /**
     * current search occurrence color
     */
    private ColorInfo currentSearchOccurrenceColorInfo;

    private KeyInfo keyInfo;

    private TextMouseInfo mouseInfo; // current mouse info

    private TextMouseInfo pressMouseInfo; // mouse info linked to 'press mouse button' event

    private TextMouseInfo releaseMouseInfo; // mouse info linked to 'release mouse button' event

    /**
     * selections
     */

    private boolean enableSelections; // allow selecting text with mouse  

    private List<TextSelection> textSelections;

    //    private Interval2dList textSelections;

    /**
     * completed selection color
     */
    private ColorInfo defaultSelectionColorInfo;

    /**
     * selection under construction
     */
    private boolean enableBuildingSelection;

    private TextSelection buildingSelection; // text selection under construction/completed

    /**
     * selection under construction color
     */
    private ColorInfo buildingColorInfo;

    private TextSelection highlightedSelection; // currently active text selection

    /**
     * currently active text selection color
     */
    private ColorInfo highlightedColorInfo;

    /**
     * selection tooltips
     */

    private boolean enableTooltips;

    /**
     * search occurrences
     */

    /**
     * reserved selection layer level for search occurrences
     */
    private final static int SearchOccurrenceLevel = 1;

    /**
     * keep selection list sorted
     */
    private boolean keepSorted;

    //    private TextSelectionComparator selectionComparator;
    private Interval2dHelper intervalHelper;

    private int visibleVerticalCharCount;

    /**
     * automatically scroll view to search occurrence when the latter changes
     */
    //    private boolean autoScrollToSearchOccurrence;

    /**
     * mouse coordinates in pixel units
     */
    //private Cursor2d mousePixelCoords;

    /**
     * "overlapping selections allowed" flag
     */
    //private boolean allowOverlappingSelections;

    public TextPanelModel()
    {
        textSelections = new ArrayList<>();
        //        textSelections = new Interval2dList();

        charColors = new ArrayList<>();

        // default text color

        defaultTextColorInfo = new ColorInfo( Color.BLACK, GuiUtils.defaultSwingBackgroundColor );

        // text cursor

        caretCursor = new TextCursor();
        caretCursor.setColorInfo( Color.WHITE, Color.RED );
        enableCaretCursor = true;
        visibleCaretCursor = true;

        // hover cursor

        hoverCursor = new TextCursor();
        hoverCursor.setColorInfo( Color.WHITE, new Color( 130, 130, 130 ) );
        enableHoverCursor = true;

        // selection

        enableSelections = true;

        defaultSelectionColorInfo = new ColorInfo( Color.BLACK, new Color( 255, 200, 0 ) );

        buildingSelection = new TextSelection();
        buildingColorInfo = new ColorInfo( Color.BLACK, Color.LIGHT_GRAY );
        buildingSelection.setColor( buildingColorInfo );
        buildingSelection.undefine();

        highlightedSelection = null;
        highlightedColorInfo = new ColorInfo( Color.BLACK, new Color( 0, 200, 255 ) );

        enableTooltips = true;

        // mouse text coordinates

        mouseInfo = new TextMouseInfo();
        pressMouseInfo = new TextMouseInfo();
        releaseMouseInfo = new TextMouseInfo();

        // list sorting

        keepSorted = true;
        if ( keepSorted )
            //            selectionComparator = new TextSelectionComparator();
            intervalHelper = new Interval2dHelper( textSelections );

        //allowOverlappingSelections = false;

        // search

        defaultSearchOccurrenceColorInfo = new ColorInfo( Color.BLACK, Color.GREEN );
        currentSearchOccurrenceColorInfo = new ColorInfo( Color.BLACK, Color.RED );
        //        autoScrollToSearchOccurrence = true;
    }

    //    public boolean isEnableHoverCursor()
    //    {
    //        return enableHoverCursor;
    //    }
    //
    //    public void setEnableHoverCursor( boolean b )
    //    {
    //        enableHoverCursor = b;
    //    }
    //
    //    public boolean isEnableTextCursor()
    //    {
    //        return enableTextCursor;
    //    }
    //
    //    public void setEnableTextCursor( boolean b )
    //    {
    //        enableTextCursor = b;
    //    }

    //    public List<String> getText()
    //    {
    //        return text;
    //    }

    public String getText()
    {
        return text;
    }

    //    public void setText( Object sender, List<String> l )
    //    {
    //        text = l;
    //        notifyObservers( new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_TextChanged ) );
    //    }

    public void setText( Object sender, String s )
    {
        boolean changed = !StringUtils.equalsNotNull( text, s );
        if ( changed )
        {
            text = s;
            lineCount = 0;
            maxLineWidth = 0;
            lines = null;

            if ( text != null )
            {
                String s1 = text.replaceAll( "\r\n", "\n" );
                String s2 = s1.replaceAll( "\r", "\n" );
                lines = s2.split( "\n" );

                //        lineCount = 0;
                //        maxLineWidth = 0;
                //        if ( text != null )
                //        {
                //            int maxWidth = 0;
                //
                //            int len = text.length();
                //            for ( int i = 0; i < len; i++ )
                //            {
                //                char c = text.charAt( i );
                //                if ( c == '\n' )
                //                {
                //                    if ( i != len - 1 ) // ignore last CR
                //                        lineCount++;
                //
                //                    if ( maxWidth > maxLineWidth )
                //                    {
                //                        maxLineWidth = maxWidth;
                //                        maxWidth = 0;
                //                    }
                //                }
                //                else
                //                    maxWidth++;
                //            }
                //        }

                lineCount = lines.length;
                for ( int i = 0; i < lineCount; i++ )
                {
                    int n = lines[ i ].length();
                    //                    if ( n > maxLineWidth )
                    //                        maxLineWidth = n;
                    maxLineWidth = Math.max( maxLineWidth, n );
                }
            }

            notifyObservers( new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_TextChanged ) );

            //computeSearchResults();
            notifySearchableTextChanged();
        }
    }

    int getLinePixelHeight()
    {
        return linePixelHeight;
    }

    public void setLinePixelHeight( int h )
    {
        linePixelHeight = h;
    }

    public Color getTextBackgroundColor()
    {
        return defaultTextColorInfo.getBackgroundColor();
    }

    public void setTextBackgroundColor( Object sender, Color col )
    {
        boolean changed = defaultTextColorInfo.getBackgroundColor() != col;
        if ( changed )
        {
            defaultTextColorInfo.setBackgroundColor( col );
            notifyObservers( new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_BackgroundColorChanged ) );
        }
    }

    public void setTextForegroundColor( Object sender, Color col )
    {
        boolean changed = defaultTextColorInfo.getForegroundColor() != col;
        if ( changed )
        {
            defaultTextColorInfo.setForegroundColor( col );
            notifyObservers( new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_ForegroundColorChanged ) );
        }
    }

    public boolean isEnableHoverCursor()
    {
        return enableHoverCursor;
    }

    public void setEnableHoverCursor( boolean b )
    {
        enableHoverCursor = b;
    }

    public TextCursor getHoverCursor()
    {
        return hoverCursor;
    }

    private void setHoverCursor( Object sender, int cursorX, int cursorY )
    {
        if ( enableHoverCursor )
        {
            boolean changed = !hoverCursor.getPosition().equals( cursorX, cursorY );
            if ( changed )
            {
                //System.out.println( Utils.getTimeStamp() + " TextPanelModel.setHoverCursor sender " + sender );

                hoverCursor.getPosition().set( cursorX, cursorY );
                MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_HoverCursorChanged, hoverCursor.getPosition().clone() );
                //LogUtils.logModelChange( this, change );
                notifyObservers( change );
            }
        }
    }

    void setHoverCursor( Object sender, Cursor2d c )
    {
        if ( enableHoverCursor )
        {
            if ( c.isDefined() )
                setHoverCursor( sender, c.getX().getValue(), c.getY().getValue() );
            else
                unsetHoverCursor( sender );
        }
    }

    //    public void setHoverCursor( Object sender, TextCursor mi )
    //    {
    //        if ( mi.isPositionDefined() )
    //            setHoverCursor( sender, mi.getPosition() );
    //        else
    //            unsetHoverCursor( sender );
    //    }

    void unsetHoverCursor( Object sender )
    {
        if ( enableHoverCursor )
        {
            boolean changed = hoverCursor.getPosition().isDefined();
            if ( changed )
            {
                //System.out.println( Utils.getTimeStamp() + " TextPanelModel.unsetHoverCursor sender " + sender );

                hoverCursor.getPosition().undefine();
                MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_HoverCursorChanged, hoverCursor.getPosition().clone() );
                //LogUtils.logModelChange( this, change );
                notifyObservers( change );
            }
        }
    }

    public boolean isEnableCaretCursor()
    {
        return enableCaretCursor;
    }

    public void setEnableCaretCursor( boolean b )
    {
        enableCaretCursor = b;
    }

    private boolean isVisibleCaretCursor()
    {
        return enableCaretCursor && visibleCaretCursor;
    }

    public void setVisibleCaretCursor( boolean b )
    {
        visibleCaretCursor = b;
    }

    boolean isEnableTooltips()
    {
        return enableTooltips;
    }

    public void setEnableTooltips( boolean b )
    {
        enableTooltips = b;
    }

    //    TextCursor getCaretCursor()
    //    {
    //        return caretCursor;
    //    }

    //    private void setCaretCursor( Object sender, Cursor2d cursor )
    //    {
    //        boolean changed = !getCaretCursor().getPosition().equals( cursor );
    //        if ( changed )
    //        {
    //            caretCursor.getPosition().set( cursor );
    //            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_CaretCursorChanged, getCaretCursor().getPosition() );
    //            //LogUtils.logModelChange( this, change );
    //            notifyObservers( change );
    //        }
    //    }
    //
    //    private void unsetCaretCursor( Object sender )
    //    {
    //        boolean changed = getCaretCursor().getPosition().isDefined();
    //        if ( changed )
    //        {
    //            getCaretCursor().getPosition().undefine();
    //            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_CaretCursorChanged, getCaretCursor().getPosition() );
    //            //LogUtils.logModelChange( this, change );
    //            notifyObservers( change );
    //        }
    //    }

    public TextCursor getCaretCursor()
    {
        return caretCursor;
    }

    private void notifyCaretCursorChange( Object sender )
    {
        MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_CaretCursorChanged, caretCursor.getPosition() );
        //LogUtils.logModelChange( this, change );
        notifyObservers( change );
    }

    public void setCaretCursor( Object sender, Cursor2d c )
    {
        if ( enableCaretCursor )
        {
            boolean changed;

            if ( c == null || !c.isDefined() )
            {
                changed = caretCursor.getPosition().isDefined();
                caretCursor.getPosition().undefine();
            }
            else
            {
                //changed = !caretCursor.getPosition().equals( c );
                changed = true;
                caretCursor.getPosition().set( c.getX().getValue(), c.getY().getValue() );
            }

            if ( changed )
                notifyCaretCursorChange( sender );
        }
    }

    public void setCaretCursor( Object sender, int x, int y )
    {
        if ( enableCaretCursor )
        {
            boolean changed = !caretCursor.getPosition().equals( x, y );
            if ( changed )
                notifyCaretCursorChange( sender );
        }
    }

    public void caretCursorToLeft( Object sender )
    {
        if ( enableCaretCursor )
        {
            caretCursor.getPosition().toLeft();
            notifyCaretCursorChange( sender );
        }
    }

    public void caretCursorToRight( Object sender )
    {
        if ( enableCaretCursor )
        {
            caretCursor.getPosition().toRight();
            notifyCaretCursorChange( sender );
        }
    }

    //    public List<TextSelection> getTextSelections()
    //    {
    //        return textSelections;
    //    }

    //    private class TextSelectionIterator implements Iterator<TextSelection>
    //    {
    //        private Iterator<Interval2d> iterator;
    //
    //        public TextSelectionIterator()
    //        {
    //            iterator = textSelections.iterator();
    //        }
    //
    //        @Override
    //        public boolean hasNext()
    //        {
    //            return iterator.hasNext();
    //        }
    //
    //        @Override
    //        public TextSelection next()
    //        {
    //            return (TextSelection)iterator.next();
    //        }
    //
    //        @Override
    //        public void remove()
    //        {
    //            iterator.remove();
    //        }
    //    }
    //
    //    public TextSelectionIterator getTextSelectionIterator()
    //    {
    //        return new TextSelectionIterator();
    //    }

    public Iterator<TextSelection> getTextSelectionIterator()
    {
        return textSelections.iterator();
    }

    private void addTextSelection( Object sender, TextSelection ts, TextPanelModelChangeId cid )
    {
        if ( ts.getColor() == null )
            ts.setColor( defaultSelectionColorInfo );
        textSelections.add( ts );

        if ( keepSorted )
            //Collections.sort( textSelections, selectionComparator );
            //            selectionComparator.sort( (List<Interval2d>)textSelections );
            intervalHelper.sort();

        MVCModelChange change = new MVCModelChange( sender, this, cid, ts );
        notifyObservers( change );
    }

    public void addTextSelection( Object sender, TextSelection ts )
    {
        addTextSelection( sender, ts, TextPanelModelChangeId.TextPanel_TextSelectionAdded );
    }

    void setKeepSorted( boolean b )
    {
        keepSorted = b;
    }

    //    /**
    //     * compute index of a TextSelection would have if inserted in list
    //     */
    //    public int possibleIndexOf( TextSelection textSelection )
    //    {
    //        if ( keepSorted )
    //        {
    //            //1 2 5 6
    //            //    4
    //
    //            int res = 0;
    //            for ( TextSelection ts : textSelections )
    //            {
    //                if ( selectionComparator.compare( ts, textSelection ) == 1 )
    //                    return res;
    //                res++;
    //            }
    //        }
    //
    //        return textSelections.size();
    //    }

    //    TextSelection getSelectionAt( int tx, int ty )
    //    {
    //        for ( TextSelection ts : textSelections )
    //            if ( ts.includesCoordinates( tx, ty ) )
    //                return ts;
    //
    //        return null;
    //    }

    public TextSelection getSelectionAt( int tx, int ty )
    {
        //        List<TextSelection> layers = new ArrayList<TextSelection>();
        //
        //        for ( TextSelection ts : textSelections )
        //            if ( ts.includesCoordinates( tx, ty ) )
        //                layers.add( ts );

        TextSelection res = null;
        int max = Integer.MIN_VALUE;
        for ( TextSelection ts : textSelections )
            if ( ts.includesCoordinates( tx, ty ) )
                if ( ts.getLayerLevel() > max )
                {
                    max = ts.getLayerLevel();
                    res = ts;
                }

        return res;
    }

    public TextSelection getSelectionUnderMouse()
    {
        if ( mouseInfo.getCharPosition().isDefined() )
        {
            int tx = mouseInfo.getCharPosition().getX().getValue();
            int ty = mouseInfo.getCharPosition().getY().getValue();
            return getSelectionAt( tx, ty );
        }

        return null;
    }

    public TextSelection getLeftSelection( TextSelection tsel )
    {
        //        TextSelection res = null;
        //        for ( TextSelection ts : textSelections )
        //        {
        //            if ( intervalHelper.compare( tsel, ts ) == -1 )
        //                return res;
        //            res = ts;
        //        }
        //
        //        return res;

        return (TextSelection)intervalHelper.getLeftInterval( tsel );
    }

    //    private TextSelection getLeftSelection( Cursor2d c )
    //    {
    //        TextSelection res = null;
    //
    //        if ( c.isDefined() )
    //            for ( TextSelection ts : textSelections )
    //            {
    //                if ( ts.getEndCursor().isGreaterThan( c ) )
    //                    return res;
    //                res = ts;
    //            }
    //
    //        return res;
    //    }

    private TextSelection getLeftSelection( Cursor2d c )
    {
        return (TextSelection)intervalHelper.getLeftInterval( c );
    }

    public TextSelection getRightSelection( TextSelection tsel )
    {
        //        for ( TextSelection ts : textSelections )
        //            if ( intervalHelper.compare( tsel, ts ) == -1 )
        //                return ts;
        //
        //        return null;

        return (TextSelection)intervalHelper.getRightInterval( tsel );
    }

    //    private TextSelection getRightSelection( Cursor2d c )
    //    {
    //        if ( c.isDefined() )
    //            for ( TextSelection ts : textSelections )
    //                if ( ts.getStartCursor().isGreaterThan( c ) )
    //                    return ts;
    //
    //        return null;
    //    }

    private TextSelection getRightSelection( Cursor2d c )
    {
        return (TextSelection)intervalHelper.getRightInterval( c );
    }

    public TextSelection getSelectionAt( Cursor2d c )
    {
        if ( c.isDefined() )
            return getSelectionAt( c.getX().getValue(), c.getY().getValue() );

        return null;
    }

    public boolean hasInterval1d( int start, int end )
    {
        return intervalHelper.hasInterval1d( start, end ) != null;
    }

    public void deleteSelectionAt( Object sender, int tx, int ty )
    {
        TextSelection ts = getSelectionAt( tx, ty );
        //        if ( ts != null )
        //        {
        //            textSelections.remove( ts );
        //            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_TextSelectionRemoved, ts );
        //            //LogUtils.logModelChange( this, change );
        //            notifyObservers( change );
        //        }
        deleteSelection( sender, ts );

        if ( highlightedSelection != null )
            if ( highlightedSelection.includesCoordinates( tx, ty ) )
                unhighlightSelection( sender );
    }

    private void notifyDeleteSelection( Object sender, TextSelection ts )
    {
        MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_TextSelectionRemoved, ts );
        notifyObservers( change );
    }

    public void deleteSelection( Object sender, TextSelection ts )
    {
        if ( ts != null )
        {
            textSelections.remove( ts );
            notifyDeleteSelection( sender, ts );
        }
    }

    public void deleteSelections( Object sender )
    {
        Iterator<TextSelection> it = textSelections.iterator();
        while ( it.hasNext() )
        {
            TextSelection ts = it.next();
            it.remove();
            notifyDeleteSelection( sender, ts );
        }
    }

    public void deleteSelections( Object sender, int layer )
    {
        Iterator<TextSelection> it = textSelections.iterator();
        while ( it.hasNext() )
        {
            TextSelection ts = it.next();
            if ( ts.getLayerLevel() == layer )
            {
                it.remove();
                notifyDeleteSelection( sender, ts );
            }
        }
    }

    TextSelection getTextSelection( int index )
    {
        return textSelections.get( index );
    }

    //    public int getSelectionCount()
    //    {
    //        return textSelections.size();
    //    }

    public TextSelection getBuildingSelection()
    {
        return buildingSelection;
    }

    /**
     * @return true if mouse has been moved between press and release events
     */
    private boolean mouseHasDragged()
    {
        return !pressMouseInfo.getCharPosition().equals( releaseMouseInfo.getCharPosition() );
    }

    //    public void selectText( Object sender )
    //    {qsd
    //        TextSelection ts = currentSelection.clone();
    //        ts.setDefined( true );
    //        ts.setColor( selectionColorInfo );
    //        addTextSelection( ts );
    //        currentSelection.setDefined( false );
    //
    //        MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_NewTextSelection, ts );
    //        notifyObservers( change );
    //    }

    ColorInfo getCharColor( int tx, int ty )
    {
        if ( enableHoverCursor && hoverCursor.getPosition().equals( tx, ty ) )
            return hoverCursor.getColorInfo();

        if ( isVisibleCaretCursor() && caretCursor.getPosition().equals( tx, ty ) )
            return caretCursor.getColorInfo();

        if ( isEnableBuildingSelection() && buildingSelection.isDefined() && buildingSelection.includesCoordinates( tx, ty ) )
            return buildingSelection.getColor();

        if ( highlightedSelection != null && highlightedSelection.includesCoordinates( tx, ty ) )
            return highlightedColorInfo;

        TextSelection ts = getSelectionAt( tx, ty );
        if ( ts != null )
            return ts.getColor();

        for ( ColorInfo ci : charColors )
            if ( ci.getPosition().equals( tx, ty ) )
                return ci;

        return defaultTextColorInfo;
    }

    public List<ColorInfo> getCharColors()
    {
        return charColors;
    }

    private boolean isInsideText( Cursor2d textCursor )
    {
        int tx = textCursor.getX().getValue();
        int ty = textCursor.getY().getValue();

        //        if ( ty >= 0 && ty < text.size() )
        if ( ty >= 0 && ty < lineCount )
        {
            //            String s = text.get( ty );
            String s = lines[ ty ];
            return tx >= 0 && tx < s.length();
        }

        return false;
    }

    //    void unsetMouseCursor( Object sender )
    //    {
    //        boolean changed = mouseCursor.isDefined();
    //        if ( changed )
    //        {
    //            mouseCursor.undefine();
    //            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_MouseCursorChanged, mouseCursor );
    //            //LogUtils.logModelChange( this, change );
    //            notifyObservers( change );
    //        }
    //    }

    //    public KeyInfo getKeyInfo()
    //    {
    //        return keyInfo;
    //    }

    //    ColorInfo getTextColorInfo()
    //    {
    //        return textColorInfo;
    //    }

    //    void mouseClick( Object sender )
    //    {
    //        MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_MouseClick, getHoverCursor().getPosition().clone() );
    //        //LogUtils.logModelChange( Thread.currentThread(), change );
    //        notifyObservers( change );
    //    }

    //    public boolean isShowSelecting()
    //    {
    //        return showSelecting;
    //    }

    //    public void setShowSelecting( boolean b )
    //    {
    //        showSelecting = b;
    //    }

    boolean isEnableSelections()
    {
        return enableSelections;
    }

    public void setEnableSelections( boolean b )
    {
        enableSelections = b;
    }

    //    void startCurrentSelection( Cursor2d c )
    //    {
    //        unsetHoverCursor( this );
    //        currentSelection.startSelection( c );
    //    }

    public void clearBuildingSelection( Object sender )
    {
        if ( isEnableBuildingSelection() )
        {
            boolean changed = buildingSelection.isDefined();
            if ( changed )
            {
                //            currentSelection.setDefined( false );
                //            currentSelection.getStartIndex().undefine();
                //            currentSelection.getEndIndex().undefine();
                buildingSelection.undefine();
                notifyObservers( new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_BuildingSelectionCleared ) );
            }
        }
    }

    /**
     * check that :
     * - does not start in the middle of another selection
     * - selection ends between the 2 consecutive selections
     */
    private boolean selectionOverlaps( Cursor2d start, Cursor2d end )
    {
        // check starts in the middle of another selection

        if ( getSelectionAt( start ) != null )
            return true;

        // check is after left neighbour end

        if ( end.isDefined() )
        {
            TextSelection left = getLeftSelection( start );
            if ( left != null )
                if ( !(end.isGreaterThan( left.getEndCursor() )) )
                    return true;

            // check is before right neighbour start

            TextSelection right = getRightSelection( start );
            if ( right != null )
                if ( !(right.getStartCursor().isGreaterThan( end )) )
                    return true;
        }

        return false;
    }

    //    void startCurrentSelection( Object sender )
    //    {
    //        System.out.println( "startCurrentSelection " + currentSelection );
    //
    //        Cursor2d c = mouseInfo.getCharPosition();
    //        unsetHoverCursor( sender );
    //        if ( !allowOverlappingSelections && getSelectionAt( c ) == null )
    //            currentSelection.startSelection( c );
    //    }
    //
    //    void modifyCurrentSelection( Object sender, Cursor2d c )
    //    {
    //        System.out.println( "modifyCurrentSelection " + currentSelection );
    //
    //        if ( allowOverlappingSelections )
    //            currentSelection.editSelection( c );
    //        else
    //        {
    //            if ( selectionOverlaps( currentSelection.getStartIndex(), c ) )
    //                clearCurrentSelection( sender );
    //            else
    //                currentSelection.editSelection( c );
    //        }
    //    }
    //
    //    void completeCurrentSelection( Object sender )
    //    {
    //        System.out.println( "completeCurrentSelection " + currentSelection );
    //
    //        if ( currentSelection.isDefined() )
    //        {
    //            currentSelection.completeSelection();
    //            if ( allowOverlappingSelections || !selectionOverlaps( currentSelection.getStartIndex(), currentSelection.getEndIndex() ) )
    //            {
    //                MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_CurrentSelectionCompleted );
    //                notifyObservers( change );
    //            }
    //        }
    //    }

    public boolean isEnableBuildingSelection()
    {
        return enableSelections && enableBuildingSelection;
    }

    public void setEnableBuildingSelection( boolean b )
    {
        enableBuildingSelection = b;
    }

    void startBuildingSelection()
    {
        //System.out.println( "startCurrentSelection " + currentSelection );
        if ( isEnableBuildingSelection() )
        {
            Cursor2d c = mouseInfo.getCharPosition();
            buildingSelection.startSelection( c );
        }
        else
            buildingSelection.undefine();
    }

    //    void modifyCurrentSelection( Object sender, Cursor2d c )
    void modifyBuildingSelection( Object sender )
    {
        //System.out.println( "modifyCurrentSelection " + currentSelection );
        if ( isEnableBuildingSelection() )
        {
            Cursor2d c = mouseInfo.getCharPosition();
            buildingSelection.editSelection( c );
            unsetHoverCursor( sender );
        }
    }

    void completeBuildingSelection( Object sender )
    {
        if ( isEnableBuildingSelection() )
        {
            buildingSelection.completeSelection();
            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_BuildingSelectionCompleted );
            notifyObservers( change );
        }
    }

    public boolean selectionOverlaps( TextSelection ts )
    {
        return selectionOverlaps( ts.getStartCursor(), ts.getEndCursor() );
    }

    public TextSelection getHighlightedSelection()
    {
        return highlightedSelection;
    }

    private void setHighlightedSelection( Object sender, TextSelection ts )
    {
        boolean changed = highlightedSelection != ts;
        if ( changed )
        {
            highlightedSelection = ts;
            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_HighlightSelectionChanged, highlightedSelection );
            notifyObservers( change );
        }
    }

    //    /**
    //     * optionally highlight text selection under mouse
    //     */
    //    void highlightSelection2( Object sender )
    //    {
    //        TextSelection newHighlight = null;
    //        int tx = mouseInfo.getCharPosition().getX().getValue();
    //        int ty = mouseInfo.getCharPosition().getY().getValue();
    //        for ( TextSelection ts : textSelections )
    //            if ( ts.includesCoordinates( tx, ty ) )
    //            {
    //                newHighlight = ts;
    //                break;
    //            }
    //
    //        //        boolean changed = highlightedSelection != oldHighlight;
    //        //        if ( changed )
    //        //        {
    //        //            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_HighlightSelectionChanged );
    //        //            notifyObservers( change );
    //        //        }
    //        setHighlightedSelection( sender, newHighlight );
    //    }

    /**
     * optionally notify a text selection is under mouse has been clicked
     */
    void notifySelectionClick( Object sender )
    {
        TextSelection ts = getSelectionUnderMouse();
        if ( ts != null )
        {
            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_SelectionClicked, ts );
            notifyObservers( change );
        }
    }

    void notifyPixelSizeChange( Object sender, Dimension size )
    {
        MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_PixelSizeChanged, new Dimension( size ) );
        notifyObservers( change );
    }

    public void highlightSelectionUnderMouse( Object sender )
    {
        TextSelection ts = getSelectionUnderMouse();
        setHighlightedSelection( sender, ts );
    }

    public void unhighlightSelection( Object sender )
    {
        setHighlightedSelection( sender, null );
    }

    void mousePressed( Object sender, int button )
    {
        //        mouseInfo.setLeftButton( false );
        //        mouseInfo.setRightButton( false );
        //
        //        switch ( button )
        //        {
        //            case MouseEvent.BUTTON1:
        //                mouseInfo.setLeftButton( true );
        //                break;
        //
        //            case MouseEvent.BUTTON3:
        //                mouseInfo.setRightButton( true );
        //                break;
        //
        //            default:
        //                break;
        //        }

        mouseInfo.setLeftButton( button == MouseEvent.BUTTON1 );
        mouseInfo.setRightButton( button == MouseEvent.BUTTON3 );
        pressMouseInfo = mouseInfo.clone();

        MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_MousePressed, pressMouseInfo );
        notifyObservers( change );
    }

    void mouseRelease( Object sender, int button )
    {
        //        switch ( button )
        //        {
        //            case MouseEvent.BUTTON1:
        //                mouseInfo.setLeftButton( true );
        //                mouseInfo.setRightButton( false );
        //                break;
        //
        //            case MouseEvent.BUTTON3:
        //                mouseInfo.setLeftButton( false );
        //                mouseInfo.setRightButton( true );
        //                break;
        //
        //            default:
        //                break;
        //        }

        mouseInfo.setLeftButton( button == MouseEvent.BUTTON1 );
        mouseInfo.setRightButton( button == MouseEvent.BUTTON3 );
        releaseMouseInfo = mouseInfo.clone();

        if ( mouseHasDragged() )
        {
            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_MouseDragEnd, releaseMouseInfo );
            notifyObservers( change );
        }
        else
        {
            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_MouseRelease, releaseMouseInfo );
            notifyObservers( change );
        }
    }

    void mouseMove( Object sender, Cursor2d textCursor )
    {
        boolean changed = !mouseInfo.getCharPosition().equals( textCursor );
        if ( changed )
        {
            mouseInfo.setCharPosition( textCursor );
            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_MouseMove, mouseInfo );
            //LogUtils.logModelChange( this, change );
            notifyObservers( change );
        }
    }

    //    void mouseDragging( Object sender, int tx, int ty )
    //    {
    //        boolean changed = !mouseInfo.position.equals( tx, ty );
    //        if ( changed )
    //        {
    //            mouseInfo.position.set( tx, ty );
    //            mouseInfo.insideText = isInsideText( tx, ty );
    //            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_MouseisDragging, mouseInfo );
    //            //LogUtils.logModelChange( this, change );
    //            notifyObservers( change );
    //        }
    //    }

    void mouseWheel( Object sender, int wheelRotation )
    {
        MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_MouseWheel, Integer.valueOf( wheelRotation ) );
        notifyObservers( change );
    }

    void mouseDragging( Object sender, Cursor2d textCursor )
    {
        boolean changed = !mouseInfo.getCharPosition().equals( textCursor );
        if ( changed )
        {
            if ( isInsideText( textCursor ) )
                mouseInfo.setCharPosition( textCursor );
            else
                mouseInfo.getCharPosition().undefine();
            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_MouseisDragging, mouseInfo );
            //LogUtils.logModelChange( this, change );
            notifyObservers( change );
        }
    }

    //    void unsetMouseCursor( Object sender )
    //    {
    //        boolean changed = mouseCursor.isDefined();
    //        if ( changed )
    //        {
    //            mouseCursor.undefine();
    //            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_MouseCursorChanged, mouseCursor );
    //            //LogUtils.logModelChange( this, change );
    //            notifyObservers( change );
    //        }
    //    }

    //    public KeyInfo getKeyInfo()
    //    {
    //        return keyInfo;
    //    }

    //    ColorInfo getTextColorInfo()
    //    {
    //        return textColorInfo;
    //    }

    //    void mouseClick( Object sender )
    //    {
    //        MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_MouseClick, getHoverCursor().getPosition().clone() );
    //        //LogUtils.logModelChange( Thread.currentThread(), change );
    //        notifyObservers( change );
    //    }

    TextMouseInfo getMouseInfo()
    {
        return mouseInfo;
    }

    public int getMousePixelX()
    {
        return mouseInfo.getPixelPosition().getX().getValue();
    }

    public int getMousePixelY()
    {
        return mouseInfo.getPixelPosition().getY().getValue();
    }

    void setMousePixelCoords( int mx, int my )
    {
        mouseInfo.setPixelPosition( mx, my );
    }

    void unsetMousePixelCoords()
    {
        mouseInfo.unsetPosition();
    }

    void keyEvent( Object sender, int kc, int km, long when )
    {
        keyInfo = new KeyInfo( kc, km );
        MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_KeyTyped, keyInfo );
        //LogUtils.logModelChange( Thread.currentThread(), change );
        notifyObservers( change );
    }

    public int getLineCount()
    {
        return lineCount;
    }

    int getMaxLineWidth()
    {
        return maxLineWidth;
    }

    public String getLine( int ty )
    {
        if ( lines == null )
            return null;

        return lines[ ty ];
    }

    /**
     * convert 1d index in character sequence to 2d text coordinates 
     * @return
     */
    //    private Cursor2d textPos1DTo2D( int i )
    //    {
    //        Cursor2d res = new Cursor2d();
    //
    //        if ( lines != null )
    //        {
    //            int n = i;
    //            for ( int j = 0; j < lines.length; j++ )
    //            {
    //                int len = lines[ j ].length();
    //                if ( n < len )
    //                {
    //                    res.set( n, j );
    //                    break;
    //                }
    //                n -= len;
    //            }
    //        }
    //
    //        return res;
    //    }

    void highlightSearchOccurrences( Object sender )
    {
        deleteSelections( sender, SearchOccurrenceLevel );

        if ( getSearchQuery() != null )
        {
            //int ql = getSearchQuery().length();
            int i = 0;
            for ( SearchOccurrence so : getSearchOccurrences() )
            {
                TextSelection ts = new TextSelection();
                ts.startSelection( new Cursor2d( so.getXPosition(), so.getYPosition() ) );
                //                ts.endSelection( new Cursor2d( so.getXPosition() + ql - 1, so.getYPosition() ) );
                ts.endSelection( new Cursor2d( so.getXPosition() + so.getLength() - 1, so.getYPosition() ) );
                ts.setLayerLevel( SearchOccurrenceLevel );
                ts.setColor( i == getCurrentOccurrenceIndex() ? currentSearchOccurrenceColorInfo : defaultSearchOccurrenceColorInfo );

                addTextSelection( sender, ts, TextPanelModelChangeId.TextPanel_SearchOccurrenceAdded );
                i++;
            }
        }
    }

    void updateSearchOccurrences()
    {
        int i = 0;
        for ( TextSelection ts : textSelections )
        {
            if ( ts.getLayerLevel() == SearchOccurrenceLevel )
            {
                ts.setColor( i == getCurrentOccurrenceIndex() ? currentSearchOccurrenceColorInfo : defaultSearchOccurrenceColorInfo );
                i++;
            }
        }
    }

    //    public boolean isAutoScrollToSearchOccurrence()
    //    {
    //        return autoScrollToSearchOccurrence;
    //    }

    public Rectangle getCaretPixelRectangle()
    {
        return caretPixelRectangle;
    }

    void setCaretPixelRectangle( Object sender, Rectangle r )
    {
        if ( enableCaretCursor )
        {
            boolean changed = caretPixelRectangle != r;
            if ( changed )
            {
                caretPixelRectangle = r;
                MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_CaretPixelPositionChanged, caretPixelRectangle );
                notifyObservers( change );
            }
        }
    }

    // SearchableText interface

    @Override
    public int getSearchableLineCount()
    {
        return getLineCount();
    }

    @Override
    public String getSearchableLine( int l )
    {
        return getLine( l );
    }

    @Override
    public Pair<Integer, Integer> findNeedle( String hayStack, String needle, int startPosition, boolean caseSensitive )
    {
        /*
            x = any aa unique
            * = quelque soit un nombre d'aa
            + = R,K,H
            B = AVLIPFWM
            - = DE
            Z = G,S,T,C,Y,N,Q
         */

        String specialChars = "x*+B-Z[]";

        boolean special = false;
        for ( int i = specialChars.length() - 1; i >= 0; i-- )
        {
            char c = specialChars.charAt( i );
            if ( needle.indexOf( c ) != -1 )
            {
                special = true;
                break;
            }
        }

        if ( special )
        {
            StringBuilder sb = new StringBuilder();
            for ( int i = 0; i < needle.length(); i++ )
            {
                char c = needle.charAt( i );
                switch ( c )
                {
                    case 'x':
                        sb.append( '.' );
                        break;

                    case '*':
                        sb.append( '.' );
                        sb.append( '+' );
                        break;

                    case '+':
                        sb.append( "[rkh]" );
                        break;

                    case 'b':
                    case 'B':
                        sb.append( "[avlipfwm]" );
                        break;

                    case '-':
                        sb.append( "[de]" );
                        break;

                    case 'z':
                    case 'Z':
                        sb.append( "[gstcynq]" );
                        break;

                    default:
                        sb.append( c );
                        break;
                }
            }

            Pattern p = Pattern.compile( sb.toString() );
            Matcher m = p.matcher( hayStack );
            if ( m.find( startPosition ) )
                return new Pair<>( m.start(), m.end() - m.start() );

            return new Pair<>( -1, 0 );
        }

        return super.findNeedle( hayStack, needle, startPosition, caseSensitive );
    }

    public ColorInfo getColorInfoAt( int x, int y )
    {
        for ( ColorInfo ci : charColors )
            if ( ci.getPosition().equals( x, y ) )
                return ci;
        return null;
    }

    public int getVisibleVerticalCharCount()
    {
        return visibleVerticalCharCount;
    }

    void setVisibleVerticalCharCount( Object sender, int count )
    {
        boolean changed = count > 0 && visibleVerticalCharCount != count;
        if ( changed )
        {
            visibleVerticalCharCount = count;
            MVCModelChange change = new MVCModelChange( sender, this, TextPanelModelChangeId.TextPanel_VisibleVerticalCharCountChanged );
            notifyObservers( change );
        }
    }

    //    @Override
    //    public boolean enableMVCLog( MVCModelChange change )
    //    {
    //        if ( change.getChangeId() instanceof TextPanelModelChangeId )
    //            switch ( (TextPanelModelChangeId)change.getChangeId() )
    //            {
    //                case TextPanel_CaretCursorChanged:
    //                case TextPanel_CaretPixelPositionChanged:
    //                case TextPanel_MouseMove:
    //                case TextPanel_MousePressed:
    //                case TextPanel_MouseRelease:
    //                case TextPanel_HoverCursorChanged:
    //                    return false;
    //
    //                default:
    //                    break;
    //            }
    //
    //        return super.enableMVCLog( change );
    //    }

    // Object interface

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for ( TextSelection ts : textSelections )
        {
            sb.append( ts.toString() );
            sb.append( ' ' );
        }

        sb.append( ' ' );
        sb.append( text );

        return sb.toString();
    }
}
