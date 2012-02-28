package negotiation.negotiationframework.protocoles.status;

import java.util.Collection;
import java.util.Date;

import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.negotiationframework.contracts.ContractIdentifier;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;

public class DestructionOrder extends ReplicationCandidature {

	private static final long serialVersionUID = -8818199856795063080L;

	public class DestructionOrderIdentifier extends ContractIdentifier {

		/**
		 *
		 */
		private static final long serialVersionUID = 7345192467418934461L;

		public DestructionOrderIdentifier(final AgentIdentifier intiator, final Date date,
				final long validityTime, final AgentIdentifier... participants) {
			super(intiator, date, validityTime, participants);
		}

		public DestructionOrderIdentifier(final AgentIdentifier intiator, final Date date,
				final long validityTime, final Collection<AgentIdentifier> participants) {
			super(intiator, date, validityTime, participants);
		}

	}

	public DestructionOrder(final ResourceIdentifier r, final AgentIdentifier a, final boolean isAgentInit) {
		super(r, a, false, isAgentInit);
	}

	@Override
	public ContractIdentifier getIdentifier() {
		return new DestructionOrderIdentifier(this.creator, this.creationTime,
				this.validityTime, this.actors);
	}

	@Override
	public AgentIdentifier getInitiator() {
		return this.getAgent();
	}

	// @Override
	// public Collection<AgentIdentifier> getNotInitiatingParticipants() {
	// return Arrays.asList(new AgentIdentifier[]{this.getResource()});
	// }

	@Override
	public String toString() {
		return this.getIdentifier() + " -> (DESTRUCTION ORDER!!)";
		// +",consensual?"+isConsensual()+"("+agentHasAccepted+","+resourceHasAccepted+")";
		// +"\n  * agent specif : "+(actionSpec.getActionAgent()!=null)
		// +"\n  * host specif : "+(actionSpec.getActionHost()!=null)
	}
}
