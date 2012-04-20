package negotiation.faulttolerance.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import negotiation.faulttolerance.candidaturewithstatus.CandidatureReplicaCoreWithStatus;
import negotiation.faulttolerance.candidaturewithstatus.CandidatureReplicaProposerWithStatus;
import negotiation.faulttolerance.candidaturewithstatus.Host;
import negotiation.faulttolerance.candidaturewithstatus.ObservingStatusService;
import negotiation.faulttolerance.candidaturewithstatus.Replica;
import negotiation.faulttolerance.collaborativecandidature.CollaborativeHost;
import negotiation.faulttolerance.collaborativecandidature.CollaborativeReplica;
import negotiation.faulttolerance.faulsimulation.FaultTriggeringService;
import negotiation.faulttolerance.faulsimulation.HostDisponibilityComputer;
import negotiation.faulttolerance.negotiatingagent.HostCore;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.InactiveProposerCore;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import negotiation.negotiationframework.selection.GreedySelectionModule.GreedySelectionType;
import negotiation.negotiationframework.selection.SimpleSelectionCore;
import negotiation.negotiationframework.selection.GreedySelectionModule;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.information.SimpleOpinionService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.replication.ReplicationHandler;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.ExperimentationProtocol;
import dimaxx.experimentation.ExperimentationResults;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin;
import dimaxx.experimentation.ObservingGlobalService;
import dimaxx.experimentation.SimulationEndedMessage;
import dimaxx.experimentation.ObservingSelfService.ActivityLog;
import dimaxx.server.HostIdentifier;
import dimaxx.tools.aggregator.HeavyAggregation;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;
import dimaxx.tools.aggregator.LightWeightedAverageDoubleAggregation;
import dimaxx.tools.distribution.DistributionParameters;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ReplicationLaborantin extends Laborantin {
	private static final long serialVersionUID = -8907201877042609757L;


	// ///////////////////////////////////////////
	// Constructor
	// ///////////////////////////////////////////


	public ReplicationLaborantin(final ReplicationExperimentationParameters p,final APILauncherModule api, final int numberOfAgentPerMAchine)
			throws CompetenceException, IfailedException, NotEnoughMachinesException {
		super(p, api, numberOfAgentPerMAchine);

//		this.myInformationService = new SimpleObservationService();
//		this.myInformationService.setMyAgent(this);


		this.myGlobalObservationService.initiate();

		this.myFaultService = new FaultTriggeringService(p);

		this.myStatusObserver= new ObservingStatusService(this, this.getSimulationParameters());


	}




	// ///////////////////////////////////////////
	// Accessors
	// ///////////////////////////////////////////


	@Override
	protected ObservingGlobalService getGlobalObservingService() {
		return this.myGlobalObservationService;
	}


	// ///////////////////////////////////////////
	// Methods
	// ///////////////////////////////////////////


}



//@StepComposant(ticker = ReplicationExperimentationProtocol._reliabilityObservationFrequency)
//public void informSystemState() {
//	// System.out.println("hiiiiiiiiiiiiiiiiiiiiiiihhhhhhhhhhhhhaaaaaaaaaaaaaaaaaaaaa!!!!!!!!!!!");
//	try {
//		this.notify(new SystemInformationMessage(
//				this.agentsStatusObservation
//				.getQuantile(
//						ReplicationExperimentationProtocol.firstTercile,
//						100),
//						this.agentsStatusObservation.getQuantile(
//								ReplicationExperimentationProtocol.lastTercile,
//								100)));
//		// this.logMonologue("yyyyyoooooooooooooooooooooouuuuuuuuuuuuuhhhhhhhhhhhhhhhhhhhhhhhhhoooooooouuuuuuuuuuuuhhhhhhhhhhhhhhhhh!");
//		this.agentsStatusObservation.clear();
//	} catch (final Exception e) {
//		this.logWarning("oooooooooooooooooooooohhhhhhhhhhhhhhhhhhhhhhhhh!"
//				+ this.isActive(),e);
//	}
//}

//
// Message
//

//	public class SystemInformationMessage implements Serializable {
//		private static final long serialVersionUID = 9097386950633875924L;
//		public final Double lowerThreshold;
//		public final Double higherThreshold;
//
//		public SystemInformationMessage(final Double lowerThreshold,
//				final Double higherThreshold) throws Exception {
//			super();
//			if (lowerThreshold == null || higherThreshold == null)
//				throw new Exception("arrrrrrrrgggggghhhhhhh!");
//			this.lowerThreshold = lowerThreshold;
//			this.higherThreshold = higherThreshold;
//		}
//
//		@Override
//		public String toString() {
//			return "\n * First=" + this.lowerThreshold + "\n * Last ="
//					+ this.higherThreshold;
//		}
//	}

// private SimpleParticipantAgent createNewHost(
// //final List<ResourceIdentifier> hostsIdentifier,
// final ResourceIdentifier id,
// final Double proc,
// final Double mem)
// throws UnInstanciableCompetenceException, DuplicateCompetenceException{
// final NegotiatingHost host =
// new NegotiatingHost(
// id,
// this.simulationInit,
// proc,
// mem);
// this.setHostObservation(host);
// if (this.getSimParameters().replicate==0)//algoNorma
// host.randomSelection=false;
// if (this.getSimParameters().replicate==1)//rep alea
// host.randomSelection=true;
// else//pas de rep
// host.randomSelection=true;

// this.hostsStates4simulationResult.put(id, new
// SimulationHostStatusEvolution(id));
// //for (ResourceIdentifier r : hostsIdentifier)
// //host.observer.registeredObservers.add(FaultEvent.class.getName(), r);
// //AgentManagementSystem.getDIMAams().addAquaintance(host);
// return host;
// //host.getMyInformation().addKnownAgents(replicasIdentifier);
// }

// private SimpleInitiatorAgent createNewAgent(final AgentIdentifier id)
// throws UnInstanciableCompetenceException, DuplicateCompetenceException{

// /*
// * Agent instanciation
// */

// final NegotiatingReplica ag =

// if (this.getSimParameters().replicate==0)//algoNorma
// ag.replicate=true;
// if (this.getSimParameters().replicate==1)//rep alea
// ag.replicate=true;
// else//pas de rep
// ag.replicate=false;

// this.agentsStates4simulationResult.put(ag.getIdentifier(), new
// SimulationAgentStatusEvolution(ag));
// /*
// * Observation registration
// */
// //AgentManagementSystem.getDIMAams().addAquaintance(ag);
// return ag;
// }

// Main

// public static void main(final String[] args) throws
// UnInstanciableCompetenceException, DuplicateCompetenceException{
// //SimulationParameters p = new SimulationParameters(
// //3, 4, 20, ZeroOneSymbolicValue.Moyen, ZeroOneSymbolicValue.Moyen, 10, 2,
// false);
// ////new Laborantin(p).launchWithFipa();
// //new Laborantin(p).launchWithoutThreads(50);
// //new Laborantin(p).launchWithDarx(7777, 7001);
// }
// final DistributionParameters<ResourceIdentifier> hostProcessor=
// new DistributionParameters<ResourceIdentifier>(
// hostsIdentifier,p.hostResourcesDispersion,10*p.amountOfResources);
// final DistributionParameters<ResourceIdentifier> hostMemory=
// new DistributionParameters<ResourceIdentifier>(
// hostsIdentifier,p.hostResourcesDispersion,10*p.amountOfResources);

/*
 * Set faults info
 */

// /*
// * simulation result
// */

// SimulationResultOld results;
// Date simulationInit = new Date();

// protected HashMap<AgentIdentifier, SimpleRationalAgent> agents =
// new HashMap<AgentIdentifier, SimpleRationalAgent>();

// //Ticker stateUpdate = new
// Ticker(NegotiationSimulationParameters._state_snapshot_frequency);
// HashMap<AgentIdentifier, SimulationAgentStatusEvolution>
// agentsStates4simulationResult =
// new HashMap<AgentIdentifier, SimulationAgentStatusEvolution>();
// //Ticker stateUpdate = new
// Ticker(NegotiationSimulationParameters._state_snapshot_frequency);
// HashMap<ResourceIdentifier, SimulationHostStatusEvolution>
// hostsStates4simulationResult =
// new HashMap<ResourceIdentifier, SimulationHostStatusEvolution>();

// HeavyQuantileAggregator<AgentInfo> agentStatistic =
// new HeavyQuantileAggregator<AgentInfo>();
// //Collection<HostInfo> hostStates4simulationResult =
// //new HashSet<HostInfo>();

// @MessageHandler
// @NotificationEnvelope
// public void receiveProtocolTime(final
// NotificationMessage<AgentEndProtocolObs> n){
// this.agentsStates4simulationResult.get(n.getNotification().getId()).updateProtoTime(n.getNotification());
// this.finishedAgent ++;
// }

// @MessageHandler
// @NotificationEnvelope
// public void receiveHostEnd(final NotificationMessage<HostEnd> n){
// this.finishedAgent ++;
// }

// @MessageHandler
// @NotificationEnvelope(simulationResultStateObservationKey)
// public void receiveAgentfinalState(final NotificationMessage<AgentInfo> n){
// this.agentsStates4simulationResult.get(n.getNotification().getMyAgentIdentifier()).update(n.getNotification());
// }

// @MessageHandler
// @NotificationEnvelope
// public void receiveHostStateInfo(final NotificationMessage<HostInfo> n){
// this.hostsStates4simulationResult.get(n.getNotification().getMyAgentIdentifier()).update(n.getNotification());
// }

// /*
// * Statistic for agent status
// */

// @StepComposant(ticker=StaticParameters._quantileInfoFrequency)
// public void informSystemState(){
// if (this.agentStatistic.getFirstTercile()!=null &&
// this.agentStatistic.getLastTercile()!=null)
// this.notify(new SystemInformationMessage());
// }

// @MessageHandler
// @NotificationEnvelope
// public void receiveAgentStateInfo(final NotificationMessage<AgentInfo> n){
// if (n.getNotification().myStatus.equals(AgentStateStatus.Full))
// this.agentStatistic.remove(n.getNotification());
// else
// this.agentStatistic.add(n.getNotification());
// }

// public class SystemInformationMessage implements Serializable{
// private static final long serialVersionUID = 9097386950633875924L;
// public final Double firstTercile;
// public final Double lastTercile;

// public SystemInformationMessage() {
// super();
// this.firstTercile = Laborantin.this.agentStatistic.getQuantile(
// StaticParameters.firstTercile,100).getMyReliability();
// this.lastTercile =
// Laborantin.this.agentStatistic.getQuantile(StaticParameters.lastTercile,100).getMyReliability();
// }

// @Override
// public String toString(){
// return
// "\n * First="+this.firstTercile
// +"\n * Last ="+this.lastTercile;
// }
// }

// /*
// * Simulation ending
// */

// @StepComposant()
// @Transient
// public boolean endSimulation(){
// if
// (this.finishedAgent==this.agentsStates4simulationResult.size()+this.hostsStates4simulationResult.size()){
// this.results = new
// SimulationResultOld(this.p,this.agentsStates4simulationResult.values(),
// this.hostsStates4simulationResult.values());
// this.logMonologue("I've finished!!");
// this.results.write();
// this.wwait(10000);
// for (final ResourceIdentifier h : this.hostsStates4simulationResult.keySet())
// HostDisponibilityTrunk.remove(h);
// this.notify(new SimulationEnded());
// return true;
// }
// return false;
// }

// public void kill() {
// for (final BasicCommunicatingAgent ag : this.getAgents())
// ag.setAlive(false);
// this.agents.clear();
// this.setAlive(false);
// }

// public class SimulationEnded implements Serializable{
// private static final long serialVersionUID = -4584449577236269574L;}

// String result ="**************\n";
// result+= "Static parameters are :\n";
// result += f.getName()+" : "+f.get(StaticParameters.class)+"\n";
// result+="**************";
// return result;
// String result =
// "t (seconds); reliab. min; reliab.  firstTercile; reliab.  mediane;  reliab. lastTercile; reliab.  max ; =\n";
// for (int i = 0; i < this.numberOfTimePoints(); i++)
// if (!this.meanReliabilityEvolution[i].isEmpty())
// result += this.geTime(i)/1000.+" ; "+
// this.meanReliabilityEvolution[i].getMin()+"; " +
// this.meanReliabilityEvolution[i].getQuantile(1,3)+"; " +
// this.meanReliabilityEvolution[i].getMediane()+"; " +
// this.meanReliabilityEvolution[i].getQuantile(2,3)+"; " +
// this.meanReliabilityEvolution[i].getMax()+"\n";
// return result;

// private String getAgentFieldTimePoint(int i, Field f){
// String result ="";
// result += f.get();

// }

// final HeavyQuantileAggregator<AgentStatusEvolution>[] agentsEvolution =
// new HeavyQuantileAggregator[this.numberOfTimePoints()];
// final HeavyQuantileAggregator<HostStatusEvolution>[] hostsEvolution =
// new HeavyQuantileAggregator[this.numberOfTimePoints()];