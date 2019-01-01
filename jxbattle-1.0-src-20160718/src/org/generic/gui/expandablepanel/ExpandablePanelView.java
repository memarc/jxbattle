package org.generic.gui.expandablepanel;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class ExpandablePanelView extends JPanel
{
    private JButton btnToggle;

    private JPanel panel;

    //    private ExpandablePanelController expandablePanelController;

    public ExpandablePanelView()
    {
        setLayout( new MigLayout( "", "[grow]", "[][grow]" ) );
        add( getBtnToggle(), "cell 0 0" );
        add( getPanel(), "cell 0 1,grow" );
    }

    JButton getBtnToggle()
    {
        if ( btnToggle == null )
        {
            btnToggle = new JButton( "New button" );
        }
        return btnToggle;
    }

    public JPanel getPanel()
    {
        if ( panel == null )
        {
            panel = new JPanel();
        }
        return panel;
    }

    //    void setController( ExpandablePanelController ddpc )
    //    {
    //        expandablePanelController = ddpc;
    //    }

    //    private Dimension getViewSize()
    //    {
    //        Dimension res = new Dimension();
    //        res.width = Math.max( btnToggle.getWidth(), panel.getWidth() + 10 );
    //        res.height = expandablePanelController == null ? 1 : expandablePanelController.getViewHeight();
    //        System.out.println( "getViewSize " + res );
    //        return res;
    //    }

    //    @Override
    //    public Dimension getPreferredSize()
    //    {
    //        return getViewSize();
    //    }
    //
    //    @Override
    //    public Dimension getMaximumSize()
    //    {
    //        return getViewSize();
    //    }
    //
    //    //    @Override
    //    //    public Dimension getSize( Dimension rv )
    //    //    {
    //    //        Dimension res = expandablePanelController.getViewSize();
    //    //        rv.width = res.width;
    //    //        rv.height = res.height;
    //    //        return rv;
    //    //    }
}
