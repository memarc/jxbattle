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

package org.generic.mvc.model.observer;

import java.util.List;

/**
 * MVC model (inspired from observer-observed design pattern)
 */
public interface MVCModel
{
    /**
     * add a model observer to observer list
     * @param observer new model observer
     */
    public void addObserver( MVCModelObserver observer );

    /**
     * add a list of model observers to observer list
     * @param observers list of model observers to add
     */
    public void addObservers( List<MVCModelObserver> observers );

    /**
     * copy this model observers to given model
     * @param model to which observers must be copied
     */
    //    public void copyObserversTo( MVCModel model );

    //    public void moveObserversTo( MVCModel model );

    /**
     * remove a model observer from observer list
     * @param observer model observer to remove
     */
    public void removeObserver( MVCModelObserver observer );

    /**
     * remove a list of model observers from observer list
     * @param observers list of model observers to remove
     */
    public void removeObservers( List<MVCModelObserver> observers );

    /**
     * remove all observers from observer list
     */
    public void removeAllObservers();

    /**
     * called when MVC model wants to notify a change to observers
     * @param change model change details
     */
    public void notifyObservers( MVCModelChange change );

    public boolean enableMVCLog( MVCModelChange change );
}
