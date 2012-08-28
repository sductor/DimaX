package negotiation.negotiationframework.protocoles.collaborative;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import negotiation.negotiationframework.SelectionCore;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;

public class ResourceInformedSelectionCore <
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends MatchingCandidature<ActionSpec>>
implements SelectionCore<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>{
	
	final SelectionCore<ActionSpec, PersonalState, ReallocationContract<Contract,ActionSpec>> referenceSelectionCore;

	private ResourceInformedSelectionCore(
			SelectionCore<ActionSpec, PersonalState, ReallocationContract<Contract,ActionSpec>> referenceSelectionCore) {
		super();
		this.referenceSelectionCore = referenceSelectionCore;
	}

	@Override
	public boolean isActive() {
		return referenceSelectionCore.isActive();
	}

	@Override
	public SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>> getMyAgent() {
		return (SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>) referenceSelectionCore.getMyAgent();
	}

	@Override
	public void setMyAgent(SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>> ag)
			throws UnrespectedCompetenceSyntaxException {
		referenceSelectionCore.setMyAgent((SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>) ag);
	}

	@Override
	public void die() {
		referenceSelectionCore.die();
	}

	@Override
	public AgentIdentifier getIdentifier() {
		return referenceSelectionCore.getIdentifier();
	}

	@Override
	public void setActive(boolean active) {
		referenceSelectionCore.setActive(active);
	}

	@Override
	public ContractTrunk<InformedCandidature<Contract, ActionSpec>> select(
			ContractTrunk<InformedCandidature<Contract, ActionSpec>> cs) {
		assert cs instanceof ResourceInformedCandidatureContractTrunk;
		ResourceInformedCandidatureContractTrunk ct = (ResourceInformedCandidatureContractTrunk) cs;
		
		return referenceSelectionCore.select(ct.g);
	}
	
	

}
