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

package jxbattle.gui.base.client.keysetuppanel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class KeySetupPanelView extends JPanel
{
    private JButton btn;

    private JLabel label;

    public KeySetupPanelView()
    {
        setLayout( new MigLayout( "", "[][grow]", "[]" ) );
        add( getBtn(), "cell 0 0" );
        add( getLabel(), "cell 1 0" );
    }

    JButton getBtn()
    {
        if ( btn == null )
        {
            btn = new JButton();
        }
        return btn;
    }

    JLabel getLabel()
    {
        if ( label == null )
        {
            label = new JLabel();
        }
        return label;
    }
}
