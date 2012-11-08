package frameworks.faulttolerance.solver;

import java.util.Comparator;
import java.util.HashMap;

import frameworks.faulttolerance.solver.jmetal.core.Operator;
import frameworks.faulttolerance.solver.jmetal.core.Problem;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.BinarySolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.IntSolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Int;
import frameworks.faulttolerance.solver.jmetal.operators.crossover.HUXCrossover;
import frameworks.faulttolerance.solver.jmetal.util.JMException;

public class JMetalRessAllocProblem extends Problem	{
	Solution allAllocated;
	Solution unAllocated;
	Solution initial;
	private boolean useBinary=true;
	RessourceAllocationProblem<Solution> p;



	int timeLimit=Integer.MAX_VALUE;
	int maxGeneration=Integer.MAX_VALUE;

	public int mu     = 30 ;

	int diversi=5;
	int nbMutation = 10;
	int nbCroisement = 10;
	int stagnationCounter=5;

	public JMetalRessAllocProblem(RessourceAllocationProblem<Solution> p){ 
		try {
			this.p=p;
			numberOfVariables_   = p.getVariableNumber() ;
			numberOfObjectives_  = 1;                              ;
			numberOfConstraints_ = p.getConstraintNumber();
			problemName_         = "RessAllocJMetalProblem";
			if (useBinary){
				length_ = new int[numberOfVariables_];
				for (int i = 0; i < numberOfVariables_; i++){
					length_[i] = 1;
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


			//setting allalcated

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("impossible");
		}
	}

	RessourceAllocationProblem<Solution> getRessAllocProblem(){
		return p;
	}

	@Override
	public void evaluate(Solution solution) throws JMException {
		double value = 0;
		int number = 0;

		if (p.isAgent){
			for (int i = 0; i < p.n; i++){
				if (!p.isViableForAgent(solution, i)){
					number++;
					value-=10*p.getAgentCriticality(i);
				}			    		
			}
		}

		assert p.isHost;
		if (p.isHost){
			for (int host_j = 0; host_j < p.m; host_j++){
				double memOverhead = p.getHostMemoryCharge(solution, host_j)-p.getHostMaxMemory(host_j);
				double procOverhead = p.getHostProcessorCharge(solution, host_j)-p.getHostMaxProcessor(host_j);
				if (memOverhead>0||procOverhead>0){
					number++;
					value-=Math.max(memOverhead,procOverhead)/(p.getAgentsChargeTotal()/p.n);				    	 
				}
			}
		}


		solution.setOverallConstraintViolation(-value);    
		solution.setNumberOfViolatedConstraint(number);       

		if (number==0){
			assert value==0;
			value= p.getSocWelfare(solution);

		}

		solution.setObjective(0, value);
	}


	public Comparator<Solution> getComparator(){
		return new Comparator<Solution>() {

			@Override
			public int compare(Solution o1, Solution o2) {
				if (o1.getObjective(0)!=o2.getObjective(0))
					return Double.compare(o1.getObjective(0), o2.getObjective(0));
				else {
					assert o1.variable_.length==o2.variable_.length;
					for (int i = 0; i < o1.variable_.length; i++){
						if (!o1.variable_[i].equals(o2.variable_[i])){
							assert length_[i]==1;
							return ((Binary)o1.variable_[i]).getIth(0)?1:-1;
						}
					}
					return 0;
				}

			}
		};
	}

	public Solution getUnallocatedSolution(){
		if (unAllocated==null){
			try{
				//setting unallocated
				if (useBinary){
					Binary[] vars = new Binary[this.getNumberOfVariables()];
					for (int i = 0; i < p.n; i++){
						for (int j=0; j < p.m; j++){
							if (p.getPos(i,j)!=-1){
								vars[p.getPos(i,j)]= new Binary(1);
								vars[p.getPos(i,j)].setIth(0,false);
							}	
						}
					}
					unAllocated= new Solution(this,vars);
					evaluate(unAllocated);
				} else {
					Int[] vars = new Int[this.getNumberOfVariables()];
					for (int i = 0; i < p.n; i++){
						for (int j=0; j < p.m; j++){
							if (p.getPos(i,j)!=-1){
								vars[p.getPos(i,j)]= new Int(0,0,1);
							}	
						}	
					}
					unAllocated= new Solution(this,vars);
					evaluate(unAllocated);
				}

				assert vectorVerif(unAllocated);
				return unAllocated;
			} catch (JMException e) {
				throw new RuntimeException("impossible");
			}

		} else {
			assert vectorVerif(unAllocated);
			return new Solution(unAllocated);
		}
	}
	public Solution getAllallocatedSolution(){

		if (allAllocated==null){
			try{
				if (useBinary){
					Binary[] vars = new Binary[this.getNumberOfVariables()];
					for (int i = 0; i < p.n; i++){
						for (int j=0; j < p.m; j++){
							if (p.getPos(i,j)!=-1){
								vars[p.getPos(i,j)]= new Binary(1);
								vars[p.getPos(i,j)].setIth(0,true);
							}	
						}	
					}
					allAllocated =new Solution(this,vars);
					evaluate(allAllocated);
				} else {
					Int[] vars = new Int[this.getNumberOfVariables()];
					for (int i = 0; i < p.n; i++){
						for (int j=0; j < p.m; j++){
							if (p.getPos(i,j)!=-1){
								vars[p.getPos(i,j)]= new Int(1,0,1);
							}	
						}	
					}
					allAllocated= new Solution(this,vars);
					evaluate(allAllocated);
				}
				assert vectorVerif(allAllocated);
				return allAllocated;
			} catch (JMException e) {
				throw new RuntimeException("impossible");
			}

		} else {
			assert vectorVerif(allAllocated);
			return new Solution(allAllocated);
		}
	}

	public void updateCurrentCharges(Solution solution) {
		p.updateCurrentCharges(solution);

	}

	public void updateCurrentReplicasNumber(Solution solution) {
		p.updateCurrentReplicasNumber(solution);
	}

	public JMetalBitFlipMutation getMutationOperator() {		
		HashMap parameters;
		parameters = new HashMap() ;
		parameters.put("probability", 1.) ;
		parameters.put("problem", p) ;
		return new JMetalBitFlipMutation(parameters);  
	}

	public Operator getCrossoverOperator() {
		HashMap parameters;
		parameters = new HashMap() ;
		parameters.put("probability", 1.) ;
		return new HUXCrossover(parameters);
	}
	
	public static <T> boolean vectorVerif(Solution y){
		for (int i = 0; i < y.variable_.length; i++){
			assert y.variable_[i]!=null:y;
		}
		return true;
	}
}

