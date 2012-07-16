package frameworks.negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;

import dima.introspectionbasedagents.services.BasicAgentModule;
import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.contracts.AbstractActionSpecif;
import frameworks.negotiation.negotiationframework.contracts.MatchingCandidature;
import frameworks.negotiation.negotiationframework.contracts.ReallocationContract;

public class CandidatureRootTable<
Contract extends MatchingCandidature,
ActionSpec extends AbstractActionSpecif>
extends BasicAgentModule<NegotiatingAgent<?,Contract>> {

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
