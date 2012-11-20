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

	/**
	 * 
	 */
	private static final long serialVersionUID = -1023510625245338664L;
	final ResourceAllocationSolver<Contract, PersonalState> solver;
	final boolean forceOptimal;
	final long maxComputingTime;

	public OptimalSelectionModule(
			final ResourceAllocationSolver<Contract, PersonalState> solver,
			final boolean forceOptimal,
			final long maxComputingTime) {
		this.solver = solver;
		this.forceOptimal=forceOptimal;
		this.maxComputingTime=maxComputingTime;
	}

	private Collection<Contract> getBestSolution() {
		try {
			return this.solver.getBestLocalSolution();
		} catch (final UnsatisfiableException e) {
			e.printStackTrace();
			return null;
		} catch (final ExceedLimitException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public Collection<Contract> selection(
			final PersonalState currentState,
			final Collection<Contract> contractsToExplore) {
		Collection<Contract> result;

		if (!contractsToExplore.isEmpty()){

			this.solver.setProblem(contractsToExplore);
			this.solver.setTimeLimit((int) this.maxComputingTime);

			if (this.forceOptimal){
				result = this.getBestSolution();
				if (result==null || !this.getMyAgent().Iaccept(contractsToExplore)) {
					return  new ArrayList<Contract>();
				} else {
					return result;
				}

			} else {
				final Date startingExploringTime = new Date();
				while (this.solver.hasNext() && new Date().getTime() - startingExploringTime.getTime()<this.maxComputingTime){
					result = this.solver.getNextLocalSolution();
					if (result==null) {
						return  new ArrayList<Contract>();
					} else if (this.getMyAgent().Iaccept(contractsToExplore)) {
						return result;
					} else {
						//on continue a chercher
					}
				}
			}
		}
		return  new ArrayList<Contract>();
	}
}
