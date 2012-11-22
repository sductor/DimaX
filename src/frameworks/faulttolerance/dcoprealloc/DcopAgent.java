package frameworks.faulttolerance.dcoprealloc;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import frameworks.faulttolerance.Replica;
import frameworks.faulttolerance.negotiatingagent.ReplicaCore;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.protocoles.InactiveProposerCore;
import frameworks.negotiation.protocoles.dcopProtocol.DcopAgentProtocol;
import frameworks.negotiation.protocoles.dcopProtocol.DcopAgentSelectionCore;

public class DcopAgent extends Replica<ReplicationCandidature>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9001586558561122701L;

	public DcopAgent(
			final AgentIdentifier id,
			final ReplicaState myState,
			final int k,
			final boolean dynamicCriticity,
			Double collectiveSeed)
					throws CompetenceException {
		super(id, myState,new ReplicaCore(false,false),
				new DcopAgentSelectionCore(),
				new InactiveProposerCore(),
				new SimpleObservationService(),
				new DcopAgentProtocol(k),
				dynamicCriticity,collectiveSeed);
	}
}
