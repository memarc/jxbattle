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

package jxbattle.bean.common.parameters.game;

import jxbattle.common.Consts;

import org.generic.bean.parameter.BoolParameter;
import org.generic.bean.parameter.EnumParameter;
import org.generic.bean.parameter.IntParameter;

/**
 * game parameters 
 */
public class GameParameters implements Cloneable
{
    /**
     * game board (dim/topology)
     */
    private BoardParameters boardParameters;

    /**
     * troops move speed
     */
    private IntParameter troopsMove;

    /**
     * spontaneous troops creation rate
     */
    private IntParameter supplyFarms;

    /**
     * game play parameters
     */
    private InitialisationParameters initialisationParameters;

    /**
     * defense/attack balance
     */
    private IntParameter fightIntensity;

    /**
     * base building allowed (or not)
     */
    private BoolParameter enableBaseBuild;

    /**
     * size of a base piece (bases are built step by step)   
     */
    private IntParameter baseBuildSteps;

    /**
     * how many troops are used to build a base piece
     */
    private IntParameter baseBuildCost;

    /**
     * base destruction allowed (or not)
     */
    private BoolParameter enableBaseScuttle;

    /**
     * how many troops are used to destroy a base piece
     */
    private IntParameter baseScuttleCost;

    /**
     * sea fill/ground rise enabled (or not)
     */
    private BoolParameter enableFill;

    /**
     * how many troops are used to raise ground level
     */
    private IntParameter fillCost;

    /**
     * sea/ground digging allowed (or not)
     */
    private BoolParameter enableDig;

    /**
     * how many troops are used to lower ground level
     */
    private IntParameter digCost;

    /**
     * how fast a occupied cell is emptying when no move exists
     */
    private IntParameter supplyDecay;

    /**
     * how hard it is to good to higher ground
     */
    private IntParameter hillSteepness;

    /**
     * enable (or not) key user commands to be repeated when hold a key down
     */
    private BoolParameter allowKeyRepeat;

    /**
     * visibility mode :
     *   0 : everything visible
     *   1 : invisible enemy outside range
     *   2 : invisible map outside range
     */
    private EnumParameter<InvisibilityMode> visibilityMode;

    /**
     * how far enemy/map is seen in map/enemy invisibility mode
     */
    private IntParameter visibilityRange;

    /**
     * show/hide enemy move vectors
     */
    private BoolParameter visibilityHideVectors;

    /**
     * keep discovered cells visible (or not)
     */
    private BoolParameter visibilityRememberCells;

    /**
     * enable simultaneously attacking a enemy cell with surrounding cells
     */
    private BoolParameter enableAttack;

    /**
     * enable send troops by parachute
     */
    private BoolParameter enableParatroops;

    /**
     * paratroops range
     */
    private IntParameter paratroopRange;

    /**
     * paratroops cost (for sender)
     */
    private IntParameter paratroopCost;

    /**
     * paratroops damage caused to receiver
     */
    private IntParameter paraTroopDamage;

    /**
     * enable fire gun troop
     */
    private BoolParameter enableGunTroops;

    /**
     * gun troop range
     */
    private IntParameter gunTroopRange;

    /**
     * gun troop cost (for sender)
     */
    private IntParameter gunTroopCost;

    /**
     * gun troop damage caused to receiver
     */
    private IntParameter gunTroopDamage;

    /**
     * managed commands switch 
     */
    private BoolParameter enableManagedCommands;

    /**
     * marching allowed (or not)
     */
    private BoolParameter enableMarching;

    /**
     * march update speed
     */
    private IntParameter marchSpeed;

    public GameParameters()
    {
        boardParameters = new BoardParameters();
        troopsMove = new IntParameter( 1, 10, 5 );

        supplyFarms = new IntParameter( 0, 10, 2 );
        supplyDecay = new IntParameter( 0, 10, 1 );

        initialisationParameters = new InitialisationParameters();
        fightIntensity = new IntParameter( 1, 10, 5 );

        enableAttack = new BoolParameter( false, false );
        enableManagedCommands = new BoolParameter( false, false );

        enableParatroops = new BoolParameter( false, false );
        paratroopRange = new IntParameter( 1, 3, 2 );
        paratroopCost = new IntParameter( 1, (int)Consts.maxTroopsLevel, (int)(Consts.maxTroopsLevel / 10) );
        paraTroopDamage = new IntParameter( 1, (int)Consts.maxTroopsLevel, (int)(Consts.maxTroopsLevel / 10) );

        enableGunTroops = new BoolParameter( false, false );
        gunTroopRange = new IntParameter( 1, 3, 2 );
        gunTroopCost = new IntParameter( 1, (int)Consts.maxTroopsLevel, (int)(Consts.maxTroopsLevel / 10) );
        gunTroopDamage = new IntParameter( 1, (int)Consts.maxTroopsLevel, (int)(Consts.maxTroopsLevel / 10) );

        enableBaseBuild = new BoolParameter( true, true );
        baseBuildSteps = new IntParameter( 1, Consts.maxTownBuildSteps, Consts.defaultTownBuildSteps );
        baseBuildCost = new IntParameter( 0, (int)Consts.maxTroopsLevel, (int)(Consts.maxTroopsLevel * 0.95) );

        enableBaseScuttle = new BoolParameter( true, true );
        baseScuttleCost = new IntParameter( 0, (int)Consts.maxTroopsLevel, (int)(Consts.maxTroopsLevel * 0.95) );

        enableFill = new BoolParameter( true, true );
        fillCost = new IntParameter( 0, (int)Consts.maxTroopsLevel, (int)(Consts.maxTroopsLevel * 0.95) );

        enableDig = new BoolParameter( true, true );
        digCost = new IntParameter( 0, (int)Consts.maxTroopsLevel, (int)(Consts.maxTroopsLevel * 0.95) );

        hillSteepness = new IntParameter( 1, 10, 5 );

        allowKeyRepeat = new BoolParameter( true, true );

        visibilityMode = new EnumParameter<InvisibilityMode>( InvisibilityMode.NONE );
        visibilityRange = new IntParameter( 1, 10, 2 );
        visibilityHideVectors = new BoolParameter( false, false );
        visibilityRememberCells = new BoolParameter( true, true );

        enableMarching = new BoolParameter( false, false );
        marchSpeed = new IntParameter( 1, Consts.maxMarchUpdateSpeed, Consts.maxMarchUpdateSpeed / 2 );
    }

    public GameParameters( GameParameters gp )
    {
        boardParameters = new BoardParameters( gp.boardParameters );
        troopsMove = new IntParameter( gp.troopsMove );
        supplyFarms = new IntParameter( gp.supplyFarms );
        initialisationParameters = new InitialisationParameters( gp.initialisationParameters );
        fightIntensity = new IntParameter( gp.fightIntensity );
        enableBaseBuild = new BoolParameter( gp.enableBaseBuild );
        baseBuildSteps = new IntParameter( gp.baseBuildSteps );
        baseBuildCost = new IntParameter( gp.baseBuildCost );
        enableBaseScuttle = new BoolParameter( gp.enableBaseScuttle );
        baseScuttleCost = new IntParameter( gp.baseScuttleCost );
        enableFill = new BoolParameter( gp.enableFill );
        fillCost = new IntParameter( gp.fillCost );
        enableDig = new BoolParameter( gp.enableDig );
        digCost = new IntParameter( gp.digCost );
        supplyDecay = new IntParameter( gp.supplyDecay );
        hillSteepness = new IntParameter( gp.hillSteepness );
        allowKeyRepeat = new BoolParameter( gp.allowKeyRepeat );
        visibilityMode = new EnumParameter<InvisibilityMode>( gp.visibilityMode );
        visibilityRange = new IntParameter( gp.visibilityRange );
        visibilityHideVectors = new BoolParameter( gp.visibilityHideVectors );
        visibilityRememberCells = new BoolParameter( gp.visibilityRememberCells );
        enableAttack = new BoolParameter( gp.enableAttack );
        enableParatroops = new BoolParameter( gp.enableParatroops );
        paratroopRange = new IntParameter( gp.paratroopRange );
        paratroopCost = new IntParameter( gp.paratroopCost );
        paraTroopDamage = new IntParameter( gp.paraTroopDamage );
        enableGunTroops = new BoolParameter( gp.enableGunTroops );
        gunTroopRange = new IntParameter( gp.gunTroopRange );
        gunTroopCost = new IntParameter( gp.gunTroopCost );
        gunTroopDamage = new IntParameter( gp.gunTroopDamage );
        enableManagedCommands = new BoolParameter( gp.enableManagedCommands );
        enableMarching = new BoolParameter( gp.enableMarching );
        marchSpeed = new IntParameter( gp.marchSpeed );
    }

    @Override
    public GameParameters clone()
    {
        return new GameParameters( this );
        //        try
        //        {
        //            GameParameters res = (GameParameters)super.clone();
        //
        //            res.boardParameters = boardParameters.clone();
        //            res.troopsMove = troopsMove.clone();
        //            res.supplyFarms = supplyFarms.clone();
        //            res.initialisationParameters = initialisationParameters.clone();
        //            res.fightIntensity = fightIntensity.clone();
        //            res.enableBaseBuild = enableBaseBuild.clone();
        //            res.baseBuildSteps = baseBuildSteps.clone();
        //            res.baseBuildCost = baseBuildCost.clone();
        //            res.enableBaseScuttle = enableBaseScuttle.clone();
        //            res.baseScuttleCost = baseScuttleCost.clone();
        //            res.enableFill = enableFill.clone();
        //            res.fillCost = fillCost.clone();
        //            res.enableDig = enableDig.clone();
        //            res.digCost = digCost.clone();
        //            res.supplyDecay = supplyDecay.clone();
        //            res.hillSteepness = hillSteepness.clone();
        //            res.allowKeyRepeat = allowKeyRepeat.clone();
        //            res.visibilityMode = visibilityMode.clone();
        //            res.visibilityRange = visibilityRange.clone();
        //            res.visibilityHideVectors = visibilityHideVectors.clone();
        //            res.visibilityRememberCells = visibilityRememberCells.clone();
        //            res.enableAttack = enableAttack.clone();
        //            res.enableParatroops = enableParatroops.clone();
        //            res.paratroopRange = paratroopRange.clone();
        //            res.paratroopCost = paratroopCost.clone();
        //            res.paraTroopDamage = paraTroopDamage.clone();
        //            res.enableGunTroops = enableGunTroops.clone();
        //            res.gunTroopRange = gunTroopRange.clone();
        //            res.gunTroopCost = gunTroopCost.clone();
        //            res.gunTroopDamage = gunTroopDamage.clone();
        //            res.enableManagedCommands = enableManagedCommands.clone();
        //            res.enableMarching = enableMarching.clone();
        //            res.marchSpeed = marchSpeed.clone();
        //
        //            return res;
        //        }
        //        catch( CloneNotSupportedException e )
        //        {
        //            e.printStackTrace();
        //            return null;
        //        }
    }

    public BoardParameters getBoardParameters()
    {
        return boardParameters;
    }

    public void setBoardParameters( BoardParameters m )
    {
        boardParameters = m;
    }

    public IntParameter getTroopsMove()
    {
        return troopsMove;
    }

    public InitialisationParameters getInitialisationParameters()
    {
        return initialisationParameters;
    }

    public IntParameter getSupplyFarms()
    {
        return supplyFarms;
    }

    public IntParameter getFightIntensity()
    {
        return fightIntensity;
    }

    public BoolParameter getEnableManagedCommands()
    {
        return enableManagedCommands;
    }

    public BoolParameter getEnableBaseBuild()
    {
        return enableBaseBuild;
    }

    public IntParameter getBaseBuildSteps()
    {
        return baseBuildSteps;
    }

    public IntParameter getBaseBuildCost()
    {
        return baseBuildCost;
    }

    public BoolParameter getEnableBaseScuttle()
    {
        return enableBaseScuttle;
    }

    public IntParameter getBaseScuttleCost()
    {
        return baseScuttleCost;
    }

    public BoolParameter getEnableFill()
    {
        return enableFill;
    }

    public IntParameter getFillCost()
    {
        return fillCost;
    }

    public BoolParameter getEnableDig()
    {
        return enableDig;
    }

    public IntParameter getDigCost()
    {
        return digCost;
    }

    public IntParameter getSupplyDecay()
    {
        return supplyDecay;
    }

    public IntParameter getHillSteepness()
    {
        return hillSteepness;
    }

    public BoolParameter getAllowKeyRepeat()
    {
        return allowKeyRepeat;
    }

    public EnumParameter<InvisibilityMode> getVisibilityMode()
    {
        return visibilityMode;
    }

    public IntParameter getVisibilityRange()
    {
        return visibilityRange;
    }

    public BoolParameter getVisibilityHideVectors()
    {
        return visibilityHideVectors;
    }

    public BoolParameter getVisibilityRememberCells()
    {
        return visibilityRememberCells;
    }

    //    public ServerGameParameters getServerGameParameters()
    //    {
    //        return serverGameParameters;
    //    }

    public BoolParameter getEnableAttack()
    {
        return enableAttack;
    }

    public BoolParameter getEnableParaTroops()
    {
        return enableParatroops;
    }

    public IntParameter getParaTroopsRange()
    {
        return paratroopRange;
    }

    public IntParameter getParaTroopsCost()
    {
        return paratroopCost;
    }

    public IntParameter getParaTroopsDamage()
    {
        return paraTroopDamage;
    }

    public BoolParameter getEnableGunTroops()
    {
        return enableGunTroops;
    }

    public IntParameter getGunTroopsRange()
    {
        return gunTroopRange;
    }

    public IntParameter getGunTroopsCost()
    {
        return gunTroopCost;
    }

    public IntParameter getGunTroopsDamage()
    {
        return gunTroopDamage;
    }

    public BoolParameter getEnableMarching()
    {
        return enableMarching;
    }

    public IntParameter getMarchSpeed()
    {
        return marchSpeed;
    }
}
