package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;

import dima.introspectionbasedagents.services.BasicAgentModule;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;

public class CandidatureRootTable<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification>
extends BasicAgentModule<CollaborativeAgent<?, ActionSpec, Contract>> {

	public Collection<ReallocationContract<Contract, ActionSpec>> getPossible(
			InformedCandidature<Contract, ActionSpec> agentCandidature){
		if (getMyAgent().getProposalComplexity()<=1)
			return new ArrayList<ReallocationContract<Contract,ActionSpec>>();
		else
			return null;
	}

	public Collection<InformedCandidature<Contract, ActionSpec>> getToBeAccepted(
			ReallocationContract<Contract, ActionSpec> request){
		if (getMyAgent().getProposalComplexity()<=1)
			return new ArrayList<InformedCandidature<Contract,ActionSpec>>();
		else
			return null;
	}

}
