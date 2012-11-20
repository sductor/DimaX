package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService;
import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.RationalAgent;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class LocalViewInformationService<
PersonalState extends AgentState,
Contract extends AbstractContractTransition> extends BasicAgentCompetence<RationalAgent<PersonalState, Contract>>
implements ObservationService<RationalAgent<PersonalState, Contract>>, ReplicationGraph{


	/**
	 * 
	 */
	private static final long serialVersionUID = 6452101945394034695L;
	ReplicationInstanceGraph rig=new ReplicationInstanceGraph(null);

	@Override
	public Set<? extends AgentIdentifier> getKnownAgents() {
		return new HashSet<AgentIdentifier>(this.rig.getEveryIdentifier());
	}

	@Override
	public void add(final AgentIdentifier agentId) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void addAll(final Collection<? extends AgentIdentifier> agents) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void remove(final AgentIdentifier agentId) {
		//do nothing
	}

	@Override
	public <Info extends Information> Info getInformation(
			final Class<Info> informationType, final AgentIdentifier agentId)
					throws NoInformationAvailableException {
		assert informationType.equals(ReplicaState.class) || informationType.equals(HostState.class);
		final Info i = (Info) this.rig.getState(agentId);
		if (i==null){
			throw new NoInformationAvailableException();
		} else {
			return i;
		}
	}

	@Override
	public <Info extends Information> Info getMyInformation(
			final Class<Info> informationType) {
		assert informationType.equals(ReplicaState.class) || informationType.equals(HostState.class);
		final Info i = (Info) this.rig.getState(this.getIdentifier());
		assert i!=null;
		return i;
	}

	@Override
	public <Info extends Information> boolean hasMyInformation(
			final Class<Info> informationType) {
		return this.rig.getState(this.getIdentifier())!= null;
	}

	@Override
	public <Info extends Information> boolean hasInformation(
			final Class<Info> informationType) {
		return informationType.equals(ReplicaState.class) || informationType.equals(HostState.class);
	}

	@Override
	public <Info extends Information> boolean hasInformation(
			final Class<Info> informationType, final AgentIdentifier agentId) {
		return this.rig.getState(agentId)!= null;
	}

	@Override
	public <Info extends Information> Map<AgentIdentifier, Info> getInformation(
			final Class<Info> informationType) throws NoInformationAvailableException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void add(final Information information) {
		this.rig.setState((AgentState) information);
	}

	@Override
	public void remove(final Information information) {
		//do nothing
	}

	@Override
	public String show(final Class<? extends Information> infotype) {
		return this.rig.toString();
	}

	//
	// INSTANCE GRAPH
	//

	public void setAgents(final Collection<ReplicaState> collection) {
		this.rig.setAgents(collection);
	}

	public void setHosts(final Collection<HostState> hostsStates) {
		this.rig.setHosts(hostsStates);
	}

	public void setState(final AgentState s) {
		this.rig.setState(s);
	}

	@Override
	public int hashCode() {
		return this.rig.hashCode();
	}

	public void addAcquaintance(final AgentIdentifier a1, final AgentIdentifier a2) {
		this.rig.addAcquaintance(a1, a2);
	}

	public AgentState getState(final AgentIdentifier id) {
		return this.rig.getState(id);
	}

	public Collection<? extends AgentIdentifier> getAcquaintances(
			final AgentIdentifier id) {
		return this.rig.getAcquaintances(id);
	}

	@Override
	public SocialChoiceType getSocialWelfare() {
		return this.rig.getSocialWelfare();
	}

	public Collection<AgentIdentifier> getEveryIdentifier() {
		return this.rig.getEveryIdentifier();
	}

	@Override
	public Collection<AgentIdentifier> getAgentsIdentifier() {
		return this.rig.getAgentsIdentifier();
	}

	@Override
	public Collection<ResourceIdentifier> getHostsIdentifier() {
		return this.rig.getHostsIdentifier();
	}

	@Override
	public Collection<ReplicaState> getAgentStates() {
		return this.rig.getAgentStates();
	}

	@Override
	public Collection<HostState> getHostsStates() {
		return this.rig.getHostsStates();
	}

	@Override
	public boolean equals(final Object obj) {
		return this.rig.equals(obj);
	}

	@Override
	public ReplicaState getAgentState(final AgentIdentifier id) {
		return this.rig.getAgentState(id);
	}

	@Override
	public HostState getHostState(final ResourceIdentifier id) {
		return this.rig.getHostState(id);
	}

	public Collection<ResourceIdentifier> getInitialReplication(
			final AgentIdentifier id) {
		return this.rig.getInitialReplication(id);
	}

	public Collection<AgentIdentifier> getInitialReplication(
			final ResourceIdentifier id) {
		return this.rig.getInitialReplication(id);
	}

	@Override
	public Collection<ResourceIdentifier> getAccessibleHosts(final AgentIdentifier id) {
		return this.rig.getAccessibleHosts(id);
	}

	@Override
	public Collection<AgentIdentifier> getAccessibleAgents(final ResourceIdentifier id) {
		return this.rig.getAccessibleAgents(id);
	}

	@Override
	public boolean areLinked(final AgentIdentifier a1, final AgentIdentifier a2) {
		return this.rig.areLinked(a1, a2);
	}



	public ReplicationInstanceGraph getUnallocatedGraph() {
		return this.rig.getUnallocatedGraph();
	}

	@Override
	public String toString() {
		return this.rig.toString();
	}

	public boolean assertAllocValid() {
		return this.rig.assertAllocValid();
	}

	public boolean assertValidity() {
		return this.rig.assertAllocValid() && this.rig.assertNeigborhoodValidity();
	}

	public boolean isCoherent() {
		return this.rig.isCoherent();
	}

	public boolean assertNeigborhoodValidity() {
		return this.rig.assertNeigborhoodValidity();
	}

}
