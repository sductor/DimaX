package frameworks.faulttolerance.solver;

import java.util.Comparator;
import java.util.HashMap;

import frameworks.faulttolerance.solver.jmetal.core.Operator;
import frameworks.faulttolerance.solver.jmetal.core.Problem;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.core.Variable;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.BinarySolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.solutionType.IntSolutionType;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Int;
import frameworks.faulttolerance.solver.jmetal.operators.crossover.HUXCrossover;
import frameworks.faulttolerance.solver.jmetal.util.JMException;

public class JMetalRessAllocProblem extends Problem	{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4152853867862709129L;
	Solution allAllocated;
	Solution unAllocated;
	Solution initial;
	private final boolean useBinary=true;
	RessourceAllocationProblem<Solution> p;



	int timeLimit=Integer.MAX_VALUE;
	private int maxGeneration=Integer.MAX_VALUE;

	public int mu     = 15 ;

	int diversi=5;
	int nbMutation = 2;
	int nbCroisement = 5;
	private int stagnationCounter=2;

	public JMetalRessAllocProblem(final RessourceAllocationProblem<Solution> p){
		super(p.getMyAgent().getRandom());
		try {
			this.p=p;
			this.numberOfVariables_   = p.getVariableNumber() ;
			this.numberOfObjectives_  = 1;                              ;
			this.numberOfConstraints_ = p.getConstraintNumber();
			this.problemName_         = "RessAllocJMetalProblem";
			if (this.useBinary){
				this.length_ = new int[this.numberOfVariables_];
				for (int i = 0; i < this.numberOfVariables_; i++){
					this.length_[i] = 1;
				}
				this.solutionType_ = new BinarySolutionType(this) ;

			} else {
				this.lowerLimit_  = new double[this.numberOfVariables_];
				this.upperLimit_ = new double[this.numberOfVariables_];
				for (int i = 0; i < this.numberOfVariables_; i++){
					this.lowerLimit_[i]=0.;
					this.upperLimit_[i]=1.;
				}
				this.solutionType_ = new IntSolutionType(this) ;
			}


			//setting allalcated

		} catch (final ClassNotFoundException e) {
			throw new RuntimeException("impossible");
		}
	}

	RessourceAllocationProblem<Solution> getRessAllocProblem(){
		return this.p;
	}

	@Override
	public void evaluate(final Solution solution) throws JMException {
		double value = 0;
		int number = 0;

		if (this.p.isAgent){
			for (int i = 0; i < this.p.n; i++){
				if (!this.p.isViableForAgent(solution, i)){
					number++;
					value-=10*this.p.getAgentCriticality(i);
				}
			}
		}

		assert this.p.isHost;
		if (this.p.isHost){
			for (int host_j = 0; host_j < this.p.m; host_j++){
				final double memOverhead = this.p.getHostMemoryCharge(solution, host_j)-this.p.getHostMaxMemory(host_j);
				final double procOverhead = this.p.getHostProcessorCharge(solution, host_j)-this.p.getHostMaxProcessor(host_j);
				if (memOverhead>0||procOverhead>0){
					number++;
					value-=Math.max(memOverhead,procOverhead)/(this.p.getAgentsChargeTotal()/this.p.n);
				}
			}
		}


		solution.setOverallConstraintViolation(-value);
		solution.setNumberOfViolatedConstraint(number);

		if (number==0){
			assert value==0;
			value= this.p.getSocWelfare(solution);

		}

		solution.setObjective(0, value);
	}


	public Comparator<Solution> getComparator(){
		return new Comparator<Solution>() {

			@Override
			public int compare(final Solution o1, final Solution o2) {
				if (o1.getObjective(0)!=o2.getObjective(0)) {
					return Double.compare(o1.getObjective(0), o2.getObjective(0));
				} else {
					assert o1.variable_.length==o2.variable_.length;
					for (int i = 0; i < o1.variable_.length; i++){
						if (!o1.variable_[i].equals(o2.variable_[i])){
							assert JMetalRessAllocProblem.this.length_[i]==1;
							return ((Binary)o1.variable_[i]).getIth(0)?1:-1;
						}
					}
					return 0;
				}

			}
		};
	}

	public Solution getUnallocatedSolution(){
		if (this.unAllocated==null){
			try{
				//setting unallocated
				if (this.useBinary){
					final Binary[] vars = new Binary[this.getNumberOfVariables()];
					for (int i = 0; i < this.p.n; i++){
						for (int j=0; j < this.p.m; j++){
							if (this.p.getPos(i,j)!=-1){
								vars[this.p.getPos(i,j)]= new Binary(1,p.getMyAgent().getRandom());
								vars[this.p.getPos(i,j)].setIth(0,false);
							}
						}
					}
					this.unAllocated= new Solution(this,vars);
					this.evaluate(this.unAllocated);
				} else {
					final Int[] vars = new Int[this.getNumberOfVariables()];
					for (int i = 0; i < this.p.n; i++){
						for (int j=0; j < this.p.m; j++){
							if (this.p.getPos(i,j)!=-1){
								vars[this.p.getPos(i,j)]= new Int(0,0,1);
							}
						}
					}
					this.unAllocated= new Solution(this,vars);
					this.evaluate(this.unAllocated);
				}

				assert JMetalRessAllocProblem.vectorVerif(this.unAllocated);
				return this.unAllocated;
			} catch (final JMException e) {
				throw new RuntimeException("impossible");
			}

		} else {
			assert JMetalRessAllocProblem.vectorVerif(this.unAllocated);
			return new Solution(this.unAllocated);
		}
	}
	public Solution getAllallocatedSolution(){

		if (this.allAllocated==null){
			try{
				if (this.useBinary){
					final Binary[] vars = new Binary[this.getNumberOfVariables()];
					for (int i = 0; i < this.p.n; i++){
						for (int j=0; j < this.p.m; j++){
							if (this.p.getPos(i,j)!=-1){
								vars[this.p.getPos(i,j)]= new Binary(1,p.getMyAgent().getRandom());
								vars[this.p.getPos(i,j)].setIth(0,true);
							}
						}
					}
					this.allAllocated =new Solution(this,vars);
					this.evaluate(this.allAllocated);
				} else {
					final Int[] vars = new Int[this.getNumberOfVariables()];
					for (int i = 0; i < this.p.n; i++){
						for (int j=0; j < this.p.m; j++){
							if (this.p.getPos(i,j)!=-1){
								vars[this.p.getPos(i,j)]= new Int(1,0,1);
							}
						}
					}
					this.allAllocated= new Solution(this,vars);
					this.evaluate(this.allAllocated);
				}
				assert JMetalRessAllocProblem.vectorVerif(this.allAllocated);
				return this.allAllocated;
			} catch (final JMException e) {
				throw new RuntimeException("impossible");
			}

		} else {
			assert JMetalRessAllocProblem.vectorVerif(this.allAllocated);
			return new Solution(this.allAllocated);
		}
	}

	public void updateCurrentCharges(final Solution solution) {
		this.p.updateCurrentCharges(solution);

	}

	public void updateCurrentReplicasNumber(final Solution solution) {
		this.p.updateCurrentReplicasNumber(solution);
	}

	public JMetalBitFlipMutation getMutationOperator() {
		HashMap parameters;
		parameters = new HashMap() ;
		parameters.put("probability", 1.) ;
		parameters.put("problem", this.p) ;
		return new JMetalBitFlipMutation(parameters);
	}

	public Operator getCrossoverOperator() {
		HashMap parameters;
		parameters = new HashMap() ;
		parameters.put("probability", 1.) ;
		return new HUXCrossover(parameters,p.getMyAgent().getRandom());
	}

	public static <T> boolean vectorVerif(final Solution y){
		for (final Variable element : y.variable_) {
			assert element!=null:y;
		}
		return true;
	}

	public int getMaxGeneration() {
		return this.maxGeneration;
	}

	public void setMaxGeneration(final int maxGeneration) {
		this.maxGeneration = maxGeneration;
	}

	public int getTimeLimit() {
		return this.timeLimit;
	}

	public void setTimeLimit(final int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public int getStagnationCounter() {
		return this.stagnationCounter;
	}

	public void setStagnationCounter(final int stagnationCounter) {
		this.stagnationCounter = stagnationCounter;
	}
}

