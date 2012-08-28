package negotiation.negotiationframework.protocoles.collaborativeold;

import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;

public class ReplicationDestructionCandidature extends
ReplicationCandidature implements DestructionCandidature<ReplicationCandidature, ReplicationSpecification>{

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
		return super.toString()+"\n IN ORDER TO CREATE: "+this.minContract.getAgentInitialState();
	}
}
