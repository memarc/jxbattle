package org.generic.gui.componentlist;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

public class JComponentListView extends JPanel
{
    private JScrollPane scrollPane;

    private JPanel listPanel;

    public JComponentListView()
    {
        setBorder( null );
        setLayout( new BorderLayout( 0, 0 ) );
        add( getScrollPane(), BorderLayout.CENTER );
    }

    public JScrollPane getScrollPane()
    {
        if ( scrollPane == null )
        {
            scrollPane = new JScrollPane();
            scrollPane.setBorder( null );
            scrollPane.setViewportView( getListPanel() );
            scrollPane.setWheelScrollingEnabled( true );
        }
        return scrollPane;
    }

    JPanel getListPanel()
    {
        if ( listPanel == null )
        {
            listPanel = new JPanel();
            listPanel.setBorder( null );
            listPanel.setLayout( new MigLayout( "", "[grow]", "[]" ) );
        }
        return listPanel;
    }
}
