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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.CellRendererPane;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.generic.gui.GuiUtils;

/**
 * JPanel with a CheckBoxBorder
 */
public class JCheckBoxPanel extends JPanel
{
    private JCheckBoxBorder checkBoxBorder;

    public JCheckBoxPanel( String checkBoxTitle )
    {
        checkBoxBorder = new JCheckBoxBorder( this );
        setBorder( checkBoxBorder );

        final JCheckBox checkBox = checkBoxBorder.getCheckBox();
        checkBox.setText( checkBoxTitle );
        checkBox.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                //boolean enable = checkBox.isSelected();
                //                for ( Component comp : getComponents() )
                //                    if ( !(comp instanceof CellRendererPane) )
                //                        //comp.setEnabled( enable );
                //                        GuiUtils.setRecursiveEnable( comp, enable );
                setEnableComponents();
            }
        } );

        setEnableComponents();
    }

    private void setEnableComponents( boolean enabled )
    {
        for ( Component comp : getComponents() )
            if ( !(comp instanceof CellRendererPane) )
                GuiUtils.setRecursiveEnable( comp, enabled );
    }

    private void setEnableComponents()
    {
        setEnableComponents( isEnabled() && getCheckBox().isSelected() );
    }

    public JCheckBox getCheckBox()
    {
        return checkBoxBorder.getCheckBox();
    }

    public boolean isCheckBoxSelected()
    {
        return getCheckBox().isSelected();
    }

    public void setCheckBoxSelected( boolean selected )
    {
        getCheckBox().setSelected( selected );
        setEnableComponents();
        repaint();
    }

    public String getCheckBoxTitle()
    {
        return getCheckBox().getText();
    }

    public void setCheckBoxTitle( String title )
    {
        getCheckBox().setText( title );
    }

    @Override
    public void setEnabled( boolean enabled )
    {
        setEnableComponents();
        getCheckBox().setEnabled( enabled );
        super.setEnabled( enabled );
    }

    @Override
    public Component add( Component comp )
    {
        setEnableComponents();
        return super.add( comp );
    }

    @Override
    public void add( Component comp, Object constraints )
    {
        setEnableComponents();
        super.add( comp, constraints );
    }

    //    @Override
    //    public Dimension getSize()
    //    {
    //        Dimension res = super.getSize();
    //        System.out.println( res );
    //        return res;
    //    }

    public static void main( String[] args )
    {
        JFrame frame = new JFrame( "aaaa" );
        Container contents = frame.getContentPane();

        contents.setLayout( new MigLayout( "", "[grow]", "[grow][]" ) );

        JCheckBoxPanel cbp1 = new JCheckBoxPanel( "cbp1" );
        contents.add( cbp1, "cell 0 0,grow" );
        cbp1.setLayout( new MigLayout( "", "[grow]", "[][]" ) );
        cbp1.add( new JLabel( "lbl1" ), "cell 0 0,growx" );
        cbp1.add( new JTextField(), "cell 0 1,growx" );

        JCheckBoxPanel cbp2 = new JCheckBoxPanel( "cbp2" );
        contents.add( cbp2, "cell 0 1,grow" );
        cbp2.setLayout( new MigLayout( "", "[grow]", "[][]" ) );
        cbp2.add( new JLabel( "lbl2" ) );
        cbp2.add( new JTextField(), "cell 0 1,growx" );

        //        frame.pack();
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
