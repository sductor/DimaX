package frameworks.negotiation.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import dima.introspectionbasedagents.services.BasicAgentModule;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver.ExceedLimitException;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.AgentState;

public class OptimalSelectionModule<
Agent extends NegotiatingAgent<PersonalState, Contract>,
PersonalState extends AgentState,
Contract extends MatchingCandidature>
extends BasicAgentModule<Agent> 
implements SelectionModule<Agent, PersonalState, Contract> {

	final ResourceAllocationSolver<Contract, PersonalState> solver;
	final boolean forceOptimal;
	final long maxComputingTime;

	public OptimalSelectionModule(
			ResourceAllocationSolver<Contract, PersonalState> solver, 
			boolean forceOptimal,
			final long maxComputingTime) {
		this.solver = solver;
		this.forceOptimal=forceOptimal;
		this.maxComputingTime=maxComputingTime;
	}

	private Collection<Contract> getBestSolution() {
		try {
			return solver.computeBestLocalSolution();
		} catch (UnsatisfiableException e) {
			e.printStackTrace();
			return null;
		} catch (ExceedLimitException e) {
			e.printStackTrace();
			return null;
		}
	}


	public Collection<Contract> selection(
			PersonalState currentState,
			final Collection<Contract> contractsToExplore) {
		Collection<Contract> result;
		
		if (!contractsToExplore.isEmpty()){
			
			solver.initiate(contractsToExplore);
			this.solver.setTimeLimit((int) this.maxComputingTime);
			
			if (forceOptimal){
				result = getBestSolution();
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
