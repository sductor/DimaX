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
import java.util.Iterator;

import frameworks.faulttolerance.solver.jmetal.core.Algorithm;
import frameworks.faulttolerance.solver.jmetal.core.Operator;
import frameworks.faulttolerance.solver.jmetal.core.Problem;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.core.SolutionSet;
import frameworks.faulttolerance.solver.jmetal.core.SolutionSortedList;
import frameworks.faulttolerance.solver.jmetal.core.SolutionSortedSet;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.BinarySolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;
import frameworks.faulttolerance.solver.jmetal.util.Configuration;
import frameworks.faulttolerance.solver.jmetal.util.JMException;
import frameworks.faulttolerance.solver.jmetal.util.PseudoRandom;
/** 
 * Class implementing a (mu + lambda) ES. Lambda must be divisible by mu
 */
public class JMetalElitistES extends Algorithm {
	JMetalRessAllocProblem problem;

	long startTime;
	SolutionSet 	populationCourante;  
	SolutionSortedSet parallelParents=null;

	/**
	 * Constructor
	 * Create a new ElitistES instance.
	 * @param problem Problem to solve.
	 * @mu Mu
	 * @lambda Lambda
	 */
	public JMetalElitistES(JMetalRessAllocProblem problem) {
		super(problem);
		this.problem=problem;
	}

	//
	// Accessors
	//

	public JMetalRessAllocProblem getProblem(){
		return (JMetalRessAllocProblem) problem_;
	}

	public void setParallelParents(SolutionSortedSet parallelParents) {
		this.parallelParents = parallelParents;
	}

	private boolean isParallel(){
		return parallelParents!=null;
	}

	//
	// Methods
	//

	/**
	 * Execute the ElitistES algorithm
	 * @throws JMException 
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException { 
//		System.out.println("execute");
		initialize();
		run();
		Iterator<Solution> sols = populationCourante.iterator();
		while (sols.hasNext()){
			if (!getProblem().p.isViable(sols.next())){
				sols.remove();
			}
		}
//				System.out.println("fin : "+populationCourante.best(getProblem().getComparator())+"  "+populationCourante);
		return populationCourante ;
	} // execute

	public void initialize() {
//		System.out.println("initializing");
		try {

			// Initialize the variables
			populationCourante          = new SolutionSet(getProblem().mu) ;   
			startTime=new Date().getTime();

			// Create the parent population of mu solutions
			Solution newIndividual;
			Solution[] intialParents=new Solution[]{problem.getUnallocatedSolution(),problem.getAllallocatedSolution(),problem.p.initialSolution};
			assert intialParents!=null;
			for (int i = 0; i < intialParents.length; i++) {
				if (hasExpired()) break;
				assert intialParents[i]!=null:Arrays.asList(intialParents);
				if (!populationCourante.contains(intialParents[i]))
					populationCourante.add(intialParents[i]);
			}
			for (int i = populationCourante.size(); i < getProblem().mu; i++) {
				if (hasExpired()) break;
				newIndividual = new Solution(problem_);

				getProblem().evaluate(newIndividual);  
				while (populationCourante.contains(newIndividual)){
					if (hasExpired()) break;
					newIndividual = new Solution(problem_); 
					getProblem().evaluate(newIndividual); 
				} 
				if (hasExpired())break;
				populationCourante.add(newIndividual);  
			} //for    
			if (!hasExpired())assert populationCourante.size()==getProblem().mu:populationCourante;  


			if (isParallel()){
				synchronized (parallelParents) {
					for (Solution s : populationCourante) {
						parallelParents.addIfImproving(s);
					}
				}
			}

		} catch (ClassNotFoundException e) {
			throw new  RuntimeException("impossible");
		} catch (JMException e) {
			throw new  RuntimeException("impossible");
		}
	}
	boolean allowStagnStop=false;//on peut arreter sur stagn que si on a deja trouver une meilleure solution
	public void run()  {
		if (hasExpired())
			return;
//		System.out.println("runn:ing");
		int   nbGen=0;
		int  stagnationCounter=problem.getStagnationCounter();
		double bestFound= populationCourante.best(getProblem().getComparator()).getObjective(0);; 
		SolutionSortedSet populationGenere = new SolutionSortedSet(getProblem().mu,getProblem().getComparator()) ;
		try{
			while (nbGen < getProblem().getMaxGeneration() && !hasExpired()) {
//				System.out.println("gen "+nbGen+" "+populationCourante.best(getProblem().getComparator()).getObjective(0)
//						+" date : "+new Date().getTime()+" "+(new Date().getTime()-startTime)+" "+getProblem().timeLimit);
				//+" iital alloc "+problem.p.initialSolution);//+"  "+populationCourante);
				populationGenere.addAll(populationCourante);
				assert populationGenere.size()==getProblem().mu:populationGenere.size()+" "+populationCourante.size()+"\n "+populationGenere+"\n "+populationCourante;
				assert populationCourante.size()==getProblem().mu:populationCourante.size();

				//STEP 0 Aléatoire
				if (nbGen>0){
					if (!populationCourante.contains(getProblem().getAllallocatedSolution())){
						populationCourante.remove(populationCourante.worst(getProblem().getComparator()));
						populationCourante.add(getProblem().getAllallocatedSolution());
					}

					if (!populationCourante.contains(getProblem().getUnallocatedSolution())){
						populationCourante.remove(populationCourante.worst(getProblem().getComparator()));
						populationCourante.add(getProblem().getUnallocatedSolution());
					}

					for (int i = 2; i < getProblem().diversi; i++){
						if (hasExpired()) break;
						Solution newIndividual = new Solution(problem_);
						getProblem().evaluate(newIndividual);  
						while (populationCourante.contains(newIndividual)){
							if (hasExpired()) break;
							newIndividual = new Solution(problem_); 
							getProblem().evaluate(newIndividual); 
						} 
						populationCourante.remove(populationCourante.worst(getProblem().getComparator()));
						populationCourante.add(newIndividual);
					}
				}
				assert populationGenere.size()==getProblem().mu:populationGenere.size()+" "+populationCourante.size()+" "+populationGenere;
				assert populationCourante.size()==getProblem().mu:populationCourante;


				//STEP 1 CrossOver	
				for (int k = 0; k < getProblem().nbCroisement; k++) {
					if (hasExpired()) break;
					for (int i = 0; i < getProblem().mu; i++) {
						if (hasExpired()) break;
						for (int j = i; j < getProblem().mu; j++) {
							if (hasExpired()) break;
							Solution[] parents = new Solution[]{populationCourante.get(i),populationCourante.get(j)};
							Solution [] offspring = (Solution [])getProblem().getCrossoverOperator().execute(parents);
							problem_.evaluate(offspring[0]);
							problem_.evaluateConstraints(offspring[0]);
							problem_.evaluate(offspring[1]);
							problem_.evaluateConstraints(offspring[1]);
							populationGenere.addIfImproving(offspring[0]);
							populationGenere.addIfImproving(offspring[1]);
							assert populationGenere.size()==getProblem().mu:populationGenere;
							assert populationCourante.size()==getProblem().mu:populationCourante;
						}
					}
				}

				//STEP 2 Mutation			
				for (int i = 0; i < getProblem().mu; i++) {
					if (hasExpired()) break;
					getProblem().updateCurrentCharges(populationCourante.get(i));
					if (getProblem().getMutationOperator().p.isAgent){
						getProblem().updateCurrentReplicasNumber(populationCourante.get(i));
					}
					for (int j = 0; j < getProblem().nbMutation; j++) {
						if (hasExpired()) break;
						Solution offspring = new Solution(populationCourante.get(i)) ;
						getProblem().getMutationOperator().execute(offspring);
						problem_.evaluate(offspring) ;
						problem_.evaluateConstraints(offspring) ;
						populationGenere.addIfImproving(offspring) ;
						assert populationGenere.size()==getProblem().mu:populationGenere;
						assert populationCourante.size()==getProblem().mu:populationCourante;
					} // for
				} // for


				//STEP 3 MISE A JOUR
				if (isParallel()){
					synchronized (parallelParents) {
						for (Solution s : populationGenere) {
							parallelParents.addIfImproving(s);
						}
						populationCourante.clear();
						populationCourante.addAll(parallelParents);
					}
				} else {
					populationCourante.clear();
					populationCourante.addAll(populationGenere);			
				}

				assert populationGenere.size()==getProblem().mu:populationGenere.size()+" "+populationCourante.size()+" "+populationGenere;
				assert populationCourante.size()==getProblem().mu:populationCourante;

				populationGenere.clear();
				if (populationCourante.best(getProblem().getComparator()).getObjective(0)>0 &&
						populationCourante.best(getProblem().getComparator()).getObjective(0)>bestFound)
					allowStagnStop=true;
				if (allowStagnStop && bestFound==populationCourante.best(getProblem().getComparator()).getObjective(0)){
					stagnationCounter--;
					if (stagnationCounter==0){
						break;
					}
				} else {
					bestFound=populationCourante.best(getProblem().getComparator()).getObjective(0);
					stagnationCounter=getProblem().getStagnationCounter();
				}

				nbGen++;
			} // while
//			System.out.println("end or break");
		} catch (ClassNotFoundException e) {
			throw new  RuntimeException("impossible");
		} catch (JMException e) {
			throw new  RuntimeException("impossible");
		}
	}

	private boolean hasExpired(){
		return (new Date().getTime()-startTime)>getProblem().getTimeLimit();
	}
} // ElitistES