package frameworks.faulttolerance.experimentation;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public interface ReplicationGraph {

	public abstract Collection<AgentIdentifier> getAgentsIdentifier();

	public abstract Collection<ResourceIdentifier> getHostsIdentifier();

	public abstract Collection<ReplicaState> getAgentStates();

	public abstract Collection<HostState> getHostsStates();

	public abstract ReplicaState getAgentState(final AgentIdentifier id);

	public abstract HostState getHostState(final ResourceIdentifier id);

	public abstract Collection<ResourceIdentifier> getAccessibleHosts(
			final AgentIdentifier id);
	
	public abstract Collection<AgentIdentifier> getAccessibleAgents(
			final ResourceIdentifier id);

	SocialChoiceType getSocialWelfare();

	boolean areLinked(AgentIdentifier a1, AgentIdentifier a2);


}