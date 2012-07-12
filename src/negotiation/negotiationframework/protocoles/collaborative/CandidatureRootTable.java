package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import dima.introspectionbasedagents.services.BasicAgentModule;

public class CandidatureRootTable<
Contract extends MatchingCandidature,
ActionSpec extends AbstractActionSpecif>
extends BasicAgentModule<SimpleNegotiatingAgent<?,Contract>> {

	/**
	 *
	 */
	private static final long serialVersionUID = 3172498344678889726L;
	private final int proposalComplexity = 1; //0 no host proposal, 1 host proposal, 2 agent fill the possible and provide requeste

	public Collection<ReallocationContract<Contract>> getPossible(
			final InformedCandidature<Contract> agentCandidature){
		if (this.getProposalComplexity()<=1) {
			return new ArrayList<ReallocationContract<Contract>>();
		} else {
			return null;
		}
	}

	public Collection<InformedCandidature<Contract>> getToBeAccepted(
			final ReallocationContract<Contract> request){
		if (this.getProposalComplexity()<=1) {
			return new ArrayList<InformedCandidature<Contract>>();
		} else {
			return null;
		}
	}


	public int getProposalComplexity() {
		return this.proposalComplexity;
	}

}
