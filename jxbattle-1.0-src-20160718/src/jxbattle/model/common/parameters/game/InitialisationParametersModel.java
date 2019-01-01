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

import jxbattle.bean.common.parameters.game.InitialisationParameters;
import jxbattle.model.common.parameters.LongParameterModel;

import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.parameter.BoolParameterModel;
import org.generic.mvc.model.parameter.IntParameterModel;

public class InitialisationParametersModel extends MVCModelImpl
{
    // observed beans

    private InitialisationParameters initialisationParameters;

    // models

    private BoolParameterModel generateRandomSeedModel;

    private LongParameterModel randomSeedModel;

    private IntParameterModel basesModel;

    private IntParameterModel randomBasesModel;

    private IntParameterModel townsModel;

    private IntParameterModel armiesModel;

    private IntParameterModel militiaModel;

    private IntParameterModel seaDensityModel;

    private IntParameterModel hillDensityModel;

    InitialisationParametersModel( InitialisationParameters ip )
    {
        initialisationParameters = ip;
    }

    public InitialisationParameters getInitialisationParameters()
    {
        return initialisationParameters;
    }

    public void setInitialisationParameters( Object sender, InitialisationParameters ip )
    {
        initialisationParameters = ip;

        getGenerateRandomSeedModel().setBoolParameter( sender, initialisationParameters.getGenerateRandomSeed() );
        getRandomSeedModel().setLongParameter( sender, initialisationParameters.getRandomSeed() );
        getBasesModel().setIntParameter( sender, initialisationParameters.getBases() );
        getRandomBasesModel().setIntParameter( sender, initialisationParameters.getRandomBases() );
        getTownsModel().setIntParameter( sender, initialisationParameters.getTowns() );
        getArmiesModel().setIntParameter( sender, initialisationParameters.getArmies() );
        getMilitiaModel().setIntParameter( sender, initialisationParameters.getMilitia() );
        getSeaDensityModel().setIntParameter( sender, initialisationParameters.getSeaDensity() );
        getHillDensityModel().setIntParameter( sender, initialisationParameters.getHillDensity() );
    }

    public BoolParameterModel getGenerateRandomSeedModel()
    {
        if ( generateRandomSeedModel == null )
            generateRandomSeedModel = new BoolParameterModel( initialisationParameters.getGenerateRandomSeed() );
        return generateRandomSeedModel;
    }

    public LongParameterModel getRandomSeedModel()
    {
        if ( randomSeedModel == null )
            randomSeedModel = new LongParameterModel( getInitialisationParameters().getRandomSeed() );
        return randomSeedModel;
    }

    public IntParameterModel getBasesModel()
    {
        if ( basesModel == null )
            basesModel = new IntParameterModel( getInitialisationParameters().getBases() );
        return basesModel;
    }

    public IntParameterModel getRandomBasesModel()
    {
        if ( randomBasesModel == null )
            randomBasesModel = new IntParameterModel( getInitialisationParameters().getRandomBases() );
        return randomBasesModel;
    }

    public IntParameterModel getTownsModel()
    {
        if ( townsModel == null )
            townsModel = new IntParameterModel( getInitialisationParameters().getTowns() );
        return townsModel;
    }

    public IntParameterModel getArmiesModel()
    {
        if ( armiesModel == null )
            armiesModel = new IntParameterModel( getInitialisationParameters().getArmies() );
        return armiesModel;
    }

    public IntParameterModel getMilitiaModel()
    {
        if ( militiaModel == null )
            militiaModel = new IntParameterModel( getInitialisationParameters().getMilitia() );
        return militiaModel;
    }

    public IntParameterModel getSeaDensityModel()
    {
        if ( seaDensityModel == null )
            seaDensityModel = new IntParameterModel( getInitialisationParameters().getSeaDensity() );
        return seaDensityModel;
    }

    public IntParameterModel getHillDensityModel()
    {
        if ( hillDensityModel == null )
            hillDensityModel = new IntParameterModel( getInitialisationParameters().getHillDensity() );
        return hillDensityModel;
    }
}
