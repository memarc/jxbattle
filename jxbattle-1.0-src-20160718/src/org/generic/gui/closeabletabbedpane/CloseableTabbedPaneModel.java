package org.generic.gui.closeabletabbedpane;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

//public class CloseableTabbedPaneModel<M extends MVCModel, V extends JComponent, C extends MVCController<M, V>> implements Iterable<Component>
public class CloseableTabbedPaneModel extends MVCModelImpl implements Iterable<Component>
{
    private List<Component> closeableTabComponents;

    private List<Component> tabComponents;

    private List<String> tabTitles;

    private int selectedTabIndex;

    public CloseableTabbedPaneModel()
    {
        tabComponents = new ArrayList<Component>();
        tabTitles = new ArrayList<String>();
        closeableTabComponents = new ArrayList<Component>();
        selectedTabIndex = -1;
    }

    //    private boolean isTabCloseable( int i )
    //    {
    //        return closeableTabComponents.contains( view.getComponent( i ) );
    //    }

    boolean isTabCloseable( Component component )
    {
        return closeableTabComponents.contains( component );
    }

    boolean isTabCloseable( int componentIndex )
    {
        if ( componentIndex >= 0 )
            return closeableTabComponents.contains( tabComponents.get( componentIndex ) );
        return false;
    }

    public void addTab( Object sender, String title, Component component )
    {
        if ( !tabComponents.contains( component ) )
        {
            tabComponents.add( component );
            tabTitles.add( title );

            //            MVCModelChange change = new MVCModelChange( sender, this, CloseableTabbedPaneChangeId.ComponentAdded, new Pair<String, Component>( title, component ) );
            MVCModelChange change = new MVCModelChange( sender, this, CloseableTabbedPaneChangeId.ComponentAdded );
            change.addData( title );
            change.addData( component );
            notifyObservers( change );
        }
    }

    public void addCloseableTab( Object sender, String title, Component component )
    {
        if ( !tabComponents.contains( component ) )
        {
            tabComponents.add( component );
            tabTitles.add( title );
            closeableTabComponents.add( component );

            //            MVCModelChange change = new MVCModelChange( sender, this, CloseableTabbedPaneChangeId.ComponentAdded, new Pair<String, Component>( title, component ) );
            MVCModelChange change = new MVCModelChange( sender, this, CloseableTabbedPaneChangeId.ComponentAdded );
            change.addData( title );
            change.addData( component );
            notifyObservers( change );
        }
    }

    public void removeTab( Object sender, int componentIndex )
    {
        if ( componentIndex <= tabComponents.size() )
        {
            Component component = tabComponents.get( componentIndex );
            tabComponents.remove( component );
            tabTitles.remove( componentIndex );
            closeableTabComponents.remove( component );

            MVCModelChange change = new MVCModelChange( sender, this, CloseableTabbedPaneChangeId.ComponentRemoved, component );
            //LogUtils.logModelChange( this, change );
            notifyObservers( change );
        }
    }

    public void selectTab( Object sender, int componentIndex )
    {
        boolean changed = selectedTabIndex != componentIndex;
        if ( changed )
        {
            selectedTabIndex = componentIndex;
            Component component = tabComponents.get( selectedTabIndex );

            MVCModelChange change = new MVCModelChange( sender, this, CloseableTabbedPaneChangeId.ComponentSelected, component );
            //LogUtils.logModelChange( this, change );
            notifyObservers( change );
        }
    }

    public void selectTab( Object sender, Component component )
    {
        int i = tabComponents.indexOf( component );
        selectTab( sender, i );
    }

    // Iterator<Component> interface

    @Override
    public TabComponentIterator iterator()
    {
        return new TabComponentIterator();
    }

    public class TabComponentIterator implements Iterator<Component>
    {
        private int componentCount;

        private int componentIndex;

        private int lastRet = -1;

        public TabComponentIterator()
        {
            componentCount = tabComponents.size();
            componentIndex = 0;
        }

        @Override
        public boolean hasNext()
        {
            return componentIndex < componentCount;
        }

        @Override
        public Component next()
        {
            Component res = tabComponents.get( componentIndex );
            lastRet = componentIndex;
            componentIndex++;
            return res;
        }

        @Override
        public void remove()
        {
        }

        public String getTitle()
        {
            return tabTitles.get( lastRet );
        }
    }
}
