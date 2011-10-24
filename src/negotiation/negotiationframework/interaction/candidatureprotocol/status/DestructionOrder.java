package negotiation.negotiationframework.interaction.candidatureprotocol.status;

import java.util.Collection;
import java.util.Date;

import negotiation.faulttolerance.ReplicationCandidature;
import negotiation.negotiationframework.interaction.ContractIdentifier;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;

public class DestructionOrder extends ReplicationCandidature {

	private static final long serialVersionUID = -8818199856795063080L;
	
	public class DestructionOrderIdentifier extends ContractIdentifier {

		public DestructionOrderIdentifier(AgentIdentifier intiator, Date date,
				long validityTime, AgentIdentifier... participants) {
			super(intiator, date, validityTime, participants);
		}

		public DestructionOrderIdentifier(AgentIdentifier intiator, Date date,
				long validityTime, Collection<AgentIdentifier> participants) {
			super(intiator, date, validityTime, participants);
		}
		
	}

	public DestructionOrder(final ResourceIdentifier r, final AgentIdentifier a, boolean isAgentInit) {
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
