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

package jxbattle.gui.composite.server.gameprofilespanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jxbattle.gui.base.common.gamesettingspanel.GameSettingsPanelController;
import jxbattle.gui.base.common.gamesettingspanel.GameSettingsPanelModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;
import jxbattle.model.common.parameters.game.GameParametersModel;
import jxbattle.model.server.GameParametersProfileModel;
import jxbattle.model.server.GameParametersProfilesModel;

import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.list.ListModelChangeId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class GameProfilesPanelController implements MVCModelObserver, MVCController<GameParametersProfilesModel, GameProfilesPanelView>
{
    private GameProfilesPanelView view;

    private GameParametersProfilesModel model;

    private GameSettingsPanelController gameSettingsPanelController;

    private ActionListener profileComboListener;

    public GameProfilesPanelController( GameProfilesPanelView v )
    {
        view = v;
        init();
    }

    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }

    private void init()
    {
        gameSettingsPanelController = new GameSettingsPanelController( view.getGameSettingsPanelView() );
        createHandlers();
        //modelToUI();
    }

    private void createHandlers()
    {
        view.getBtnResetDefaults().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onResetToDefaults( GameProfilesPanelController.this );
            }
        } );

        //        view.getCmbProfiles().addActionListener( new ActionListener()
        //        {
        //            @SuppressWarnings("unused")
        //            @Override
        //            public void actionPerformed( ActionEvent e )
        //            {
        //                onComboItemSelect( GameProfilesPanelController.this );
        //            }
        //        } );

        view.getTfProfileName().addKeyListener( new KeyAdapter()
        {
            @Override
            public void keyReleased( KeyEvent e )
            {
                checkProfileName( GameProfilesPanelController.this );
            }
        } );

        view.getBtnCopyProfile().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onCopyProfile( GameProfilesPanelController.this );
            }
        } );

        view.getBtnDeleteProfile().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onDeleteProfile( GameProfilesPanelController.this );
            }
        } );
    }

    private ActionListener getComboProfilesListener()
    {
        if ( profileComboListener == null )
        {
            profileComboListener = new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    onComboItemSelect( GameProfilesPanelController.this );
                }
            };
        }

        return profileComboListener;
    }

    private void addComboProfilesListener()
    {
        view.getCmbProfiles().addActionListener( getComboProfilesListener() );
    }

    private void removeComboProfilesListener()
    {
        view.getCmbProfiles().removeActionListener( getComboProfilesListener() );
    }

    private void m2uiProfiles()
    {
        DefaultComboBoxModel<GameParametersProfileModel> cmbModel = new DefaultComboBoxModel<>();

        //        SyncIterator<GameParametersProfileModel> it = model.iterator();
        //        try
        //        {
        //            while ( it.hasNext() )
        //                cmbModel.addElement( it.next() );
        //        }
        //        finally
        //        {
        //            it.close();
        //        }

        for ( GameParametersProfileModel gpf : model )
            cmbModel.addElement( gpf );

        view.getCmbProfiles().setModel( cmbModel );
    }

    private void modelToUI_edt()
    {
        m2uiProfiles();
        //        GameParametersProfileModel current = model.getCurrentProfileModel();
        GameParametersProfileModel current = model.getCurrent();
        //        m2uiCurrentGameParametersProfile( model.getCurrentProfileModel() );
        m2uiCurrentGameParametersProfile( current );

        if ( current != null )
            view.getCmbProfiles().setSelectedItem( current );

        view.getLblDuplicateName().setVisible( false );
    }

    private void modelToUI()
    {
        if ( SwingUtilities.isEventDispatchThread() )
            modelToUI_edt();
        else
            SwingUtilities.invokeLater( new Runnable()
            {

                @Override
                public void run()
                {
                    modelToUI_edt();
                }
            } );
    }

    private void onComboItemSelect( Object sender )
    {
        GameParametersProfileModel gpf = getSelectedProfile();
        //        model.setCurrentProfileModel( sender, gpf );
        model.setCurrent( sender, gpf );
    }

    private void onCopyProfile( Object sender )
    {
        GameParametersProfileModel newProfile = model.cloneCurrentProfileModel( sender );
        model.add( sender, newProfile );
        //        model.setCurrentProfileModel( sender, newProfile );
        model.setCurrent( sender, newProfile );
    }

    private void onDeleteProfile( Object sender )
    {
        GameParametersProfileModel gpf = getSelectedProfile();

        if ( gpf != null && JOptionPane.showConfirmDialog( view, "Delete profile '" + gpf.getProfileName() + "' ?", "Confirm", JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION )
            model.remove( sender, gpf );
    }

    private void onResetToDefaults( Object sender )
    {
        if ( JOptionPane.showConfirmDialog( view, "Reset all parameters to default values ?", "Confirm", JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION )
        {
            GameParametersModel paramsModel = getCurrentParameters();

            paramsModel.getInitialisationParametersModel().getGenerateRandomSeedModel().resetToDefault( sender );
            paramsModel.getInitialisationParametersModel().getRandomSeedModel().resetToDefault( sender );

            paramsModel.getBoardParametersModel().getXCellCountModel().resetToDefault( sender );
            paramsModel.getBoardParametersModel().getYCellCountModel().resetToDefault( sender );
            paramsModel.getBoardParametersModel().getWrappingModeModel().resetToDefault( sender );

            paramsModel.getInitialisationParametersModel().getBasesModel().resetToDefault( sender );
            paramsModel.getInitialisationParametersModel().getRandomBasesModel().resetToDefault( sender );
            paramsModel.getInitialisationParametersModel().getTownsModel().resetToDefault( sender );
            paramsModel.getInitialisationParametersModel().getArmiesModel().resetToDefault( sender );
            paramsModel.getInitialisationParametersModel().getMilitiaModel().resetToDefault( sender );
            paramsModel.getInitialisationParametersModel().getSeaDensityModel().resetToDefault( sender );
            paramsModel.getInitialisationParametersModel().getHillDensityModel().resetToDefault( sender );

            paramsModel.getTroopsMoveModel().resetToDefault( sender );
            paramsModel.getSupplyFarmsModel().resetToDefault( sender );
            paramsModel.getSupplyDecayModel().resetToDefault( sender );
            paramsModel.getFightIntensityModel().resetToDefault( sender );

            paramsModel.getEnableBaseBuildModel().resetToDefault( sender );
            paramsModel.getEnableBaseScuttleModel().resetToDefault( sender );
            paramsModel.getEnableFillModel().resetToDefault( sender );
            paramsModel.getEnableDigModel().resetToDefault( sender );
            paramsModel.getBaseBuildCostModel().resetToDefault( sender );
            paramsModel.getBaseBuildStepsModel().resetToDefault( sender );
            paramsModel.getBaseScuttleCostModel().resetToDefault( sender );
            paramsModel.getFillCostModel().resetToDefault( sender );
            paramsModel.getDigCostModel().resetToDefault( sender );

            paramsModel.getHillSteepnessModel().resetToDefault( sender );

            paramsModel.getAllowKeyRepeatModel().resetToDefault( sender );
            paramsModel.getEnableAttackModel().resetToDefault( sender );

            paramsModel.getEnableParaTroopsModel().resetToDefault( sender );
            paramsModel.getParaTroopsRangeModel().resetToDefault( sender );
            paramsModel.getParaTroopsCostModel().resetToDefault( sender );
            paramsModel.getParaTroopsDamageModel().resetToDefault( sender );

            paramsModel.getEnableGunTroopsModel().resetToDefault( sender );
            paramsModel.getGunTroopsRangeModel().resetToDefault( sender );
            paramsModel.getGunTroopsCostModel().resetToDefault( sender );
            paramsModel.getGunTroopsDamageModel().resetToDefault( sender );

            paramsModel.getVisibilityModeModel().resetToDefault( sender );
            paramsModel.getVisibilityRangeModel().resetToDefault( sender );
            paramsModel.getVisibilityRememberCellsModel().resetToDefault( sender );
            paramsModel.getVisibilityHideVectorsModel().resetToDefault( sender );
        }
    }

    private void checkProfileName( Object sender )
    {
        String profileName = view.getTfProfileName().getText();

        boolean isDuplicate = model.isDuplicateProfileName( profileName );
        view.getLblDuplicateName().setVisible( isDuplicate );

        if ( !isDuplicate )
            //            model.getCurrentProfileModel().setProfileName( sender, profileName );
            model.getCurrent().setProfileName( sender, profileName );
    }

    private void updateUIState( GameParametersProfileModel gpf )
    {
        boolean rw = gpf == null ? true : !gpf.getReadOnly();

        view.getBtnDeleteProfile().setEnabled( rw );
        view.getBtnResetDefaults().setEnabled( rw );
    }

    private GameParametersProfileModel getSelectedProfile()
    {
        int si = view.getCmbProfiles().getSelectedIndex();
        if ( si != -1 )
            return model.get( si );

        return null;
    }

    private GameParametersModel getCurrentParameters()
    {
        //        return model.getCurrentProfileModel().getGameParametersModel();
        return model.getCurrent().getGameParametersModel();
    }

    private void m2uiCurrentGameParametersProfile( GameParametersProfileModel gpfm )
    {
        if ( gpfm != null )
        {
            gameSettingsPanelController.setModel( new GameSettingsPanelModel( gpfm.getGameParametersModel(), gpfm.getReadOnly() ) );
            view.getCmbProfiles().setSelectedItem( gpfm );
            view.getLblDuplicateName().setVisible( false );
            view.getTfProfileName().setText( gpfm.getProfileName() );
            updateUIState( gpfm );
        }
    }

    private void close_edt()
    {
        if ( gameSettingsPanelController != null )
        {
            gameSettingsPanelController.close();
            gameSettingsPanelController = null;
        }
        unsubscribeModel();
        view = null;
    }

    @Override
    public void close()
    {
        if ( SwingUtilities.isEventDispatchThread() )
            close_edt();
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    close_edt();
                }
            } );
    }

    // MVCController interface

    @Override
    public GameProfilesPanelView getView()
    {
        return view;
    }

    @Override
    public GameParametersProfilesModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( GameParametersProfilesModel m )
    {
        unsubscribeModel();
        removeComboProfilesListener();
        //        model = null;
        //
        //        if ( m instanceof GameParametersProfilesModel )
        //        {
        //            model = (GameParametersProfilesModel)m;
        model = m;
        subscribeModel();
        //        }

        modelToUI();
        addComboProfilesListener();
    }

    // MVCModelObserver interface

    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof XBMVCModelChangeId )
            switch ( (XBMVCModelChangeId)change.getChangeId() )
            {
                case GameParametersProfileChanged:
                    //m2uiCurrentGameParametersProfile( (GameParametersProfileModel)change.getChangedObject() );
                    //                    if ( change.getSender() != this )
                    m2uiCurrentGameParametersProfile( (GameParametersProfileModel)change.getData() );
                    break;

                case GameParametersProfileNameChanged:
                    view.getCmbProfiles().revalidate();
                    view.getCmbProfiles().updateUI();
                    break;

                default:
                    break;
            }

        if ( change.getChangeId() instanceof ListModelChangeId )
            switch ( (ListModelChangeId)change.getChangeId() )
            {
                case List_AddElement:
                case List_RemoveElement:
                    m2uiProfiles();
                    break;

                default:
                    break;
            }
    }

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        // called from code that is potentially not in the EDT...

        if ( SwingUtilities.isEventDispatchThread() )
            processModelChange( change );
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    processModelChange( change );
                }
            } );
    }

    @Override
    public void subscribeModel()
    {
        if ( model != null )
            model.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        if ( model != null )
            model.removeObserver( this );
    }
}
