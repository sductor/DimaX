package frameworks.negotiation.faulttolerance.experimentation;

import java.io.IOException;



import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.kernel.LaunchableCompetentComponent;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.launch.APIAgent.APILauncherModule;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.experimentation.Experimentator;
import frameworks.experimentation.IfailedException;
import frameworks.experimentation.Laborantin;
import frameworks.negotiation.faulttolerance.Replica;
import frameworks.negotiation.faulttolerance.faulsimulation.FaultTriggeringService;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicaStateOpinionHandler;
import frameworks.negotiation.negotiationframework.NegotiationParameters;
import frameworks.negotiation.negotiationframework.opinion.Believer;
import frameworks.negotiation.negotiationframework.opinion.OpinionService;
import frameworks.negotiation.negotiationframework.opinion.SimpleOpinionService;
import frameworks.negotiation.negotiationframework.protocoles.status.CentralisedStatusCompterCompetence;
import frameworks.negotiation.negotiationframework.rationality.RationalAgent;
import frameworks.negotiation.negotiationframework.rationality.SimpleRationalAgent;

public class ReplicationLaborantin extends Laborantin implements Believer {

	/**
	 *
	 */
	private static final long serialVersionUID = 2531929413258878919L;

	public ReplicationLaborantin(final ReplicationExperimentationParameters p,
			final APILauncherModule api) throws CompetenceException,
			IfailedException, NotEnoughMachinesException {
		super(p, new ReplicationObservingGlobalService(p), api);
		myInformationService =  new SimpleOpinionService(new ReplicaStateOpinionHandler(p._socialWelfare, this.getIdentifier()));
		if (p._usedProtocol.equals(NegotiationParameters.key4CentralisedstatusProto)){
			cscc.setMyAgent(this);
			cscc.setActive(true);
			for (LaunchableCompetentComponent ag : getAgents()){
				if (ag instanceof Replica)
					cscc.addAcquaintance(ag.getIdentifier());
			}
		} else {
			cscc.setActive(false);
		}
	}

	//
	// Competences
	// ///////////////////////////////////////////

	@Competence
	public final FaultTriggeringService myFaultService =
	new FaultTriggeringService(this.getSimulationParameters());

	//	@Competence
	//	protected final CentralisedObservingStatusService myStatusObserver =
	//	new CentralisedObservingStatusService(this, this.getSimulationParameters());

	@Competence
	public final CentralisedStatusCompterCompetence cscc = new CentralisedStatusCompterCompetence(ReplicaState.class, ExperimentationParameters._maxSimulationTime);


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

	@Override
	public OpinionService getMyOpinion() {
		return (OpinionService) myInformationService;
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
