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

public interface MVCModelObserver
{
    /**
     * notification que le modèle a été modifié (avec des précisions)
     */
    public void modelChanged( MVCModelChange change );

    /**
     * inscription de l'observateur à tous les modèles qu'il observe
     */
    public void subscribeModel();

    /**
     * désinscription de l'observateur de tous les modèles qu'il observe
     */
    public void unsubscribeModel();

    /**
     * cleanup (unsubscribe from model, close view, ...)
     */
    public void close();
}