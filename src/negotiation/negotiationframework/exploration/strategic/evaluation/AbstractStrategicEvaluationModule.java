package negotiation.negotiationframework.exploration.strategic.evaluation;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;

public interface AbstractStrategicEvaluationModule
<Contract extends AbstractContractTransition<ActionSpec>,
ActionSpec extends AbstractActionSpecification> {

	//Evaluation under uncertainty
	public int strategiclyCompare(Contract c1, Contract c2) throws NoInformationAvailableException;
}
