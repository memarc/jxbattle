package org.generic.gui.closeabletabbedpane;

import java.awt.Graphics;

import javax.swing.JTabbedPane;

public class CloseableTabbedPaneView extends JTabbedPane
{
    private CloseableTabbedPaneUIModel uiModel;

    //private TabCloseUI closeUI = new TabCloseUI( this );

    public CloseableTabbedPaneView( int flag )
    {
        super( flag );
    }

    void setUIModel( CloseableTabbedPaneUIModel ium )
    {
        uiModel = ium;
    }

    public void paint( Graphics g )
    {
        super.paint( g );
        if ( uiModel != null )
            uiModel.paint( g );
    }

    public String getTabTitleAt( int index )
    {
        return super.getTitleAt( index ).trim();
    }
}
