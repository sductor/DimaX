package negotiation.interactionprotocols.socialNegotiation;

import java.util.Map;

import negotiation.agentframework.informedagent.AgentState;
import negotiation.agentframework.informedagent.InformationService;
import negotiation.agentframework.informedagent.InformationService.MissingInformationException;
import negotiation.agentframework.negotiatingagent.RationalExecutor;
import negotiation.agentframework.rationalagent.AgentActionSpecification;
import negotiation.agentframework.rationalagent.ContractTransition;
import negotiation.agentframework.rationalagent.AgentActionSpecification.ActionArguments;

public interface SocialInvolvedAgent<
State extends AgentState,
InformedState extends AgentState,
Contract extends SocialContract<Info> & ContractTransition<ActionSpec,ActionArgs>,
ActionSpec extends AgentActionSpecification,
ActionArgs extends ActionArguments,
Info extends Comparable<Info>
> 
extends 
RationalExecutor<State, Contract,InformedState>{

	public Info computePersonnalGain(Contract c) throws MissingInformationException;
	
	public Map<ActionSpec, ActionArgs> getMyArguments(Contract c);

}
