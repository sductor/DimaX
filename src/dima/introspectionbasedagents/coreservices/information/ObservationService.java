package dima.introspectionbasedagents.coreservices.information;

import java.util.Collection;
import java.util.HashMap;


import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.competences.AgentCompetence;
import dimaxx.tools.aggregator.FunctionnalCompensativeAggregator;
import dimaxx.tools.aggregator.UtilitaristAnalyser;

public interface ObservationService extends AgentCompetence<BasicCompetentAgent> {

	//
	// Acquaintance
	//

	public Collection<AgentIdentifier> getKnownAgents();

	public void add(AgentIdentifier agentId);

	public void addAll(Collection<? extends AgentIdentifier> agents);

	public void remove(AgentIdentifier agentId);
	

	//
	// Information
	//

	public <Info extends Information> Info getInformation(
			Class<Info> informationType, AgentIdentifier agentId)
			throws NoInformationAvailableException;

	public <Info extends Information> Info getMyInformation(Class<Info> informationType);

	public <Info extends Information> HashMap<AgentIdentifier, Info> getInformation(Class<Info> informationType) throws NoInformationAvailableException;
	
	public void add(Information information);

	public void remove(Information information);

	// public void beInformedOfContractExecution(Contract c);

	public interface Information extends Comparable<Information>, UtilitaristAnalyser<Information>, FunctionnalCompensativeAggregator<Information>  {

		public AgentIdentifier getMyAgentIdentifier();
		
		public long getUptime();
		
		public Long getCreationTime();
	}

	public String show(Class<? extends Information> infotype);

}
