package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.kernel.BasicCompetentAgent;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService;
import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.SimpleNegotiatingAgent;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.RationalAgent;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class LocalViewInformationService<
PersonalState extends AgentState,
Contract extends AbstractContractTransition> extends BasicAgentCompetence<RationalAgent<PersonalState, Contract>> 
implements ObservationService<RationalAgent<PersonalState, Contract>>, ReplicationGraph{


	ReplicationInstanceGraph rig=new ReplicationInstanceGraph(null);

	@Override
	public Set<? extends AgentIdentifier> getKnownAgents() {
		return new HashSet<AgentIdentifier>(rig.getEveryIdentifier());
	}

	@Override
	public void add(AgentIdentifier agentId) {
		throw new RuntimeException("not implemented");		
	}

	@Override
	public void addAll(Collection<? extends AgentIdentifier> agents) {
		throw new RuntimeException("not implemented");		
	}

	@Override
	public void remove(AgentIdentifier agentId) {
		//do nothing	
	}

	@Override
	public <Info extends Information> Info getInformation(
			Class<Info> informationType, AgentIdentifier agentId)
					throws NoInformationAvailableException {
		assert informationType.equals(ReplicaState.class) || informationType.equals(HostState.class);
		Info i = (Info) rig.getState(agentId);
		if (i==null){
			throw new NoInformationAvailableException();
		} else {
			return i;
		}
	}

	@Override
	public <Info extends Information> Info getMyInformation(
			Class<Info> informationType) {
		assert informationType.equals(ReplicaState.class) || informationType.equals(HostState.class);
		Info i = (Info) rig.getState(getIdentifier());
		assert i!=null;
		return i;
	}

	@Override
	public <Info extends Information> boolean hasMyInformation(
			Class<Info> informationType) {
		return rig.getState(getIdentifier())!= null;
	}

	@Override
	public <Info extends Information> boolean hasInformation(
			Class<Info> informationType) {
		return informationType.equals(ReplicaState.class) || informationType.equals(HostState.class);
	}

	@Override
	public <Info extends Information> boolean hasInformation(
			Class<Info> informationType, AgentIdentifier agentId) {
		return rig.getState(agentId)!= null;		
	}

	@Override
	public <Info extends Information> Map<AgentIdentifier, Info> getInformation(
			Class<Info> informationType) throws NoInformationAvailableException {
		throw new RuntimeException("not implemented");	
	}

	@Override
	public void add(Information information) {
		rig.setState((AgentState) information);
	}

	@Override
	public void remove(Information information) {
		//do nothing	
	}

	@Override
	public String show(Class<? extends Information> infotype) {
		return rig.toString();
	}

	//
	// INSTANCE GRAPH
	//

	public void setAgents(Collection<ReplicaState> collection) {
		rig.setAgents(collection);
	}

	public void setHosts(Collection<HostState> hostsStates) {
		rig.setHosts(hostsStates);
	}

	public void setState(AgentState s) {
		rig.setState(s);
	}

	public int hashCode() {
		return rig.hashCode();
	}

	public void addAcquaintance(AgentIdentifier a1, AgentIdentifier a2) {
		rig.addAcquaintance(a1, a2);
	}

	public AgentState getState(AgentIdentifier id) {
		return rig.getState(id);
	}

	public Collection<? extends AgentIdentifier> getAcquaintances(
			AgentIdentifier id) {
		return rig.getAcquaintances(id);
	}

	public SocialChoiceType getSocialWelfare() {
		return rig.getSocialWelfare();
	}

	public Collection<AgentIdentifier> getEveryIdentifier() {
		return rig.getEveryIdentifier();
	}

	public Collection<AgentIdentifier> getAgentsIdentifier() {
		return rig.getAgentsIdentifier();
	}

	public Collection<ResourceIdentifier> getHostsIdentifier() {
		return rig.getHostsIdentifier();
	}

	public Collection<ReplicaState> getAgentStates() {
		return rig.getAgentStates();
	}

	public Collection<HostState> getHostsStates() {
		return rig.getHostsStates();
	}

	public boolean equals(Object obj) {
		return rig.equals(obj);
	}

	public ReplicaState getAgentState(AgentIdentifier id) {
		return rig.getAgentState(id);
	}

	public HostState getHostState(ResourceIdentifier id) {
		return rig.getHostState(id);
	}

	public Collection<ResourceIdentifier> getInitialReplication(
			AgentIdentifier id) {
		return rig.getInitialReplication(id);
	}

	public Collection<AgentIdentifier> getInitialReplication(
			ResourceIdentifier id) {
		return rig.getInitialReplication(id);
	}

	public Collection<ResourceIdentifier> getAccessibleHosts(AgentIdentifier id) {
		return rig.getAccessibleHosts(id);
	}

	public Collection<AgentIdentifier> getAccessibleAgents(ResourceIdentifier id) {
		return rig.getAccessibleAgents(id);
	}

	public boolean areLinked(AgentIdentifier a1, AgentIdentifier a2) {
		return rig.areLinked(a1, a2);
	}



	public ReplicationInstanceGraph getUnallocatedGraph() {
		return rig.getUnallocatedGraph();
	}

	public String toString() {
		return rig.toString();
	}

	public boolean assertAllocValid() {
		return rig.assertAllocValid();
	}

	public boolean assertValidity() {
		return rig.assertAllocValid() && rig.assertNeigborhoodValidity();
	}

	public boolean isCoherent() {
		return rig.isCoherent();
	}

	public boolean assertNeigborhoodValidity() {
		return rig.assertNeigborhoodValidity();
	}

}
