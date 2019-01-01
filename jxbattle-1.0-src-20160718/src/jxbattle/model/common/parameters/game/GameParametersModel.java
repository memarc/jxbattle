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

package jxbattle.model.common.parameters.game;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.parameter.BoolParameterModel;
import org.generic.mvc.model.parameter.EnumParameterModel;
import org.generic.mvc.model.parameter.IntParameterModel;

import jxbattle.bean.common.parameters.game.GameParameters;
import jxbattle.bean.common.parameters.game.InitialisationParameters;
import jxbattle.bean.common.parameters.game.InvisibilityMode;
import jxbattle.model.common.observer.XBMVCModelChangeId;

public class GameParametersModel extends MVCModelImpl
{
    // observed beans

    private GameParameters gameParameters;

    // models

    private InitialisationParametersModel initialisationParametersModel;

    private BoardParametersModel boardParametersModel;

    //private ServerGameParametersModel serverGameParametersModel;

    private IntParameterModel fightIntensityModel;

    private IntParameterModel supplyDecayModel;

    private IntParameterModel supplyFarmsModel;

    private IntParameterModel troopsMoveModel;

    private IntParameterModel baseBuildCostModel;

    private IntParameterModel baseBuildStepsModel;

    private IntParameterModel baseScuttleCostModel;

    private IntParameterModel fillCostModel;

    private IntParameterModel digCostModel;

    private IntParameterModel hillSteepnessModel;

    private IntParameterModel visibilityRangeModel;

    private BoolParameterModel enableBaseBuildModel;

    private BoolParameterModel enableBaseScuttleModel;

    private BoolParameterModel enableFillModel;

    private BoolParameterModel enableDigModel;

    private BoolParameterModel allowKeyRepeatModel;

    private BoolParameterModel enableAttackModel;

    private BoolParameterModel enableManagedCommandsModel;

    // para

    private BoolParameterModel enableParaTroopsModel;

    private IntParameterModel paraTroopsRangeModel;

    private IntParameterModel paraTroopsCostModel;

    private IntParameterModel paraTroopsDamageModel;

    // gun

    private BoolParameterModel enableGunTroopsModel;

    private IntParameterModel gunTroopsRangeModel;

    private IntParameterModel gunTroopsCostModel;

    private IntParameterModel gunTroopsDamageModel;

    // visibility

    private EnumParameterModel<InvisibilityMode> visibilityModeModel;

    private BoolParameterModel visibilityRememberCellsModel;

    private BoolParameterModel visibilityHideVectorsModel;

    // march

    private BoolParameterModel enableMarchingModel;

    private IntParameterModel marchSpeedModel;

    public GameParametersModel()
    {
    }

    public GameParametersModel( GameParameters gp )
    {
        gameParameters = gp;
    }

    public GameParameters getGameParameters()
    {
        return gameParameters;
    }

    public void setGameParameters( Object sender, GameParameters gp )
    {
        gameParameters = gp;

        if ( gameParameters != null )
        {
            getInitialisationParametersModel().setInitialisationParameters( sender, gameParameters.getInitialisationParameters() );
            getBoardParametersModel().setBoardParameters( sender, gameParameters.getBoardParameters() );
            getFightIntensityModel().setIntParameter( sender, gameParameters.getFightIntensity() );
            getSupplyDecayModel().setIntParameter( sender, gameParameters.getSupplyDecay() );
            getSupplyFarmsModel().setIntParameter( sender, gameParameters.getSupplyFarms() );
            getTroopsMoveModel().setIntParameter( sender, gameParameters.getTroopsMove() );
            getBaseBuildCostModel().setIntParameter( sender, gameParameters.getBaseBuildCost() );
            getBaseBuildStepsModel().setIntParameter( sender, gameParameters.getBaseBuildSteps() );
            getBaseScuttleCostModel().setIntParameter( sender, gameParameters.getBaseScuttleCost() );
            getFillCostModel().setIntParameter( sender, gameParameters.getFillCost() );
            getDigCostModel().setIntParameter( sender, gameParameters.getDigCost() );
            getHillSteepnessModel().setIntParameter( sender, gameParameters.getHillSteepness() );
            getVisibilityRangeModel().setIntParameter( sender, gameParameters.getVisibilityRange() );
            getEnableBaseBuildModel().setBoolParameter( sender, gameParameters.getEnableBaseBuild() );
            getEnableBaseScuttleModel().setBoolParameter( sender, gameParameters.getEnableBaseScuttle() );
            getEnableFillModel().setBoolParameter( sender, gameParameters.getEnableFill() );
            getEnableDigModel().setBoolParameter( sender, gameParameters.getEnableDig() );
            getAllowKeyRepeatModel().setBoolParameter( sender, gameParameters.getAllowKeyRepeat() );

            getEnableAttackModel().setBoolParameter( sender, gameParameters.getEnableAttack() );
            getEnableManagedCommandsModel().setBoolParameter( sender, gameParameters.getEnableManagedCommands() );

            getEnableParaTroopsModel().setBoolParameter( sender, gameParameters.getEnableParaTroops() );
            getParaTroopsRangeModel().setIntParameter( sender, gameParameters.getParaTroopsRange() );
            getParaTroopsCostModel().setIntParameter( sender, gameParameters.getParaTroopsCost() );
            getParaTroopsDamageModel().setIntParameter( sender, gameParameters.getParaTroopsDamage() );

            getEnableGunTroopsModel().setBoolParameter( sender, gameParameters.getEnableGunTroops() );
            getGunTroopsRangeModel().setIntParameter( sender, gameParameters.getGunTroopsRange() );
            getGunTroopsCostModel().setIntParameter( sender, gameParameters.getGunTroopsCost() );
            getGunTroopsDamageModel().setIntParameter( sender, gameParameters.getGunTroopsDamage() );

            getVisibilityModeModel().setEnumParameter( sender, gameParameters.getVisibilityMode() );
            getVisibilityRememberCellsModel().setBoolParameter( sender, gameParameters.getVisibilityRememberCells() );
            getVisibilityHideVectorsModel().setBoolParameter( sender, gameParameters.getVisibilityHideVectors() );

            getEnableMarchingModel().setBoolParameter( sender, gameParameters.getEnableMarching() );
            getMarchSpeedModel().setIntParameter( sender, gameParameters.getMarchSpeed() );
        }

        MVCModelChange change = new MVCModelChange( sender, this, XBMVCModelChangeId.GameParametersChanged, gameParameters );
        notifyObservers( change );
    }

    public InitialisationParametersModel getInitialisationParametersModel()
    {
        if ( initialisationParametersModel == null )
            initialisationParametersModel = new InitialisationParametersModel( gameParameters.getInitialisationParameters() );
        return initialisationParametersModel;
    }

    public BoardParametersModel getBoardParametersModel()
    {
        if ( boardParametersModel == null )
            boardParametersModel = new BoardParametersModel( gameParameters.getBoardParameters() );
        return boardParametersModel;
    }

    public IntParameterModel getSupplyDecayModel()
    {
        if ( supplyDecayModel == null )
            supplyDecayModel = new IntParameterModel( gameParameters.getSupplyDecay() );
        return supplyDecayModel;
    }

    public IntParameterModel getFightIntensityModel()
    {
        if ( fightIntensityModel == null )
            fightIntensityModel = new IntParameterModel( gameParameters.getFightIntensity() );
        return fightIntensityModel;
    }

    public IntParameterModel getSupplyFarmsModel()
    {
        if ( supplyFarmsModel == null )
            supplyFarmsModel = new IntParameterModel( gameParameters.getSupplyFarms() );
        return supplyFarmsModel;
    }

    public IntParameterModel getTroopsMoveModel()
    {
        if ( troopsMoveModel == null )
            troopsMoveModel = new IntParameterModel( gameParameters.getTroopsMove() );
        return troopsMoveModel;
    }

    public IntParameterModel getBaseBuildCostModel()
    {
        if ( baseBuildCostModel == null )
            baseBuildCostModel = new IntParameterModel( gameParameters.getBaseBuildCost() );
        return baseBuildCostModel;
    }

    public IntParameterModel getBaseBuildStepsModel()
    {
        if ( baseBuildStepsModel == null )
            baseBuildStepsModel = new IntParameterModel( gameParameters.getBaseBuildSteps() );
        return baseBuildStepsModel;
    }

    public IntParameterModel getBaseScuttleCostModel()
    {
        if ( baseScuttleCostModel == null )
            baseScuttleCostModel = new IntParameterModel( gameParameters.getBaseScuttleCost() );
        return baseScuttleCostModel;
    }

    public IntParameterModel getFillCostModel()
    {
        if ( fillCostModel == null )
            fillCostModel = new IntParameterModel( gameParameters.getFillCost() );
        return fillCostModel;
    }

    public IntParameterModel getDigCostModel()
    {
        if ( digCostModel == null )
            digCostModel = new IntParameterModel( gameParameters.getDigCost() );
        return digCostModel;
    }

    public IntParameterModel getHillSteepnessModel()
    {
        if ( hillSteepnessModel == null )
            hillSteepnessModel = new IntParameterModel( gameParameters.getHillSteepness() );
        return hillSteepnessModel;
    }

    public IntParameterModel getVisibilityRangeModel()
    {
        if ( visibilityRangeModel == null )
            visibilityRangeModel = new IntParameterModel( gameParameters.getVisibilityRange() );
        return visibilityRangeModel;
    }

    public BoolParameterModel getEnableBaseBuildModel()
    {
        if ( enableBaseBuildModel == null )
            enableBaseBuildModel = new BoolParameterModel( gameParameters.getEnableBaseBuild() );
        return enableBaseBuildModel;
    }

    public BoolParameterModel getEnableBaseScuttleModel()
    {
        if ( enableBaseScuttleModel == null )
            enableBaseScuttleModel = new BoolParameterModel( gameParameters.getEnableBaseScuttle() );
        return enableBaseScuttleModel;
    }

    public BoolParameterModel getEnableFillModel()
    {
        if ( enableFillModel == null )
            enableFillModel = new BoolParameterModel( gameParameters.getEnableFill() );
        return enableFillModel;
    }

    public BoolParameterModel getEnableDigModel()
    {
        if ( enableDigModel == null )
            enableDigModel = new BoolParameterModel( gameParameters.getEnableDig() );
        return enableDigModel;
    }

    public BoolParameterModel getAllowKeyRepeatModel()
    {
        if ( allowKeyRepeatModel == null )
            allowKeyRepeatModel = new BoolParameterModel( gameParameters.getAllowKeyRepeat() );
        return allowKeyRepeatModel;
    }

    public BoolParameterModel getEnableAttackModel()
    {
        if ( enableAttackModel == null )
            enableAttackModel = new BoolParameterModel( gameParameters.getEnableAttack() );
        return enableAttackModel;
    }

    public BoolParameterModel getEnableManagedCommandsModel()
    {
        if ( enableManagedCommandsModel == null )
            enableManagedCommandsModel = new BoolParameterModel( gameParameters.getEnableManagedCommands() );
        return enableManagedCommandsModel;
    }

    public BoolParameterModel getEnableParaTroopsModel()
    {
        if ( enableParaTroopsModel == null )
            enableParaTroopsModel = new BoolParameterModel( gameParameters.getEnableParaTroops() );
        return enableParaTroopsModel;
    }

    public IntParameterModel getParaTroopsRangeModel()
    {
        if ( paraTroopsRangeModel == null )
            paraTroopsRangeModel = new IntParameterModel( gameParameters.getParaTroopsRange() );
        return paraTroopsRangeModel;
    }

    public IntParameterModel getParaTroopsCostModel()
    {
        if ( paraTroopsCostModel == null )
            paraTroopsCostModel = new IntParameterModel( gameParameters.getParaTroopsCost() );
        return paraTroopsCostModel;
    }

    public IntParameterModel getParaTroopsDamageModel()
    {
        if ( paraTroopsDamageModel == null )
            paraTroopsDamageModel = new IntParameterModel( gameParameters.getParaTroopsDamage() );
        return paraTroopsDamageModel;
    }

    public BoolParameterModel getEnableGunTroopsModel()
    {
        if ( enableGunTroopsModel == null )
            enableGunTroopsModel = new BoolParameterModel( gameParameters.getEnableGunTroops() );
        return enableGunTroopsModel;
    }

    public IntParameterModel getGunTroopsRangeModel()
    {
        if ( gunTroopsRangeModel == null )
            gunTroopsRangeModel = new IntParameterModel( gameParameters.getGunTroopsRange() );
        return gunTroopsRangeModel;
    }

    public IntParameterModel getGunTroopsCostModel()
    {
        if ( gunTroopsCostModel == null )
            gunTroopsCostModel = new IntParameterModel( gameParameters.getGunTroopsCost() );
        return gunTroopsCostModel;
    }

    public IntParameterModel getGunTroopsDamageModel()
    {
        if ( gunTroopsDamageModel == null )
            gunTroopsDamageModel = new IntParameterModel( gameParameters.getGunTroopsDamage() );
        return gunTroopsDamageModel;
    }

    public EnumParameterModel<InvisibilityMode> getVisibilityModeModel()
    {
        if ( visibilityModeModel == null )
            visibilityModeModel = new EnumParameterModel<>( gameParameters.getVisibilityMode() );
        return visibilityModeModel;
    }

    public BoolParameterModel getVisibilityRememberCellsModel()
    {
        if ( visibilityRememberCellsModel == null )
            visibilityRememberCellsModel = new BoolParameterModel( gameParameters.getVisibilityRememberCells() );
        return visibilityRememberCellsModel;
    }

    public BoolParameterModel getVisibilityHideVectorsModel()
    {
        if ( visibilityHideVectorsModel == null )
            visibilityHideVectorsModel = new BoolParameterModel( gameParameters.getVisibilityHideVectors() );
        return visibilityHideVectorsModel;
    }

    public BoolParameterModel getEnableMarchingModel()
    {
        if ( enableMarchingModel == null )
            enableMarchingModel = new BoolParameterModel( gameParameters.getEnableMarching() );
        return enableMarchingModel;
    }

    public IntParameterModel getMarchSpeedModel()
    {
        if ( marchSpeedModel == null )
            marchSpeedModel = new IntParameterModel( gameParameters.getMarchSpeed() );
        return marchSpeedModel;
    }

    public void prepareRandomSeed()
    {
        if ( getInitialisationParametersModel().getGenerateRandomSeedModel().getValue() )
            getInitialisationParametersModel().getRandomSeedModel().setValue( this, System.currentTimeMillis() );
    }

    public void checkParameters() throws Exception
    {
        // empty map because of null values ?

        InitialisationParameters ip = gameParameters.getInitialisationParameters();
        if ( ip.getBases().getValue() == 0 && ip.getRandomBases().getValue() == 0 && ip.getTowns().getValue() == 0 && ip.getArmies().getValue() == 0 && ip.getMilitia().getValue() == 0 )
            throw new Exception( "Bases, random bases, towns, armies, and militia parameters are all null.\nThis leads to an empty map, please modify these values." );
    }
}
