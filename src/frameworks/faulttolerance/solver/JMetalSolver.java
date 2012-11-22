package frameworks.faulttolerance.solver;

import java.util.Collection;
import java.util.Iterator;

import dima.basicagentcomponents.AgentIdentifier;

import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.faulttolerance.solver.jmetal.core.Algorithm;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;
import frameworks.faulttolerance.solver.jmetal.encodings.variable.Int;
import frameworks.faulttolerance.solver.jmetal.util.JMException;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class JMetalSolver extends ResourceAllocationInterface<Solution> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2633654479948927257L;
	private Algorithm algorithm;
	private final boolean useBinary=true;

	private boolean solved=false;
	private Iterator<Solution> solutions=null;

	private JMetalRessAllocProblem p=null;



	public JMetalSolver(final SocialChoiceType socialChoice, final boolean isAgent,
			final boolean isHost) {
		super(socialChoice, isAgent, isHost);
	}

	@Override
	public void setProblem(final Collection<ReplicationCandidature> concerned) {
		super.setProblem(concerned);
		this.solved=false;
		solutions=null;
	}

	@Override
	public void setProblem(final ReplicationGraph rig, final Collection<AgentIdentifier> fixedVar) {
		super.setProblem(rig, fixedVar);
		this.solved=false;
		solutions=null;
	}

	@Override
	protected void initiateSolver() throws UnsatisfiableException {

		try {
			this.setConstantHandling();
			this.p=new JMetalRessAllocProblem(this);
			this.algorithm = new JMetalElitistES(this.getProblem());
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Solution solveProb(final boolean opt) throws UnsatisfiableException {
		if (!opt){
			assert this.solved=true;
			assert this.solutions.hasNext();
			final Solution sik = this.solutions.next();
			assert this.assertIsViable(sik):sik;
			return sik;
		} else {
			try{
				final Solution bestSol = this.algorithm.execute().best(((JMetalRessAllocProblem)this.algorithm.getProblem()).getComparator());
				assert this.isViable(bestSol);
				return bestSol;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public boolean hasNext() {
		if (!this.solved) {
			try{
				this.solutions = this.algorithm.execute().iterator();
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			this.solved=true;
			return hasNext();
		} else {
			return this.solutions.hasNext();
		}
	}

	@Override
	public void setTimeLimit(final int millisec) {
		this.getProblem().timeLimit = millisec;

	}

	@Override
	protected double readVariable(final Solution var, final int varPos) {
		assert var!=null;
		assert var.getDecisionVariables()[varPos]!=null;
		if (this.useBinary){
			return ((Binary)var.getDecisionVariables()[varPos]).getIth(0)?1.:0.;
		} else {
			try {
				return var.getDecisionVariables()[varPos].getValue();
			} catch (final JMException e) {
				throw new RuntimeException(e);
			}
		}
	}



	@Override
	public Solution getInitialAllocAsSolution(final double[] intialAlloc) {
		try {
			Solution initSol;
			if (this.useBinary){
				final Binary[] vars = new Binary[this.getVariableNumber()];
				for (int i = 0; i < this.n; i++){
					for (int j = 0; j < this.m; j++){
						if (!this.isConstant(i,j)){
							vars[this.getPos(i,j)]= new Binary(1,getMyAgent().getRandom());
							vars[this.getPos(i,j)].setIth(0,intialAlloc[this.getPos(i,j)]==1.);
						}
					}
				}
				initSol = new Solution(this.getProblem(),vars);
				this.p.evaluate(initSol);
			} else {
				assert this.m==1:this.m;
				final Int[] vars = new Int[this.getVariableNumber()];
				for (int i = 0; i < this.n; i++){
					for (int j = 0; j < this.m; j++){
						if (!this.isConstant(i,j)){
							vars[this.getPos(i,j)]= new Int((int)intialAlloc[this.getPos(i,j)],0,1);
						}
					}
				}
				initSol = new Solution(this.getProblem(),vars);
				this.p.evaluate(initSol);
			}
			assert JMetalRessAllocProblem.vectorVerif(initSol);
			return initSol;
		} catch (final JMException e) {
			throw new RuntimeException();
		}
	}

	public JMetalRessAllocProblem getProblem() {
		return this.p;
	}

	@Override
	protected void initiateSolverPost() throws UnsatisfiableException {
		// ne fait rien
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