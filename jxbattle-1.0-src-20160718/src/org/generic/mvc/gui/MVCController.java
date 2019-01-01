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

package org.generic.mvc.gui;

import java.awt.Component;

import org.generic.mvc.model.observer.MVCModel;

public interface MVCController<M extends MVCModel, V extends Component>
{
    /**
     * return controlled view
     * note : do not lazily initialise 'view' member in this getter
     * since it may be accessed from multiple threads (at least main and EDT)
     * and hence may be initialised twice
     * @return view managed by controller
     */
    public V getView();

    /**
     * @return model managed by controller 
     */
    public M getModel();

    /**
     * set model to manage by controller
     */
    public void setModel( M model );

    /**
     * cleanup (close view, ...)
     */
    public void close();
}
