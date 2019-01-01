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

import java.util.Iterator;
import java.util.List;

import org.generic.LogUtils;
import org.generic.list.SyncList;
import org.generic.mvc.model.MVCModelError;

public class MVCModelImpl implements MVCModel
{
    private SyncList<MVCModelObserver> observers;

    //    private List<MVCModelObserver> observers;

    private SyncList<MVCModel> relatedModels; // observers added to this will also be added to related models

    //    private List<MVCModel> relatedModels; // observers added to this will also be added to related models

    public MVCModelImpl()
    {
        observers = new SyncList<>();
        //        observers = new ArrayList<>();
        //        observers = Collections.synchronizedList( new ArrayList<MVCModelObserver>() );

        relatedModels = new SyncList<>();
        //        relatedModels = new ArrayList<>();
        //observers.getMutex().setDebug( true );
    }

    /**
     * add target model for observers
     * ie. when an observer is added to this model, it is also added to related models
     */
    protected void addRelatedModel( MVCModel m )
    {
        if ( m == null )
            throw new MVCModelError( "invalid null MVCModel" );

        if ( !relatedModels.contains( m ) )
        {
            m.addObservers( observers );
            relatedModels.add( m );
        }
    }

    protected void removeRelatedModel( MVCModel m )
    {
        if ( m != null )
        {
            relatedModels.remove( m );
            m.removeObservers( observers );
        }
    }

    public SyncList<MVCModelObserver> getObservers()
    //    public List<MVCModelObserver> getObservers()
    {
        return observers;
    }

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        if ( obs != null )
        {
            //            observers.addUnique( obs );
            if ( !observers.contains( obs ) )
                observers.add( obs );

            for ( MVCModel rm : relatedModels )
                rm.addObserver( obs );
        }
    }

    @Override
    public void addObservers( List<MVCModelObserver> obs )
    {
        if ( obs != null )
        {
            //     observers.addUnique( obs );
            for ( MVCModelObserver o : obs )
                if ( !observers.contains( obs ) )
                    observers.add( o );

            for ( MVCModel rm : relatedModels )
                rm.addObservers( obs );
        }
    }

    //    @Override
    //    public void moveObserversTo( MVCModel model )
    //    {
    //        model.addObservers( getObservers() );
    //        removeAllObservers();
    //    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        if ( obs != null )
        {
            observers.remove( obs );
            for ( MVCModel rm : relatedModels )
                rm.removeObserver( obs );
        }
    }

    @Override
    public void removeObservers( List<MVCModelObserver> obs )
    {
        if ( obs != null )
        {
            observers.removeAll( obs );
            for ( MVCModel rm : relatedModels )
                rm.removeObservers( obs );
        }
    }

    @Override
    public void removeAllObservers()
    {
        observers.clear();
        for ( MVCModel rm : relatedModels )
            rm.removeAllObservers();
    }

    @Override
    public void notifyObservers( MVCModelChange change )
    {
        //        SyncIterator<MVCModelObserver> it = observers.iterator();
        //        try
        //        {
        //            while ( it.hasNext() )
        //                it.next().modelChanged( change );
        //        }
        //        finally
        //        {
        //            it.close();
        //        }

        //        if ( enableMVCLog( change ) )
        //            doLog( change ); 

        if ( true )
        {
            if ( observers.size() > 0 )
                for ( MVCModelObserver o : observers )
                    o.modelChanged( change );
        }
        else
            synchronized( observers )
            {
                Iterator<MVCModelObserver> i = observers.iterator(); // Must be in synchronized block
                while ( i.hasNext() )
                    i.next().modelChanged( change );
            }

        //ThreadUtils.sleep( 0, 10 ); // TODO let EDT work (better than Thread.yield())
    }

    @Override
    public boolean enableMVCLog( MVCModelChange change )
    {
        return false;
    }

    private void doLog( MVCModelChange change )
    {
        //        if ( change.getChangeId() instanceof TextPanelModelChangeId )
        //            switch ( (TextPanelModelChangeId)change.getChangeId() )
        //            {
        //                case TextPanel_CaretCursorChanged:
        //                case TextPanel_CaretPixelPositionChanged:
        //                case TextPanel_MouseMove:
        //                case TextPanel_MousePressed:
        //                case TextPanel_MouseRelease:
        //                case TextPanel_HoverCursorChanged:
        //                    //                    System.out.println();
        //                    break;
        //
        //                default:
        LogUtils.logModelChange( this, change );
        //                    break;
        //            }
    }
}
