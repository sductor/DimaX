package negotiation.negotiationframework.exploration.strategic.exploration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.strategic.StrategicNegotiatingAgent;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dimaxx.tools.mappedcollections.HashedHashList;

public  class MatchingAllocationNeighborhood<ActionSpec extends AbstractActionSpecification>
extends BasicAgentModule<StrategicNegotiatingAgent<?, MatchingCandidature<ActionSpec>, ActionSpec>>
implements AbstractContractNeighborhood<MatchingCandidature<ActionSpec>, ActionSpec>
{
	private static final long serialVersionUID = -1739492494969146550L;

	public final static AgentIdentifier dummyManager = new AgentName("exploration");
	public final static long validityTime = ReplicationExperimentationProtocol._contractExpirationTime;

	@Override
	public AllocationTransition<MatchingCandidature<ActionSpec>, ActionSpec> getEmptyContract()
	{
		return new AllocationTransition(MatchingAllocationNeighborhood.dummyManager,MatchingAllocationNeighborhood.validityTime);
	}

	@Override
	public Iterator<MatchingCandidature<ActionSpec>> getNeighbors(
			final AllocationTransition<MatchingCandidature<ActionSpec>, ActionSpec> c,
			final Collection<AgentIdentifier> knownAgents,
			final Collection<String> knownActions) {
		return new AllocationNeighbors(c, knownAgents);
	}

	@Override
	public AllocationTransition<MatchingCandidature<ActionSpec>, ActionSpec> getRandomContract(
			final Collection<AgentIdentifier> knownAgents,
			final Collection<String> knownActions) {
		final Random rand = new Random();
		final int prof = rand.nextInt(knownActions.size()*knownAgents.size());
		final AllocationTransition<MatchingCandidature<ActionSpec>, ActionSpec> c = this.getEmptyContract();
		for (int i = 0; i < prof; i++) {
			c.add(this.getNeighbors(c, knownAgents, knownActions).next());
		}
		return c;
	}


	/**
	 * Methode du max tres peu efficace : coûteuse en mémoire
	 */
	private class AllocationNeighbors implements Iterator<MatchingCandidature<ActionSpec>>{

		//Each number of case correspond to an agent
		//each vector in each case correspond to the already assigned resources.
		//The unexplored mathching is obtain by doing the difference with the known hosts.
		HashedHashList<AgentIdentifier,ResourceIdentifier> min;
		ArrayList<ResourceIdentifier> knownHosts = new ArrayList<ResourceIdentifier>();
		MatchingCandidature<ActionSpec> last;

		public AllocationNeighbors(final AllocationTransition<MatchingCandidature<ActionSpec>, ActionSpec> contractToExplore,
				final Collection<AgentIdentifier> knownAgents) {
			for (final AgentIdentifier ag : knownAgents) {
				if (ag instanceof ResourceIdentifier){
					this.knownHosts.add((ResourceIdentifier) ag);
					knownAgents.remove(ag);
				}
			}

			//The neighbors are the deals resulting from the add of actions (an action can represent an allocation or a desallocation)
			//max is thus initialized this way : it contains all the action still possible (the action of contract to explore have already been executed)
			this.min = new HashedHashList<AgentIdentifier, ResourceIdentifier>();
			for (final AgentIdentifier id : knownAgents) {
				this.min.put(id, new ArrayList<ResourceIdentifier>());
			}

			for (final AgentIdentifier id : contractToExplore.getAllParticipants()){
				for (final MatchingCandidature<ActionSpec> c : contractToExplore.getAssociatedActions(id)) {
					this.min.get(id).add(c.getResource());
				}
				if (this.min.get(id).containsAll(this.knownHosts)) {
					this.min.remove(id);
				}
			}
		}

		@Override
		public boolean hasNext() {
			return !this.min.isEmpty();
		}

		@Override
		public MatchingCandidature<ActionSpec> next() {
			//Selection aléatoir d'un agent et d'un des hôtes vers lequel il peut encore faire une action
			final Random rand = new Random();
			final AgentIdentifier replicaToadd = new ArrayList<AgentIdentifier>(this.min.keySet()).get(rand.nextInt(this.min.size()));


			final List<ResourceIdentifier> availableHosts = new ArrayList<ResourceIdentifier>(this.knownHosts);
			availableHosts.removeAll(this.min.get(replicaToadd));

			final ResourceIdentifier choosenHost = availableHosts.get(rand.nextInt(availableHosts.size()));
			final MatchingCandidature<ActionSpec> move =
					new MatchingCandidature<ActionSpec>(MatchingAllocationNeighborhood.this.getMyAgent().getIdentifier(),replicaToadd,	choosenHost,MatchingAllocationNeighborhood.validityTime);
			//mis a jour des listes
			this.min.get(replicaToadd).add(choosenHost);
			if (this.min.get(replicaToadd).containsAll(this.knownHosts)) {
				this.min.remove(replicaToadd);
			}
			this.last = move;
			return move;
		}

		@Override
		public void remove() {
			throw new RuntimeException("this iterator is not aimed to be used like this");
		}

	}

}
