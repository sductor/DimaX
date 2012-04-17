package negotiation.negotiationframework.exploration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.rationality.SocialChoiceFunction;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.kernel.model.Model;
import choco.kernel.model.ModelException;
import choco.kernel.model.variables.Operator;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.solver.Solver;
import dima.introspectionbasedagents.services.BasicAgentModule;


public abstract class ChocoAllocationSolver<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec> extends BasicAgentModule implements AllocationSolver<Contract, ActionSpec, PersonalState> {


	protected Solver s;

	protected Contract[] concerned;
	protected IntegerVariable[] replicas;
	protected IntegerVariable socialWelfareValue;

	protected final String socialWelfare;

	public ChocoAllocationSolver(String socialWelfare){
		this.socialWelfare = socialWelfare;
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.exploration.AllocationSolver#initiate(java.util.List, PersonalState, java.lang.String)
	 */
	@Override
	public abstract void initiate(
			Collection<Contract> concerned, 
			PersonalState currentState);

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.exploration.AllocationSolver#getAllSolution()
	 */
	@Override
	public  Collection<Collection<Contract>> getAllSolution(){
		Collection<Collection<Contract>> result = new ArrayList<Collection<Contract>>();
		s.solve();
		result.add(generateSolution());
		while (s.isFeasible()){
			s.nextSolution();
			result.add(generateSolution());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.exploration.AllocationSolver#getBestSolution()
	 */
	@Override
	public Collection<Contract> getBestSolution(){
		s.maximize(s.getVar(socialWelfareValue), true);
		return generateSolution();
	}

	/**
	 * Transforme la solution actuelle du solveur en candidature accepté
	 * @return la liste des candidature de la solution du solveur différentes de l'allcoation courante
	 */
	private Collection<Contract> generateSolution(){
		assert concerned.length==replicas.length;
		ArrayList<Contract> results = new ArrayList<Contract>();
		
		if (!s.isFeasible())
			return results;
		
		for (int i = 0; i < concerned.length; i++){
			assert s.getVar(replicas[i]).getVal()==1 || s.getVar(replicas[i]).getVal()==0;
			boolean allocated = s.getVar(replicas[i]).getVal()==1;
			Contract c = concerned[i];

			if ((c.isMatchingCreation() && allocated) || (!c.isMatchingCreation() && !allocated))
				results.add(c);
		}		
		return results;
	}
}



