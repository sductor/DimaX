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
import java.util.Properties;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

import frameworks.faulttolerance.solver.jmetal.core.Operator;
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
	public JMetalBitFlipMutation(HashMap<String, Object> parameters) {
		super(parameters) ;
		if (parameters.get("probability") != null)
			mutationProbability_ = (Double) parameters.get("probability") ; 
		if (parameters.get("problem") != null)
			p = (RessourceAllocationProblem) parameters.get("problem") ;
		agentOrder=new ArrayList<Integer>(p.n);
		addedRep=new double[p.n];
		for (int i = 0; i<p.n;i++){
			agentOrder.add(i);
			addedRep[i]=0;
		}
		hostOrder=new ArrayList<Integer>(p.m);
		for (int j = 0; j<p.m;j++){
			hostOrder.add(j);
		}
		addedCharge=new double[p.m];
	} // BitFlipMutation


	/**
	 * Perform the mutation operation
	 * @param probability Mutation probability
	 * @param solution The solution to mutate
	 * @throws JMException
	 */
	public  void doMutation(double probability, Solution solution){
		if (p.isAgent)
			Collections.shuffle(hostOrder);

		for (int j = 0; j < p.m; j++){

			Collections.shuffle(agentOrder);
			addedCharge[j]=0.;
			int numberOfAgent=0;
			double alphaChargeJ= p.getHostAvailableCharge(j)*p.n/p.getAgentsChargeTotal();
			for (Integer i : agentOrder){
				if (p.getPos(i, j)!=-1){

					double agentSoftCrit = 0.5+Math.pow(p.getAgentCriticality(i)-p.getAgentMeanCriticality(),equityInd)/2.;
					boolean allocated=((Binary) solution.getDecisionVariables()[p.getPos(i, j)]).bits_.get(0);
					double optimistAgentcharge=Math.min(p.getAgentMemorycharge(i), p.getAgentProcessorCharge(i));

					double mutProb= getMutationProbability(
							probability,
							allocated, 
							i, j, 
							p.currentCharges[j]+addedCharge[j],
							agentSoftCrit,
							alphaChargeJ,
							numberOfAgent);
					if (PseudoRandom.randDouble() < mutProb) {
						((Binary) solution.getDecisionVariables()[p.getPos(i, j)]).bits_.flip(0);
						addedCharge[j]+=allocated?-optimistAgentcharge:+optimistAgentcharge;
						addedRep[i]+=allocated?-1:+1;
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
	public Object execute(Object object) throws JMException {
		Solution solution = (Solution) object;

		if (!VALID_TYPES.contains(solution.getType().getClass())) {
			Configuration.logger_.severe("BitFlipMutation.execute: the solution " +
					"is not of the right type. The type should be 'Binary', " +
					"'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if 

		double proba = mutationProbability_;//*PseudoRandom.randDouble();
		doMutation(proba, solution);
		return solution;
	} // execute

	private double getMutationProbability(double probability, boolean allocated, int agent, int host, 
			double hostCharge,double agentSoftCrit, double alphaChargeHost, int numberOfAgent){

		double hostChargePercent=Math.max(1,hostCharge/p.getHostMaxCharge(host));
		if (!allocated){
			double ammortissement = (alphaChargeHost/p.n);
			return probability*agentSoftCrit*(1-hostChargePercent)*ammortissement;
			//			}
		} else {
			double ammortissement;
			if (hostCharge>p.getHostMaxCharge(host)){
				agentSoftCrit=Math.pow(2*agentSoftCrit,3)/2.;
				ammortissement=1;
			}else {
				ammortissement=1-((double)(numberOfAgent)/(double)p.n);
			}
			return probability*hostChargePercent*(1-agentSoftCrit);			
		}
	}

} // BitFlipMutation
