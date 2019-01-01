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

package jxbattle.gui.base.common.gamesettingspanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import jxbattle.bean.common.parameters.game.InvisibilityMode;
import jxbattle.bean.common.parameters.game.WrappingMode;
import jxbattle.gui.base.common.parameters.LongTextFieldController;
import jxbattle.model.common.parameters.game.GameParametersModel;

import org.generic.gui.GuiUtils;
import org.generic.gui.parameters.BoolCheckController;
import org.generic.gui.parameters.IntSpinnerController;
import org.generic.gui.parameters.IntSpinnerModel;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

/*
 * TODO bug serveur : cumul du timeout socket et update tick
 * TODO bug : lenteur en réseau 
 * TODO à priori réglé : fuites sur la formation en croix (dépend du param "move" : si lent, pas de fuite, si rapide, fuite)

 * TODO finir implémenter options
 * TODO options : fusionner move et fight intensity
 * TODO rayon invisibilité augmente avec relief
 * TODO autres topologies
 * TODO alliances avec mélange de flux, mines, téléporteur ?
 * TODO IA
 * TODO viseur guns/para : touche espace affiche la ligne de visée en pointillés
 * TODO help mode : faire clignoter (+ tooltip) les cellules en opposition de direction, les cases vides si farm>0, les cases trop peu remplies à coté de l'ennemi (sauf si combat), les cases bien remplis pouvant attaquer, les cases pleines inutilisées au milieu de cases amies, les cases pointant vers la mer ou le bord (si pas de wrap)
 * TODO chat
 * TODO clic sur label nom joueur/% : passer du % de couverture à la masse de liquide  

 * TODO indicateur de ping/bloquage réseau pendant le jeu
 * TODO panel statut des joueurs : activité (% commandes/total)
 * TODO game log : sauver les parametres de jeu
 * TODO game log : afficher les quantités de troupes déplacées en mode debug
 * TODO raccourcis clavier redéfinissables
 * TODO séparer la GUI serveur et le serveur
 * TODO multiparties (serveur de parties)
 * TODO mode spectateur
 * TODO editeur de carte

 * TODO zqsd : dir
 * TODO actions avec 1 2 3 ...
 * TODO touches configurables
 * TODO plantage para 
 */

public class GameSettingsPanelController implements MVCController<GameSettingsPanelModel, GameSettingsPanelView>, MVCModelObserver
{
    private GameSettingsPanelModel model;

    private GameParametersModel gameParametersModel;

    private GameSettingsPanelView view;

    private LongTextFieldController randomSeedController;

    private IntSpinnerController boardXController;

    private IntSpinnerController boardYController;

    private IntSpinnerController basesController;

    private IntSpinnerController randomBasesController;

    private IntSpinnerController townsController;

    private IntSpinnerController armiesController;

    private IntSpinnerController militiaController;

    private IntSpinnerController seaDensityController;

    private IntSpinnerController hillDensityController;

    private IntSpinnerController troopsMoveController;

    private IntSpinnerController supplyFarmsController;

    private IntSpinnerController supplyDecayController;

    private IntSpinnerController fightIntensityController;

    private IntSpinnerController baseBuildCostController;

    private IntSpinnerController buildStepsController;

    private IntSpinnerController baseScuttleCostController;

    private IntSpinnerController fillCostController;

    private IntSpinnerController digCostController;

    private IntSpinnerController hillSteepnessController;

    private IntSpinnerController visibilityRangeController;

    private BoolCheckController generateRandomSeedController;

    private BoolCheckController allowKeyRepeatController;

    private BoolCheckController enableAttackController;

    private BoolCheckController enableManagedCommandsController;

    // para

    private BoolCheckController enableParaTroopsController;

    private IntSpinnerController paraTroopsRangeController;

    private IntSpinnerController paraTroopsCostController;

    private IntSpinnerController paraTroopsDamageController;

    // gun
    private BoolCheckController enableGunTroopsController;

    private IntSpinnerController gunTroopsRangeController;

    private IntSpinnerController gunTroopsCostController;

    private IntSpinnerController gunTroopsDamageController;

    // build/scuttle base

    private BoolCheckController enableBuildBaseController;

    private BoolCheckController enableScuttleBaseController;

    private BoolCheckController enableFillController;

    private BoolCheckController enableDigController;

    private BoolCheckController hideEnemyVectorsController;

    private BoolCheckController rememberExploredCellsController;

    //private BoolCheckController drawGridController;

    // march

    private BoolCheckController enableMarchingController;

    private IntSpinnerController marchSpeedController;

    public GameSettingsPanelController( GameSettingsPanelView v )
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
        ToolTipManager.sharedInstance().setInitialDelay( 500 );
        ToolTipManager.sharedInstance().setReshowDelay( 0 );

        randomSeedController = new LongTextFieldController( view.getTfRandomSeed() );
        boardXController = new IntSpinnerController( view.getSpinBoardx() );
        boardYController = new IntSpinnerController( view.getSpinBoardy() );
        basesController = new IntSpinnerController( view.getSpinBases() );
        randomBasesController = new IntSpinnerController( view.getSpinRandomBases() );
        townsController = new IntSpinnerController( view.getSpinTowns() );
        armiesController = new IntSpinnerController( view.getSpinArmies() );
        militiaController = new IntSpinnerController( view.getSpinMilitia() );
        seaDensityController = new IntSpinnerController( view.getSpinSeaDensity() );
        hillDensityController = new IntSpinnerController( view.getSpinHillDensity() );
        troopsMoveController = new IntSpinnerController( view.getSpinTroopsMove() );
        supplyFarmsController = new IntSpinnerController( view.getSpinFarms() );
        supplyDecayController = new IntSpinnerController( view.getSpinDecay() );
        fightIntensityController = new IntSpinnerController( view.getSpinFight() );
        baseBuildCostController = new IntSpinnerController( view.getSpinBaseBuildCost() );
        buildStepsController = new IntSpinnerController( view.getSpinBuildSteps() );
        baseScuttleCostController = new IntSpinnerController( view.getSpinBaseScuttleCost() );
        fillCostController = new IntSpinnerController( view.getSpinFillCost() );
        digCostController = new IntSpinnerController( view.getSpinDigCost() );
        hillSteepnessController = new IntSpinnerController( view.getSpinHillSteepness() );
        visibilityRangeController = new IntSpinnerController( view.getSpinVisibilityRange() );
        generateRandomSeedController = new BoolCheckController( view.getCbGenerateRandom() );
        allowKeyRepeatController = new BoolCheckController( view.getCbAllowKeyRepeat() );
        enableAttackController = new BoolCheckController( view.getCbEnableAttack() );
        enableManagedCommandsController = new BoolCheckController( view.getCbManagedCommands() );

        enableParaTroopsController = new BoolCheckController( view.getPnlPara().getCheckBox() );
        paraTroopsRangeController = new IntSpinnerController( view.getSpinParaRange() );
        paraTroopsCostController = new IntSpinnerController( view.getSpinParaCost() );
        paraTroopsDamageController = new IntSpinnerController( view.getSpinParaDamage() );

        enableGunTroopsController = new BoolCheckController( view.getPnlGun().getCheckBox() );
        gunTroopsRangeController = new IntSpinnerController( view.getSpinGunRange() );
        gunTroopsCostController = new IntSpinnerController( view.getSpinGunCost() );
        gunTroopsDamageController = new IntSpinnerController( view.getSpinGunDamage() );

        enableBuildBaseController = new BoolCheckController( view.getCbEnableBuildBase() );
        enableScuttleBaseController = new BoolCheckController( view.getCbEnableScuttleBase() );
        enableFillController = new BoolCheckController( view.getCbEnableFill() );
        enableDigController = new BoolCheckController( view.getCbEnableDig() );
        hideEnemyVectorsController = new BoolCheckController( view.getCbHideEnemyVectors() );
        rememberExploredCellsController = new BoolCheckController( view.getCbRememberExploredCells() );
        //drawGridController = new BoolCheckController( view.getCbDrawGrid() );

        enableMarchingController = new BoolCheckController( view.getPnlMarch().getCheckBox() );
        marchSpeedController = new IntSpinnerController( view.getSpinMarchSpeed() );

        createHandlers();
    }

    private void createHandlers()
    {
        // visibility mode

        //        view.getRadioVisibilityAll().addActionListener( new ActionListener()
        //        {
        //            @SuppressWarnings("unused")
        //            @Override
        //            public void actionPerformed( ActionEvent e )
        //            {
        //                gpm.getVisibilityModeModel().setValue( this, InvisibilityMode.NONE );
        //            }
        //        } );

        view.getPnlEnemyMap().getCheckBox().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                JCheckBox cb = (JCheckBox)e.getSource();
                if ( !cb.isSelected() )
                    gameParametersModel.getVisibilityModeModel().setValue( GameSettingsPanelController.this, InvisibilityMode.NONE );
                else
                {
                    // none -> map by default
                    boolean enemy = view.getRadioVisibilityEnemy().isSelected();
                    boolean map = view.getRadioVisibilityMap().isSelected();
                    if ( !enemy && !map )
                    {
                        gameParametersModel.getVisibilityModeModel().setValue( GameSettingsPanelController.this, InvisibilityMode.INVISIBLE_MAP );
                    }
                    else
                    {
                        if ( view.getRadioVisibilityMap().isSelected() )
                            gameParametersModel.getVisibilityModeModel().setValue( GameSettingsPanelController.this, InvisibilityMode.INVISIBLE_MAP );
                        else if ( view.getRadioVisibilityEnemy().isSelected() )
                            gameParametersModel.getVisibilityModeModel().setValue( GameSettingsPanelController.this, InvisibilityMode.INVISIBLE_ENEMY );
                    }
                }
            }
        } );

        view.getRadioVisibilityEnemy().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                gameParametersModel.getVisibilityModeModel().setValue( GameSettingsPanelController.this, InvisibilityMode.INVISIBLE_ENEMY );
            }
        } );

        view.getRadioVisibilityMap().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                gameParametersModel.getVisibilityModeModel().setValue( GameSettingsPanelController.this, InvisibilityMode.INVISIBLE_MAP );
            }
        } );

        // wrapping mode

        view.getPnlWrapping().getCheckBox().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                JCheckBox cb = (JCheckBox)e.getSource();
                if ( !cb.isSelected() )
                    gameParametersModel.getBoardParametersModel().getWrappingModeModel().setValue( GameSettingsPanelController.this, WrappingMode.NONE );
                else
                {
                    // none -> full by default
                    boolean top = view.getRadioWrappingTopDown().isSelected();
                    boolean left = view.getRadioWrappingLeftRight().isSelected();
                    boolean full = view.getRadioWrappingFull().isSelected();
                    if ( !top && !left && !full )
                        gameParametersModel.getBoardParametersModel().getWrappingModeModel().setValue( GameSettingsPanelController.this, WrappingMode.FULL );
                    else
                    {
                        if ( view.getRadioWrappingTopDown().isSelected() )
                            gameParametersModel.getBoardParametersModel().getWrappingModeModel().setValue( GameSettingsPanelController.this, WrappingMode.TOP_DOWN );
                        else if ( view.getRadioWrappingLeftRight().isSelected() )
                            gameParametersModel.getBoardParametersModel().getWrappingModeModel().setValue( GameSettingsPanelController.this, WrappingMode.LEFT_RIGHT );
                        else if ( view.getRadioWrappingFull().isSelected() )
                            gameParametersModel.getBoardParametersModel().getWrappingModeModel().setValue( GameSettingsPanelController.this, WrappingMode.FULL );
                    }
                }
            }
        } );

        view.getRadioWrappingLeftRight().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                gameParametersModel.getBoardParametersModel().getWrappingModeModel().setValue( GameSettingsPanelController.this, WrappingMode.LEFT_RIGHT );
            }
        } );

        view.getRadioWrappingTopDown().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                gameParametersModel.getBoardParametersModel().getWrappingModeModel().setValue( GameSettingsPanelController.this, WrappingMode.TOP_DOWN );
            }
        } );

        view.getRadioWrappingFull().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                gameParametersModel.getBoardParametersModel().getWrappingModeModel().setValue( GameSettingsPanelController.this, WrappingMode.FULL );
            }
        } );
    }

    private void modelToUI_edt()
    {
        //        boolean genSeed = model.getGameParametersProfileModel().getGameParametersModel().getInitialisationParametersModel().getGenerateRandomSeedModel().getValue();
        boolean genSeed = model.getGameParametersModel().getInitialisationParametersModel().getGenerateRandomSeedModel().getValue();
        //        boolean readOnlyProfile = model.getGameParametersProfileModel().getReadOnly();
        view.getTfRandomSeed().setVisible( model.isViewOnly() || !genSeed );

        //        if ( model.isViewOnly() || readOnlyProfile )
        if ( model.isViewOnly() )
        {
            uiSetVisibilityMode();
            uiSetWrappingMode();
            uiSetParaTroops();
            GuiUtils.setRecursiveEnable( view.getPnlGameplay(), false );
            GuiUtils.setRecursiveEnable( view.getPnlMap(), false );
        }
        else
        {
            GuiUtils.setRecursiveEnable( view, true );

            uiEnableRandomSeed();
            uiEnableBuild();
            uiEnableScuttle();
            uiEnableFill();
            uiEnableDig();
            uiSetVisibilityMode();
            uiSetWrappingMode();
            uiSetParaTroops();
        }
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

    private void uiEnableBuild()
    {
        boolean b = gameParametersModel.getEnableBaseBuildModel().getValue();
        view.getSpinBaseBuildCost().setEnabled( b );
        view.getLblBuildCost().setEnabled( b );
        uiEnableBuildSteps();
    }

    private void uiEnableScuttle()
    {
        boolean b = gameParametersModel.getEnableBaseScuttleModel().getValue();
        view.getSpinBaseScuttleCost().setEnabled( b );
        view.getLblScuttleCost().setEnabled( b );
        uiEnableBuildSteps();
    }

    private void uiEnableBuildSteps()
    {
        boolean bb = gameParametersModel.getEnableBaseBuildModel().getValue();
        boolean bs = gameParametersModel.getEnableBaseScuttleModel().getValue();
        view.getSpinBuildSteps().setEnabled( bb || bs );
        view.getLblBuildscuttleSteps().setEnabled( bb || bs );
    }

    private void uiEnableFill()
    {
        boolean b = gameParametersModel.getEnableFillModel().getValue();
        view.getSpinFillCost().setEnabled( b );
        view.getLblFillCost().setEnabled( b );
    }

    private void uiEnableDig()
    {
        boolean b = gameParametersModel.getEnableDigModel().getValue();
        view.getSpinDigCost().setEnabled( b );
        view.getLblDigCost().setEnabled( b );
    }

    private void uiSetVisibilityMode()
    {
        switch ( (InvisibilityMode)gameParametersModel.getVisibilityModeModel().getValue() )
        {
            case NONE:
                //view.getRadioVisibilityAll().setSelected( true );
                view.getPnlEnemyMap().setCheckBoxSelected( false );
                //                view.getLblRange().setEnabled( false );
                //                view.getSpinVisibilityRange().setEnabled( false );
                //                view.getCbRememberExploredCells().setEnabled( false );
                view.getCbRememberExploredCells().setSelected( false );
                view.getButtonGroupVisibility().clearSelection();
                break;

            case INVISIBLE_ENEMY:
                view.getPnlEnemyMap().setCheckBoxSelected( true );
                view.getRadioVisibilityEnemy().setSelected( true );
                //                view.getLblRange().setEnabled( true );
                //                view.getSpinVisibilityRange().setEnabled( true );
                //                view.getCbRememberExploredCells().setEnabled( true );
                break;

            case INVISIBLE_MAP:
                view.getPnlEnemyMap().setCheckBoxSelected( true );
                view.getRadioVisibilityMap().setSelected( true );
                //                view.getLblRange().setEnabled( true );
                //                view.getSpinVisibilityRange().setEnabled( true );
                //                view.getCbRememberExploredCells().setEnabled( true );
                break;

            default:
                throw new MVCModelError( "invalid visibility mode " + gameParametersModel.getGameParameters().getVisibilityMode().getValue() );
        }
    }

    private void uiSetWrappingMode()
    {
        switch ( (WrappingMode)gameParametersModel.getBoardParametersModel().getWrappingModeModel().getValue() )
        {
            case NONE:
                view.getPnlWrapping().setCheckBoxSelected( false );
                view.getButtonGroupWrapping().clearSelection();
                break;

            case LEFT_RIGHT:
                view.getPnlWrapping().setCheckBoxSelected( true );
                view.getRadioWrappingLeftRight().setSelected( true );
                break;

            case TOP_DOWN:
                view.getPnlWrapping().setCheckBoxSelected( true );
                view.getRadioWrappingTopDown().setSelected( true );
                break;

            case FULL:
                view.getPnlWrapping().setCheckBoxSelected( true );
                view.getRadioWrappingFull().setSelected( true );
                break;

            default:
                throw new MVCModelError( "invalid visibility mode " + gameParametersModel.getGameParameters().getVisibilityMode().getValue() );
        }
    }

    private void uiSetParaTroops()
    {
        view.getPnlPara().setCheckBoxSelected( gameParametersModel.getEnableParaTroopsModel().getValue() );
    }

    private void uiSetGunTroops()
    {
        view.getPnlGun().setCheckBoxSelected( gameParametersModel.getEnableGunTroopsModel().getValue() );
    }

    private void uiEnableRandomSeed()
    {
        boolean b = gameParametersModel.getInitialisationParametersModel().getGenerateRandomSeedModel().getValue();
        view.getTfRandomSeed().setVisible( !b );
    }

    private void uiSetMarching()
    {
        view.getPnlMarch().setCheckBoxSelected( gameParametersModel.getEnableMarchingModel().getValue() );
    }

    // MVCModelObserver interface

    private void processModelChange( MVCModelChange change )
    {
        //        if ( change.getChangedObject() == gpm.getServerGameParametersModel().getGenerateRandomSeedModel() )
        //            uiEnableRandomSeed();
        //        else if ( change.getChangedObject() == gpm.getEnableBuildModel() )
        //            uiEnableBuild();
        //        else if ( change.getChangedObject() == gpm.getEnableScuttleModel() )
        //            uiEnableScuttle();
        //        else if ( change.getChangedObject() == gpm.getEnableFillModel() )
        //            uiEnableFill();
        //        else if ( change.getChangedObject() == gpm.getEnableDigModel() )
        //            uiEnableDig();
        //        else if ( change.getChangedObject() == gpm.getVisibilityModeModel() )
        //            uiSetVisibilityMode();
        //        else if ( change.getChangedObject() == gpm.getBoardParametersModel().getWrappingModeModel() )
        //            uiSetWrappingMode();
        //        else if ( change.getChangedObject() == gpm.getEnableParaTroopsModel() )
        //            uiSetParaTroops();
        //        else if ( change.getChangedObject() == gpm.getEnableGunTroopsModel() )
        //            uiSetGunTroops();

        if ( change.getSourceModel() == gameParametersModel.getInitialisationParametersModel().getGenerateRandomSeedModel() )
            uiEnableRandomSeed();
        else if ( change.getSourceModel() == gameParametersModel.getEnableBaseBuildModel() )
            uiEnableBuild();
        else if ( change.getSourceModel() == gameParametersModel.getEnableBaseScuttleModel() )
            uiEnableScuttle();
        else if ( change.getSourceModel() == gameParametersModel.getEnableFillModel() )
            uiEnableFill();
        else if ( change.getSourceModel() == gameParametersModel.getEnableDigModel() )
            uiEnableDig();
        else if ( change.getSourceModel() == gameParametersModel.getVisibilityModeModel() )
            uiSetVisibilityMode();
        else if ( change.getSourceModel() == gameParametersModel.getBoardParametersModel().getWrappingModeModel() )
            uiSetWrappingMode();
        else if ( change.getSourceModel() == gameParametersModel.getEnableParaTroopsModel() )
            uiSetParaTroops();
        else if ( change.getSourceModel() == gameParametersModel.getEnableGunTroopsModel() )
            uiSetGunTroops();
        else if ( change.getSourceModel() == gameParametersModel.getEnableMarchingModel() )
            uiSetMarching();
    }

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        // called from code that is potentially not in the EDT...

        //        if ( SwingUtilities.isEventDispatchThread() )
        //            processModelChange( change );
        //        else
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
        if ( gameParametersModel != null )
        {
            gameParametersModel.getInitialisationParametersModel().getGenerateRandomSeedModel().addObserver( this );
            gameParametersModel.getEnableBaseBuildModel().addObserver( this );
            gameParametersModel.getEnableBaseScuttleModel().addObserver( this );
            gameParametersModel.getEnableFillModel().addObserver( this );
            gameParametersModel.getEnableDigModel().addObserver( this );
            gameParametersModel.getVisibilityModeModel().addObserver( this );
            gameParametersModel.getBoardParametersModel().getWrappingModeModel().addObserver( this );
        }
    }

    @Override
    public void unsubscribeModel()
    {
        if ( gameParametersModel != null )
        {
            gameParametersModel.getInitialisationParametersModel().getGenerateRandomSeedModel().removeObserver( this );
            gameParametersModel.getEnableBaseBuildModel().removeObserver( this );
            gameParametersModel.getEnableBaseScuttleModel().removeObserver( this );
            gameParametersModel.getEnableFillModel().removeObserver( this );
            gameParametersModel.getEnableDigModel().removeObserver( this );
            gameParametersModel.getVisibilityModeModel().removeObserver( this );
            gameParametersModel.getBoardParametersModel().getWrappingModeModel().removeObserver( this );
        }
    }

    // MVCController interface

    @Override
    public GameSettingsPanelView getView()
    {
        return view;
    }

    @Override
    public GameSettingsPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( GameSettingsPanelModel m )
    {
        unsubscribeModel();
        //        model = null;
        //        if ( m instanceof GameSettingsPanelModel )
        //        {
        //            model = (GameSettingsPanelModel)m;
        model = m;

        //        gameParametersModel = model.getGameParametersProfileModel().getGameParametersModel();
        gameParametersModel = model.getGameParametersModel();

        randomSeedController.setModel( gameParametersModel.getInitialisationParametersModel().getRandomSeedModel() );
        boardXController.setModel( new IntSpinnerModel( gameParametersModel.getBoardParametersModel().getXCellCountModel() ) );
        boardYController.setModel( new IntSpinnerModel( gameParametersModel.getBoardParametersModel().getYCellCountModel() ) );
        basesController.setModel( new IntSpinnerModel( gameParametersModel.getInitialisationParametersModel().getBasesModel() ) );
        randomBasesController.setModel( new IntSpinnerModel( gameParametersModel.getInitialisationParametersModel().getRandomBasesModel() ) );
        townsController.setModel( new IntSpinnerModel( gameParametersModel.getInitialisationParametersModel().getTownsModel() ) );
        armiesController.setModel( new IntSpinnerModel( gameParametersModel.getInitialisationParametersModel().getArmiesModel() ) );
        militiaController.setModel( new IntSpinnerModel( gameParametersModel.getInitialisationParametersModel().getMilitiaModel() ) );
        seaDensityController.setModel( new IntSpinnerModel( gameParametersModel.getInitialisationParametersModel().getSeaDensityModel() ) );
        hillDensityController.setModel( new IntSpinnerModel( gameParametersModel.getInitialisationParametersModel().getHillDensityModel() ) );
        troopsMoveController.setModel( new IntSpinnerModel( gameParametersModel.getTroopsMoveModel() ) );
        supplyFarmsController.setModel( new IntSpinnerModel( gameParametersModel.getSupplyFarmsModel() ) );
        supplyDecayController.setModel( new IntSpinnerModel( gameParametersModel.getSupplyDecayModel() ) );
        fightIntensityController.setModel( new IntSpinnerModel( gameParametersModel.getFightIntensityModel() ) );

        IntSpinnerModel bbcm = new IntSpinnerModel( gameParametersModel.getBaseBuildCostModel() );
        bbcm.setDisplayedMinValue( 0 );
        bbcm.setDisplayedMaxValue( 10 );
        baseBuildCostController.setModel( bbcm );

        IntSpinnerModel bscm = new IntSpinnerModel( gameParametersModel.getBaseScuttleCostModel() );
        bscm.setDisplayedMinValue( 0 );
        bscm.setDisplayedMaxValue( 10 );
        baseScuttleCostController.setModel( bscm );

        buildStepsController.setModel( new IntSpinnerModel( gameParametersModel.getBaseBuildStepsModel() ) );

        IntSpinnerModel fcm = new IntSpinnerModel( gameParametersModel.getFillCostModel() );
        fcm.setDisplayedMinValue( 0 );
        fcm.setDisplayedMaxValue( 10 );
        fillCostController.setModel( fcm );

        IntSpinnerModel dcm = new IntSpinnerModel( gameParametersModel.getDigCostModel() );
        dcm.setDisplayedMinValue( 0 );
        dcm.setDisplayedMaxValue( 10 );
        digCostController.setModel( dcm );

        hillSteepnessController.setModel( new IntSpinnerModel( gameParametersModel.getHillSteepnessModel() ) );
        visibilityRangeController.setModel( new IntSpinnerModel( gameParametersModel.getVisibilityRangeModel() ) );
        generateRandomSeedController.setModel( gameParametersModel.getInitialisationParametersModel().getGenerateRandomSeedModel() );
        allowKeyRepeatController.setModel( gameParametersModel.getAllowKeyRepeatModel() );
        enableAttackController.setModel( gameParametersModel.getEnableAttackModel() );
        enableManagedCommandsController.setModel( gameParametersModel.getEnableManagedCommandsModel() );

        enableParaTroopsController.setModel( gameParametersModel.getEnableParaTroopsModel() );
        paraTroopsRangeController.setModel( new IntSpinnerModel( gameParametersModel.getParaTroopsRangeModel() ) );

        IntSpinnerModel ptcm = new IntSpinnerModel( gameParametersModel.getParaTroopsCostModel() );
        ptcm.setDisplayedMinValue( 1 );
        ptcm.setDisplayedMaxValue( 10 );
        paraTroopsCostController.setModel( ptcm );

        IntSpinnerModel ptdm = new IntSpinnerModel( gameParametersModel.getParaTroopsDamageModel() );
        ptdm.setDisplayedMinValue( 1 );
        ptdm.setDisplayedMaxValue( 10 );
        paraTroopsDamageController.setModel( ptdm );

        enableGunTroopsController.setModel( gameParametersModel.getEnableGunTroopsModel() );
        gunTroopsRangeController.setModel( new IntSpinnerModel( gameParametersModel.getGunTroopsRangeModel() ) );

        IntSpinnerModel gtcm = new IntSpinnerModel( gameParametersModel.getGunTroopsCostModel() );
        gtcm.setDisplayedMinValue( 1 );
        gtcm.setDisplayedMaxValue( 10 );
        gunTroopsCostController.setModel( gtcm );

        IntSpinnerModel gtdm = new IntSpinnerModel( gameParametersModel.getGunTroopsDamageModel() );
        gtdm.setDisplayedMinValue( 1 );
        gtdm.setDisplayedMaxValue( 10 );
        gunTroopsDamageController.setModel( gtdm );

        enableBuildBaseController.setModel( gameParametersModel.getEnableBaseBuildModel() );
        enableScuttleBaseController.setModel( gameParametersModel.getEnableBaseScuttleModel() );
        enableFillController.setModel( gameParametersModel.getEnableFillModel() );
        enableDigController.setModel( gameParametersModel.getEnableDigModel() );
        hideEnemyVectorsController.setModel( gameParametersModel.getVisibilityHideVectorsModel() );
        rememberExploredCellsController.setModel( gameParametersModel.getVisibilityRememberCellsModel() );

        enableMarchingController.setModel( gameParametersModel.getEnableMarchingModel() );
        IntSpinnerModel msm = new IntSpinnerModel( gameParametersModel.getMarchSpeedModel() );
        msm.setDisplayedMinValue( 1 );
        msm.setDisplayedMaxValue( 10 );
        marchSpeedController.setModel( msm );

        subscribeModel();
        //        }

        modelToUI();
    }

    private void close_edt()
    {
        unsubscribeModel();

        randomSeedController.close();
        boardXController.close();
        boardYController.close();
        basesController.close();
        randomBasesController.close();
        townsController.close();
        armiesController.close();
        militiaController.close();
        seaDensityController.close();
        hillDensityController.close();
        troopsMoveController.close();
        supplyFarmsController.close();
        supplyDecayController.close();
        fightIntensityController.close();
        baseBuildCostController.close();
        buildStepsController.close();
        baseScuttleCostController.close();
        fillCostController.close();
        digCostController.close();
        hillSteepnessController.close();
        visibilityRangeController.close();
        generateRandomSeedController.close();
        allowKeyRepeatController.close();
        enableAttackController.close();
        enableManagedCommandsController.close();
        enableParaTroopsController.close();
        paraTroopsRangeController.close();
        paraTroopsCostController.close();
        paraTroopsDamageController.close();
        enableGunTroopsController.close();
        gunTroopsRangeController.close();
        gunTroopsCostController.close();
        gunTroopsDamageController.close();
        enableBuildBaseController.close();
        enableScuttleBaseController.close();
        enableFillController.close();
        enableDigController.close();
        hideEnemyVectorsController.close();
        rememberExploredCellsController.close();
        enableMarchingController.close();
        marchSpeedController.close();

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
}
