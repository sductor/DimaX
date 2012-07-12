package negotiation.faulttolerance.experimentation;

import java.io.IOException;

import negotiation.faulttolerance.faulsimulation.FaultTriggeringService;
import negotiation.negotiationframework.rationality.RationalAgent;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;

import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.launch.APIAgent.APILauncherModule;
import dimaxx.experimentation.Experimentator;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin;

public class ReplicationLaborantin extends Laborantin {

	/**
	 *
	 */
	private static final long serialVersionUID = 2531929413258878919L;

	public ReplicationLaborantin(final ReplicationExperimentationParameters p,
			final APILauncherModule api) throws CompetenceException,
			IfailedException, NotEnoughMachinesException {
		super(p, new ReplicationObservingGlobalService(p), api);
	}

	//
	// Competences
	// ///////////////////////////////////////////

	@Competence
	protected final FaultTriggeringService myFaultService =
	new FaultTriggeringService(this.getSimulationParameters());

	//	@Competence
	//	protected final CentralisedObservingStatusService myStatusObserver =
	//	new CentralisedObservingStatusService(this, this.getSimulationParameters());

	//
	// Accessors
	// ///////////////////////////////////////////

	@Override
	public ReplicationExperimentationParameters getSimulationParameters() {
		return (ReplicationExperimentationParameters) super.getSimulationParameters();
	}

	@Override
	public ReplicationObservingGlobalService getObservingService() {
		return (ReplicationObservingGlobalService) super.getObservingService();
	}

	@Override
	public RationalAgent getAgent(final AgentIdentifier id){
		return (RationalAgent) this.agents.get(id);
	}


	//
	// Main
	//

	public static void main(final String[] args)
			throws CompetenceException, IllegalArgumentException, IllegalAccessException, JDOMException, IOException, IfailedException, NotEnoughMachinesException{
		//		System.out.println(1<<10000);

		final Experimentator exp =
				new Experimentator(
						ReplicationExperimentationParameters.getDefaultParameters(),
						new FinalExperimentsLogger(),
						ReplicationExperimentationParameters.iterationNumber);
		exp.run(args);
	}
}
