package frameworks.faulttolerance.centralizedsolver;

import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.PreStepComposant;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.faulttolerance.Host;
import frameworks.faulttolerance.centralizedsolver.CentralizedCoordinator.StateUpdate;
import frameworks.faulttolerance.negotiatingagent.HostCore;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.solver.JMetalElitistES;
import frameworks.faulttolerance.solver.JMetalSolver;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.protocoles.InactiveCommunicationProtocole;
import frameworks.negotiation.protocoles.InactiveProposerCore;
import frameworks.negotiation.selection.InactiveSelectionCore;

public class CentralizedHost extends Host{

	/**
	 * 
	 */
	private static final long serialVersionUID = -698107071186904302L;
	JMetalElitistES solver;

	public CentralizedHost(
			final ResourceIdentifier id, final HostState myState,final JMetalSolver p,
			Double collectiveSeed)
					throws CompetenceException {
		super(
				id,
				myState,
				new HostCore(null, false, false),
				new InactiveSelectionCore(),
				new InactiveProposerCore(),
				new SimpleObservationService(),
				new InactiveCommunicationProtocole(),
				collectiveSeed);
		this.solver = new JMetalElitistES(p.getProblem());
		this.solver.getProblem().setMaxGeneration(1);
		this.solver.getProblem().setStagnationCounter(Integer.MAX_VALUE);
	}

	@PreStepComposant@Transient
	public boolean intitialize(){
		this.solver.initialize();
		return true;
	}

	@StepComposant
	public void solve(){
		this.solver.getProblem().setTimeLimit((int) (ExperimentationParameters._maxSimulationTime-this.getUptime()));
		this.solver.run();
	}

	@MessageHandler
	public void updateMyState(final StateUpdate m){
		this.logMonologue("state update",LogService.onBoth);
		this.setNewState((HostState) m.getNewState());
	}
}
