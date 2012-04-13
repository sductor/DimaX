package negotiation.negotiationframework.protocoles.collaborative;

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
extends BasicAgentModule<SimpleNegotiatingAgent<?, ActionSpec, Contract>> {
	
	public Collection<ReallocationContract<Contract, ActionSpec>> getPossible(
			InformedCandidature<Contract, ActionSpec> agentCandidature){
		return null;
	}
	
	public Collection<InformedCandidature<Contract, ActionSpec>> getToBeAccepted(
			ReallocationContract<Contract, ActionSpec> request){
		return null;
	}
	
}
