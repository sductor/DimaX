package frameworks.faulttolerance.solver;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.FileHandler;

import java.util.logging.Logger;

import sun.security.action.GetLongAction;

import dima.introspectionbasedagents.modules.faults.Assert;

import frameworks.faulttolerance.solver.jmetal.core.Algorithm;
import frameworks.faulttolerance.solver.jmetal.core.Operator;
import frameworks.faulttolerance.solver.jmetal.core.Problem;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.BinarySolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.IntSolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Int;
import frameworks.faulttolerance.solver.jmetal.operators.crossover.CrossoverFactory;
import frameworks.faulttolerance.solver.jmetal.operators.crossover.HUXCrossover;
import frameworks.faulttolerance.solver.jmetal.util.Configuration;
import frameworks.faulttolerance.solver.jmetal.util.JMException;
import frameworks.faulttolerance.solver.jmetal.util.PseudoRandom;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class RessAllocJMetalSolver2 extends ResourceAllocationInterface<Solution> {

	private Algorithm algorithm;
	private boolean useBinary=true;

	private boolean solved=false;
	private Iterator<Solution> solutions;
	private int timeLimit=Integer.MAX_VALUE;

	int mu     = 100  ;
	int explorationFactorPerPoint = 1000;
	int maxGeneration=3;


	public RessAllocJMetalSolver2(SocialChoiceType socialChoice, boolean isAgent,
			boolean isHost) {
		super(socialChoice, isAgent, isHost);
	}

	public class RessAllocJMetalProblem extends Problem	{


		RessAllocJMetalProblem() throws UnsatisfiableException, ClassNotFoundException{ 
			numberOfVariables_   = RessAllocJMetalSolver2.this.getVariableNumber() ;
			numberOfObjectives_  = 1;                              ;
			numberOfConstraints_ = RessAllocJMetalSolver2.this.getConstraintNumber();
			problemName_         = "RessAllocJMetalProblem";
			if (useBinary){
				length_ = new int[numberOfVariables_];
				for (int i = 0; i < numberOfVariables_; i++){
					length_[i] = 2;
				}
				solutionType_ = new BinarySolutionType(this) ;
			} else {
				lowerLimit_  = new double[numberOfVariables_];
				upperLimit_ = new double[numberOfVariables_]; 
				for (int i = 0; i < numberOfVariables_; i++){
					lowerLimit_[i]=0.;
					upperLimit_[i]=1.;
				}
				solutionType_ = new IntSolutionType(this) ;
			}
		}

		RessourceAllocationProblem<Solution> getRessAllocProblem(){
			return RessAllocJMetalSolver2.this;
		}

		@Override
		public void evaluate(Solution solution) throws JMException {
			double [] fx = new double[1] ;
			//				fx[0] = isViable(solution)?getSocWelfare(solution):-1;
			fx[0] = getSocWelfare(solution);
			solution.setObjective(0, fx[0]);
		}

		@Override
		public void evaluateConstraints(Solution solution) throws JMException {

			double total = 0.0;
			int number = 0;

			if (isAgent){
				for (int i = 0; i < n; i++){
					if (!isViableForAgent(solution, i)){
						number++;
						total+=1000*getAgentCriticality(i);	//Double.POSITIVE_INFINITY;//	
					}			    		
				}
			}

			assert isHost;
			if (isHost){
				for (int host_j = 0; host_j < m; host_j++){
					double memOverhead = getHostMemoryCharge(solution, host_j)-getHostMaxMemory(host_j);
					double procOverhead = getHostProcessorCharge(solution, host_j)-getHostMaxProcessor(host_j);
					if (memOverhead>0){
						number++;
						total+=memOverhead*10;	//Double.POSITIVE_INFINITY;//					    	 
					}
					if (procOverhead>0){
						number++;
						total+=procOverhead*10;	//	Double.POSITIVE_INFINITY;//		    	 
					}
				}
			}

			//Pas de prise en compte du upgrading
			//			if (isLocal()){
			//				if (!isUpgrading(solution)){
			//					number++;
			//					total+=0;		//	Double.POSITIVE_INFINITY;//
			//				}
			//
			//			}

//			assert Assert.IIF(number==0, isViable(solution));

			solution.setOverallConstraintViolation(total);    
			solution.setNumberOfViolatedConstraint(number);       
		}	

		public Comparator<Solution> getComparator(){
			return new Comparator<Solution>() {

				@Override
				public int compare(Solution o1, Solution o2) {
					double o1Value=o1.getNumberOfViolatedConstraint()==0?o1.getObjective(0):-o1.getOverallConstraintViolation();
					double o2Value=o2.getNumberOfViolatedConstraint()==0?o2.getObjective(0):-o2.getOverallConstraintViolation();
					return Double.compare(o2Value, o1Value);
				}
			};
		}

	}


	@Override
	protected void initiateSolver() throws UnsatisfiableException {

		try {	
			//					QualityIndicator indicators ; // Object to get quality indicators
			//
			//		// Logger object and file to store log messages
			//		Object logger_ = Configuration.logger_ ;
			//		fileHandler_ = new FileHandler("NSGAII_main.log"); 
			//		logger_.addHandler(fileHandler_) ;

			setConstantHandling();	
			RessAllocJMetalProblem p =new RessAllocJMetalProblem();

			Operator crossover;
			Operator mutation;
			HashMap parameters;

			// Requirement: lambda must be divisible by mu & lambda >> 2*mu²


			int lambda = 2*mu*mu+explorationFactorPerPoint*mu ;
			//			System.out.println(mu+" parents, "+2*mu*mu+" corssovered childs, "+(lambda-2*mu*mu)
			//+" mutants childs, "+((lambda - 2*mu* mu)/ mu )+" mutant childs per parents");

			Solution[]initialParents;
			if (isLocal()){
				initialParents=new Solution[]{getUnallocatedSolution(p),getAllallocatedSolution(p)};//////////////////////////////////////////////////////////////////////////////////////////////////,intialSolution};
			} else {
				initialParents=new Solution[]{getUnallocatedSolution(p),getAllallocatedSolution(p)};				
			}
			algorithm = new RessAllocElitistES(p, mu, lambda, initialParents);
			//algorithm = new NonElitistES(problem, mu, lambda);

			/* Algorithm params*/
			algorithm.setInputParameter("maxEvaluations", maxGeneration*mu+2*mu*mu+lambda);
			algorithm.setInputParameter("timeLimits", timeLimit);
			algorithm.setInputParameter("comparator", p.getComparator());

			/* Mutation and Crossover for Real codification */
			parameters = new HashMap() ;
			parameters.put("probability", 1.) ;
			parameters.put("problem", RessAllocJMetalSolver2.this) ;
			mutation = new RessAllocBitFlipMutation(parameters);   
			// Crossover operator
			parameters = new HashMap() ;
			parameters.put("probability", 1.) ;
			crossover = new HUXCrossover(parameters);

			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Solution solveProb(boolean opt) throws UnsatisfiableException {
		try{
			if (!opt){
				if (!solved){
					solutions = algorithm.execute().iterator();
				}
				assert solutions.hasNext();
				return solutions.next();
			} else {
				return algorithm.execute().best(((RessAllocJMetalProblem)algorithm.getProblem()).getComparator());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean hasNext() {
		if (!solved)
			return true;
		else if (!solutions.hasNext()){
			solved=false;
			return false;
		} else
			return true;
	}

	@Override
	public void setTimeLimit(int millisec) {
		timeLimit = millisec;

	}

	@Override
	protected double read(Solution var, int agent, int host) {
		assert var!=null;
		assert var.getDecisionVariables()[getPos(agent, host)]!=null;
		if (useBinary){
			return ((Binary)var.getDecisionVariables()[getPos(agent, host)]).getIth(0)?1.:0.;
		} else {
			try {
				return var.getDecisionVariables()[getPos(agent, host)].getValue();
			} catch (JMException e) {
				throw new RuntimeException(e);
			}
		}
	}



	@Override
	public Solution getInitialAllocAsSolution(double[] intialAlloc) {
		if (useBinary){
			assert m==1:m;
			Binary[] vars = new Binary[algorithm.getProblem().getNumberOfVariables()];
			for (int i = 0; i < n; i++){
				if (getPos(i,0)!=-1){
					vars[getPos(i,0)]= new Binary(2);
					vars[getPos(i,0)].setIth(0,intialAlloc[i]==1);
				}	
			}
			return new Solution(algorithm.getProblem(),vars);
		} else {
			assert m==1:m;
			Int[] vars = new Int[algorithm.getProblem().getNumberOfVariables()];
			for (int i = 0; i < n; i++){
				if (getPos(i,0)!=-1){
					vars[getPos(i,0)]= new Int((int)intialAlloc[i],0,1);
				}	
			}
			return new Solution(algorithm.getProblem(),vars);
		}
	}

	public Solution getUnallocatedSolution(Problem p){
		if (useBinary){
			Binary[] vars = new Binary[p.getNumberOfVariables()];
			for (int i = 0; i < n; i++){
				for (int j=0; j < m; j++){
					if (getPos(i,0)!=-1){
						vars[getPos(i,j)]= new Binary(2);
						vars[getPos(i,j)].setIth(0,false);
					}	
				}
			}
			return new Solution(p,vars);
		} else {
			Int[] vars = new Int[p.getNumberOfVariables()];
			for (int i = 0; i < n; i++){
				for (int j=0; j < m; j++){
					if (getPos(i,j)!=-1){
						vars[getPos(i,j)]= new Int(0,0,1);
					}	
				}	
			}
			return new Solution(p,vars);
		}
	}
	public Solution getAllallocatedSolution(Problem p){
		if (useBinary){
			Binary[] vars = new Binary[p.getNumberOfVariables()];
			for (int i = 0; i < n; i++){
				for (int j=0; j < m; j++){
					if (getPos(i,j)!=-1){
						vars[getPos(i,j)]= new Binary(2);
						vars[getPos(i,j)].setIth(0,true);
					}	
				}	
			}
			return new Solution(p,vars);
		} else {
			Int[] vars = new Int[p.getNumberOfVariables()];
			for (int i = 0; i < n; i++){
				for (int j=0; j < m; j++){
					if (getPos(i,j)!=-1){
						vars[getPos(i,j)]= new Int(1,0,1);
					}	
				}	
			}
			return new Solution(p,vars);
		}
	}

}










//
//if (o1.getNumberOfViolatedConstraint()==0&&o2.getNumberOfViolatedConstraint()==0){
//	int c = Double.compare(o2.getObjective(0),o1.getObjective(0));
//	assert isViable(o1)&&isViable(o2):RessAllocJMetalSolver.this.print(o1)+" ::: "+RessAllocJMetalSolver.this.print(o2)+
//	"\n"+o1.getOverallConstraintViolation()+" "+o2.getOverallConstraintViolation()+" "+o1.getNumberOfViolatedConstraint()+" "+o2.getNumberOfViolatedConstraint();
//	//					if (c==1){
//	//						System.out.println("safe "+JMetalSolver.this.print(o1)+" > "+JMetalSolver.this.print(o2));
//	//					} else 
//	//						System.out.println("safe "+JMetalSolver.this.print(o2)+" > "+JMetalSolver.this.print(o1));
//	return c;
//} else {
//	assert !isViable(o1)||!isViable(o2):RessAllocJMetalSolver.this.print(o1)+" ::: "+RessAllocJMetalSolver.this.print(o2);
//	int c =Double.compare(o2.getOverallConstraintViolation(),o1.getOverallConstraintViolation());
//	//					if (c==1){
//	//						System.out.println("not safe "+JMetalSolver.this.print(o1)+" > "+JMetalSolver.this.print(o2));
//	//					} else 
//	//						System.out.println("not safe "+JMetalSolver.this.print(o2)+" > "+JMetalSolver.this.print(o1));
//	return c;
//}