//  BinarySolutionType.java
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

package frameworks.faulttolerance.solver.jmetal.encodings.solutionType;

import frameworks.faulttolerance.solver.jmetal.core.Problem;
import frameworks.faulttolerance.solver.jmetal.core.SolutionType;
import frameworks.faulttolerance.solver.jmetal.core.Variable;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;

/**
 * Class representing the solution type of solutions composed of Binary
 * variables
 */
public class BinarySolutionType extends SolutionType {

	/**
	 * Constructor
	 * @param problem
	 * @throws ClassNotFoundException
	 */
	public BinarySolutionType(final Problem problem) throws ClassNotFoundException {
		super(problem) ;
	} // Constructor

	/**
	 * Creates the variables of the solution
	 * @param decisionVariables
	 */
	@Override
	public Variable[] createVariables() {
		final Variable[]  variables = new Variable[this.problem_.getNumberOfVariables()];

		for (int var = 0; var < this.problem_.getNumberOfVariables(); var++) {
			variables[var] = new Binary(this.problem_.getLength(var), problem_.getRandom());
		}

		return variables ;
	}

	@Override
	public boolean equals(final Object o){
		return o instanceof BinarySolutionType;
	}
}