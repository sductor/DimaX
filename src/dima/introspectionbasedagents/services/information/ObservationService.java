package dima.introspectionbasedagents.services.information;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.tools.aggregator.FunctionnalCompensativeAggregator;
import dimaxx.tools.aggregator.UtilitaristAnalyser;

public interface ObservationService extends AgentCompetence<BasicCompetentAgent> {

	//
	// Acquaintance
	//

	public Set<? extends AgentIdentifier> getKnownAgents();

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

	public <Info extends Information> boolean hasMyInformation(Class<Info> informationType);

	public <Info extends Information> boolean hasInformation(Class<Info> informationType);

	public <Info extends Information> boolean hasInformation(Class<Info> informationType, AgentIdentifier agentId);

	public <Info extends Information> HashMap<AgentIdentifier, Info> getInformation(Class<Info> informationType) throws NoInformationAvailableException;

	public void add(Information information);

	public void remove(Information information);

	// public void beInformedOfContractExecution(Contract c);

	public interface Information
	extends //Comparable<K>,
	UtilitaristAnalyser<Information>, FunctionnalCompensativeAggregator<Information>  {

		public AgentIdentifier getMyAgentIdentifier();

		public long getUptime();

		public Long getCreationTime();

		int isNewerThan(Information that);
	}

	public String show(Class<? extends Information> infotype);

}
