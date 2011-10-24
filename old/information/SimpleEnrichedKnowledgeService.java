package negotiation.negotiationframework.information;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import negotiation.negotiationframework.agent.AgentState;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;

public class SimpleEnrichedKnowledgeService<PersonalState extends AgentState>  implements KnowledgeService<PersonalState>{

	//
	// Fields
	//

	protected EnrichedInformationHandler myAcquaintances;
	protected EnrichedState<PersonalState> myEnrichedState;

	//
	// Constructor
	//

	public SimpleEnrichedKnowledgeService(
			final PersonalState neo) {
		this.myAcquaintances = new EnrichedInformationHandler();
		this.myEnrichedState = new EnrichedState<PersonalState>(neo);
	}

	//
	// Accessors
	//

	@Override
	public PersonalState getMyCurrentState(){
		return this.myEnrichedState.getMyState();
	}

	@Override
	public void setNewState(final PersonalState s) {
		this.myEnrichedState.update(s);
	}

	//
	// Accessors
	//

	@Override
	public Collection<AgentIdentifier> getKnownAgents() {
		return  this.myAcquaintances.getKnownAgents();
	}

	@Override
	public void addAll(final Collection<? extends AgentIdentifier> agents){
		this.myAcquaintances.getKnownAgents().addAll(agents);
	}

	
	public void add(AgentIdentifier agentId) {
		myAcquaintances.add(agentId);
	}

	public void remove(AgentIdentifier agentId) {
		myAcquaintances.remove(agentId);
	}

	public <Info extends Information> Info get(Class<Info> informationType,
			AgentIdentifier agentId) {
		return myAcquaintances.get(informationType, agentId);
	}

	public <Info extends Information> void add(Info information) {
		myAcquaintances.add(information);
	}

	public <Info extends Information> void remove(Info information) {
		myAcquaintances.remove(information);
	}














	//
	// Subclasses
	//
		
	protected class EnrichedInformationHandler extends SimpleInformationService {
		private static final long serialVersionUID = -242055400437558760L;

		//		protected final Map<AgentIdentifier, EnrichedState<InformedState>> information =
		//			new HashMap<AgentIdentifier, EnrichedState<InformedState>>();


		//		protected void update(final AgentIdentifier id){
		//			if (this.information.containsKey(id) &&
		//					SimpleStateService.this.isStillValid(this.information.get(id)))
		//				removeInformation(id);
		//		}
		//
		//		protected void addInformation(final EnrichedState<InformedState> info){
		//			addAcquaintance(info.getMyAgentIdentifier());
		//			this.information.put(info.getMyAgentIdentifier(), info);
		//		}
		//
		//		protected EnrichedState<InformedState> removeInformation(final AgentIdentifier id){
		//			return this.information.remove(id);
		//		}

		//		public boolean iKnowThisState(final AgentIdentifier id){
		//			this.update(id);
		//			return this.information.containsKey(id);
		//		}
		//
		//		public EnrichedState<InformedState> getAgentState(final AgentIdentifier id){
		//			this.update(id);
		//			if (this.information.containsKey(id))
		//				return this.information.get(id);
		//			else
		//				return null;
		//		}



	}
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
			if (this.myState!=null)
				this.infoDynamicity=s.getUptime()-this.myState.getUptime();
			this.myState = s;
		}

		@Override
		public AgentIdentifier getMyAgentIdentifier() {
			return this.myState.getMyAgentIdentifier();
		}

		@Override
		public boolean equals(final Object o){
			return this.getMyAgentIdentifier().equals(o);
		}

		public int hashcode(){
			return this.getMyAgentIdentifier().hashCode();
		}

		@Override
		public long getUptime() {
			return this.myState.getUptime();
		}
		@Override
		public void resetUptime() {
			this.myState.resetUptime();
		}

		@Override
		public boolean setLost(final ResourceIdentifier h, final boolean isLost) {
			return this.myState.setLost(h, isLost);
		}

	}
}
