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

package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.BinarySolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.IntSolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;
import frameworks.faulttolerance.solver.jmetal.operators.mutation.Mutation;
import frameworks.faulttolerance.solver.jmetal.util.Configuration;
import frameworks.faulttolerance.solver.jmetal.util.JMException;
import frameworks.faulttolerance.solver.jmetal.util.PseudoRandom;

/**
 * This class implements a bit flip mutation operator.
 * NOTE: the operator is applied to binary or integer solutions, considering the
 * whole solution as a single variable.
 */
public class JMetalBitFlipMutation extends Mutation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -616654849500403866L;

	/**
	 * Valid solution types to apply this operator
	 */
	private static List VALID_TYPES = Arrays.asList(BinarySolutionType.class,
			IntSolutionType.class) ;

	private Double mutationProbability_ = null ;
	RessourceAllocationProblem p;
	ArrayList<Integer> agentOrder;
	ArrayList<Integer> hostOrder;
	double[] addedCharge;
	double[] addedRep;
	int equityInd;

	/**
	 * Constructor
	 * Creates a new instance of the Bit Flip mutation operator
	 */
	public JMetalBitFlipMutation(final HashMap<String, Object> parameters) {
		super(parameters) ;
		if (parameters.get("probability") != null) {
			this.mutationProbability_ = (Double) parameters.get("probability") ;
		}
		if (parameters.get("problem") != null) {
			this.p = (RessourceAllocationProblem) parameters.get("problem") ;
		}
		this.agentOrder=new ArrayList<Integer>(this.p.n);
		this.addedRep=new double[this.p.n];
		for (int i = 0; i<this.p.n;i++){
			this.agentOrder.add(i);
			this.addedRep[i]=0;
		}
		this.hostOrder=new ArrayList<Integer>(this.p.m);
		for (int j = 0; j<this.p.m;j++){
			this.hostOrder.add(j);
		}
		this.addedCharge=new double[this.p.m];
	} // BitFlipMutation


	/**
	 * Perform the mutation operation
	 * @param probability Mutation probability
	 * @param solution The solution to mutate
	 * @throws JMException
	 */
	public  void doMutation(final double probability, final Solution solution){
		if (this.p.isAgent) {
			Collections.shuffle(this.hostOrder);
		}

		for (int j = 0; j < this.p.m; j++){

			Collections.shuffle(this.agentOrder);
			this.addedCharge[j]=0.;
			int numberOfAgent=0;
			final double alphaChargeJ= this.p.getHostAvailableCharge(j)*this.p.n/this.p.getAgentsChargeTotal();
			for (final Integer i : this.agentOrder){
				if (this.p.getPos(i, j)!=-1){

					final double agentSoftCrit = 0.5+Math.pow(this.p.getAgentCriticality(i)-this.p.getAgentMeanCriticality(),this.equityInd)/2.;
					final boolean allocated=((Binary) solution.getDecisionVariables()[this.p.getPos(i, j)]).bits_.get(0);
					final double optimistAgentcharge=Math.min(this.p.getAgentMemorycharge(i), this.p.getAgentProcessorCharge(i));

					final double mutProb= this.getMutationProbability(
							probability,
							allocated,
							i, j,
							this.p.currentCharges[j]+this.addedCharge[j],
							agentSoftCrit,
							alphaChargeJ,
							numberOfAgent);
					if (PseudoRandom.randDouble() < mutProb) {
						((Binary) solution.getDecisionVariables()[this.p.getPos(i, j)]).bits_.flip(0);
						this.addedCharge[j]+=allocated?-optimistAgentcharge:+optimistAgentcharge;
						this.addedRep[i]+=allocated?-1:+1;
					}
				}
				numberOfAgent++;
			}
		}
	}

	/**
	 * Executes the operation
	 * @param object An object containing a solution to mutate
	 * @return An object containing the mutated solution
	 * @throws JMException
	 */
	@Override
	public Object execute(final Object object) throws JMException {
		final Solution solution = (Solution) object;

		if (!JMetalBitFlipMutation.VALID_TYPES.contains(solution.getType().getClass())) {
			Configuration.logger_.severe("BitFlipMutation.execute: the solution " +
					"is not of the right type. The type should be 'Binary', " +
					"'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

			final Class cls = java.lang.String.class;
			final String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if

		final double proba = this.mutationProbability_;//*PseudoRandom.randDouble();
		this.doMutation(proba, solution);
		return solution;
	} // execute

	private double getMutationProbability(final double probability, final boolean allocated, final int agent, final int host,
			final double hostCharge,double agentSoftCrit, final double alphaChargeHost, final int numberOfAgent){

		final double hostChargePercent=Math.max(1,hostCharge/this.p.getHostMaxCharge(host));
		if (!allocated){
			final double ammortissement = alphaChargeHost/this.p.n;
			return probability*agentSoftCrit*(1-hostChargePercent)*ammortissement;
			//			}
		} else {
			double ammortissement;
			if (hostCharge>this.p.getHostMaxCharge(host)){
				agentSoftCrit=Math.pow(2*agentSoftCrit,3)/2.;
				ammortissement=1;
			}else {
				ammortissement=1-(double)numberOfAgent/(double)this.p.n;
			}
			return probability*hostChargePercent*(1-agentSoftCrit);
		}
	}

} // BitFlipMutation
