package frameworks.faulttolerance.solver;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.FileHandler;

import java.util.logging.Logger;

import sun.security.action.GetLongAction;

import dima.introspectionbasedagents.modules.faults.Assert;

import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
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

public class JMetalSolver extends ResourceAllocationInterface<Solution> {

	private Algorithm algorithm;
	private boolean useBinary=true;

	private boolean solved=false;
	private Iterator<Solution> solutions;

	private JMetalRessAllocProblem p=null;



	public JMetalSolver(SocialChoiceType socialChoice, boolean isAgent,
			boolean isHost) {
		super(socialChoice, isAgent, isHost);
	}

	@Override
	protected void initiateSolver() throws UnsatisfiableException {

		try {
			setConstantHandling();	
			p=new JMetalRessAllocProblem(this);	
			algorithm = new JMetalElitistES(getProblem());
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
					solved=true;
				}
				assert solutions.hasNext();
				Solution sik = solutions.next();
				assert isViable(sik);
				return sik;
			} else {
				Solution bestSol = algorithm.execute().best(((JMetalRessAllocProblem)algorithm.getProblem()).getComparator());
				assert isViable(bestSol);
				return bestSol;
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
		getProblem().timeLimit = millisec;

	}

	@Override
	protected double readVariable(Solution var, int varPos) {
		assert var!=null;
		assert var.getDecisionVariables()[varPos]!=null;
		if (useBinary){
			return ((Binary)var.getDecisionVariables()[varPos]).getIth(0)?1.:0.;
		} else {
			try {
				return var.getDecisionVariables()[varPos].getValue();
			} catch (JMException e) {
				throw new RuntimeException(e);
			}
		}
	}



	@Override
	public Solution getInitialAllocAsSolution(double[] intialAlloc) {
		try {
			Solution initSol;
			if (useBinary){
				Binary[] vars = new Binary[getVariableNumber()];
				for (int i = 0; i < n; i++){
					for (int j = 0; j < m; j++){
						if (!isConstant(i,j)){
							vars[getPos(i,j)]= new Binary(1);
							vars[getPos(i,j)].setIth(0,intialAlloc[getPos(i,j)]==1.);
						}
					}
				}
				initSol = new Solution(getProblem(),vars);
				p.evaluate(initSol);
			} else {
				assert m==1:m;
				Int[] vars = new Int[getVariableNumber()];
				for (int i = 0; i < n; i++){
					for (int j = 0; j < m; j++){
						if (!isConstant(i,j)){
							vars[getPos(i,j)]= new Int((int)intialAlloc[getPos(i,j)],0,1);
						}	
					}
				}
				initSol = new Solution(getProblem(),vars);
				p.evaluate(initSol);
			}
			assert JMetalRessAllocProblem.vectorVerif(initSol);
			return initSol;
		} catch (JMException e) {
			throw new RuntimeException();
		}
	}

	public JMetalRessAllocProblem getProblem() {
		return p;
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