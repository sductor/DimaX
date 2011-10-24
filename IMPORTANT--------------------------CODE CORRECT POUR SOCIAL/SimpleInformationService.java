package negotiation.agentframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import negotiation.agentframework.SimpleStateService.AcquaintanceHandler;
import negotiation.agentframework.SimpleStateService.EnrichedState;
import negotiation.agentframework.SimpleStateService.InformationProtocol;
import negotiation.agentframework.SimpleStateService.StateUpdateMessage;
import negotiation.agentframework.informationservice.Information;
import negotiation.agentframework.informationservice.InformationService;
import negotiation.agentframework.informationservice.InformationService.MissingInformationException;
import negotiation.agentframework.rationalagent.AgentState;
import negotiation.agentframework.rationalagent.RationalAgent;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.BasicCommunicatingCompetence;
import dima.introspectionbasedagents.annotations.CompetenceProtocol;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.competences.CompetentAgent;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;

@CompetenceProtocol(InformationProtocol.class)
public class SimpleInformationService
<PersonalState extends AgentState,
InformedState extends AgentState>
extends
BasicCommunicatingCompetence<RationalAgent<PersonalState,?,?,InformedState>>
implements
InformationService<PersonalState, InformedState>{
	private static final long serialVersionUID = -6359058240187342173L;

	public class InformationProtocol extends Protocol{
		private static final long serialVersionUID = 8226160674483610375L;
		public InformationProtocol(final CompetentAgent com) {
			super(com);}
		public static final String infoExchange = "information";
		public static final String globalBeliefExchange = "globalBelief";

	}

	//
	// Fields
	//

	protected AcquaintanceHandler myAcquaintances;
	protected EnrichedState<PersonalState> myEnrichedState;

	//
	// Constructor
	//

	public SimpleStateService(
			final RationalAgent<PersonalState,?,?,InformedState> ag,
			final PersonalState neo) {
		super(ag);
		this.myAcquaintances = new AcquaintanceHandler();
		this.myEnrichedState = new EnrichedState<PersonalState>(neo);
	}

	//
	// Accessors
	//

	protected AcquaintanceHandler getMyAcquaintancesHandler() {
		return this.myAcquaintances;
	}


	@Override
	public PersonalState getMyCurrentState(){
		return this.myEnrichedState.getMyState();
	}

	@Override
	public void setNewState(final PersonalState s) {
		this.myEnrichedState.update(s);
	}

	//
	// Methods
	//

	@Override
	public Set<AgentIdentifier> getKnownAgents() {
		return  getMyAcquaintancesHandler().getKnownAgents();
	}

	@Override
	public void addKnownAgents(final Collection<? extends AgentIdentifier> agents){
		getMyAcquaintancesHandler().getKnownAgents().addAll(agents);
	}

	@Override
	public boolean iKnowThisAgentInfomation(final AgentIdentifier id) {
		return  getMyAcquaintancesHandler().iKnowThisState(id);
	}

	@Override
	public InformedState getAgentState(final AgentIdentifier id)
	throws MissingInformationException {
		if ( getMyAcquaintancesHandler().getAgentState(id)==null){
			final MissingInformationException excep = new MissingInformationException();
			excep.addMissingAgent(id);
			throw excep;
		} else
			return getMyAcquaintancesHandler().getAgentState(id).getMyState();
	}

	@Override
	public Collection<InformedState> getAgentState(
			final Collection<AgentIdentifier> ids)
			throws MissingInformationException {
		final Collection<InformedState> result = new ArrayList<InformedState>();
		final MissingInformationException excep = new MissingInformationException();
		boolean error = false;
		for (final AgentIdentifier id : ids)
			if ( getMyAcquaintancesHandler().getAgentState(id)==null){
				error = true;
				excep.addMissingAgent(id);
			} else
				result.add( getMyAcquaintancesHandler().getAgentState(id).getMyState());
		if (error)
			throw excep;
		else
			return result;
	}

	/*
	 * 
	 */

	@Override
	public Boolean hasInformation(final MissingInformationException e) {
		for (final AgentIdentifier id : e.getMissingAgents())
			if (!iKnowThisAgentInfomation(id))
				return false;
		return true;
	}

	@Override
	public void obtainInformation(final MissingInformationException e) {
		for (final AgentIdentifier id : e.getMissingAgents())
			askInformation(id);
	}

	protected void askInformation(final AgentIdentifier id) {
		final FipaACLMessage m = new FipaACLMessage(
				Performative.QueryRef,
				InformationProtocol.infoExchange,
				InformationProtocol.class);
		this.sendMessage(id, m);
	}

	//
	// Behavior
	//

	@MessageHandler
	@FipaACLEnvelope(
			performative=Performative.QueryRef,
			content=InformationProtocol.infoExchange,
			protocol=InformationProtocol.class)
			public void sendInformation(final FipaACLMessage m){
		getMyAcquaintancesHandler().addAcquaintance(m.getReplyTo());
		sendMyState(m.getReplyTo());
	}


	@MessageHandler
	@FipaACLEnvelope(
			performative=Performative.Inform,
			content=InformationProtocol.infoExchange,
			protocol=InformationProtocol.class)
			public void receiveInformation(
					final StateUpdateMessage<EnrichedState<InformedState>> m){
		getMyAcquaintancesHandler().addInformation(m.getState());
	}

	//
	// Primitives
	//

	protected boolean isStillValid(final EnrichedState<InformedState> id) {
		return true;
	}
	

	protected void sendMyState(final AgentIdentifier id) {
		this.sendMessage(
				id,
				new StateUpdateMessage<EnrichedState<PersonalState>>(
						Performative.Inform,
						InformationProtocol.infoExchange,
						this.myEnrichedState));
	}


	//
	// Subclasses
	//

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	public class EnrichedState<State extends AgentState> implements Information, AgentState{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6659167432850630911L;
		private State myState;
		private long infoDynamicity=Long.MAX_VALUE;


		public EnrichedState(final State myState) {
			super();
			this.myState = myState;
		}

		public State getMyState() {
			return this.myState;
		}

		public double getLastInfoDynamicity(){
			return this.infoDynamicity;
		}


		public void update(final State s){
			this.infoDynamicity=s.getUptime()-this.myState.getUptime();
			this.myState = s;
		}

		@Override
		public AgentIdentifier getMyAgentIdentifier() {
			return this.myState.getMyAgentIdentifier();
		}

		@Override
		public boolean equals(final Object o){
			return getMyAgentIdentifier().equals(o);
		}

		public int hashcode(){
			return getMyAgentIdentifier().hashCode();
		}

		@Override
		public long getUptime() {
			return this.myState.getUptime();
		}
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	protected class AcquaintanceHandler implements DimaComponentInterface {
		private static final long serialVersionUID = -242055400437558760L;

		protected final Map<AgentIdentifier, EnrichedState<InformedState>> information =
			new HashMap<AgentIdentifier, EnrichedState<InformedState>>();

		private final Set<AgentIdentifier> knownAgents =
			new HashSet<AgentIdentifier>();

		protected void addAcquaintance(final AgentIdentifier id){
			this.knownAgents.add(id);
		}

		protected void update(final AgentIdentifier id){
			if (this.information.containsKey(id) &&
					SimpleStateService.this.isStillValid(this.information.get(id)))
				removeInformation(id);
		}

		protected void addInformation(final EnrichedState<InformedState> info){
			addAcquaintance(info.getMyAgentIdentifier());
			this.information.put(info.getMyAgentIdentifier(), info);
		}

		protected EnrichedState<InformedState> removeInformation(final AgentIdentifier id){
			return this.information.remove(id);
		}

		public boolean iKnowThisState(final AgentIdentifier id){
			this.update(id);
			return this.information.containsKey(id);
		}

		public EnrichedState<InformedState> getAgentState(final AgentIdentifier id){
			this.update(id);
			if (this.information.containsKey(id))
				return this.information.get(id);
			else
				return null;
		}

		public Set<AgentIdentifier> getKnownAgents(){
			return this.knownAgents;
		}
	}

	/*
	 *
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	protected class StateUpdateMessage<State extends AgentState> extends FipaACLMessage{
		private static final long serialVersionUID = 1L;

		State state;

		public StateUpdateMessage(
				final Performative performative,
				final String content,
				final State state) {
			super(performative, content, InformationProtocol.class);
			this.state = state;
		}

		public State getState() {
			return this.state;
		}
	}


}
