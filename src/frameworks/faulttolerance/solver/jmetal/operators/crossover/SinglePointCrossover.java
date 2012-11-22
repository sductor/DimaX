//  SinglePointCrossover.java
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

import dima.introspectionbasedagents.kernel.PseudoRandom;

import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.BinarySolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.IntSolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;
import frameworks.faulttolerance.solver.jmetal.util.Configuration;
import frameworks.faulttolerance.solver.jmetal.util.JMException;

/**
 * This class allows to apply a Single Point crossover operator using two parent
 * solutions.
 */
public class SinglePointCrossover extends Crossover {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6780769144842574020L;

	/**
	 * Valid solution types to apply this operator
	 */
	private static List VALID_TYPES = Arrays.asList(BinarySolutionType.class,
			IntSolutionType.class) ;

	private final PseudoRandom seed;
	private Double crossoverProbability_ = null;

	/**
	 * Constructor
	 * Creates a new instance of the single point crossover operator
	 */
	public SinglePointCrossover(final HashMap<String, Object> parameters, PseudoRandom seed) {
		super(parameters) ;
		this.seed=seed;
		if (parameters.get("probability") != null) {
			this.crossoverProbability_ = (Double) parameters.get("probability") ;
		}
	} // SinglePointCrossover


	/**
	 * Constructor
	 * Creates a new instance of the single point crossover operator
	 */
	//public SinglePointCrossover(Properties properties) {
	//    this();
	//} // SinglePointCrossover

	/**
	 * Perform the crossover operation.
	 * @param probability Crossover probability
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @return An array containig the two offsprings
	 * @throws JMException
	 */
	public Solution[] doCrossover(final double probability,
			final Solution parent1,
			final Solution parent2) throws JMException {
		final Solution[] offSpring = new Solution[2];
		offSpring[0] = new Solution(parent1);
		offSpring[1] = new Solution(parent2);
		try {
			if (seed.randDouble() < probability) {
				if (parent1.getType().getClass() == BinarySolutionType.class) {
					//1. Compute the total number of bits
					int totalNumberOfBits = 0;
					for (int i = 0; i < parent1.getDecisionVariables().length; i++) {
						totalNumberOfBits +=
								((Binary) parent1.getDecisionVariables()[i]).getNumberOfBits();
					}

					//2. Calcule the point to make the crossover
					final int crossoverPoint = seed.randInt(0, totalNumberOfBits - 1);

					//3. Compute the variable that containt the crossoverPoint bit
					int variable = 0;
					int acountBits =
							((Binary) parent1.getDecisionVariables()[variable]).getNumberOfBits();

					while (acountBits < crossoverPoint + 1) {
						variable++;
						acountBits +=
								((Binary) parent1.getDecisionVariables()[variable]).getNumberOfBits();
					}

					//4. Compute the bit into the variable selected
					final int diff = acountBits - crossoverPoint;
					final int intoVariableCrossoverPoint =
							((Binary) parent1.getDecisionVariables()[variable]).getNumberOfBits() - diff;

					//5. Make the crossover into the the gene;
					Binary offSpring1, offSpring2;
					offSpring1 =
							(Binary) parent1.getDecisionVariables()[variable].deepCopy();
					offSpring2 =
							(Binary) parent2.getDecisionVariables()[variable].deepCopy();

					for (int i = intoVariableCrossoverPoint;
							i < offSpring1.getNumberOfBits();
							i++) {
						final boolean swap = offSpring1.bits_.get(i);
						offSpring1.bits_.set(i, offSpring2.bits_.get(i));
						offSpring2.bits_.set(i, swap);
					}

					offSpring[0].getDecisionVariables()[variable] = offSpring1;
					offSpring[1].getDecisionVariables()[variable] = offSpring2;

					//6. Apply the crossover to the other variables
					for (int i = 0; i < variable; i++) {
						offSpring[0].getDecisionVariables()[i] =
								parent2.getDecisionVariables()[i].deepCopy();

						offSpring[1].getDecisionVariables()[i] =
								parent1.getDecisionVariables()[i].deepCopy();

					}

					//7. Decode the results
					for (int i = 0; i < offSpring[0].getDecisionVariables().length; i++) {
						((Binary) offSpring[0].getDecisionVariables()[i]).decode();
						((Binary) offSpring[1].getDecisionVariables()[i]).decode();
					}
				} // Binary or BinaryReal
				else { // Integer representation
					final int crossoverPoint = seed.randInt(0, parent1.numberOfVariables() - 1);
					int valueX1;
					int valueX2;
					for (int i = crossoverPoint; i < parent1.numberOfVariables(); i++) {
						valueX1 = (int) parent1.getDecisionVariables()[i].getValue();
						valueX2 = (int) parent2.getDecisionVariables()[i].getValue();
						offSpring[0].getDecisionVariables()[i].setValue(valueX2);
						offSpring[1].getDecisionVariables()[i].setValue(valueX1);
					} // for
				} // Int representation
			}
		} catch (final ClassCastException e1) {
			Configuration.logger_.severe("SinglePointCrossover.doCrossover: Cannot perfom " +
					"SinglePointCrossover");
			final Class cls = java.lang.String.class;
			final String name = cls.getName();
			throw new JMException("Exception in " + name + ".doCrossover()");
		}
		return offSpring;
	} // doCrossover

	/**
	 * Executes the operation
	 * @param object An object containing an array of two solutions
	 * @return An object containing an array with the offSprings
	 * @throws JMException
	 */
	@Override
	public Object execute(final Object object) throws JMException {
		final Solution[] parents = (Solution[]) object;

		if (!(SinglePointCrossover.VALID_TYPES.contains(parents[0].getType().getClass())  &&
				SinglePointCrossover.VALID_TYPES.contains(parents[1].getType().getClass())) ) {

			Configuration.logger_.severe("SinglePointCrossover.execute: the solutions " +
					"are not of the right type. The type should be 'Binary' or 'Int', but " +
					parents[0].getType() + " and " +
					parents[1].getType() + " are obtained");

			final Class cls = java.lang.String.class;
			final String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if

		if (parents.length < 2) {
			Configuration.logger_.severe("SinglePointCrossover.execute: operator " +
					"needs two parents");
			final Class cls = java.lang.String.class;
			final String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}

		Solution[] offSpring;
		offSpring = this.doCrossover(this.crossoverProbability_,
				parents[0],
				parents[1]);

		//-> Update the offSpring solutions
		for (final Solution element : offSpring) {
			element.setCrowdingDistance(0.0);
			element.setRank(0);
		}
		return offSpring;
	} // execute
} // SinglePointCrossover
