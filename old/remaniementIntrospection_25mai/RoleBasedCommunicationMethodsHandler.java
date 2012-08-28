package dima.introspectionbasedagents.kernel.competences;

import java.util.Date;
import java.util.HashMap;

import dima.basiccommunicationcomponents.AbstractMessage;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.kernel.shells.BasicCommunicatingMethodTrunk;
import dima.introspectionbasedagents.kernel.shells.CommunicationMethodsTrunk;
import dima.introspectionbasedagents.ontologies.Envelope;

public class RoleBasedCommunicationMethodsHandler extends HashMap<Envelope, CommunicationMethodsTrunk> implements DimaComponentInterface {

	/**
	 *
	 */
	private static final long serialVersionUID = -5326452456286798811L;

	public RoleBasedCommunicationMethodsHandler(final AgentCompetence competence, final Date horloge){
		super();
		final CommunicationMethodsTrunk agentComMethods = new BasicCommunicatingMethodTrunk(competence, horloge);
		agentComMethods.init();
		for (final Envelope e : agentComMethods.getHandledEnvellope())
			this.put(e, agentComMethods);
	}

	public void add(final AgentCompetence competence, final Date horloge) throws DuplicateCompetenceException  {

		final CommunicationMethodsTrunk agentComMethods = new BasicCommunicatingMethodTrunk(competence, horloge);
		agentComMethods.init();
		for (final Envelope e : agentComMethods.getHandledEnvellope())
			if (this.put(e, agentComMethods) !=null) throw new DuplicateCompetenceException();

	}

	public CommunicationMethodsTrunk getAgentOf(final AbstractMessage mess) {
		return this.get(BasicCommunicatingMethodTrunk.getEnvellopeOfMessage(mess));
	}


}
