package org.generic.gui.searchpanel;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.generic.gui.GuiUtils;

public class SearchPanelView extends JPanel
{
    private JLabel lblFind;

    private JTextField txtSearch;

    private JButton btnPrevious;

    private JButton btnNext;

    private JButton btnFirst;

    private JButton btnLast;

    private JCheckBox cbImmediateSearch;

    private JButton btnRunSearch;

    private JPanel panel;

    private JLabel lblOccurrences;

    public SearchPanelView()
    {
        setBorder( null );
        setLayout( new MigLayout( "", "[][][][][]", "[][][]" ) );
        add( getLblFind(), "cell 0 0" );
        add( getTxtSearch(), "cell 1 0 4 1,growx" );
        //add( getPanel(), "cell 6 0 1 2,grow" );
        add( getCbImmediateSearch(), "cell 0 1 4 1" );
        add( getBtnRunSearch(), "cell 4 1" );
        add( getBtnFirst(), "cell 0 2,growx" );
        add( getBtnPrevious(), "cell 1 2,growx" );
        add( getBtnNext(), "cell 2 2,growx" );
        add( getBtnLast(), "cell 3 2,growx" );
        add( getLblOccurrences(), "cell 4 2" );
    }

    JLabel getLblFind()
    {
        if ( lblFind == null )
        {
            lblFind = new JLabel( "Find" );
            lblFind.setEnabled( false );
        }
        return lblFind;
    }

    public JTextField getTxtSearch()
    {
        if ( txtSearch == null )
        {
            txtSearch = new JTextField();
            txtSearch.setEnabled( false );
        }
        return txtSearch;
    }

    JButton getBtnPrevious()
    {
        if ( btnPrevious == null )
        {
            btnPrevious = new JButton( "<" );
            btnPrevious.setEnabled( false );
            btnPrevious.setToolTipText( "Got to previous occurrence" );
        }
        return btnPrevious;
    }

    JButton getBtnNext()
    {
        if ( btnNext == null )
        {
            btnNext = new JButton( ">" );
            btnNext.setEnabled( false );
            btnNext.setToolTipText( "Go to next occurrence" );
        }
        return btnNext;
    }

    JButton getBtnFirst()
    {
        if ( btnFirst == null )
        {
            btnFirst = new JButton( "|<" );
            btnFirst.setEnabled( false );
            btnFirst.setToolTipText( "Go to first occurrence" );
        }
        return btnFirst;
    }

    JButton getBtnLast()
    {
        if ( btnLast == null )
        {
            btnLast = new JButton( ">|" );
            btnLast.setEnabled( false );
            btnLast.setToolTipText( "Go to last occurrence" );
        }
        return btnLast;
    }

    JCheckBox getCbImmediateSearch()
    {
        if ( cbImmediateSearch == null )
        {
            cbImmediateSearch = new JCheckBox( "Immediate search" );
            cbImmediateSearch.setEnabled( false );
        }
        return cbImmediateSearch;
    }

    JButton getBtnRunSearch()
    {
        if ( btnRunSearch == null )
        {
            btnRunSearch = new JButton( "Run search" );
            //            btnRunSearch.setToolTipText( "<html>x = any aa, one time<br/>* = any aa, any length<br/>+ = R,K,H<br/>B = AVLIPFWM<br/>- = DE<br/>Z = G,S,T,C,Y,N,Q</html>" );
            btnRunSearch.setEnabled( false );
        }
        return btnRunSearch;
    }

    boolean hasPanel()
    {
        return panel != null;
    }

    JPanel getPanel()
    {
        if ( panel == null )
        {
            panel = new JPanel();
            panel.setBorder( null );
            add( panel, "cell 6 0 1 3,grow" );
            panel.setLayout( new BorderLayout( 0, 0 ) );
        }
        return panel;
    }

    void enablePanel( boolean b )
    {
        if ( hasPanel() )
            GuiUtils.setRecursiveEnable( panel, b );
    }

    void setPanelComponent( Component c )
    {
        JPanel p = getPanel();
        p.removeAll();
        p.add( c, BorderLayout.CENTER );
    }

    JLabel getLblOccurrences()
    {
        if ( lblOccurrences == null )
        {
            lblOccurrences = new JLabel();
        }
        return lblOccurrences;
    }
}
