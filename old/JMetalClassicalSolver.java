package frameworks.faulttolerance.solver;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.FileHandler;

import java.util.logging.Logger;

import dima.introspectionbasedagents.modules.faults.Assert;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.IntRealSolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.encodings.variable.Int;
import jmetal.metaheuristics.abyss.AbYSS;
import jmetal.metaheuristics.cellde.CellDE;
import jmetal.metaheuristics.densea.DENSEA;
import jmetal.metaheuristics.fastPGA.FastPGA;
import jmetal.metaheuristics.gde3.GDE3;
import jmetal.metaheuristics.ibea.IBEA;
import jmetal.metaheuristics.mocell.MOCell;
import jmetal.metaheuristics.mochc.MOCHC;
import jmetal.metaheuristics.moead.MOEAD;
import jmetal.metaheuristics.moead.pMOEAD;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.omopso.OMOPSO;
import jmetal.metaheuristics.paes.PAES;
import jmetal.metaheuristics.pesa2.PESA2;
import jmetal.metaheuristics.randomSearch.RandomSearch;
import jmetal.metaheuristics.singleObjective.differentialEvolution.DE;
import jmetal.metaheuristics.singleObjective.evolutionStrategy.ElitistES;
import jmetal.metaheuristics.singleObjective.evolutionStrategy.NonElitistES;
import jmetal.metaheuristics.singleObjective.geneticAlgorithm.gGA;
import jmetal.metaheuristics.singleObjective.geneticAlgorithm.ssGA;
import jmetal.metaheuristics.singleObjective.particleSwarmOptimization.PSO;
import jmetal.metaheuristics.smpso.SMPSO;
import jmetal.metaheuristics.smsemoa.SMSEMOA;
import jmetal.metaheuristics.spea2.SPEA2;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.localSearch.MutationLocalSearch;
import jmetal.operators.mutation.BitFlipMutation;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.mutation.NonUniformMutation;
import jmetal.operators.mutation.UniformMutation;
import jmetal.operators.selection.BestSolutionSelection;
import jmetal.operators.selection.BinaryTournament;
import jmetal.operators.selection.SelectionFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.comparators.FPGAFitnessComparator;
import jmetal.util.comparators.FitnessComparator;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class JMetalClassicalSolver extends ResourceAllocationInterface<Solution> {

	private Algorithm algorithm;

	int heuristic;

	private boolean useBinary;
	private int timeLimit;
	private boolean solved=false;
	private boolean singleObj;

	private Iterator<Solution> solutions;

	public JMetalClassicalSolver(SocialChoiceType socialChoice, boolean isAgent,
			boolean isHost, int heuristic) {
		super(socialChoice, isAgent, isHost);
		this.heuristic=heuristic;
	}

	public class RessAllocJMetalProblem extends Problem	{


		RessAllocJMetalProblem(boolean useBinary) throws UnsatisfiableException, ClassNotFoundException{  
			JMetalClassicalSolver.this.useBinary=useBinary;
			numberOfVariables_   = JMetalClassicalSolver.this.getVariableNumber() ;
			numberOfObjectives_  = singleObj?1:n;                              ;
			numberOfConstraints_ = JMetalClassicalSolver.this.getConstraintNumber();
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

		@Override
		public void evaluate(Solution solution) throws JMException {
			if (singleObj){
				double [] fx = new double[1] ;
				//				fx[0] = isViable(solution)?getSocWelfare(solution):-1;
				fx[0] = getSocWelfare(solution);
				solution.setObjective(0, fx[0]);
			} else {
				double [] fx = new double[n] ; 
				for (int agent_i = 0; agent_i < n; agent_i++){
					//					fx[agent_i] = isViable(solution)?getIndividualWelfare(solution, agent_i):-1;
					fx[agent_i] =getIndividualWelfare(solution, agent_i);
					solution.setObjective(agent_i, fx[agent_i]);
				}
			}
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

			assert Assert.IIF(number==0, isViable(solution));

			solution.setOverallConstraintViolation(-total);    
			solution.setNumberOfViolatedConstraint(number);       
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
			setHeuristic(heuristic);
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
				return algorithm.execute().best(getComparator());
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

	private Comparator<Solution> getComparator(){
		return new Comparator<Solution>() {

			@Override
			public int compare(Solution o1, Solution o2) {

				if (o1.getNumberOfViolatedConstraint()==0&&o2.getNumberOfViolatedConstraint()==0){
					int c = Double.compare(o2.getObjective(0),o1.getObjective(0));
					assert isViable(o1)&&isViable(o2):JMetalClassicalSolver.this.print(o1)+" ::: "+JMetalClassicalSolver.this.print(o2)+
					"\n"+o1.getOverallConstraintViolation()+" "+o2.getOverallConstraintViolation()+" "+o1.getNumberOfViolatedConstraint()+" "+o2.getNumberOfViolatedConstraint();
					//					if (c==1){
					//						System.out.println("safe "+JMetalSolver.this.print(o1)+" > "+JMetalSolver.this.print(o2));
					//					} else 
					//						System.out.println("safe "+JMetalSolver.this.print(o2)+" > "+JMetalSolver.this.print(o1));
					return c;
				} else {
					assert !isViable(o1)||!isViable(o2):JMetalClassicalSolver.this.print(o1)+" ::: "+JMetalClassicalSolver.this.print(o2);
					int c =Double.compare(o2.getOverallConstraintViolation(),o1.getOverallConstraintViolation());
					//					if (c==1){
					//						System.out.println("not safe "+JMetalSolver.this.print(o1)+" > "+JMetalSolver.this.print(o2));
					//					} else 
					//						System.out.println("not safe "+JMetalSolver.this.print(o2)+" > "+JMetalSolver.this.print(o1));
					return c;
				}
			}
		};
	}


	private void setHeuristic(int h) throws JMException, UnsatisfiableException, ClassNotFoundException{
		Operator crossover;
		Operator mutation;
		Operator selection;
		Operator localSearch;
		HashMap parameters;

		Problem problem = new RessAllocJMetalProblem(false);
		switch (h){

		case 1://"Elitist Perso" :	
			// Requirement: lambda must be divisible by mu
			int mu     = 100  ;
			int lambda = 10000 ;

			algorithm = new RessAllocElitistES(new RessAllocJMetalProblem(true), mu, lambda);
			//algorithm = new NonElitistES(problem, mu, lambda);

			/* Algorithm params*/
			algorithm.setInputParameter("maxEvaluations", 20000);
			algorithm.setInputParameter("timeLimits", Integer.MAX_VALUE);
			algorithm.setInputParameter("comparator", getComparator());

			/* Mutation and Crossover for Real codification */
			parameters = new HashMap() ;
			parameters.put("probability", 1.) ;
			parameters.put("problem", JMetalClassicalSolver.this) ;
			mutation = new RessAllocBitFlipMutation(parameters);   
			//			mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);                 

			// Crossover operator
			parameters = new HashMap() ;
			parameters.put("probability", 1.0) ;
			crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);

			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);

			break;
		case 2://"Elitist" :	
			// Requirement: lambda must be divisible by mu
			int mu2     = 100  ;
			int lambda2 = 10000 ;
			int bits2 = 1 ; // Length of bit string in the OneMax problem1

			algorithm = new ElitistES(new RessAllocJMetalProblem(true), mu2, lambda2);
			//algorithm = new NonElitistES(problem, mu, lambda);

			/* Algorithm params*/
			algorithm.setInputParameter("maxEvaluations", 20000);

			/* Mutation and Crossover for Real codification */
			parameters = new HashMap() ;
			parameters.put("probability", 1.0/bits2) ;
			mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);                    

			algorithm.addOperator("mutation",mutation);

			break;
		case 3://NOnlitist
			algorithm = new NonElitistES(problem, 1500, 500);
			algorithm.setInputParameter("maxEvaluations", 15) ;
			// Mutation and Crossover for Real codification 
			parameters = new HashMap() ;
			parameters.put("probability", 0.2) ;
			crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);
			//		crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);  
			parameters = new HashMap() ;
			parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
			mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);       
			// Selection Operator 
			//		parameters = null ;
			//		selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ; 
			//		parameters =  new HashMap() ;
			//		parameters.put("problem", myHeuristic.getProblem()) ;
			//		selection = SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", parameters) ;  
			parameters =  new HashMap() ;
			parameters.put("comparator", getComparator()) ;
			selection = new BestSolutionSelection(parameters) ;  
			//localSearchOperator
			parameters = new HashMap() ;
			parameters.put("problem", problem) ;
			parameters.put("mutation", mutation) ;
			parameters.put("improvementRounds", 20) ; 
			localSearch=new MutationLocalSearch(parameters);
			// Add the operators to the algorithm
			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);
			algorithm.addOperator("selection",selection);
			algorithm.addOperator("improvement",localSearch);
			break;
		case 4://"MOCHC"
			algorithm = new MOCHC(new RessAllocJMetalProblem(true));
			algorithm.setInputParameter("initialConvergenceCount",0.25);
			algorithm.setInputParameter("preservedPopulation",0.05);
			algorithm.setInputParameter("convergenceValue",3);
			algorithm.setInputParameter("populationSize",100);
			algorithm.setInputParameter("maxEvaluations",60000);
			Operator crossoverOperator      ;
			Operator mutationOperator       ;
			Operator parentsSelection       ;
			Operator newGenerationSelection ;
			// Crossover operator
			parameters = new HashMap() ;
			parameters.put("probability", 1.0) ;
			crossoverOperator = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);
			//parentsSelection = new RandomSelection();
			//newGenerationSelection = new RankingAndCrowdingSelection(problem);
			parameters = null ;
			parentsSelection = SelectionFactory.getSelectionOperator("RandomSelection", parameters) ; 
			parameters = new HashMap() ;
			parameters.put("problem", problem) ;
			newGenerationSelection = SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", parameters) ;  
			// Mutation operator
			parameters = new HashMap() ;
			parameters.put("probability", 0.35) ;
			mutationOperator = MutationFactory.getMutationOperator("BitFlipMutation", parameters);     
			algorithm.addOperator("crossover",crossoverOperator);
			algorithm.addOperator("cataclysmicMutation",mutationOperator);
			algorithm.addOperator("parentSelection",parentsSelection);
			algorithm.addOperator("newGenerationSelection",newGenerationSelection);	
			break; 
		case 5://FastPGA
			algorithm = new FastPGA(problem);
			algorithm.setInputParameter("maxPopSize",100);
			algorithm.setInputParameter("initialPopulationSize",100);
			algorithm.setInputParameter("maxEvaluations",25000);
			algorithm.setInputParameter("a",20.0);
			algorithm.setInputParameter("b",1.0);
			algorithm.setInputParameter("c",20.0);
			algorithm.setInputParameter("d",0.0);
			// Parameter "termination"
			// If the preferred stopping criterium is PPR based, termination must 
			// be set to 0; otherwise, if the algorithm is intended to iterate until 
			// a give number of evaluations is carried out, termination must be set to 
			// that number
			algorithm.setInputParameter("termination",1);
			// Mutation and Crossover for Real codification 
			parameters = new HashMap() ;
			parameters.put("probability", 0.9) ;
			parameters.put("distributionIndex", 20.0) ;
			crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   
			//crossover.setParameter("probability",0.9);                   
			//crossover.setParameter("distributionIndex",20.0);
			parameters = new HashMap() ;
			parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
			parameters.put("distributionIndex", 20.0) ;
			mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);         
			// Mutation and Crossover for Binary codification			    
			parameters = new HashMap() ; 
			parameters.put("comparator", new FPGAFitnessComparator()) ;
			selection = new BinaryTournament(parameters);			    
			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);
			algorithm.addOperator("selection",selection);
			break;
		case 6://IBEA
			algorithm = new IBEA(problem);

			// Algorithm parameters
			algorithm.setInputParameter("populationSize",100);
			algorithm.setInputParameter("archiveSize",100);
			algorithm.setInputParameter("maxEvaluations",25000);

			// Mutation and Crossover for Real codification 
			parameters = new HashMap() ;
			parameters.put("probability", 0.9) ;
			parameters.put("distributionIndex", 20.0) ;
			crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

			parameters = new HashMap() ;
			parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
			parameters.put("distributionIndex", 20.0) ;
			mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);         

			/* Selection Operator */
			parameters = new HashMap() ; 
			parameters.put("comparator", new FitnessComparator()) ;
			selection = new BinaryTournament(parameters);

			// Add the operators to the algorithm
			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);
			algorithm.addOperator("selection",selection);
			break;
		case 7://MOCELL
			algorithm = new MOCell(problem);

			// Algorithm parameters
			algorithm.setInputParameter("populationSize",100);
			algorithm.setInputParameter("archiveSize",100);
			algorithm.setInputParameter("maxEvaluations",25000);
			algorithm.setInputParameter("feedBack",20);

			// Mutation and Crossover for Real codification 
			parameters = new HashMap() ;
			parameters.put("probability", 0.9) ;
			parameters.put("distributionIndex", 20.0) ;
			crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

			parameters = new HashMap() ;
			parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
			parameters.put("distributionIndex", 20.0) ;
			mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

			// Selection Operator 
			parameters = null ;
			selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;         

			// Add the operators to the algorithm
			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);
			algorithm.addOperator("selection",selection);
			break;
		case 8 : //"Abyss"		
			algorithm = new AbYSS(problem) ;	    
			algorithm.setInputParameter("populationSize", 20);
			algorithm.setInputParameter("refSet1Size"   , 10);
			algorithm.setInputParameter("refSet2Size"   , 10);
			algorithm.setInputParameter("archiveSize"   , 100);
			algorithm.setInputParameter("maxEvaluations", 25000);		
			// Mutation and Crossover for Real codification 
			parameters = new HashMap() ;
			parameters.put("probability", 0.2) ;
			crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);
			//		crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);  
			parameters = new HashMap() ;
			parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
			mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);       
			// Selection Operator 
			//		parameters = null ;
			//		selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ; 
			//		parameters =  new HashMap() ;
			//		parameters.put("problem", myHeuristic.getProblem()) ;
			//		selection = SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", parameters) ;  
			parameters =  new HashMap() ;
			parameters.put("comparator", getComparator()) ;
			selection = new BestSolutionSelection(parameters) ;  
			//localSearchOperator
			parameters = new HashMap() ;
			parameters.put("problem", problem) ;
			parameters.put("mutation", mutation) ;
			parameters.put("improvementRounds", 20) ; 
			localSearch=new MutationLocalSearch(parameters);
			// Add the operators to the algorithm
			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);
			algorithm.addOperator("selection",selection);
			algorithm.addOperator("improvement",localSearch);
			break;
			//		case 9 : 
			//			algorithm = new PESA2(problem);
			//
			//			// Algorithm parameters 
			//			algorithm.setInputParameter("populationSize",10);
			//			algorithm.setInputParameter("archiveSize",100);
			//			algorithm.setInputParameter("bisections",5);
			//			algorithm.setInputParameter("maxEvaluations",25000);
			//
			//			// Mutation and Crossover for Real codification 
			//			parameters = new HashMap() ;
			//			parameters.put("probability", 0.9) ;
			//			parameters.put("distributionIndex", 20.0) ;
			//			crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   
			//
			//			parameters = new HashMap() ;
			//			parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
			//			parameters.put("distributionIndex", 20.0) ;
			//			mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);    
			//
			//			// Mutation and Crossover Binary codification
			//			/*
			//		    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover");                   
			//		    crossover.setParameter("probability",0.9);                   
			//		    mutation = MutationFactory.getMutationOperator("BitFlipMutation");                    
			//		    mutation.setParameter("probability",1.0/80);
			//			 */
			//
			//			// Add the operators to the algorithm
			//			algorithm.addOperator("crossover",crossover);
			//			algorithm.addOperator("mutation",mutation);
			//			break;
		case 10 : 
			algorithm = new RandomSearch(problem);

			// Algorithm parameters
			algorithm.setInputParameter("maxEvaluations",25000);
			break;
		case 11 : 
			algorithm = new gGA(problem) ; // Generational GA
			//algorithm = new ssGA(problem); // Steady-state GA
			//algorithm = new scGA(problem) ; // Synchronous cGA
			//algorithm = new acGA(problem) ;   // Asynchronous cGA

			/* Algorithm parameters*/
			algorithm.setInputParameter("populationSize",100);
			algorithm.setInputParameter("maxEvaluations", 1000000);
			// Mutation and Crossover for Real codification 
			parameters = new HashMap() ;
			parameters.put("probability", 0.9) ;
			parameters.put("distributionIndex", 20.0) ;
			crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

			parameters = new HashMap() ;
			parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
			parameters.put("distributionIndex", 20.0) ;
			mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

			/*
		    // Mutation and Crossover for Binary codification 
		    parameters = new HashMap() ;
		    parameters.put("probability", 0.9) ;
		    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);                   

		    parameters = new HashMap() ;
		    parameters.put("probability", 1.0/bits) ;
		    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);                    
			 */
			/* Selection Operator */
			parameters = null ;
			selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;                            

			/* Add the operators to the algorithm*/
			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);
			algorithm.addOperator("selection",selection);
			break;
		case 12 : 
			algorithm = new ssGA(problem);
			//algorithm = new gGA(problem) ;

			// Algorithm params
			algorithm.setInputParameter("populationSize",512);
			algorithm.setInputParameter("maxEvaluations",2000000);

			// Mutation and Crossover for Real codification
			parameters = new HashMap() ;
			parameters.put("probability", 0.95) ;
			crossover = CrossoverFactory.getCrossoverOperator("TwoPointsCrossover", parameters);
			//crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover");

			parameters = new HashMap() ;
			parameters.put("probability", 0.2) ;
			mutation = MutationFactory.getMutationOperator("SwapMutation", parameters);                    

			/* Selection Operator */
			parameters = null;
			selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;                            

			/* Add the operators to the algorithm*/
			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);
			algorithm.addOperator("selection",selection);
			break;
		case 13 : 
			algorithm = new SPEA2(problem);

			// Algorithm parameters
			algorithm.setInputParameter("populationSize",100);
			algorithm.setInputParameter("archiveSize",100);
			algorithm.setInputParameter("maxEvaluations",25000);

			// Mutation and Crossover for Real codification 
			parameters = new HashMap() ;
			parameters.put("probability", 0.9) ;
			parameters.put("distributionIndex", 20.0) ;
			crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

			parameters = new HashMap() ;
			parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
			parameters.put("distributionIndex", 20.0) ;
			mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

			// Selection operator 
			parameters = null ;
			selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;                           

			// Add the operators to the algorithm
			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);
			algorithm.addOperator("selection",selection);
			break;
		case 14 :
			algorithm = new DENSEA(problem);		    
			// Algorithm parameters
			algorithm.setInputParameter("populationSize",100);
			algorithm.setInputParameter("maxEvaluations",25000);		    
			// Mutation and Crossover Binary codification 
			parameters = new HashMap() ;
			parameters.put("probability", 0.9) ;
			crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);                   
			crossover.setParameter("probability",0.9);  
			parameters = new HashMap() ;
			parameters.put("probability", 1.0/149) ;
			mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);  
			// Selection Operator 
			parameters = null ;
			selection = new BinaryTournament(parameters);    
			// Add the operators to the algorithm
			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);
			algorithm.addOperator("selection",selection);
			break;
		default :
			throw new JMException("n'existe pas");
		}


	}

}
//case 1:------------------------------------------------
//	algorithm = new PAES(problem);
//
//	// Algorithm parameters
//	algorithm.setInputParameter("archiveSize",100);
//	algorithm.setInputParameter("biSections",5);
//	algorithm.setInputParameter("maxEvaluations",25000);
//
//	// Mutation (Real variables)
//	parameters = new HashMap() ;
//	parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
//	parameters.put("distributionIndex", 20.0) ;
//	mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
//
//	// Mutation (BinaryReal variables)
//	//mutation = MutationFactory.getMutationOperator("BitFlipMutation");                    
//	//mutation.setParameter("probability",0.1);
//
//	// Add the operators to the algorithm
//	algorithm.addOperator("mutation", mutation);
//	break;


//		case 7://DensEA -----------------------------------------------------------------------------
//			algorithm = new RandomSearch(problem);
//			// Algorithm parameters
//			algorithm.setInputParameter("maxEvaluations",0);
//			break;
////			algorithm = new DENSEA(problem);		    
////			// Algorithm parameters
////			algorithm.setInputParameter("populationSize",100);
////			algorithm.setInputParameter("maxEvaluations",25000);		    
////			// Mutation and Crossover Binary codification 
////			parameters = new HashMap() ;
////			parameters.put("probability", 0.9) ;
////			crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);                   
////			crossover.setParameter("probability",0.9);  
////			parameters = new HashMap() ;
////			parameters.put("probability", 1.0/149) ;
////			mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);  
////			// Selection Operator 
////			parameters = null ;
////			selection = new BinaryTournament(parameters);    
////			// Add the operators to the algorithm
////			algorithm.addOperator("crossover",crossover);
////			algorithm.addOperator("mutation",mutation);
////			algorithm.addOperator("selection",selection);
////			break;












//6-----------------------------------------NE SAPPLIQUES PAS????-------------------------------------


//		case 9://GDE3
//			algorithm = new RandomSearch(problem);
//			// Algorithm parameters
//			algorithm.setInputParameter("maxEvaluations",0);
//			break;
////			algorithm = new GDE3(problem);				    
////			// Algorithm parameters
////			algorithm.setInputParameter("populationSize",100);
////			algorithm.setInputParameter("maxIterations",250);				    
////			// Crossover operator 
////			parameters = new HashMap() ;
////			parameters.put("CR", 0.5) ;
////			parameters.put("F", 0.5) ;
////			crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);    
////			// Add the operators to the algorithm
////			parameters = null ;
////			selection = SelectionFactory.getSelectionOperator("DifferentialEvolutionSelection", parameters) ;
////			algorithm.addOperator("crossover",crossover);
////			algorithm.addOperator("selection",selection);
////			break;
//		case 10://CEllDe
//			algorithm = new RandomSearch(problem);
//			// Algorithm parameters
//			algorithm.setInputParameter("maxEvaluations",0);
//			break;
////			algorithm = new CellDE(problem);
////
////			// Algorithm parameters
////			algorithm.setInputParameter("populationSize",100);
////			algorithm.setInputParameter("archiveSize",100);
////			algorithm.setInputParameter("maxEvaluations",25000);
////			algorithm.setInputParameter("feedBack", 20);
////
////			// Crossover operator 
////			parameters = new HashMap() ;
////			parameters.put("CR", 0.5) ;
////			parameters.put("F", 0.5) ;
////			crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);                   
////
////			// Add the operators to the algorithm
////			parameters = null ;
////			selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ; 
////
////			algorithm.addOperator("crossover",crossover);
////			algorithm.addOperator("selection",selection);
////			break;
//case 4 ://PSO INUTILISABLE!!!
//	algorithm = new RandomSearch(problem);
//	// Algorithm parameters
//	algorithm.setInputParameter("maxEvaluations",0);
//	break;
////	useBinary=false;
////	algorithm = new PSO(new RessAllocJMetalProblem(false)) ;
////	algorithm.setInputParameter("swarmSize",50);
////	algorithm.setInputParameter("maxIterations",5000);
////	// Mutation and Crossover for Real codification 
////	parameters = new HashMap() ;
////	parameters.put("probability", 0.2) ;
////	crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);
////	//		crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);  
////	parameters = new HashMap() ;
////	parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
////	mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);       
////	// Selection Operator 
////	//		parameters = null ;
////	//		selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ; 
////	//		parameters =  new HashMap() ;
////	//		parameters.put("problem", myHeuristic.getProblem()) ;
////	//		selection = SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", parameters) ;  
////	parameters =  new HashMap() ;
////	parameters.put("comparator", getComparator()) ;
////	selection = new BestSolutionSelection(parameters) ;  
////	//localSearchOperator
////	parameters = new HashMap() ;
////	parameters.put("problem", problem) ;
////	parameters.put("mutation", mutation) ;
////	parameters.put("improvementRounds", 20) ; 
////	localSearch=new MutationLocalSearch(parameters);
////	// Add the operators to the algorithm
////	algorithm.addOperator("crossover",crossover);
////	algorithm.addOperator("mutation",mutation);
////	algorithm.addOperator("selection",selection);
////	algorithm.addOperator("improvement",localSearch);
////	break;
//case 5://"Asynchronous cGA? --> DE"
//	algorithm = new RandomSearch(problem);
//	// Algorithm parameters
//	algorithm.setInputParameter("maxEvaluations",0);
//	break;
////	algorithm = new DE(problem) ; 
////	algorithm.setInputParameter("populationSize",100);
////	algorithm.setInputParameter("maxEvaluations", 1000000);
////	// Mutation and Crossover for Real codification 
////	parameters = new HashMap() ;
////	parameters.put("probability", 0.2) ;
////	crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);
////	//		crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);  
////	parameters = new HashMap() ;
////	parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
////	mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);       
////	// Selection Operator 
////	//		parameters = null ;
////	//		selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ; 
////	//		parameters =  new HashMap() ;
////	//		parameters.put("problem", myHeuristic.getProblem()) ;
////	//		selection = SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", parameters) ;  
////	parameters =  new HashMap() ;
////	parameters.put("comparator", getComparator()) ;
////	selection = new BestSolutionSelection(parameters) ;  
////	//localSearchOperator
////	parameters = new HashMap() ;
////	parameters.put("problem", problem) ;
////	parameters.put("mutation", mutation) ;
////	parameters.put("improvementRounds", 20) ; 
////	localSearch=new MutationLocalSearch(parameters);
////	// Add the operators to the algorithm
////	algorithm.addOperator("crossover",crossover);
////	algorithm.addOperator("mutation",mutation);
////	algorithm.addOperator("selection",selection);
////	algorithm.addOperator("improvement",localSearch);
////	break;
//case 22 : 
//	algorithm = new RandomSearch(problem);
//	// Algorithm parameters
//	algorithm.setInputParameter("maxEvaluations",0);
//	break;
////	algorithm = new PSO(problem) ;
////
////	// Algorithm parameters
////	algorithm.setInputParameter("swarmSize",50);
////	algorithm.setInputParameter("maxIterations",5000);
////
////	parameters = new HashMap() ;
////	parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
////	parameters.put("distributionIndes", 20.0) ;
////	mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
////
////	algorithm.addOperator("mutation", mutation);
////	break;
//case 23 : 
//	algorithm = new RandomSearch(problem);
//	// Algorithm parameters
//	algorithm.setInputParameter("maxEvaluations",0);
//	break;
////	algorithm = new SMPSO(problem) ;
////
////	// Algorithm parameters
////	algorithm.setInputParameter("swarmSize",100);
////	algorithm.setInputParameter("archiveSize",100);
////	algorithm.setInputParameter("maxIterations",250);
////
////	parameters = new HashMap() ;
////	parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
////	parameters.put("distributionIndex", 20.0) ;
////	mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
////
////	algorithm.addOperator("mutation", mutation); 
////	break;
//case 24 : 
//	algorithm = new RandomSearch(problem);
//	// Algorithm parameters
//	algorithm.setInputParameter("maxEvaluations",0);
//	break;//--------------------------------------------------------------------------------------------------------------------------------------------------
////	algorithm = new SMSEMOA(problem);
////
////	// Algorithm parameters
////	algorithm.setInputParameter("populationSize", 100);
////	algorithm.setInputParameter("maxEvaluations", 25000);
////	algorithm.setInputParameter("offset", 100.0);
////
////	// Mutation and Crossover for Real codification 
////	parameters = new HashMap() ;
////	parameters.put("probability", 0.9) ;
////	parameters.put("distributionIndex", 20.0) ;
////	crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   
////
////	parameters = new HashMap() ;
////	parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
////	parameters.put("distributionIndex", 20.0) ;
////	mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
////
////	// Selection Operator
////	parameters = null ;
////	selection = SelectionFactory.getSelectionOperator("RandomSelection", parameters);
////	// also possible
////	//selection = SelectionFactory.getSelectionOperator("BinaryTournament2");
////
////	// Add the operators to the algorithm
////	algorithm.addOperator("crossover", crossover);
////	algorithm.addOperator("mutation", mutation);
////	algorithm.addOperator("selection", selection);
////	break;
//case 13://MOEAD
//	algorithm = new RandomSearch(problem);
//	// Algorithm parameters
//	algorithm.setInputParameter("maxEvaluations",0);
//	break;
////	algorithm = new MOEAD(problem);
////	//algorithm = new MOEAD_DRA(problem);
////
////	// Algorithm parameters
////	algorithm.setInputParameter("populationSize",300);
////	algorithm.setInputParameter("maxEvaluations",150000);
////
////	// Directory with the files containing the weight vectors used in 
////	// Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
////	// on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
////	// of CS & EE, University of Essex, 02/2009.
////	// http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar
////	algorithm.setInputParameter("dataDirectory",
////			"/Users/antonio/Softw/pruebas/data/MOEAD_parameters/Weight");
////
////	// Crossover operator 
////	parameters = new HashMap() ;
////	parameters.put("CR", 1.0) ;
////	parameters.put("F", 0.5) ;
////	crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);                   
////
////	// Mutation operator
////	parameters = new HashMap() ;
////	parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
////	parameters.put("distributionIndex", 20.0) ;
////	mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
////
////	algorithm.addOperator("crossover",crossover);
////	algorithm.addOperator("mutation",mutation);
////	break;
//case 14://pMOEAD
//	algorithm = new RandomSearch(problem);
//	// Algorithm parameters
//	algorithm.setInputParameter("maxEvaluations",0);
//	break;
////	algorithm = new pMOEAD(problem);
////
////	// Algorithm parameters
////	int numberOfThreads = 4 ;
////	algorithm.setInputParameter("populationSize",300);
////	algorithm.setInputParameter("maxEvaluations",150000);
////	algorithm.setInputParameter("numberOfThreads", numberOfThreads);
////
////	// Directory with the files containing the weight vectors used in 
////	// Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
////	// on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
////	// of CS & EE, University of Essex, 02/2009.
////	// http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar
////	algorithm.setInputParameter("dataDirectory",
////			"/Users/antonio/Softw/pruebas/data/MOEAD_parameters/Weight");
////
////	// Crossover operator 
////	parameters = new HashMap() ;
////	parameters.put("CR", 1.0) ;
////	parameters.put("F", 0.5) ;
////	crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);                   
////	crossover.setParameter("CR", 1.0);                   
////	crossover.setParameter("F", 0.5);
////
////	// Mutation operator
////	parameters = new HashMap() ;
////	parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
////	parameters.put("distributionIndex", 20.0) ;
////	mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);  
////
////	algorithm.addOperator("crossover",crossover);
////	algorithm.addOperator("mutation",mutation);
//case 15://NSGAII
//	algorithm = new RandomSearch(problem);
//	// Algorithm parameters
//	algorithm.setInputParameter("maxEvaluations",0);
//	break;
////	algorithm = new NSGAII(problem);
////	//algorithm = new ssNSGAII(problem);
////
////	// Algorithm parameters
////	algorithm.setInputParameter("populationSize",100);
////	algorithm.setInputParameter("maxEvaluations",25000);
////
////	// Mutation and Crossover for Real codification 
////	parameters = new HashMap() ;
////	parameters.put("probability", 0.9) ;
////	parameters.put("distributionIndex", 20.0) ;
////	crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   
////
////	parameters = new HashMap() ;
////	parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
////	parameters.put("distributionIndex", 20.0) ;
////	mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
////
////	// Selection Operator 
////	parameters = null ;
////	selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;                           
////
////	// Add the operators to the algorithm
////	algorithm.addOperator("crossover",crossover);
////	algorithm.addOperator("mutation",mutation);
////	algorithm.addOperator("selection",selection);
//case 16 ://OMOPSO
//	algorithm = new RandomSearch(problem);
//	// Algorithm parameters
//	algorithm.setInputParameter("maxEvaluations",0);
//	break;
////	algorithm = new OMOPSO(problem) ;
////
////	Integer maxIterations = 250 ;
////	Double perturbationIndex = 0.5 ;
////	Double mutationProbability = 1.0/problem.getNumberOfVariables() ;
////
////	// Algorithm parameters
////	algorithm.setInputParameter("swarmSize",100);
////	algorithm.setInputParameter("archiveSize",100);
////	algorithm.setInputParameter("maxIterations",maxIterations);
////
////	parameters = new HashMap() ;
////	parameters.put("probability", mutationProbability) ;
////	parameters.put("perturbation", perturbationIndex) ;
////	Mutation uniformMutation = new UniformMutation(parameters);
////
////	parameters = new HashMap() ;
////	parameters.put("probability", mutationProbability) ;
////	parameters.put("perturbation", perturbationIndex) ;
////	parameters.put("maxIterations", maxIterations) ;
////	Mutation nonUniformMutation = new NonUniformMutation(parameters);
////
////	// Add the operators to the algorithm
////	algorithm.addOperator("uniformMutation",uniformMutation);
////	algorithm.addOperator("nonUniformMutation",nonUniformMutation);
////	break;