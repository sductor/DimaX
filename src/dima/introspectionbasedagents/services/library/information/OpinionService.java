package dima.introspectionbasedagents.services.library.information;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;
import dimaxx.tools.aggregator.AbstractMinMaxAggregation;

public interface OpinionService
extends ObservationService{

	public <Info extends Information> Opinion<Info> getOpinion(
			Class<Info> informationType, AgentIdentifier agentId) throws NoInformationAvailableException;

	public <Info extends Information> Opinion<Info> getGlobalOpinion(
			Class<Info> myInfoType) throws NoInformationAvailableException;
	// public <Info extends Information> void collectInformation(Class<Info>
	// informationType);
	//
	// public <Info extends Information> void receiveInformation(Info o);
	//
	// public <Info extends Information> void collectOpinion(Class<Info>
	// informationType);
	//
	// public <Info extends Information> void receiveOpinion(Opinion<Info> o);



	/*
	 *
	 */

	public interface Opinion<Info extends Information> extends Information, AbstractCompensativeAggregation<Info>, AbstractMinMaxAggregation<Info> {

		public AgentIdentifier getCreator();

		public Collection<AgentIdentifier> getAggregatedAgents();

		public boolean isCertain();

		/*
		 *
		 */

		/**
		 * @return represent the heterogeneity of collected agent state
		 */
		public Double getOpinionDispersion();

		/*
		 *
		 */

		/**
		 * @return the minimum time an agent has changed its state
		 */
		public Long getMinInformationDynamicity();

		/**
		 * @return the maximum time an agent has changed its state
		 */
		public Long getMaxInformationDynamicity();

	}
}

//
// public interface OpinionService
// extends InformationService{
//
// public <Info extends Information> Info getBelief(Class<Info> informationType,
// AgentIdentifier id)
// throws NoInformationAvailableException;
//
// public <Info extends Information> Float getBeliefConfidence(Info information)
// throws NoInformationAvailableException;
//
// public <Info extends Information> Info getMyOpinion(Class<Info>
// informationType)
// throws NoInformationAvailableException ;
//
// public <Info extends Information> void collectInformation(Class<Info>
// informationType);
//
// }