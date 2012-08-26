package frameworks.negotiation.exploration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;
import dima.introspectionbasedagents.modules.faults.Assert;
import dima.introspectionbasedagents.services.BasicAgentModule;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;


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
		Boolean feasible = this.s.maximize(true);
		Collection<Contract> result = new ArrayList<Contract>(); 
		if (feasible!=null && feasible){
			result = this.generateSolution();
		}
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
		if (this.hasNext==null){//initialisation			
			this.hasNext=this.s.solve();
//			assert Assert.Imply(s.isFeasible()==null,hasNext==null):hasNext+" "+s.isFeasible();
//			assert Assert.Imply(hasNext,s.isFeasible()!=null):hasNext+" "+s.isFeasible();
			if (this.hasNext==null) {
				this.hasNext=false;
			}
//			assert Assert.Imply(s.isFeasible()==null,hasNext==false):hasNext+" "+s.isFeasible();
//			assert Assert.Imply(hasNext,s.isFeasible()!=null):hasNext+" "+s.isFeasible();
		}

//		assert this.hasNext!=null;
//		assert s!=null;
//		assert Assert.Imply(s.isFeasible()!=null,hasNext==s.isFeasible()):hasNext+" "+s.isFeasible();
//		assert Assert.Imply(hasNext,s.isFeasible()!=null):hasNext+" "+s.isFeasible();
		return this.hasNext;
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.exploration.AllocationSolver#getAllSolution()
	 */
	@Override
	public Collection<Contract> getNextSolution(){
		assert Assert.Imply(s.isFeasible()!=null,hasNext==s.isFeasible()):hasNext+" "+s.isFeasible();
		if (!this.hasNext){
			throw new NoSuchElementException();
		} else if (hasNext==null){
			hasNext();
			return getNextSolution();
		} else {
			assert hasNext:hasNext;
			final Collection<Contract> result = this.generateSolution();
			assert s!=null;
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
//		assert this.s.isFeasible()!=null && this.s.isFeasible():this.s.isFeasible()+" "+hasNext;
//		assert hasNext:hasNext;
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



