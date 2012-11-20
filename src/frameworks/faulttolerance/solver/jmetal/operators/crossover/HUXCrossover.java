//  HUXCrossover.java
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

package frameworks.faulttolerance.solver.jmetal.operators.crossover;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.BinarySolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;
import frameworks.faulttolerance.solver.jmetal.util.Configuration;
import frameworks.faulttolerance.solver.jmetal.util.JMException;
import frameworks.faulttolerance.solver.jmetal.util.PseudoRandom;

/**
 * This class allows to apply a HUX crossover operator using two parent
 * solutions.
 * NOTE: the operator is applied to the first variable of the solutions, and
 * the type of the solutions must be Binary or BinaryReal
 */
public class HUXCrossover extends Crossover{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3815886405200251271L;

	/**
	 * Valid solution types to apply this operator
	 */
	private static List VALID_TYPES = Arrays.asList(BinarySolutionType.class) ;

	private Double probability_ = null ;
	/**
	 * Constructor
	 * Create a new instance of the HUX crossover operator.
	 */
	public HUXCrossover(final HashMap<String, Object> parameters) {
		super(parameters) ;

		if (parameters.get("probability") != null) {
			this.probability_ = (Double) parameters.get("probability") ;
		}
	} // HUXCrossover


	/**
	 * Constructor
	 * Create a new intance of the HUX crossover operator.
	 */
	//public HUXCrossover(Properties properties) {
	// this();
	//} // HUXCrossover



	/**
	 * Perform the crossover operation
	 * @param probability Crossover probability
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @return An array containing the two offsprings
	 * @throws JMException
	 */
	public Solution[] doCrossover(final double   probability,
			final Solution parent1,
			final Solution parent2) throws JMException {
		final Solution [] offSpring = new Solution[2];
		offSpring[0] = new Solution(parent1);
		offSpring[1] = new Solution(parent2);
		try {
			if (PseudoRandom.randDouble() < probability) {
				for (int var = 0; var < parent1.getDecisionVariables().length; var++) {
					final Binary p1 = (Binary)parent1.getDecisionVariables()[var];
					final Binary p2 = (Binary)parent2.getDecisionVariables()[var];

					for (int bit = 0; bit < p1.getNumberOfBits(); bit++) {
						if (p1.bits_.get(bit) != p2.bits_.get(bit)) {
							if (PseudoRandom.randDouble() < 0.5) {
								((Binary)offSpring[0].getDecisionVariables()[var])
								.bits_.set(bit,p2.bits_.get(bit));
								((Binary)offSpring[1].getDecisionVariables()[var])
								.bits_.set(bit,p1.bits_.get(bit));
							}
						}
					}
				}
				//7. Decode the results
				for (int i = 0; i < offSpring[0].getDecisionVariables().length; i++)
				{
					((Binary)offSpring[0].getDecisionVariables()[i]).decode();
					((Binary)offSpring[1].getDecisionVariables()[i]).decode();
				}
			}
		}catch (final ClassCastException e1) {

			Configuration.logger_.severe("HUXCrossover.doCrossover: Cannot perfom " +
					"SinglePointCrossover ") ;
			final Class cls = java.lang.String.class;
			final String name = cls.getName();
			throw new JMException("Exception in " + name + ".doCrossover()") ;
		}
		return offSpring;
	} // doCrossover


	/**
	 * Executes the operation
	 * @param object An object containing an array of two solutions
	 * @return An object containing the offSprings
	 */
	@Override
	public Object execute(final Object object) throws JMException {
		final Solution [] parents = (Solution [])object;

		if (parents.length < 2)
		{
			Configuration.logger_.severe("HUXCrossover.execute: operator needs two " +
					"parents");
			final Class cls = java.lang.String.class;
			final String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()") ;
		}

		if (!(HUXCrossover.VALID_TYPES.contains(parents[0].getType().getClass())  &&
				HUXCrossover.VALID_TYPES.contains(parents[1].getType().getClass())) ) {

			Configuration.logger_.severe("HUXCrossover.execute: the solutions " +
					"are not of the right type. The type should be 'Binary' of " +
					"'BinaryReal', but " +
					parents[0].getType() + " and " +
					parents[1].getType() + " are obtained");

			final Class cls = java.lang.String.class;
			final String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()") ;

		} // if

		final Solution [] offSpring = this.doCrossover(this.probability_,
				parents[0],
				parents[1]);

		for (final Solution element : offSpring) {
			element.setCrowdingDistance(0.0);
			element.setRank(0);
		}

		return offSpring;
	} // execute
} // HUXCrossover
