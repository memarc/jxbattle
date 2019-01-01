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
import org.generic.bean.parameter.IntParameter;
import org.generic.bean.parameter.LongParameter;

/**
 * game initialisation parameters
 */
public class InitialisationParameters implements Cloneable {
	/**
	 * generate a random seed (or not), used only on server side
	 */
	private BoolParameter generateRandomSeed;

	/**
	 * random generator seed
	 */
	private LongParameter randomSeed;

	/**
	 * fixed base count
	 */
	private IntParameter bases;

	/**
	 * random base count
	 */
	private IntParameter randomBases;

	/**
	 * random small empty base density
	 */
	private IntParameter towns;

	/**
	 * full cell count
	 */
	private IntParameter armies;

	/**
	 * randomly placed cell density
	 */
	private IntParameter militia;

	/**
	 * sea density
	 */
	private IntParameter seaDensity;

	/**
	 * hill density
	 */
	private IntParameter hillDensity;

	public InitialisationParameters() {
		generateRandomSeed = new BoolParameter(true, true);
		randomSeed = new LongParameter(0, Long.MAX_VALUE, 0);
		bases = new IntParameter(0, Consts.maxBaseCount, 0); // 3
		randomBases = new IntParameter(0, Consts.maxRandomBaseCount, 0); // 3
		towns = new IntParameter(0, 10, 0); // 10
		armies = new IntParameter(0, Consts.maxArmiesCount, 0); // 3
		militia = new IntParameter(0, 10, 0); // 10
		seaDensity = new IntParameter(0, 10, 4);
		hillDensity = new IntParameter(0, 10, 4);
	}

	public InitialisationParameters(InitialisationParameters ip) {
		generateRandomSeed = new BoolParameter(ip.generateRandomSeed);
		randomSeed = new LongParameter(ip.randomSeed);
		bases = new IntParameter(ip.bases);
		randomBases = new IntParameter(ip.randomBases);
		towns = new IntParameter(ip.towns);
		armies = new IntParameter(ip.armies);
		militia = new IntParameter(ip.militia);
		seaDensity = new IntParameter(ip.seaDensity);
		hillDensity = new IntParameter(ip.hillDensity);
	}

	@Override
	public InitialisationParameters clone() {
		// try
		// {
		// InitialisationParameters res =
		// (InitialisationParameters)super.clone();
		//
		// res.generateRandomSeed = generateRandomSeed.clone();
		// res.randomSeed = randomSeed.clone();
		// res.bases = bases.clone();
		// res.randomBases = randomBases.clone();
		// res.towns = towns.clone();
		// res.armies = armies.clone();
		// res.militia = militia.clone();
		// res.seaDensity = seaDensity.clone();
		// res.hillDensity = hillDensity.clone();
		//
		// return res;
		// }
		// catch( CloneNotSupportedException e )
		// {
		// e.printStackTrace();
		// return null;
		// }
		return new InitialisationParameters(this);
	}

	public BoolParameter getGenerateRandomSeed() {
		return generateRandomSeed;
	}

	public LongParameter getRandomSeed() {
		return randomSeed;
	}

	public IntParameter getBases() {
		return bases;
	}

	public IntParameter getRandomBases() {
		return randomBases;
	}

	public IntParameter getTowns() {
		return towns;
	}

	public IntParameter getArmies() {
		return armies;
	}

	public IntParameter getMilitia() {
		return militia;
	}

	public IntParameter getSeaDensity() {
		return seaDensity;
	}

	public IntParameter getHillDensity() {
		return hillDensity;
	}
}
