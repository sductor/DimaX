package negotiation.interactionprotocols.socialNegotiation;

import java.util.Collection;

import negotiation.agentframework.informedagent.AgentState;
import negotiation.agentframework.negotiatingagent.ProposerAgent;
import negotiation.agentframework.rationalagent.AgentActionSpecification;
import negotiation.agentframework.rationalagent.ContractTransition;
import negotiation.agentframework.rationalagent.AgentActionSpecification.ActionArguments;

public interface SocialProposerAgent<
State extends AgentState,
Contract extends ContractTransition<ActionSpec, ActionArgs>,
InformedState extends AgentState,
ActionSpec extends AgentActionSpecification,
ActionArgs extends ActionArguments,
Info extends Comparable<Info>> extends ProposerAgent<State,Contract,InformedState>{

	
	Info computeSocialGain(final Collection<SocialAnswer<ActionSpec, ActionArgs, Info>> individualGain);
}
