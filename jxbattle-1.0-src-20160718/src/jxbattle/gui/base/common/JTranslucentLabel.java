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

package jxbattle.gui.base.common;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;

/**
 * a JLabel with a translucent background
 */
public class JTranslucentLabel extends JLabel
{
    private float alpha;

    public JTranslucentLabel( float a )
    {
        setOpaque( true );
        setAlpha( a );
    }

    public void setAlpha( float a )
    {
        alpha = a;
    }

    @Override
    public void paintComponent( Graphics g )
    {
        ((Graphics2D)g).setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );
        super.paintComponent( g );
    }
}
