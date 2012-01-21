package negotiation.faulttolerance.candidaturenegotiation.mirrordestruction;

import negotiation.faulttolerance.ReplicationCandidature;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;

public class ReplicationDestructionCandidature extends
ReplicationCandidature {

	/**
	 *
	 */
	private static final long serialVersionUID = -8125870770061452392L;
	private final ReplicationCandidature minContract;


	public ReplicationDestructionCandidature(
			final ResourceIdentifier r,
			final AgentIdentifier a,
			final ReplicationCandidature minContract, final boolean agentCreator) {
		super(r, a, false, agentCreator);
		this.minContract = minContract;
	}


	public ReplicationCandidature getMinContract() {
		return this.minContract;
	}

	@Override
	public String toString(){
		return super.toString()+" in order to create : "+this.minContract.getAgentInitialState();
	}
}
