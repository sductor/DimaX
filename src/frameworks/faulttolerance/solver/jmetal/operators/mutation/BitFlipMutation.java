//  BitFlipMutation.java
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

package frameworks.faulttolerance.solver.jmetal.operators.mutation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.BinarySolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.IntSolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;
import frameworks.faulttolerance.solver.jmetal.util.Configuration;
import frameworks.faulttolerance.solver.jmetal.util.JMException;
import frameworks.faulttolerance.solver.jmetal.util.PseudoRandom;

/**
 * This class implements a bit flip mutation operator.
 * NOTE: the operator is applied to binary or integer solutions, considering the
 * whole solution as a single variable.
 */
public class BitFlipMutation extends Mutation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5277788581065071404L;

	/**
	 * Valid solution types to apply this operator
	 */
	private static List VALID_TYPES = Arrays.asList(BinarySolutionType.class,
			IntSolutionType.class) ;

	private Double mutationProbability_ = null ;

	/**
	 * Constructor
	 * Creates a new instance of the Bit Flip mutation operator
	 */
	public BitFlipMutation(final HashMap<String, Object> parameters) {
		super(parameters) ;
		if (parameters.get("probability") != null) {
			this.mutationProbability_ = (Double) parameters.get("probability") ;
		}
	} // BitFlipMutation

	/**
	 * Perform the mutation operation
	 * @param probability Mutation probability
	 * @param solution The solution to mutate
	 * @throws JMException
	 */
	public void doMutation(final double probability, final Solution solution) throws JMException {
		try {
			if (solution.getType().getClass() == BinarySolutionType.class ) {
				for (int i = 0; i < solution.getDecisionVariables().length; i++) {
					for (int j = 0; j < ((Binary) solution.getDecisionVariables()[i]).getNumberOfBits(); j++) {
						if (PseudoRandom.randDouble() < probability) {
							((Binary) solution.getDecisionVariables()[i]).bits_.flip(j);
						}
					}
				}

				for (int i = 0; i < solution.getDecisionVariables().length; i++) {
					((Binary) solution.getDecisionVariables()[i]).decode();
				}
			} // if
			else { // Integer representation
				for (int i = 0; i < solution.getDecisionVariables().length; i++)
				{
					if (PseudoRandom.randDouble() < probability) {
						final int value = PseudoRandom.randInt(
								(int)solution.getDecisionVariables()[i].getLowerBound(),
								(int)solution.getDecisionVariables()[i].getUpperBound());
						solution.getDecisionVariables()[i].setValue(value);
					} // if
				}
			} // else
		} catch (final ClassCastException e1) {
			Configuration.logger_.severe("BitFlipMutation.doMutation: " +
					"ClassCastException error" + e1.getMessage());
			final Class cls = java.lang.String.class;
			final String name = cls.getName();
			throw new JMException("Exception in " + name + ".doMutation()");
		}
	} // doMutation

	/**
	 * Executes the operation
	 * @param object An object containing a solution to mutate
	 * @return An object containing the mutated solution
	 * @throws JMException
	 */
	@Override
	public Object execute(final Object object) throws JMException {
		final Solution solution = (Solution) object;

		if (!BitFlipMutation.VALID_TYPES.contains(solution.getType().getClass())) {
			Configuration.logger_.severe("BitFlipMutation.execute: the solution " +
					"is not of the right type. The type should be 'Binary', " +
					"'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

			final Class cls = java.lang.String.class;
			final String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if

		this.doMutation(this.mutationProbability_, solution);
		return solution;
	} // execute
} // BitFlipMutation
