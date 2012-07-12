package negotiation.faulttolerance.faulsimulation;

import java.util.ArrayList;
import java.util.Collection;

import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.negotiationframework.NegotiatingAgent;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

/**
 * Service used by the agent to be concious of the system state
 */
public abstract class FaultObservationService extends
BasicAgentCompetence<NegotiatingAgent<?, ?>> {
	private static final long serialVersionUID = 2339746438446977252L;

	//
	// Fields
	//
	//
	//	final ObservationService myAgentInformation;
	//	private final HostDisponibilityComputer myDispoInfo;


	// astuce artificielles pour maintenir le graphe de voisinage par latence
	// la méthode propre est de calculer la latence quand un hote réapparait
	// pour savoir s'il doit revenir dans les known agents
	private final Collection<AgentIdentifier> initiallyKnownAgent = new ArrayList<AgentIdentifier>();

	//
	// Constructor
	//

	@ProactivityInitialisation
	public void initiate() {
		this.initiallyKnownAgent.addAll(this.getMyAgent().getMyInformation()
				.getKnownAgents());
	}

	//
	// Accessors
	//

	//	public Double getDisponibility(final ResourceIdentifier host) {
	//		return HostDisponibilityComputer.getDisponibility(this.getMyAgent().getMyInformation(),host);
	//	}
	//
	//	public Double getDisponibility(final Collection<ResourceIdentifier> hosts) {
	//		return HostDisponibilityComputer.getDisponibility(this.getMyAgent().getMyInformation(),hosts);
	//	}

	//
	// Abstract methods
	//

	protected abstract void resetMyState();

	protected abstract void resetMyUptime();

	//
	// Methods
	//

	@MessageHandler
	// @NotificationEnvelope
	public void faultObservation(final FaultEvent f) {// final
		// NotificationMessage<FaultEvent>
		// m) {
		// final FaultEvent f = m.getNotification();
		// logMonologue("i ve received "+f+" "+f.getHost().equals(this.getIdentifier())+"\n"+getMyCurrentState());
		if (f.getHost().equals(this.getMyAgent().getIdentifier())) {
			final HostState myState = (HostState) this.getMyAgent()
					.getMyCurrentState();
			if (myState.isFaulty() == true) {
				throw new RuntimeException(
						"nnnnnnnooooooooooooonnnnnnnnnnnnnnn!!!!!!!!!! :\n" + f
						+ this.getMyAgent().getMyCurrentState());
			}
			myState.setFaulty(true);
			this.logWarning("I've failed!! =( Those replicas are dead : "
					+ ((HostState) this.getMyAgent().getMyCurrentState())
					.getMyResourceIdentifiers(),LogService.onBoth);
			//
			this.getMyAgent().getMyProtocol().setLost(f.getHost());
			this.getMyAgent().getMyProtocol().stop();
			this.resetMyState();
			//			this.resetMyUptime();
		} else {
			this.getMyAgent().getMyInformation().remove(f.getHost());
			if (this.getMyAgent().getMyCurrentState().setLost(f.getHost(), true)) {
				this.logWarning("I've lost a replica :" + f.getHost()
						+ " !! =(",LogService.onBoth);
			}
			this.getMyAgent().getMyProtocol().setLost(f.getHost());
		}
	}

	@MessageHandler
	// @NotificationEnvelope
	public void repairObservation(final RepairEvent f) {// final
		// NotificationMessage<RepairEvent>
		// m) {
		// final RepairEvent f = m.getNotification();
		if (f.getHost().equals(this.getMyAgent().getIdentifier())) {
			final HostState myState = (HostState) this.getMyAgent()
					.getMyCurrentState();
			if (myState.isFaulty() == false) {
				throw new RuntimeException(
						"nnnnnnnooooooooooooonnnnnnnnnnnnnnn!!!!!!!!!! :\n" + f
						+ "\n" + this.getMyAgent().getMyCurrentState());
			}
			myState.setFaulty(false);
			this.logMonologue("I'm repaired!! =)",LogService.onBoth);
			//
			this.resetMyState();
			//			this.resetMyUptime();
			this.getMyAgent().getMyProtocol().start();
		} else if (this.initiallyKnownAgent.contains(f.getHost())) {
			this.getMyAgent().getMyInformation().add(f.getHost());
		}
	}
}

// //
// // Primitive
// //
// @MessageHandler
// @NotificationEnvelope
// public void setHostsInfo(final NotificationMessage<HostDisponibilityTrunk>
// n){
// final HostDisponibilityTrunk d = n.getNotification();
// for (final ResourceIdentifier h : d.getHosts())
// this.add(h, d.getLambda(h), d.getCreationtime(h));
//
// }
//
// private void add(final ResourceIdentifier h, final Double lambda, final Long
// creationTime) {
// this.myDispoInfo.add(h, lambda, creationTime);
// }
//
//
// private void resetUptime(final ResourceIdentifier h) {
// this.myDispoInfo.resetUptime(h);
// }

//
// private void set(ResourceIdentifier h, Double lambda) {
// myDispoInfo.set(h, lambda);
// }

// @StepComposant
// @Transient
// boolean registerToEvents(){
// if (this.appliHasStarted){
// for (final AgentIdentifier host : this.getKnownAgents()){
// this.observe(host, FaultEvent.class);
// this.observe(host, RepairEvent.class);
// }
// return true;
// }else
// return false;
// }

// //
// logMonologue(m.getNotification().getHost().getMyAgentIdentifier()+" is dead\n"+getMyCurrentState());
// // System.err.println(
// // "1 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "
// //
// +m.getNotification().getHost().getMyAgentIdentifier()+"\n"+getMyCurrentState());
// if (this.getMyCurrentState().getMyReplicas().contains(
// m.getNotification().getHost())){
// this.getKnownAgents().remove(m.getNotification().getHost());
// this.myInitiatorRole.agentIsLost(m.getNotification().getHost());
// this.setNewState(this.getMyCurrentState().update(
// m.getNotification().getHost(),false));
// // System.err.println(
// // "2 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n"+getMyCurrentState());
// if (this.getMyCurrentState().getMyReplicas().isEmpty()){
// this.logMonologue("I'm dead !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
// this.iAMDead=true;
// // System.err.println("I'm dead");
// this.endSimulation();
// }
// }
//
// this.getKnownAgents().remove(
// m.getNotification().getHost());