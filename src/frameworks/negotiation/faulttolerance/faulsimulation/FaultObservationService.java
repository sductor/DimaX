package frameworks.negotiation.faulttolerance.faulsimulation;

import java.util.ArrayList;
import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import frameworks.negotiation.faulttolerance.negotiatingagent.HostState;
import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.negotiationframework.contracts.ContractTrunk;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;
import frameworks.negotiation.negotiationframework.rationality.AgentState;

/**
 * Service used by the agent to be concious of the system state
 */
public abstract class FaultObservationService
<PersonalState extends AgentState,
Contract extends AbstractContractTransition> extends
BasicAgentCompetence<NegotiatingAgent<PersonalState, Contract>> {
	private static final long serialVersionUID = 2339746438446977252L;

	//
	// Fields
	//

	private final Collection<AgentIdentifier> initiallyKnownAgent = new ArrayList<AgentIdentifier>();
	private boolean imFaulty;

	//
	// Accessors
	//

	public boolean isImFaulty() {
		return imFaulty;
	}


	//
	// Behavior
	//

	@ProactivityInitialisation
	public void initiate() {
		this.initiallyKnownAgent.addAll(this.getMyAgent().getMyInformation()
				.getKnownAgents());
	}

	//
	//Abstract
	//

	public abstract Contract generateDestructionContract(final AgentIdentifier id);


	public abstract  void endSimulation();

	//
	// Behavior
	//

	@MessageHandler
	public void faultObservation(final FaultStatusMessage m){
		if (m.getHost().equals(getMyAgent().getIdentifier())){
			logMonologue("my status has changed!! "+m);
			if (m.isFaultEvent())
				setImFaulty();
			else
				setImRepaired();
		} else {
			if (m.isFaultEvent())
				setIsFaulty(m.getHost());
			else
				setIsRepaired(m.getHost());
		}
	}

	//
	// Primitive
	//

	private void setImFaulty(){
		getMyAgent().getMyProtocol().getContracts().clear();
		getMyAgent().getMyProtocol().setActive(false);
		Collection<? extends AgentIdentifier> myResources = getMyAgent().getMyCurrentState().getMyResourceIdentifiers();
		PersonalState myState = getMyAgent().getMyCurrentState();
		for (AgentIdentifier id : myResources){
			try {
				myState = generateDestructionContract(id).computeResultingState(myState);
			} catch (IncompleteContractException e) {
				throw new RuntimeException("impossible");
			}
		}
		((HostState) myState).setFaulty(true);
		getMyAgent().setNewState(myState);
		imFaulty = true;
	}

	private void setImRepaired(){
		getMyAgent().getMyProtocol().setActive(true);		
	}

	/*
	 * 
	 */

	private void setIsFaulty(AgentIdentifier id){
		Collection<Contract> lost = getMyAgent().getMyProtocol().getContracts().getContracts(id);
		getMyAgent().getMyProtocol().getContracts().removeAll(lost);
		this.getMyAgent().getMyInformation().remove(id);
		try {
			if (getMyAgent().getMyCurrentState().getMyResourceIdentifiers().contains(id)){
			logMonologue("argh i'm affected by a fault!!!! "+id);
			PersonalState myState=generateDestructionContract(id).computeResultingState(getMyAgent().getMyCurrentState());
			getMyAgent().setNewState(myState);
			if (!myState.isValid())
				endSimulation();
			}
		} catch (IncompleteContractException e) {
			throw new RuntimeException("impossible");			
		}
	}

	private void setIsRepaired(AgentIdentifier id){
		if (initiallyKnownAgent.contains(id)) 
			this.getMyAgent().getMyInformation().add(id);
	}
}










////
//// Accessors
////
//
////	public Double getDisponibility(final ResourceIdentifier host) {
////		return HostDisponibilityComputer.getDisponibility(this.getMyAgent().getMyInformation(),host);
////	}
////
////	public Double getDisponibility(final Collection<ResourceIdentifier> hosts) {
////		return HostDisponibilityComputer.getDisponibility(this.getMyAgent().getMyInformation(),hosts);
////	}
//
////
//// Abstract methods
////
//
//protected abstract void resetMyState();
//
//protected abstract void resetMyUptime();
//
////
//// Methods
////
////
////@MessageHandler
////// @NotificationEnvelope
////public void faultObservation(final FaultEvent f) {// final
////	// NotificationMessage<FaultEvent>
////	// m) {
////	// final FaultEvent f = m.getNotification();
////	// logMonologue("i ve received "+f+" "+f.getHost().equals(this.getIdentifier())+"\n"+getMyCurrentState());
////	if (f.getHost().equals(this.getMyAgent().getIdentifier())) {
////		final HostState myState = (HostState) this.getMyAgent()
////				.getMyCurrentState();
////		if (myState.isFaulty() == true) {
////			throw new RuntimeException(
////					"nnnnnnnooooooooooooonnnnnnnnnnnnnnn!!!!!!!!!! :\n" + f
////					+ this.getMyAgent().getMyCurrentState());
////		}
////		myState.setFaulty(true);
////		this.logWarning("I've failed!! =( Those replicas are dead : "
////				+ ((HostState) this.getMyAgent().getMyCurrentState())
////				.getMyResourceIdentifiers(),LogService.onBoth);
////		//
////		this.getMyAgent().getMyProtocol().setLost(f.getHost());
////		this.getMyAgent().getMyProtocol().stop();
////		this.resetMyState();
////		//			this.resetMyUptime();
////	} else {
////		this.getMyAgent().getMyInformation().remove(f.getHost());
////		if (this.getMyAgent().getMyCurrentState().setLost(f.getHost(), true)) {
////			this.logWarning("I've lost a replica :" + f.getHost()
////					+ " !! =(",LogService.onBoth);
////		}
////		this.getMyAgent().getMyProtocol().setLost(f.getHost());
////	}
////}
////
////@MessageHandler
////// @NotificationEnvelope
////public void repairObservation(final RepairEvent f) {// final
////	// NotificationMessage<RepairEvent>
////	// m) {
////	// final RepairEvent f = m.getNotification();
////	if (f.getHost().equals(this.getMyAgent().getIdentifier())) {
////		final HostState myState = (HostState) this.getMyAgent()
////				.getMyCurrentState();
////		if (myState.isFaulty() == false) {
////			throw new RuntimeException(
////					"nnnnnnnooooooooooooonnnnnnnnnnnnnnn!!!!!!!!!! :\n" + f
////					+ "\n" + this.getMyAgent().getMyCurrentState());
////		}
////		myState.setFaulty(false);
////		this.logMonologue("I'm repaired!! =)",LogService.onBoth);
////		//
////		this.resetMyState();
////		//			this.resetMyUptime();
////		this.getMyAgent().getMyProtocol().start();
////	} else if (this.initiallyKnownAgent.contains(f.getHost())) {
////		this.getMyAgent().getMyInformation().add(f.getHost());
////	}
////}
//
//// Accessors
////
//
////	public Double getDisponibility(final ResourceIdentifier host) {
////		return HostDisponibilityComputer.getDisponibility(this.getMyAgent().getMyInformation(),host);
////	}
////
////	public Double getDisponibility(final Collection<ResourceIdentifier> hosts) {
////		return HostDisponibilityComputer.getDisponibility(this.getMyAgent().getMyInformation(),hosts);
////	}
//
////
//// Abstract methods
////
//
//protected abstract void resetMyState();
//
//protected abstract void resetMyUptime();
//
////
//// Methods
////
////
//// Accessors
////
//
////	public Double getDisponibility(final ResourceIdentifier host) {
////		return HostDisponibilityComputer.getDisponibility(this.getMyAgent().getMyInformation(),host);
////	}
////
////	public Double getDisponibility(final Collection<ResourceIdentifier> hosts) {
////		return HostDisponibilityComputer.getDisponibility(this.getMyAgent().getMyInformation(),hosts);
////	}
//
////
//// Abstract methods
////
//
//protected abstract void resetMyState();
//
//protected abstract void resetMyUptime();
//
////
//// Methods
////
////
////@MessageHandler
////// @NotificationEnvelope
////public void faultObservation(final FaultEvent f) {// final
////	// NotificationMessage<FaultEvent>
////	// m) {
////	// final FaultEvent f = m.getNotification();
////	// logMonologue("i ve received "+f+" "+f.getHost().equals(this.getIdentifier())+"\n"+getMyCurrentState());
////	if (f.getHost().equals(this.getMyAgent().getIdentifier())) {
////		final HostState myState = (HostState) this.getMyAgent()
////				.getMyCurrentState();
////		if (myState.isFaulty() == true) {
////			throw new RuntimeException(
////					"nnnnnnnooooooooooooonnnnnnnnnnnnnnn!!!!!!!!!! :\n" + f
////					+ this.getMyAgent().getMyCurrentState());
////		}
////		myState.setFaulty(true);
////		this.logWarning("I've failed!! =( Those replicas are dead : "
////				+ ((HostState) this.getMyAgent().getMyCurrentState())
////				.getMyResourceIdentifiers(),LogService.onBoth);
////		//
////		this.getMyAgent().getMyProtocol().setLost(f.getHost());
////		this.getMyAgent().getMyProtocol().stop();
////		this.resetMyState();
////		//			this.resetMyUptime();
////	} else {
////		this.getMyAgent().getMyInformation().remove(f.getHost());
////		if (this.getMyAgent().getMyCurrentState().setLost(f.getHost(), true)) {
////			this.logWarning("I've lost a replica :" + f.getHost()
////					+ " !! =(",LogService.onBoth);
////		}
////		this.getMyAgent().getMyProtocol().setLost(f.getHost());
////	}
////}
////
////@MessageHandler
////// @NotificationEnvelope
////public void repairObservation(final RepairEvent f) {// final
////	// NotificationMessage<RepairEvent>
////	// m) {
////	// final RepairEvent f = m.getNotification();
////	if (f.getHost().equals(this.getMyAgent().getIdentifier())) {
////		final HostState myState = (HostState) this.getMyAgent()
////				.getMyCurrentState();
////		if (myState.isFaulty() == false) {
////			throw new RuntimeException(
////					"nnnnnnnooooooooooooonnnnnnnnnnnnnnn!!!!!!!!!! :\n" + f
////					+ "\n" + this.getMyAgent().getMyCurrentState());
////		}
////		myState.setFaulty(false);
////		this.logMonologue("I'm repaired!! =)",LogService.onBoth);
////		//
////		this.resetMyState();
////		//			this.resetMyUptime();
////		this.getMyAgent().getMyProtocol().start();
////	} else if (this.initiallyKnownAgent.contains(f.getHost())) {
////		this.getMyAgent().getMyInformation().add(f.getHost());
////	}
////}//
////@MessageHandler
////// @NotificationEnvelope
////public void faultObservation(final FaultEvent f) {// final
////	// NotificationMessage<FaultEvent>
////	// m) {
////	// final FaultEvent f = m.getNotification();
////	// logMonologue("i ve received "+f+" "+f.getHost().equals(this.getIdentifier())+"\n"+getMyCurrentState());
////	if (f.getHost().equals(this.getMyAgent().getIdentifier())) {
////		final HostState myState = (HostState) this.getMyAgent()
////				.getMyCurrentState();
////		if (myState.isFaulty() == true) {
////			throw new RuntimeException(
////					"nnnnnnnooooooooooooonnnnnnnnnnnnnnn!!!!!!!!!! :\n" + f
////					+ this.getMyAgent().getMyCurrentState());
////		}
////		myState.setFaulty(true);
////		this.logWarning("I've failed!! =( Those replicas are dead : "
////				+ ((HostState) this.getMyAgent().getMyCurrentState())
////				.getMyResourceIdentifiers(),LogService.onBoth);
////		//
////		this.getMyAgent().getMyProtocol().setLost(f.getHost());
////		this.getMyAgent().getMyProtocol().stop();
////		this.resetMyState();
////		//			this.resetMyUptime();
////	} else {
////	//
//// Accessors
////
//
////	public Double getDisponibility(final ResourceIdentifier host) {
////		return HostDisponibilityComputer.getDisponibility(this.getMyAgent().getMyInformation(),host);
////	}
////
////	public Double getDisponibility(final Collection<ResourceIdentifier> hosts) {
////		return HostDisponibilityComputer.getDisponibility(this.getMyAgent().getMyInformation(),hosts);
////	}
//
////
//// Abstract methods
////
//
//protected abstract void resetMyState();
//
//protected abstract void resetMyUptime();
//
////
//// Methods
////
////
////@MessageHandler
////// @NotificationEnvelope
////public void faultObservation(final FaultEvent f) {// final
////	// NotificationMessage<FaultEvent>
////	// m) {
////	// final FaultEvent f = m.getNotification();
////	// logMonologue("i ve received "+f+" "+f.getHost().equals(this.getIdentifier())+"\n"+getMyCurrentState());
////	if (f.getHost().equals(this.getMyAgent().getIdentifier())) {
////		final HostState myState = (HostState) this.getMyAgent()
////				.getMyCurrentState();
////		if (myState.isFaulty() == true) {
////			throw new RuntimeException(
////					"nnnnnnnooooooooooooonnnnnnnnnnnnnnn!!!!!!!!!! :\n" + f
////					+ this.getMyAgent().getMyCurrentState());
////		}
////		myState.setFaulty(true);
////		this.logWarning("I've failed!! =( Those replicas are dead : "
////				+ ((HostState) this.getMyAgent().getMyCurrentState())
////				.getMyResourceIdentifiers(),LogService.onBoth);
////		//
////		this.getMyAgent().getMyProtocol().setLost(f.getHost());
////		this.getMyAgent().getMyProtocol().stop();
////		this.resetMyState();
////		//			this.resetMyUptime();
////	} else {
////		this.getMyAgent().getMyInformation().remove(f.getHost());
////		if (this.getMyAgent().getMyCurrentState().setLost(f.getHost(), true)) {
////			this.logWarning("I've lost a replica :" + f.getHost()
////					+ " !! =(",LogService.onBoth);
////		}
////		this.getMyAgent().getMyProtocol().setLost(f.getHost());
////	}
////}
////
////@MessageHandler
////// @NotificationEnvelope
////public void repairObservation(final RepairEvent f) {// final
////	// NotificationMessage<RepairEvent>
////	// m) {
////	// final RepairEvent f = m.getNotification();
////	if (f.getHost().equals(this.getMyAgent().getIdentifier())) {
////		final HostState myState = (HostState) this.getMyAgent()
////				.getMyCurrentState();
////		if (myState.isFaulty() == false) {
////			throw new RuntimeException(
////					"nnnnnnnooooooooooooonnnnnnnnnnnnnnn!!!!!!!!!! :\n" + f
////					+ "\n" + this.getMyAgent().getMyCurrentState());
////		}
////		myState.setFaulty(false);
////		this.logMonologue("I'm repaired!! =)",LogService.onBoth);
////		//
////		this.resetMyState();
////		//			this.resetMyUptime();
////		this.getMyAgent().getMyProtocol().start();
////	} else if (this.initiallyKnownAgent.contains(f.getHost())) {
////		this.getMyAgent().getMyInformation().add(f.getHost());
////	}
////}	this.getMyAgent().getMyInformation().remove(f.getHost());
////		if (this.getMyAgent().getMyCurrentState().setLost(f.getHost(), true)) {
////			this.logWarning("I've lost a replica :" + f.getHost()
////					+ " !! =(",LogService.onBoth);
////		}
////		this.getMyAgent().getMyProtocol().setLost(f.getHost());
////	}
////}
////
////@MessageHandler
////// @NotificationEnvelope
////public void repairObservation(final RepairEvent f) {// final
////	// NotificationMessage<RepairEvent>
////	// m) {
////	// final RepairEvent f = m.getNotification();
////	if (f.getHost().equals(this.getMyAgent().getIdentifier())) {
////		final HostState myState = (HostState) this.getMyAgent()
////				.getMyCurrentState();
////		if (myState.isFaulty() == false) {
////			throw new RuntimeException(
////					"nnnnnnnooooooooooooonnnnnnnnnnnnnnn!!!!!!!!!! :\n" + f
////					+ "\n" + this.getMyAgent().getMyCurrentState());
////		}
////		myState.setFaulty(false);
////		this.logMonologue("I'm repaired!! =)",LogService.onBoth);
////		//
////		this.resetMyState();
////		//			this.resetMyUptime();
////		this.getMyAgent().getMyProtocol().start();
////	} else if (this.initiallyKnownAgent.contains(f.getHost())) {
////		this.getMyAgent().getMyInformation().add(f.getHost());
////	}
////}
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