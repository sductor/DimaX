package negotiation.interactionprotocols;

import java.util.Collection;
import java.util.Date;

import negotiation.agentframework.ActionSpecification;
import negotiation.agentframework.ContractIdentifier;
import negotiation.agentframework.ContractTransition;
import tools.datastructures.HashedHashSet;
import dima.basicagentcomponents.AgentIdentifier;

public abstract class SimpleContract
<ActionSpec extends ActionSpecification>
implements ContractTransition<ActionSpec>{
	private static final long serialVersionUID = -7195071407055918012L;


	private final ContractIdentifier id;

	private final HashedHashSet<AgentIdentifier, ActionIdentifier> actions =
		new HashedHashSet<AgentIdentifier, ActionIdentifier>();

	private final Date creationTime;
	private final long validityTime;

	private SimpleContract(
			final AgentIdentifier initiator, 
			final Date creationtime, final long validityTime, 
			final Collection<AgentIdentifier> participant) {
		super();
		this.id = new ContractIdentifier(initiator, this.creationTime, this.validityTime, participant);
		this.validityTime=validityTime;
		creationTime = new Date();
	}
	
	public SimpleContract(final ContractIdentifier id) {
		this(id.getInitiator(), id.get);
		this.id = id;
		this.validityTime=validityTime;
	}

	public SimpleContract(final AgentIdentifier initiator, final long validityTime, final Collection<AgentIdentifier> participant) {
		super();
		this.id = new ContractIdentifier(initiator, this.creationTime, this.validityTime, participant);
		this.validityTime=validityTime;
	}

	/*
	 *
	 */

	@Override
	public  ContractIdentifier getIdentifier() {
		return this.id;
	}

	@Override
	public Collection<AgentIdentifier> getParticipants(){
		return this.id.getParticipants();
	}

	@Override
	public AgentIdentifier getInitiator() {
		return this.id.getInitiator();
	}

	/*
	 *
	 */

	public Collection<ActionIdentifier> getAssociatedActions(final AgentIdentifier id) {
		return this.actions.get(id);
	}

	/*
	 *
	 */

	protected boolean addAction(final ActionIdentifier n) {
		boolean test = true;
		for (final AgentIdentifier id : n.getActingAgents())
			test = this.actions.add(id, n);
		return test;
	}

	protected boolean removeAction(final ActionIdentifier a) {
		boolean test = false;
		for (final AgentIdentifier ag : this.getParticipants())
			test = this.actions.remove(ag, a);
		return test;
	}

	
	/*
	 * 
	 */
	

	public SimpleContract<ActionSpec> clone(){
		return new SimpleContract<ActionSpec>(id.getInitiator(),validityTime,);
	}

	@Override
	public boolean equals(final Object o){
		if (o instanceof SimpleCandidature) {
			@SuppressWarnings("unchecked")
			final SimpleCandidature<ActionSpec> that = (SimpleCandidature<ActionSpec>) o;
			return that.getIdentifier().equals(this.getIdentifier());
		}
		return false;
	}

	@Override
	public int hashCode(){
		return this.getIdentifier().hashCode();
	}
	//
	// Subclass
	//

	/*
	 *
	 *
	 *
	 *
	 *
	 */


	final class ActionIdentifier {

		private final String actionName;
		private final ContractIdentifier contract;
		private final Collection<AgentIdentifier> actingAgents;
		private final ActionSpec actionSpec;

		public ActionIdentifier(
				final String actionName,
				final Collection<AgentIdentifier> actingAgents,
				final ActionSpec actionSpec,
				final ContractIdentifier contract) {
			super();
			this.actionName = actionName;
			this.actingAgents = actingAgents;
			this.contract = contract;
			this.actionSpec = actionSpec;
		}


		public String getActionName() {
			return this.actionName;
		}

		public Collection<AgentIdentifier> getActingAgents() {
			return this.actingAgents;
		}

		public ActionSpec getSpecif(){
			return this.actionSpec;
		}


		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(final Object o){
			try {
				return ((ActionIdentifier) o).actionName.equals(this.actionName) &&
				((ActionIdentifier) o).contract.equals(this.contract);
			} catch (final Exception e) {
				return false;
			}
		}
	}

	
}
