package frameworks.negotiation.negotiationframework.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import dima.introspectionbasedagents.services.BasicAgentModule;
import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.contracts.MatchingCandidature;
import frameworks.negotiation.negotiationframework.exploration.AllocationSolver;
import frameworks.negotiation.negotiationframework.rationality.AgentState;

public class OptimalSelectionModule<
Agent extends NegotiatingAgent<PersonalState, Contract>,
PersonalState extends AgentState,
Contract extends MatchingCandidature>
extends BasicAgentModule<Agent> 
implements SelectionModule<Agent, PersonalState, Contract> {

	final AllocationSolver<Contract, PersonalState> solver;
	final boolean forceOptimal;
	final long maxComputingTime;

	public OptimalSelectionModule(
			AllocationSolver<Contract, PersonalState> solver, 
			boolean forceOptimal,
			final long maxComputingTime) {
		this.solver = solver;
		this.forceOptimal=forceOptimal;
		this.maxComputingTime=maxComputingTime;
	}

	public Collection<Contract> getBestSolution() {
		return solver.getBestSolution();
	}


	public Collection<Contract> selection(
			PersonalState currentState,
			final Collection<Contract> contractsToExplore) {
		Collection<Contract> result;
		if (!contractsToExplore.isEmpty()){
			solver.initiate(contractsToExplore);
			this.solver.setTimeLimit((int) this.maxComputingTime);
			if (forceOptimal){
				result = solver.getBestSolution();
				if (result==null || !getMyAgent().Iaccept(contractsToExplore))
					return  new ArrayList<Contract>();
				else 
					return result;

			} else {
				final Date startingExploringTime = new Date();
				while (this.solver.hasNext() && (new Date().getTime() - startingExploringTime.getTime()<this.maxComputingTime)){
					result = solver.getNextSolution();
					if (result==null)
						return  new ArrayList<Contract>();
					else if (getMyAgent().Iaccept(contractsToExplore))
						return result;	
					else {
						//on continue a chercher
					}						
				}
			}
		}
		return  new ArrayList<Contract>();	
	}
}
