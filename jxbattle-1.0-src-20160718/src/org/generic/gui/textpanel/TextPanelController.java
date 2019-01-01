package org.generic.gui.textpanel;

import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import org.generic.bean.cursor2d.Cursor2d;
import org.generic.gui.GuiUtils;
import org.generic.gui.searchpanel.SearchOccurrence;
import org.generic.gui.searchpanel.SearchPanelModelChangeId;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class TextPanelController implements MVCController<TextPanelModel, TextPanelView>, MVCModelObserver //, MouseListener
{
    private TextPanelView view;

    private TextPanelModel model;

    private TextPanelUIModel uiModel;

    public TextPanelController( TextPanelView v )
    {
        view = v;
        init();
    }

    private void init()
    {
        //view.setFocusable( true );
        uiModel = new TextPanelUIModel( view );
        if ( view != null )
            view.setUiModel( uiModel );
        uiModel.setFont( GuiUtils.getFixedNormalFont() );

        view.setFocusTraversalKeysEnabled( false ); // get 'tab' "shift tab' key events

        createHandlers();
    }

    //    private void setMousePos( int mx, int my )
    //    {
    //        int tx = mx / view.getCharWidth();
    //        int ty = my / view.getCharHeight();
    //
    //        System.out.println( "mx " + mx + " my " + my + " tx " + tx + " ty " + ty );
    //
    //        if ( ty < model.getText().size() )
    //        {
    //            String s = model.getText().get( ty );
    //            if ( tx < s.length() )
    //                model.setHoverCursor( TextPanelController.this, tx, ty );
    //            else
    //                model.unsetHoverCursor( this );
    //        }
    //        else
    //            model.unsetHoverCursor( this );
    //    }

    private void setMouseCursor( int mx, int my, boolean isDragging )
    {
        model.setMousePixelCoords( mx, my );

        Cursor2d textCursor = uiModel.mouseToText( mx, my );
        //        System.out.println( "mx " + mx + " my " + my ); //+ " tx " + tx + " ty " + ty );

        if ( isDragging )
            model.mouseDragging( TextPanelController.this, textCursor );
        else
            model.mouseMove( TextPanelController.this, textCursor );

        //System.out.println( "setMousePos " + model.getMouseCursor() );
    }

    private void createHandlers()
    {
        view.addMouseMotionListener( new MouseMotionAdapter()
        {
            @Override
            public void mouseMoved( MouseEvent e )
            {
                if ( model != null )
                {
                    int mx = e.getX();
                    int my = e.getY();
                    setMouseCursor( mx, my, false );
                }
            }

            @Override
            public void mouseDragged( MouseEvent e )
            {
                if ( model != null )
                {
                    int mx = e.getX();
                    int my = e.getY();
                    setMouseCursor( mx, my, true );
                }
            }
        } );

        view.addMouseListener( new MouseAdapter()
        {
            @Override
            public void mouseExited( MouseEvent e )
            {
                //System.out.println( TextPanelController.this + " mouseExited" );
                model.unsetHoverCursor( TextPanelController.this );
                model.unsetMousePixelCoords();
                modelToUI();
            }

            @Override
            public void mousePressed( MouseEvent e )
            {
                //model.mousePressed( TextPanelController.this, e.getX(), e.getY() );
                model.mousePressed( TextPanelController.this, e.getButton() );
                GuiUtils.propageMouseEventToParent( view, e );
            }

            @Override
            public void mouseReleased( MouseEvent e )
            {
                model.mouseRelease( TextPanelController.this, e.getButton() );
            }

            @Override
            public void mouseEntered( MouseEvent e )
            {
                if ( view != null && model.getCaretCursor().isPositionDefined() )
                    view.requestFocusInWindow();
            }

            @Override
            public void mouseClicked( MouseEvent e )
            {
                GuiUtils.propageMouseEventToParent( view, e );
            }
        } );

        view.addMouseWheelListener( new MouseWheelListener()
        {
            @Override
            public void mouseWheelMoved( MouseWheelEvent e )
            {
                model.mouseWheel( TextPanelController.this, e.getWheelRotation() );
            }
        } );

        view.addKeyListener( new KeyAdapter()
        {
            @Override
            public void keyReleased( KeyEvent e )
            {
                //                System.out.println( "TextPanelController.keyReleased code=" + e.getKeyCode() + " char '" + e.getKeyChar() + "'" );
                //if ( KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == view )
                model.keyEvent( TextPanelController.this, e.getKeyCode(), e.getModifiersEx(), e.getWhen() );
            }
        } );

        view.addComponentListener( new ComponentAdapter()
        {
            @Override
            public void componentResized( ComponentEvent e )
            {
                model.notifyPixelSizeChange( TextPanelController.this, view.getSize() );
            }
        } );
    }

    //    public Cursor2d getCaretPixelPosition()
    //    {
    //        TextCursor cc = model.getCaretCursor();
    //        return uiModel.textToMouse( cc.getPosition().getX().getValue(), cc.getPosition().getY().getValue() );
    //    }

    private Rectangle getCaretRectangle()
    {
        TextCursor cc = model.getCaretCursor();
        if ( cc.isPositionDefined() )
        {
            Cursor2d c = uiModel.textToMouse( cc.getPosition().getX().getValue(), cc.getPosition().getY().getValue() );
            Rectangle res = new Rectangle( c.getX().getValue(), c.getY().getValue(), uiModel.getCharPixelWidth(), uiModel.getCharPixelHeight() );
            return res;
        }

        return null;
    }

    //    public Rectangle getSelectionRectangle( int ind )
    //    {
    //        TextSelection ts = model.getTextSelection( ind );
    //
    //        Cursor2d c = uiModel.textToMouse( ts.getStartIndex(), 0 );
    //        int w = ts.getEndIndex() - ts.getStartIndex();
    //        Rectangle res = new Rectangle( c.getX().getValue(), c.getY().getValue(), uiModel.getCharPixelWidth() * w, uiModel.getCharPixelHeight() * w );
    //        return res;
    //    }

    public Rectangle getSearchOccurrenceRectangle( int ind )
    {
        SearchOccurrence so = model.getSearchOccurrences().get( ind );

        //        Cursor2d c = uiModel.textToMouse( so.getXPosition(), 0 );
        Cursor2d c = uiModel.textToMouse( so.getXPosition(), so.getYPosition() );
        int w = so.getLength();
        Rectangle res = new Rectangle( c.getX().getValue(), c.getY().getValue(), uiModel.getCharPixelWidth() * w, uiModel.getCharPixelHeight() );
        return res;
    }

    public void scrollToSearchOccurrenceRectangle( int i, boolean center )
    {
        Rectangle r = getSearchOccurrenceRectangle( i );
        GuiUtils.scrollToRectangle( view, r, center );
    }

    public int computeVisibleHorizontalCharCount( int pixelWidth )
    {
        return uiModel.computeVisibleHorizontalCharCount( pixelWidth );
    }

    //    public int getCharPixelHeight()
    //    {
    //        return uiModel.getCharPixelHeight();
    //    }

    public int getVisibleVerticalLineCount()
    {
        return uiModel.getVisibleVerticalChars();
    }

    private void updateCaretPixelPosition()
    {
        model.setCaretPixelRectangle( TextPanelController.this, getCaretRectangle() );
    }

    private void updateUI()
    {
        if ( view != null )
        {
            view.revalidate();
            view.repaint();
        }
    }

    private void modelToUI()
    {
        updateUI();
    }

    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof TextPanelModelChangeId )
            switch ( (TextPanelModelChangeId)change.getChangeId() )
            {
                case TextPanel_TextChanged:
                case TextPanel_FontChanged:
                    uiModel.updateSize();
                    updateUI();
                    break;

                case TextPanel_HoverCursorChanged:
                    //LogUtils.logModelChange( this, change );
                    //if ( change.getSender() != this )
                    updateUI();
                    break;

                case TextPanel_CaretCursorChanged:
                    //                    scrollToCaret();
                    updateCaretPixelPosition();
                    if ( model.getCaretCursor().isPositionDefined() )
                        view.requestFocusInWindow();
                    break;

                //            case TextPanel_KeyTyped:
                //                view.requestFocusInWindow();
                //                break;

                //                case TextPanel_MouseMove:
                //                    if ( model.isEnableHoverCursor() && model.getMouseInfo().isInsideText() )
                //                        model.setHoverCursor( this, model.getMouseInfo().getPosition() );
                //                    else
                //                        model.unsetHoverCursor( this );
                //                    break;

                //                case TextPanel_MouseRelease:
                //                    if ( !model.mouseHasDragged() )
                //                    {
                //                        if ( model.isEnableTextCursor() && model.getMouseInfo().isInsideText() )
                //                            model.setTextCursor( this, model.getMouseInfo().getPosition() );
                //                        else
                //                            model.unsetTextCursor( this );
                //                    }
                //                    else
                //                    {
                //                        model.textSelected( this );
                //                    }
                //                    break;

                case TextPanel_MousePressed:
                    TextMouseInfo mi = (TextMouseInfo)change.getData();
                    if ( mi.isLeftButton() )
                        model.startBuildingSelection();
                    break;

                case TextPanel_MouseMove:
                    mi = (TextMouseInfo)change.getData();
                    model.setHoverCursor( TextPanelController.this, mi.getCharPosition() );
                    break;

                case TextPanel_MouseRelease:
                    mi = (TextMouseInfo)change.getData();
                    if ( mi.isLeftButton() )
                        model.setCaretCursor( TextPanelController.this, mi.getCharPosition() );
                    model.notifySelectionClick( TextPanelController.this );
                    updateUI();
                    break;

                case TextPanel_MouseisDragging:
                    mi = (TextMouseInfo)change.getData();
                    if ( mi.isLeftButton() )
                        model.modifyBuildingSelection( TextPanelController.this ); // , mi.getCharPosition() );
                    updateUI();
                    break;

                case TextPanel_MouseDragEnd:
                    mi = (TextMouseInfo)change.getData();
                    if ( mi.isLeftButton() )
                        model.completeBuildingSelection( TextPanelController.this );
                    break;

                //                case TextPanel_CurrentSelectionCompleted:
                //                    model.clearCurrentSelection( this );
                //                    break;

                case TextPanel_TextSelectionAdded:
                case TextPanel_TextSelectionRemoved:
                case TextPanel_BuildingSelectionCleared:
                case TextPanel_HighlightSelectionChanged:
                case TextPanel_BackgroundColorChanged:
                case TextPanel_ForegroundColorChanged:
                    updateUI();
                    break;

                case TextPanel_PixelSizeChanged:
                    model.setVisibleVerticalCharCount( TextPanelController.this, uiModel.getVisibleVerticalChars() );
                    break;

                default:
                    break;
            }
        else if ( change.getChangeId() instanceof SearchPanelModelChangeId )
            switch ( (SearchPanelModelChangeId)change.getChangeId() )
            {
                case CurrentOccurrenceChanged:
                    model.updateSearchOccurrences();
                    //model.setCaretCursor( TextPanelController.this, model.getCurrentSearchOccurrencePosition() );
                    updateUI();
                    break;

                case SearchResultsChanged:
                    model.highlightSearchOccurrences( TextPanelController.this );
                    updateUI();
                    break;

                default:
                    break;
            }
    }

    //    private void scrollToCaret()
    //    {
    //        if ( model.isAutoScrollToSearchOccurrence() )
    //        {
    //            Rectangle c = getCaretRectangle();
    //            if ( c != null )
    //            {
    //                Rectangle vr = view.getVisibleRect();
    //                //                System.out.println( "\n\n[scroll] visible " + vr.x + " -> " + (vr.x + vr.width) );
    //                System.out.println( "\n\n[scroll] visible " + vr );
    //                System.out.println( "[scroll] curs " + c );
    //                //                int mc = c.x + c.width / 2;
    //                //                int mv = vr.x + vr.width / 2;
    //                //System.out.println( "[scroll] mc " + mc + " mv " + mv );
    //                //                if ( mc < mv )
    //                //                    c.x -= 100;
    //                //                else
    //                //                    c.x += 100;
    //
    //                int w = uiModel.getVisibleRect().width;
    //                int h = uiModel.getVisibleRect().height;
    //                c.x -= w / 2;
    //                c.y -= h / 2;
    //                c.width = w;
    //                c.height = h;
    //                System.out.println( "[scroll] scroll to " + c );
    //                view.scrollRectToVisible( c );
    //                updateUI();
    //            }
    //        }
    //    }

    //    public int getCharWidth()
    //    {
    //        return view.getCharWidth();
    //    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        if ( SwingUtilities.isEventDispatchThread() )
            processModelChange( change );
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    processModelChange( change );
                }
            } );
    }

    @Override
    public void subscribeModel()
    {
        if ( model != null )
            model.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        if ( model != null )
            model.removeObserver( this );
    }

    // MVCController interface

    @Override
    public TextPanelView getView()
    {
        return view;
    }

    @Override
    public TextPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( TextPanelModel m )
    {
        unsubscribeModel();

        //        model = null;
        //        if ( m instanceof TextPanelModel )
        //        {
        //            model = (TextPanelModel)m;
        model = m;
        uiModel.setAppModel( model );
        uiModel.updateSize();

        subscribeModel();
        //        }

        modelToUI();
    }

    private void close_edt()
    {
        unsubscribeModel();
        view = null;
    }

    @Override
    public void close()
    {
        if ( SwingUtilities.isEventDispatchThread() )
            close_edt();
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    close_edt();
                }
            } );
    }

    //    // MouseListener interface
    //
    //    public void mouseClicked( MouseEvent e )
    //    {
    //    }
    //
    //    public void mousePressed( MouseEvent e )
    //    {
    //    }
    //
    //    public void mouseReleased( MouseEvent e )
    //    {
    //    }
    //
    //    public void mouseEntered( MouseEvent e )
    //    {
    //    }
    //
    //    public void mouseExited( MouseEvent e )
    //    {
    //        model.unsetHoverCursor( this );
    //    }
}