package negotiation.faulttolerance.experimentation;

import java.io.IOException;
import java.util.Collection;

import org.jdom.JDOMException;

import negotiation.faulttolerance.candidaturewithstatus.ObservingStatusService;
import negotiation.faulttolerance.faulsimulation.FaultTriggeringService;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.Experimentator;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin;
import dimaxx.experimentation.ObservingGlobalService;

public class ReplicationLaborantin extends Laborantin {

	public ReplicationLaborantin(ReplicationExperimentationParameters p,
			APILauncherModule api) throws CompetenceException,
			IfailedException, NotEnoughMachinesException {
		super(p, new ReplicationObservingGlobalService(), api);
	}

	//
	// Competences
	// ///////////////////////////////////////////


	@Competence
	protected FaultTriggeringService myFaultService = 
	new FaultTriggeringService(getSimulationParameters());

	@Competence
	protected ObservingStatusService myStatusObserver = 
	new ObservingStatusService(this, getSimulationParameters());

	//
	// Accessors
	// ///////////////////////////////////////////

	public ReplicationExperimentationParameters getSimulationParameters() {
		return (ReplicationExperimentationParameters) super.getSimulationParameters();
	}

	public ReplicationObservingGlobalService getObservingService() {
		return (ReplicationObservingGlobalService) super.getObservingService();
	}

	public SimpleRationalAgent getAgent(final AgentIdentifier id){
		return (SimpleRationalAgent) this.agents.get(id);
	}
	

	//
	// Main
	//
	
	public static void main(final String[] args) 
			throws CompetenceException, IllegalArgumentException, IllegalAccessException, JDOMException, IOException{
		final Experimentator exp = new Experimentator(ReplicationExperimentationParameters.getDefaultParameters());
		exp.run(args);
	}
}
