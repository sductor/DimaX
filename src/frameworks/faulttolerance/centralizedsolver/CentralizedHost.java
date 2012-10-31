package frameworks.faulttolerance.centralizedsolver;

import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.Host;
import frameworks.faulttolerance.centralizedsolver.CentralizedCoordinator.StateUpdate;
import frameworks.faulttolerance.negotiatingagent.HostCore;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.solver.JMetalElitistES;
import frameworks.faulttolerance.solver.JMetalRessAllocProblem;
import frameworks.faulttolerance.solver.JMetalSolver;
import frameworks.faulttolerance.solver.RessourceAllocationProblem;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.protocoles.InactiveCommunicationProtocole;
import frameworks.negotiation.protocoles.InactiveProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.rationality.RationalCore;
import frameworks.negotiation.selection.InactiveSelectionCore;

public class CentralizedHost extends Host{

	JMetalElitistES solver;

	public CentralizedHost(
			ResourceIdentifier id, HostState myState,JMetalSolver p) 
					throws CompetenceException {
		super(
				id, 
				myState, 
				new HostCore(null, false, false),
				new InactiveSelectionCore(),
				new InactiveProposerCore(),
				new SimpleObservationService(),
				new InactiveCommunicationProtocole());
		solver = new JMetalElitistES(p.getProblem());
	}

	@StepComposant
	public void solve(){
			solver.initialize();
			solver.run();
	}

	@MessageHandler
	public void updateMyState(StateUpdate m){
		logMonologue("state update",LogService.onBoth);
		setNewState((HostState) m.getNewState());
	}
}
