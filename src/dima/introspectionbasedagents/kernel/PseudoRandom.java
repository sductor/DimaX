//  PseudoRandom.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package dima.introspectionbasedagents.kernel;

/**
 * Class representing a pseudo-random number generator
 */
public class PseudoRandom extends java.util.Random  {

	public Double seed=null;

	/**
	 * generator used to obtain the random values
	 */
	private static RandomGenerator random = null;

	/**
	 * other generator used to obtain the random values
	 */
//	private static java.util.Random randomJava = null;

	/**
	 * Constructor.
	 * Creates a new instance of PseudoRandom.
	 */
	public PseudoRandom(Double seed) {
		super(seed.longValue());
		if (PseudoRandom.random == null){
			//this.random = new java.util.Random((long)seed);
			PseudoRandom.random = new RandomGenerator(seed);
//			PseudoRandom.randomJava = new java.util.Random((long) seed.doubleValue());
		}
	} // PseudoRandom

	/**
	 * Returns a random int value using the Java random generator.
	 * @return A random int value.
	 */
	public int randInt() {
		if (PseudoRandom.random == null) {
			new PseudoRandom(seed);
		}
		return nextInt();
	} // randInt

	/**
	 * Returns a random double value using the PseudoRandom generator.
	 * Returns A random double value.
	 */
	public double randDouble() {
		if (PseudoRandom.random == null) {
			new PseudoRandom(seed);
		}
		return PseudoRandom.random.rndreal(0.0,1.0);
		//return randomJava.nextDouble();
	} // randDouble

	/**
	 * Returns a random int value between a minimum bound and maximum bound using
	 * the PseudoRandom generator.
	 * @param minBound The minimum bound.
	 * @param maxBound The maximum bound.
	 * Return A pseudo random int value between minBound and maxBound.
	 */
	public int randInt(final int minBound, final int maxBound) {
		if (PseudoRandom.random == null) {
			new PseudoRandom(seed);
		}
		return PseudoRandom.random.rnd(minBound,maxBound);
		//return minBound + randomJava.nextInt(maxBound-minBound+1);
	} // randInt

	/** Returns a random double value between a minimum bound and a maximum bound
	 * using the PseudoRandom generator.
	 * @param minBound The minimum bound.
	 * @param maxBound The maximum bound.
	 * @return A pseudo random double value between minBound and maxBound
	 */
	public double randDouble(final double minBound, final double maxBound) {
		if (PseudoRandom.random == null) {
			new PseudoRandom(seed);
		}
		return PseudoRandom.random.rndreal(minBound,maxBound);
		//return minBound + (maxBound - minBound)*randomJava.nextDouble();
	} // randDouble
} // PseudoRandom
