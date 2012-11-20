package frameworks.faulttolerance.centralizedsolver;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.Replica;
import frameworks.faulttolerance.centralizedsolver.CentralizedCoordinator.StateUpdate;
import frameworks.faulttolerance.negotiatingagent.ReplicaCore;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.protocoles.InactiveCommunicationProtocole;
import frameworks.negotiation.protocoles.InactiveProposerCore;
import frameworks.negotiation.selection.InactiveSelectionCore;

public class CentralizedReplica extends Replica{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5854849267426769347L;

	public CentralizedReplica(final AgentIdentifier id, final ReplicaState myState,final boolean dynamicCriticity)
			throws CompetenceException {
		super(id,
				myState,
				new ReplicaCore(false, false),
				new InactiveSelectionCore(),
				new InactiveProposerCore(),
				new SimpleObservationService(),
				new InactiveCommunicationProtocole(), dynamicCriticity);
	}

	@MessageHandler
	public void updateMyState(final StateUpdate m){
		this.logMonologue("state update",LogService.onBoth);
		this.setNewState(m.getNewState());
	}
}
