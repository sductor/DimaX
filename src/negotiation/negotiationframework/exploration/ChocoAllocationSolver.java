package negotiation.negotiationframework.exploration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;
import dima.introspectionbasedagents.services.BasicAgentModule;


public abstract class ChocoAllocationSolver<
Contract extends MatchingCandidature,
PersonalState extends AgentState> extends BasicAgentModule implements AllocationSolver<Contract, PersonalState> {

	/**
	 *
	 */
	private static final long serialVersionUID = -4922949128483962580L;

	protected CPSolver s;

	protected Contract[] concerned;
	protected IntegerVariable[] replicas;
	protected IntegerVariable socialWelfareValue;

	protected final SocialChoiceType socialWelfare;


	public ChocoAllocationSolver(final SocialChoiceType socialWelfare){
		this.socialWelfare = socialWelfare;
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.exploration.AllocationSolver#initiate(java.util.List, PersonalState, java.lang.String)
	 */
	@Override
	public abstract void initiate(Collection<Contract> concerned);

	@Override
	public void setTimeLimit(final int millisec) {
		this.s.setTimeLimit(millisec);
	}

	/*
	 *
	 */

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.exploration.AllocationSolver#getBestSolution()
	 */
	/**
	 * impossible de la lancer *avant* une exploration des solutions...
	 */
	@Deprecated
	@Override
	public Collection<Contract> getBestSolution(){
		this.s.resetSearchStrategy();
		this.s.setObjective(this.s.getVar(this.socialWelfareValue));
		this.s.maximize(true);
		final Collection<Contract> result = this.generateSolution();
		this.s.resetSearchStrategy();
		this.s.setObjective(null);
		return  result;
	}

	/*
	 *
	 */

	Boolean hasNext=null;

	/**
	 * has next updated initially (hasNext==null) and in getNextSolution()
	 */
	@Override
	public boolean hasNext() {
		if (this.hasNext==null){
			this.s.solve();
			this.hasNext=this.s.isFeasible()!=null && this.s.isFeasible()!=false;
		}

		assert this.hasNext!=null;
		return this.hasNext;
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.exploration.AllocationSolver#getAllSolution()
	 */
	@Override
	public Collection<Contract> getNextSolution(){
		if (this.hasNext=false || this.hasNext==null){
			throw new NoSuchElementException();
		} else {
			final Collection<Contract> result = this.generateSolution();
			this.hasNext = this.s.nextSolution();
			if (this.hasNext==null) {
				this.hasNext=false;
			}
			return result;
		}
	}

	/*
	 *
	 */

	/**
	 * Transforme la solution actuelle du solveur en candidature accepté
	 * @return la liste des candidature de la solution du solveur différentes de l'allcoation courante
	 */
	private Collection<Contract> generateSolution(){
		assert this.concerned.length==this.replicas.length;
		assert this.s.isFeasible()==true;
		final ArrayList<Contract> results = new ArrayList<Contract>();

		for (int i = 0; i < this.concerned.length; i++){
			assert this.s.getVar(this.replicas[i]).getVal()==1 || this.s.getVar(this.replicas[i]).getVal()==0;
			final boolean allocated = this.s.getVar(this.replicas[i]).getVal()==1;
			final Contract c = this.concerned[i];

			if (c.isMatchingCreation() && allocated || !c.isMatchingCreation() && !allocated) {
				results.add(c);
			}
		}
		return results;
	}


}



