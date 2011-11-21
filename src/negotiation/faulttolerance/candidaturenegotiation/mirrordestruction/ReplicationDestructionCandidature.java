package negotiation.faulttolerance.candidaturenegotiation.mirrordestruction;

import dima.basicagentcomponents.AgentIdentifier;

import negotiation.faulttolerance.ReplicationCandidature;
import negotiation.negotiationframework.interaction.ResourceIdentifier;

public class ReplicationDestructionCandidature extends
ReplicationCandidature {

	private final ReplicationCandidature minContract;
	

	public ReplicationDestructionCandidature(
			ResourceIdentifier r,
			AgentIdentifier a, 
			ReplicationCandidature minContract, boolean agentCreator) {
		super(r, a, false, agentCreator);
		this.minContract = minContract;
	}


	public ReplicationCandidature getMinContract() {
		return minContract;
	}
	
	public String toString(){
		return super.toString()+" in order to create : "+minContract.getAgentInitialState();
	}
}
