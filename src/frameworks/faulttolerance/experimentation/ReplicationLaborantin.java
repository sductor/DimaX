package frameworks.faulttolerance.experimentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;



import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.kernel.LaunchableCompetentComponent;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.launch.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.experimentation.Experimentator;
import frameworks.experimentation.IfailedException;
import frameworks.experimentation.Laborantin;
import frameworks.faulttolerance.Replica;
import frameworks.faulttolerance.faulsimulation.FaultTriggeringService;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicaStateOpinionHandler;
import frameworks.negotiation.NegotiationParameters;
import frameworks.negotiation.opinion.Believer;
import frameworks.negotiation.opinion.OpinionService;
import frameworks.negotiation.opinion.SimpleOpinionService;
import frameworks.negotiation.protocoles.status.CentralisedStatusCompterCompetence;
import frameworks.negotiation.rationality.RationalAgent;
import frameworks.negotiation.rationality.SimpleRationalAgent;

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

	public static Integer informativeParameter;
	
	public static void main(final String[] args)
			throws CompetenceException, IllegalArgumentException, IllegalAccessException, JDOMException, IOException, IfailedException, NotEnoughMachinesException{
		//		System.out.println(1<<10000);
		ReplicationExperimentationParameters.maxHostAccessibleParAgent=30;
		
		
		informativeParameter = 0;//new Integer(args[2]);
		Experimentator exp =
				new Experimentator(
						new ReplicationExperimentationGenerator().getDefaultParameters(),
						new FinalExperimentsLogger(),
						new ArrayList(Arrays.asList(new ReplicationExperimentationGenerator().getSeeds())));
		exp.run(args);		
//		informativeParameter = 1;//new Integer(args[2]);
//		exp =
//				new Experimentator(
//						new ReplicationExperimentationGenerator().getDefaultParameters(),
//						new FinalExperimentsLogger(),
//						Arrays.asList(new ReplicationExperimentationGenerator().getSeeds()));
//		exp.run(args);
	}


}
