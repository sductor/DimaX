//  ElitistES.java
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


import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import frameworks.faulttolerance.solver.RessAllocJMetalSolver.RessAllocJMetalProblem;
import frameworks.faulttolerance.solver.jmetal.core.Algorithm;
import frameworks.faulttolerance.solver.jmetal.core.Operator;
import frameworks.faulttolerance.solver.jmetal.core.Problem;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.core.SolutionSet;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.BinarySolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;
import frameworks.faulttolerance.solver.jmetal.util.Configuration;
import frameworks.faulttolerance.solver.jmetal.util.JMException;
import frameworks.faulttolerance.solver.jmetal.util.PseudoRandom;
/** 
 * Class implementing a (mu + lambda) ES. Lambda must be divisible by mu
 */
public class RessAllocElitistES extends Algorithm {
	private int     mu_     ;
	private int     lambda_ ;
	Solution[] intialParents;
	/**
	 * Constructor
	 * Create a new ElitistES instance.
	 * @param problem Problem to solve.
	 * @mu Mu
	 * @lambda Lambda
	 */
	public RessAllocElitistES(Problem problem, int mu, int lambda,Solution[] intialParents){
		super(problem) ;
		if (((double)lambda/(double)mu!=lambda/mu) || (lambda<2*mu*mu))
			throw new IllegalArgumentException("divis?"+((double)lambda/(double)mu!=lambda/mu)+" range?"+lambda+", "+2*mu*mu);
		mu_      = mu     ;
		lambda_  = lambda ;
		this.intialParents=intialParents;
		assert intialParents!=null;
	} // ElitistES

	/**
	 * Execute the ElitistES algorithm
	 * @throws JMException 
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int maxEvaluations ;
		int evaluations    ;

		SolutionSet population          ;
		SolutionSet offspringPopulation ;  

		Operator   mutationOperator ;
		Operator crossoverOperator;

		Comparator comparator       ;
		int timeLimits; 

		// Read the params
		maxEvaluations = ((Integer)this.getInputParameter("maxEvaluations")).intValue(); 
		// Read the params
		timeLimits = ((Integer)this.getInputParameter("timeLimits")).intValue(); 
		// Read the params
		comparator = ((Comparator)this.getInputParameter("comparator"));                

		// Initialize the variables
		population          = new SolutionSet(mu_) ;   
		offspringPopulation = new SolutionSet(mu_ + lambda_) ;

		evaluations  = 0;                

		// Read the operators
		mutationOperator  = this.operators_.get("mutation");
		crossoverOperator  = this.operators_.get("crossover");

		long startTime=new Date().getTime();

		// Create the parent population of mu solutions
		Solution newIndividual;
		assert intialParents!=null;
		for (int i = 0; i < intialParents.length; i++) {
			assert intialParents[i]!=null:Arrays.asList(intialParents);
			problem_.evaluate(intialParents[i]); 
			problem_.evaluateConstraints(intialParents[i]) ;         
			evaluations++;
			population.add(intialParents[i]);
		}
		for (int i = 0; i < mu_-intialParents.length; i++) {
			newIndividual = new Solution(problem_);                    
			problem_.evaluate(newIndividual); 
			problem_.evaluateConstraints(newIndividual) ;           
			evaluations++;
			population.add(newIndividual);
		} //for       

		// Main loop
		int offsprings ;
		int generation=0;
		offsprings = (lambda_ - 2*mu_* mu_)/ mu_ ; 
		while (evaluations < maxEvaluations && new Date().getTime()-startTime<timeLimits) {
//			System.out.println("evolving generation "+generation);
			generation++;
			// STEP 1. Generate the mu+lambda population
			//STEP 1.1 CrossOver
			for (int i = 0; i < mu_; i++) {
				for (int j = 0; j < mu_; j++) {
					Solution[] parents = new Solution[]{population.get(i),population.get(j)};
					Solution [] offspring = (Solution [])crossoverOperator.execute(parents);
					problem_.evaluate(offspring[0]);
					problem_.evaluateConstraints(offspring[0]);
					problem_.evaluate(offspring[1]);
					problem_.evaluateConstraints(offspring[1]);
					evaluations += 2;
					offspringPopulation.add(offspring[0]);
					offspringPopulation.add(offspring[1]);
				}
			}

			//STEP 1.2 Mutation			
			for (int i = 0; i < mu_; i++) {
				if (mutationOperator instanceof RessAllocBitFlipMutation)
					((RessAllocBitFlipMutation)mutationOperator).p.updateCurrentCharges(population.get(i));
				for (int j = 0; j < offsprings; j++) {
					Solution offspring = new Solution(population.get(i)) ;
					mutationOperator.execute(offspring);
					problem_.evaluate(offspring) ;
					problem_.evaluateConstraints(offspring) ;
					offspringPopulation.add(offspring) ;
					evaluations ++ ;
				} // for
			} // for

			// STEP 2. Add the mu individuals to the offspring population
			for (int i = 0 ; i < mu_; i++) {
				offspringPopulation.add(population.get(i)) ;
			} // for
			population.clear() ;

			// STEP 3. Sort the mu+lambda population
			offspringPopulation.sort(comparator) ;

			// STEP 4. Create the new mu population
			for (int i = 0; i < mu_; i++)
				population.add(offspringPopulation.get(i)) ;

			//      System.out.println("Evaluation: " + evaluations + " Fitness: " + 
			//          population.get(0).getObjective(0)) ; 

			// STEP 6. Delete the mu+lambda population
			offspringPopulation.clear() ;
		} // while

		return population ;
	} // execute
} // ElitistES