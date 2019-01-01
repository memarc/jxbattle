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

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

// cf http://www.javalobby.org/java/forums/t33048.html

/**
 * Border with a JCheckBox title
 */
public class JCheckBoxBorder extends ComponentTitledBorder
{
    public JCheckBoxBorder( JComponent container )
    {
        super( new JCheckBox( "", false ), container, BorderFactory.createEtchedBorder() );
        getCheckBox().setFocusPainted( false );
    }

    public JCheckBox getCheckBox()
    {
        return (JCheckBox)component;
    }

    public JComponent getContainer()
    {
        return container;
    }
}
