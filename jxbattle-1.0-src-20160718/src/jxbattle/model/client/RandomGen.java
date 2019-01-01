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

package jxbattle.model.client;

import org.generic.MTRandom;

public class RandomGen
{
    private MTRandom randomGenerator;

    //    private MyRandom randomGenerator;
    //    private Random randomGenerator;

    public RandomGen( long randomSeed )
    {
        randomGenerator = new MTRandom( randomSeed );
        //        randomGenerator = new MyRandom( randomSeed );
        //        randomGenerator = new Random( randomSeed );
    }

    /**
     * pick floating point between -1 and 1
     */
    private float posnegRandomFloat()
    {
        return randomGenerator.nextFloat() * 2.0f - 1.0f;
    }

    /**
     * pick floating point between 0 and 1
     */
    float posRandomFloat()
    {
        return randomGenerator.nextFloat();
    }

    /**
     * pick floating point between 0 and +amplitude (excluded)
     */
    float posRandomFloat( float amplitude )
    {
        return randomGenerator.nextFloat() * amplitude;
    }

    /**
     * pick int between -amplitude (included) and +amplitude (excluded)
     */
    float posnegRandomFloat( int amplitude )
    {
        return posnegRandomFloat() * amplitude;
    }

    /**
     * pick int between 0 and +amplitude (excluded)
     */
    public int posRandomInt( int amplitude )
    {
        return randomGenerator.nextInt( amplitude );
    }

    /**
     * pick int between -amplitude (included) and +amplitude (excluded)
     */
    int posnegRandomInt( int amplitude )
    {
        return randomGenerator.nextInt( amplitude * 2 ) - amplitude;
    }
}
