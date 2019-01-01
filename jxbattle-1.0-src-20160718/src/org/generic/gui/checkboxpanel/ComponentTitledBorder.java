/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.generic.gui.checkboxpanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

// cf http://www.javalobby.org/java/forums/t33048.html

/**
 * Border with customisable title 
 */
public class ComponentTitledBorder implements Border, MouseListener, SwingConstants
{
    protected Component component;

    protected JComponent container;

    private int offset = 5;

    private Rectangle rect;

    private Border border;

    public ComponentTitledBorder( Component comp, JComponent container, Border border )
    {
        this.component = comp;
        this.container = container;
        this.border = border;
        container.addMouseListener( this );
    }

    @Override
    public boolean isBorderOpaque()
    {
        return true;
    }

    @Override
    public void paintBorder( Component c, Graphics g, int x, int y, int width, int height )
    {
        Insets borderInsets = border.getBorderInsets( c );
        Insets insets = getBorderInsets( c );
        int temp = (insets.top - borderInsets.top) / 2;
        border.paintBorder( c, g, x, y + temp, width, height - temp );
        Dimension size = component.getPreferredSize();
        rect = new Rectangle( offset, 0, size.width, size.height );
        SwingUtilities.paintComponent( g, component, (Container)c, rect );
    }

    @Override
    public Insets getBorderInsets( Component c )
    {
        Dimension size = component.getPreferredSize();
        Insets insets = border.getBorderInsets( c );
        insets.top = Math.max( insets.top, size.height );
        return insets;
    }

    private void dispatchEvent( MouseEvent me )
    {
        if ( rect != null && rect.contains( me.getX(), me.getY() ) )
        {
            Point pt = me.getPoint();
            pt.translate( -offset, 0 );
            component.setBounds( rect );
            component.dispatchEvent( new MouseEvent( component, me.getID(), me.getWhen(), me.getModifiers(), pt.x, pt.y, me.getClickCount(), me.isPopupTrigger(), me.getButton() ) );
            if ( !component.isValid() )
                container.repaint();
        }
    }

    @Override
    public void mouseClicked( MouseEvent me )
    {
        dispatchEvent( me );
    }

    @Override
    public void mouseEntered( MouseEvent me )
    {
        dispatchEvent( me );
    }

    @Override
    public void mouseExited( MouseEvent me )
    {
        dispatchEvent( me );
    }

    @Override
    public void mousePressed( MouseEvent me )
    {
        dispatchEvent( me );
    }

    @Override
    public void mouseReleased( MouseEvent me )
    {
        dispatchEvent( me );
    }

    //    public static void main( String[] args )
    //    {
    //        try
    //        {
    //            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
    //        }
    //        catch( Exception e )
    //        {
    //            e.printStackTrace();
    //        }
    //
    //        JPanel panel1 = new JPanel();
    //        panel1.setLayout( new BorderLayout() );
    //        panel1.add( new JLabel( "lbl1: " ), BorderLayout.NORTH );
    //        panel1.add( new JTextField( "tototp" ), BorderLayout.CENTER );
    //        panel1.add( new JLabel( "lbl2: " ), BorderLayout.SOUTH );
    //
    //        final JPanel proxyPanel = new JPanel();
    //        proxyPanel.add( new JLabel( "Proxy Host: " ) );
    //        proxyPanel.add( new JTextField( "proxy.xyz.com" ) );
    //        proxyPanel.add( new JLabel( "  Proxy Port" ) );
    //        proxyPanel.add( new JTextField( "8080" ) );
    //        final JCheckBox checkBox = new JCheckBox( "Use Proxy", true );
    //        checkBox.setFocusPainted( false );
    //        ComponentTitledBorder componentBorder = new ComponentTitledBorder( checkBox, proxyPanel, BorderFactory.createEtchedBorder() );
    //        checkBox.addActionListener( new ActionListener()
    //        {
    //            @SuppressWarnings("unused")
    //            @Override
    //            public void actionPerformed( ActionEvent e )
    //            {
    //                boolean enable = checkBox.isSelected();
    //                Component comp[] = proxyPanel.getComponents();
    //                for ( int i = 0; i < comp.length; i++ )
    //                {
    //                    comp[ i ].setEnabled( enable );
    //                }
    //            }
    //        } );
    //        proxyPanel.setBorder( componentBorder );
    //        JFrame frame = new JFrame( "ComponentTitledBorder - santhosh@in.fiorano.com" );
    //        Container contents = frame.getContentPane();
    //        //        contents.setLayout( new FlowLayout() );
    //        contents.setLayout( new BorderLayout() );
    //        contents.add( panel1, BorderLayout.NORTH );
    //        contents.add( proxyPanel, BorderLayout.CENTER );
    //        frame.pack();
    //        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    //        frame.setVisible( true );
    //    }
}
