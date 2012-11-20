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
import java.util.Date;
import java.util.Iterator;

import frameworks.faulttolerance.solver.jmetal.core.Algorithm;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.core.SolutionSet;
import frameworks.faulttolerance.solver.jmetal.core.SolutionSortedSet;
import frameworks.faulttolerance.solver.jmetal.util.JMException;
/**
 * Class implementing a (mu + lambda) ES. Lambda must be divisible by mu
 */
public class JMetalElitistES extends Algorithm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3585529454776560239L;

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
	public JMetalElitistES(final JMetalRessAllocProblem problem) {
		super(problem);
		this.problem=problem;
	}

	//
	// Accessors
	//

	@Override
	public JMetalRessAllocProblem getProblem(){
		return (JMetalRessAllocProblem) this.problem_;
	}

	public void setParallelParents(final SolutionSortedSet parallelParents) {
		this.parallelParents = parallelParents;
	}

	private boolean isParallel(){
		return this.parallelParents!=null;
	}

	//
	// Methods
	//

	/**
	 * Execute the ElitistES algorithm
	 * @throws JMException
	 */
	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		//		System.out.println("execute");
		this.initialize();
		this.run();
		final Iterator<Solution> sols = this.populationCourante.iterator();
		while (sols.hasNext()){
			if (!this.getProblem().p.isViable(sols.next())){
				sols.remove();
			}
		}
		//				System.out.println("fin : "+populationCourante.best(getProblem().getComparator())+"  "+populationCourante);
		return this.populationCourante ;
	} // execute

	public void initialize() {
		//		System.out.println("initializing");
		try {

			// Initialize the variables
			this.populationCourante          = new SolutionSet(this.getProblem().mu) ;
			this.startTime=new Date().getTime();

			// Create the parent population of mu solutions
			Solution newIndividual;
			final Solution[] intialParents=new Solution[]{this.problem.getUnallocatedSolution(),this.problem.getAllallocatedSolution(),this.problem.p.initialSolution};
			assert intialParents!=null;
			for (int i = 0; i < intialParents.length; i++) {
				if (this.hasExpired()) {
					break;
				}
				assert intialParents[i]!=null:Arrays.asList(intialParents);
				if (!this.populationCourante.contains(intialParents[i])) {
					this.populationCourante.add(intialParents[i]);
				}
			}
			for (int i = this.populationCourante.size(); i < this.getProblem().mu; i++) {
				if (this.hasExpired()) {
					break;
				}
				newIndividual = new Solution(this.problem_);

				this.getProblem().evaluate(newIndividual);
				while (this.populationCourante.contains(newIndividual)){
					if (this.hasExpired()) {
						break;
					}
					newIndividual = new Solution(this.problem_);
					this.getProblem().evaluate(newIndividual);
				}
				if (this.hasExpired()) {
					break;
				}
				this.populationCourante.add(newIndividual);
			} //for
			if (!this.hasExpired()) {
				assert this.populationCourante.size()==this.getProblem().mu:this.populationCourante;
			}


			if (this.isParallel()){
				synchronized (this.parallelParents) {
					for (final Solution s : this.populationCourante) {
						this.parallelParents.addIfImproving(s);
					}
				}
			}

		} catch (final ClassNotFoundException e) {
			throw new  RuntimeException("impossible");
		} catch (final JMException e) {
			throw new  RuntimeException("impossible");
		}
	}
	boolean allowStagnStop=false;//on peut arreter sur stagn que si on a deja trouver une meilleure solution
	public void run()  {
		if (this.hasExpired()) {
			return;
		}
		//		System.out.println("runn:ing");
		int   nbGen=0;
		int  stagnationCounter=this.problem.getStagnationCounter();
		double bestFound= this.populationCourante.best(this.getProblem().getComparator()).getObjective(0);;
		final SolutionSortedSet populationGenere = new SolutionSortedSet(this.getProblem().mu,this.getProblem().getComparator()) ;
		try{
			while (nbGen < this.getProblem().getMaxGeneration() && !this.hasExpired()) {
				//				System.out.println("gen "+nbGen+" "+populationCourante.best(getProblem().getComparator()).getObjective(0)
				//						+" date : "+new Date().getTime()+" "+(new Date().getTime()-startTime)+" "+getProblem().timeLimit);
				//+" iital alloc "+problem.p.initialSolution);//+"  "+populationCourante);
				populationGenere.addAll(this.populationCourante);
				assert populationGenere.size()==this.getProblem().mu:populationGenere.size()+" "+this.populationCourante.size()+"\n "+populationGenere+"\n "+this.populationCourante;
				assert this.populationCourante.size()==this.getProblem().mu:this.populationCourante.size();

				//STEP 0 Aléatoire
				if (nbGen>0){
					if (!this.populationCourante.contains(this.getProblem().getAllallocatedSolution())){
						this.populationCourante.remove(this.populationCourante.worst(this.getProblem().getComparator()));
						this.populationCourante.add(this.getProblem().getAllallocatedSolution());
					}

					if (!this.populationCourante.contains(this.getProblem().getUnallocatedSolution())){
						this.populationCourante.remove(this.populationCourante.worst(this.getProblem().getComparator()));
						this.populationCourante.add(this.getProblem().getUnallocatedSolution());
					}

					for (int i = 2; i < this.getProblem().diversi; i++){
						if (this.hasExpired()) {
							break;
						}
						Solution newIndividual = new Solution(this.problem_);
						this.getProblem().evaluate(newIndividual);
						while (this.populationCourante.contains(newIndividual)){
							if (this.hasExpired()) {
								break;
							}
							newIndividual = new Solution(this.problem_);
							this.getProblem().evaluate(newIndividual);
						}
						this.populationCourante.remove(this.populationCourante.worst(this.getProblem().getComparator()));
						this.populationCourante.add(newIndividual);
					}
				}
				assert populationGenere.size()==this.getProblem().mu:populationGenere.size()+" "+this.populationCourante.size()+" "+populationGenere;
				assert this.populationCourante.size()==this.getProblem().mu:this.populationCourante;


				//STEP 1 CrossOver
				for (int k = 0; k < this.getProblem().nbCroisement; k++) {
					if (this.hasExpired()) {
						break;
					}
					for (int i = 0; i < this.getProblem().mu; i++) {
						if (this.hasExpired()) {
							break;
						}
						for (int j = i; j < this.getProblem().mu; j++) {
							if (this.hasExpired()) {
								break;
							}
							final Solution[] parents = new Solution[]{this.populationCourante.get(i),this.populationCourante.get(j)};
							final Solution [] offspring = (Solution [])this.getProblem().getCrossoverOperator().execute(parents);
							this.problem_.evaluate(offspring[0]);
							this.problem_.evaluateConstraints(offspring[0]);
							this.problem_.evaluate(offspring[1]);
							this.problem_.evaluateConstraints(offspring[1]);
							populationGenere.addIfImproving(offspring[0]);
							populationGenere.addIfImproving(offspring[1]);
							assert populationGenere.size()==this.getProblem().mu:populationGenere;
							assert this.populationCourante.size()==this.getProblem().mu:this.populationCourante;
						}
					}
				}

				//STEP 2 Mutation
				for (int i = 0; i < this.getProblem().mu; i++) {
					if (this.hasExpired()) {
						break;
					}
					this.getProblem().updateCurrentCharges(this.populationCourante.get(i));
					if (this.getProblem().getMutationOperator().p.isAgent){
						this.getProblem().updateCurrentReplicasNumber(this.populationCourante.get(i));
					}
					for (int j = 0; j < this.getProblem().nbMutation; j++) {
						if (this.hasExpired()) {
							break;
						}
						final Solution offspring = new Solution(this.populationCourante.get(i)) ;
						this.getProblem().getMutationOperator().execute(offspring);
						this.problem_.evaluate(offspring) ;
						this.problem_.evaluateConstraints(offspring) ;
						populationGenere.addIfImproving(offspring) ;
						assert populationGenere.size()==this.getProblem().mu:populationGenere;
						assert this.populationCourante.size()==this.getProblem().mu:this.populationCourante;
					} // for
				} // for


				//STEP 3 MISE A JOUR
				if (this.isParallel()){
					synchronized (this.parallelParents) {
						for (final Solution s : populationGenere) {
							this.parallelParents.addIfImproving(s);
						}
						this.populationCourante.clear();
						this.populationCourante.addAll(this.parallelParents);
					}
				} else {
					this.populationCourante.clear();
					this.populationCourante.addAll(populationGenere);
				}

				assert populationGenere.size()==this.getProblem().mu:populationGenere.size()+" "+this.populationCourante.size()+" "+populationGenere;
				assert this.populationCourante.size()==this.getProblem().mu:this.populationCourante;

				populationGenere.clear();
				if (this.populationCourante.best(this.getProblem().getComparator()).getObjective(0)>0 &&
						this.populationCourante.best(this.getProblem().getComparator()).getObjective(0)>bestFound) {
					this.allowStagnStop=true;
				}
				if (this.allowStagnStop && bestFound==this.populationCourante.best(this.getProblem().getComparator()).getObjective(0)){
					stagnationCounter--;
					if (stagnationCounter==0){
						break;
					}
				} else {
					bestFound=this.populationCourante.best(this.getProblem().getComparator()).getObjective(0);
					stagnationCounter=this.getProblem().getStagnationCounter();
				}

				nbGen++;
			} // while
			//			System.out.println("end or break");
		} catch (final ClassNotFoundException e) {
			throw new  RuntimeException("impossible");
		} catch (final JMException e) {
			throw new  RuntimeException("impossible");
		}
	}

	private boolean hasExpired(){
		return new Date().getTime()-this.startTime>this.getProblem().getTimeLimit();
	}
} // ElitistES