package frameworks.faulttolerance.centralizedsolver;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.Replica;
import frameworks.faulttolerance.centralizedsolver.CentralizedCoordinator.StateUpdate;
import frameworks.faulttolerance.negotiatingagent.ReplicaCore;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.protocoles.InactiveCommunicationProtocole;
import frameworks.negotiation.protocoles.InactiveProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.rationality.RationalCore;
import frameworks.negotiation.selection.InactiveSelectionCore;

public class CentralizedReplica extends Replica{

	public CentralizedReplica(AgentIdentifier id, ReplicaState myState,boolean dynamicCriticity)
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
	public void updateMyState(StateUpdate m){
		logMonologue("state update",LogService.onBoth);
		setNewState(m.getNewState());
	}
}
