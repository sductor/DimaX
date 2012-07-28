package frameworks.negotiation.faulttolerance.faulsimulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.negotiation.faulttolerance.experimentation.ReplicationExperimentationParameters;
import frameworks.negotiation.faulttolerance.experimentation.ReplicationLaborantin;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;

public class FaultTriggeringService extends BasicAgentCompetence<ReplicationLaborantin> {
	private static final long serialVersionUID = -5136934098832050285L;

	//
	// Fields
	//

	private final ReplicationExperimentationParameters p;
	private final HashSet<AgentIdentifier> currentlyFaultyHost=new HashSet<AgentIdentifier>();

	//
	// Constructor
	//

	public FaultTriggeringService(
			final ReplicationExperimentationParameters p) {
		super();
		this.p=p;
	}

	//
	// Behavior
	//

	int i = 0;


	@StepComposant(ticker=ReplicationExperimentationParameters._host_maxFaultfrequency)
	public void toggleFault() {
		int nbMax = this.p.host_maxSimultaneousFailure.intValue();
		if (nbMax>0){
			final List<ResourceIdentifier> hosts = new ArrayList<ResourceIdentifier>();
			hosts.addAll(HostDisponibilityComputer.getHosts(this.getMyAgent().myInformationService));
			//			hosts.remove(0);
			Collections.shuffle(hosts);
			for (final ResourceIdentifier h : hosts) {
				final FaultStatusMessage sentence =
						HostDisponibilityComputer.eventOccur(this.getMyAgent().myInformationService, h, currentlyFaultyHost.contains(h));

				if (sentence != null) {
					// Execution de la sentence!! muahaha!!!
					boolean b = currentlyFaultyHost.add(h);
					assert b;
										this.logWarning("executing this sentence : " + sentence
												+ " (" + this.i + ")",LogService.onBoth);
					// Déclaration public
					for (final AgentIdentifier id : this.getMyAgent().myInformationService.getKnownAgents()) {
						this.getMyAgent().sendMessage(id, sentence);
					}

					if (sentence.isFaultEvent())
					{
						nbMax--;// il est mort! =(
					}
					if (nbMax == 0) {
						break;
					}
				}
			}
			this.i++;
		}
	}
}

// @StepComposant(ticker=StaticParameters._host_maxFaultfrequency)
// public void changeFaultStatus(){
// if (!this.iMFaulty){
// if (getMyAgent().getMyCurrentState().IshouldFail()){
// this.iMFaulty=true;
// this.mustDeclareFaulty=true;
// this.logMonologue("I'm faulty");
// this.getMyCurrentState().resetUptime();
// // this.getMyCurrentState().reset
// // this.myParticipantRole.reset();
// HostDisponibilityTrunk.resetUptime(this.getMyCurrentState().getMyAgentIdentifier());
// this.notify(new FaultEvent());
// }
// } else if (this.getMyCurrentState().IShoudBeRepaired()){
// this.iMFaulty=false;
// this.logMonologue("I'm repaired");
// this.getMyCurrentState().resetUptime();
// HostDisponibilityTrunk.resetUptime(this.getMyCurrentState().getMyAgentIdentifier());
// this.notify(new RepairEvent());
// }
// }

//
// Primitives
//

// @StepComposant(ticker=NegotiationParameters._dispo_update_frequency)
// public void updateMyDispo(){
// final Random r = new Random();
// if (r.nextDouble()<StaticParameters._dispoVariationProba){//On met a jour
// final int signe = r.nextBoolean()?1:-1;
// set(this.getMyCurrentState().getMyAgentIdentifier(),
// Math.max(0., Math.min(StaticParameters._dispoMax, signe *
// r.nextDouble()*StaticParameters._dispoVariationAmplitude)));
// }
// }
