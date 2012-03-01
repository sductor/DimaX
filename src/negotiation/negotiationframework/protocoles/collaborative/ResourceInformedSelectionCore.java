package negotiation.negotiationframework.protocoles.collaborative;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import dima.introspectionbasedagents.services.BasicAgentCommunicatingCompetence;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import negotiation.negotiationframework.NegotiationProtocol;
import negotiation.negotiationframework.SelectionCore;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.UnknownContractException;

public class ResourceInformedSelectionCore <
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends MatchingCandidature<ActionSpec>>
extends BasicAgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>>
implements SelectionCore<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>{

	@Override
	public ContractTrunk<InformedCandidature<Contract, ActionSpec>> select(
			ContractTrunk<InformedCandidature<Contract, ActionSpec>> cs) {
		assert cs instanceof ResourceInformedCandidatureContractTrunk;
		ResourceInformedCandidatureContractTrunk<Contract, ActionSpec> ct = 
				(ResourceInformedCandidatureContractTrunk<Contract, ActionSpec>) cs;
		InformedCandidatureRationality<ActionSpec, PersonalState, Contract> myCore = 
				(InformedCandidatureRationality<ActionSpec, PersonalState, Contract>) getMyAgent().getMyCore();

		ContractTrunk<InformedCandidature<Contract, ActionSpec>> returned = 
				new ContractTrunk<InformedCandidature<Contract,ActionSpec>>(getIdentifier());
		HashSet<InformedCandidature<Contract, ActionSpec>> accepted = new HashSet<InformedCandidature<Contract, ActionSpec>>();
		HashSet<InformedCandidature<Contract, ActionSpec>> rejected = new HashSet<InformedCandidature<Contract, ActionSpec>>() ;
		rejected.addAll(ct.getAllInitiatorContracts());
		rejected.addAll(ct.getParticipantOnWaitContracts());

		try {
			if (!ct.getRequestableReallocationContracts().isEmpty()){
				ReallocationContract<Contract, ActionSpec> r = Collections.max(
						ct.getRequestableReallocationContracts(),myCore.getReferenceAllocationComparator(getMyAgent().getMyCurrentState()));
				for (Contract c : r)
					accepted.add(ct.getContract(c.getIdentifier()));
			}
		} catch (UnknownContractException e) {
			throw new RuntimeException(e);
		}
		//		assert	this.validityVerification(accepted, rejected);

		rejected.removeAll(accepted);

		// ACCEPTATION
		for (final InformedCandidature<Contract, ActionSpec> c : accepted) {
			returned.addContract(c);
			returned.addAcceptation(this.getMyAgent().getIdentifier(), c);
		}

		// REFUS
		for (final InformedCandidature<Contract, ActionSpec> c : rejected) {
			returned.addContract(c);
			returned.addRejection(this.getMyAgent().getIdentifier(), c);
		}

		this.logMonologue("Setting my answer "+returned, NegotiationProtocol.log_selectionStep);
		this.notify(new IllAnswer<PersonalState,  InformedCandidature<Contract,ActionSpec>>(returned, getMyAgent().getMyCurrentState()));
		this.logMonologue("After being delaed by relevant services "+returned, NegotiationProtocol.log_selectionStep);

		return returned;
	}


}
