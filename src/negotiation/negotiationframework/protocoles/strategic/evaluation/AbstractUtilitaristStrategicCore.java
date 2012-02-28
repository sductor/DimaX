package negotiation.negotiationframework.protocoles.strategic.evaluation;

import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;

public interface AbstractUtilitaristStrategicCore
<Contract extends AbstractContractTransition<ActionSpec>,
ActionSpec extends AbstractActionSpecification>
extends DimaComponentInterface{


	public  boolean iThinkItwillAccept(final AgentIdentifier id,  final Contract c);

	public boolean iMRiskAdverse();

	public  Float getAgentBelievedStateConfidence(final AgentIdentifier id);

	public   Double getConfidenceOfInformationAbout(final Collection<? extends AgentIdentifier> ids);

	public Double evaluateContractPersonalUtility(final Contract c);

	public  Double evaluateContractUtility(final AgentIdentifier id, final Contract c);
}
