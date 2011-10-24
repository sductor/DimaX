package negotiation.agentframework.informationservice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import negotiation.agentframework.rationalagent.AgentState;
import negotiation.ressourcenegotiation.NegotiationException;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.CompetenceProtocol;
import dima.introspectionbasedagents.competences.CompetentAgent;
import dima.introspectionbasedagents.ontologies.Protocol;
import negotiation.agentframework.informationservice.InformationService.InformationProtocol;

@CompetenceProtocol(InformationProtocol.class)
public interface InformationService
<PersonalState extends AgentState,
InformedState extends Information>
extends StateService<PersonalState>{

	public class InformationProtocol extends Protocol{
		private static final long serialVersionUID = 8226160674483610375L;
		public InformationProtocol(final CompetentAgent com) {
			super(com);}
		public static final String infoExchange = "information";
		public static final String globalBeliefExchange = "globalBelief";

	}


	public boolean iKnowThisAgentInfomation(AgentIdentifier id);

	public InformedState getAgentState(AgentIdentifier id) throws MissingInformationException;

	public Collection<InformedState> getAgentState(Collection<AgentIdentifier> id) throws MissingInformationException;


	/*
	 * 
	 */

	public Boolean hasInformation(MissingInformationException e);

	public void obtainInformation(MissingInformationException ids);



	//
	// EXCEPTIONS
	//


	/**
	 * This exception is raised when no information can be used to build a belief
	 */
	public class NoInformationAvailableException extends NegotiationException {
		private static final long serialVersionUID = -3462896169714528630L;
	}

	/**
	 * This exception is raised when some information are not exact.
	 * This class allow to compute a quality of the approximation
	 * and to aggregate this value with the computed evaluation gain.
	 */
	public class MissingInformationException extends NegotiationException {

		private static final long serialVersionUID = -3709020842395073948L;

		//
		// Fields
		//

		Collection<AgentIdentifier> missingAgents =
			new ArrayList<AgentIdentifier>();
		//
		// Constructor
		//

		public MissingInformationException(final AgentIdentifier... agents) {
			for (final AgentIdentifier ag : agents)
				this.missingAgents.add(ag);
		}

		public MissingInformationException(final Collection<AgentIdentifier> ag) {
			this.missingAgents = ag;
		}

		//
		// Accessors
		//


		public Collection<AgentIdentifier> getMissingAgents() {
			return this.missingAgents;
		}

		public void addMissingAgent(final AgentIdentifier id) {
			this.missingAgents.add(id);
		}
	}

}