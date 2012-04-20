package negotiation.faulttolerance.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

import negotiation.faulttolerance.candidaturewithstatus.Host;
import negotiation.faulttolerance.candidaturewithstatus.ObservingStatusService;
import negotiation.faulttolerance.candidaturewithstatus.Replica;
import negotiation.faulttolerance.collaborativecandidature.CollaborativeHost;
import negotiation.faulttolerance.collaborativecandidature.CollaborativeReplica;
import negotiation.negotiationframework.NegotiationParameters;
import negotiation.negotiationframework.contracts.ResourceIdentifier;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.services.information.SimpleOpinionService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.ExperimentationResults;
import dimaxx.experimentation.ObservingGlobalService;
import dimaxx.experimentation.ObservingSelfService.ActivityLog;
import dimaxx.experimentation.SimulationEndedMessage;
import dimaxx.tools.aggregator.HeavyAggregation;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;
import dimaxx.tools.aggregator.LightWeightedAverageDoubleAggregation;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ReplicationObservingGlobalService extends ObservingGlobalService<ReplicationLaborantin>{


	//
	// Fields
	//

	/**
	 * 
	 */
	private static final long serialVersionUID = -6071939423880629421L;
	/*
	 * Agent
	 */
	/* Quantile */
	HeavyDoubleAggregation[] agentsReliabilityEvolution;
	/* Mean */
	LightWeightedAverageDoubleAggregation[] criticite;
	/* Disponibility */
	HeavyDoubleAggregation[] agentsDispoEvolution;
	/* Quantile */
	HeavyDoubleAggregation[] agentsSaturationEvolution;
	/* Point */
	// Map<AgentIdentifier, Double> firstReplicationtime =
	// new HashMap<AgentIdentifier, Double>();
	// Map<AgentIdentifier, Double> lifeTime =
	// new HashMap<AgentIdentifier, Double>();
	// Map<AgentIdentifier, Double> lastAction =
	// new HashMap<AgentIdentifier, Double>();
	// Map<AgentIdentifier, Double> protocoleExecutiontime =
	// new HashMap<AgentIdentifier, Double>();
	/*
	 * Host
	 */
	/* Quantile */
	HeavyDoubleAggregation[] hostsChargeEvolution;
	/* Mean */
	LightAverageDoubleAggregation[] faulty;
	private int remainingHost=0;

	//
	// Constructor
	//

	@ProactivityInitialisation
	public final void initiateHostNumber(){
		for (final AgentIdentifier id : getMyAgent().getIdentifiers()) {
			if (id instanceof ResourceIdentifier) {
				this.remainingHost++;
			}
		}
	}
	
	@Override
	public void initiate() {
		this.agentsReliabilityEvolution = new HeavyDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		this.agentsDispoEvolution = new HeavyDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		this.criticite = new LightWeightedAverageDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		this.hostsChargeEvolution = new HeavyDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		this.faulty = new LightAverageDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		this.agentsSaturationEvolution = new HeavyDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];

		for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
			this.hostsChargeEvolution[i] = new HeavyDoubleAggregation();
			this.agentsSaturationEvolution[i] = new HeavyDoubleAggregation();
			this.agentsReliabilityEvolution[i] = new HeavyDoubleAggregation();
			this.agentsDispoEvolution[i] = new HeavyDoubleAggregation();
			this.criticite[i] = new LightWeightedAverageDoubleAggregation();
			this.faulty[i] = new LightAverageDoubleAggregation();
		}
	}


	//
	// Methods
	//


	@Override
	protected void updateInfo(ExperimentationResults notification) {
		if (notification instanceof ReplicationResultAgent){
			final ReplicationResultAgent ag = (ReplicationResultAgent) notification;
			getMyAgent().getSimulationParameters();
			int i = ObservingGlobalService.getTimeStep(ag);


			this.updateAnAgentValue(ag, i);


			if (ag.isLastInfo()) {
				for (i = ObservingGlobalService.getTimeStep(ag) + 1; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
					this.updateAnAgentValue(ag, i);
				}
			}
		} else if (notification instanceof ReplicationResultHost){
			final ReplicationResultHost h = (ReplicationResultHost) notification;
			int i = ObservingGlobalService.getTimeStep(h);
			this.updateAnHostValue(h, i);


			if (h.isLastInfo()) {
				for (i = ObservingGlobalService.getTimeStep(h) + 1;
						i < ObservingGlobalService.getNumberOfTimePoints();
						i++) {
					this.updateAnHostValue(h, i);
				}
			}
		} else
			assert 1<0;			
	}private void updateAnAgentValue(final ReplicationResultAgent ag, final int i) {
		if (i < ObservingGlobalService.getNumberOfTimePoints()) {
			this.agentsSaturationEvolution[i].add(
					(double)ag.getNumberOfAllocatedResources()/
					(getMyAgent().getSimulationParameters()).kAccessible);
			this.agentsReliabilityEvolution[i].add(ag.getReliability());
			this.agentsDispoEvolution[i].add(ag.getDisponibility());
			this.criticite[i].add(ag.disponibility==0. ? 0. : 1., ag.criticity);
			if (getMyAgent().myStatusObserver.iObserveStatus()) {
				getMyAgent().myStatusObserver.incr(ag,i);
			}
		}
		// firstReplicationtime.put(ag.id, );
		// lifeTime.put(ag.id, );
		// lastAction.put(ag.id, );
		// protocoleExecutiontime.put(ag.id, );
	}private void updateAnHostValue(final ReplicationResultHost h, final int i) {
		getMyAgent().getSimulationParameters();
		/**/
		if (i < ObservingGlobalService.getNumberOfTimePoints()) {
			this.hostsChargeEvolution[i].add(h.charge);
			this.faulty[i].add(h.isFaulty ? 0. : 1.);
		}
	}

	@Override
	protected synchronized void writeResult() {
		LogService.logOnFile(
				getMyAgent().getSimulationParameters().getResultPath(),
				"launched :\n--> " + new Date().toString() + "\n "
						+ getMyAgent().getSimulationParameters().getSimulationName()
						+ getMyAgent().getSimulationParameters() + "\n results are :",
						true, false);
		LogService.logOnFile(getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs(getMyAgent().getSimulationParameters(),"reliability",
						this.agentsReliabilityEvolution, 0.75 * (getAliveAgents().size() / getMyAgent()
								.getSimulationParameters().nbAgents), getMyAgent()
								.getSimulationParameters().nbAgents), true,
								false);
		LogService.logOnFile(getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs(getMyAgent().getSimulationParameters(),"disponibilite",
						this.agentsDispoEvolution, 0.75 * (getAliveAgents().size() / getMyAgent()
								.getSimulationParameters().nbAgents), getMyAgent()
								.getSimulationParameters().nbAgents), true,
								false);
		// Taux de survie = moyenne pond��r�� des (wi, li) | li ��� {0,1} agent
		// mort/vivant
		LogService.logOnFile(getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getMeanTimeEvolutionObs(getMyAgent().getSimulationParameters(),"survie : moyenne ponderee des (wi, mort/vivant)", this.criticite,
						0.75 * (getAliveAgents().size() / getMyAgent()
								.getSimulationParameters().nbAgents), getMyAgent()
								.getSimulationParameters().nbAgents), true,
								false);
		// Writing.log(this.p.f, getQuantilePointObs("First Replication Time",
		// firstReplicationtime.values(),0.75*p.nbAgents), true, false);
		// Writing.log(this.p.f, getQuantilePointObs("Life Time",
		// lifeTime.values(),0.75*p.nbAgents), true, false);
		// Writing.log(this.p.f, getQuantilePointObs("Time Since Last Action",
		// lastAction.values(),0.75*p.nbAgents), true, false);
		// Writing.log(this.p.f, getQuantilePointObs("Protocol Execution Time",
		// protocoleExecutiontime.values(),0.75*p.nbAgents), true, false);
		/**/
		LogService.logOnFile(getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs(getMyAgent().getSimulationParameters(),"charge",
						this.hostsChargeEvolution, 0.75,
						getMyAgent().getSimulationParameters().nbHosts), true, false);
		LogService.logOnFile(getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs(getMyAgent().getSimulationParameters(),"agentSaturation",
						this.agentsSaturationEvolution, 0.75,
						getMyAgent().getSimulationParameters().nbAgents), true, false);
		LogService.logOnFile(getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getMeanTimeEvolutionObs(getMyAgent().getSimulationParameters(),"percent of hosts that are alive",
						this.faulty, 0.75,
						getMyAgent().getSimulationParameters().nbHosts), true, false);
		LogService.logOnFile(getMyAgent().getSimulationParameters().getResultPath(), "Optimal? "+this.analyseOptimal(), true, false);
		if (getMyAgent().myStatusObserver.iObserveStatus()) {
			getMyAgent().myStatusObserver.writeStatusResult();
		}
		this.logWarning(this.getIdentifier()+" OOOOOOOOOKKKKKKKKKKKK?????????"+
				this.analyseOptimal()+" for protocol "+getMyAgent().getSimulationParameters()._usedProtocol,
				LogService.onBoth);

	}

	public void setAgentHasEnded(AgentIdentifier id){
		super.setAgentHasEnded(id);
		if (id instanceof ResourceIdentifier)
			remainingHost--;
	}
	
	//
	// Primitives
	//

	private boolean analyseOptimal(){
		final Comparator<ReplicationResultAgent> reliaComp = new Comparator<ReplicationResultAgent>() {
			@Override
			public int compare(final ReplicationResultAgent o1,
					final ReplicationResultAgent o2) {
				return o1.disponibility.compareTo(o2.disponibility);
			}
		};

		final LinkedList<ReplicationResultAgent> reliaStates = new LinkedList<ReplicationResultAgent>();
		reliaStates.addAll(this.finalStates);

		Collections.sort(reliaStates, reliaComp);


		ReplicationResultAgent prev = reliaStates.removeFirst();

		while(!reliaStates.isEmpty()){
			if (prev.getDisponibility()<reliaStates.getFirst().getDisponibility() &&
					prev.criticity>reliaStates.getFirst().criticity) {
				return false;
			}

			prev = reliaStates.removeFirst();
		}
		return true;
	}


	protected void setObservation(){
		//Use to print at the end of the method the observation graph
		final Collection<AgentIdentifier> observedHostResultLog  =
				new ArrayList<AgentIdentifier>();
		final Collection<AgentIdentifier> observedRepResultLog  =
				new ArrayList<AgentIdentifier>();
		final Collection<AgentIdentifier> reliabilityStatusLog  =
				new ArrayList<AgentIdentifier>();
		final HashedHashSet<AgentIdentifier, AgentIdentifier> opinionsLog =
				new HashedHashSet<AgentIdentifier, AgentIdentifier>();
		//Use to print at the end of the method the observation graph

		//Activating status observation
		if (getMyAgent().getSimulationParameters()._usedProtocol.equals(NegotiationParameters.key4CentralisedstatusProto)
				|| getMyAgent().getSimulationParameters()._usedProtocol.equals(NegotiationParameters.key4statusProto)) {
			this.getMyAgent().myStatusObserver.activateCompetence(true);
		} else {
			this.getMyAgent().myStatusObserver.activateCompetence(false);
		}


		for (final BasicCompetentAgent ag : this.getMyAgent().getAgents()) {
			//Observation about agent
			if (ag instanceof Replica || ag instanceof CollaborativeReplica){
				//Observation de l'évolution des états de l'agent
				ag.addObserver(this.getIdentifier(), ActivityLog.class);
				observedRepResultLog.add(ag.getIdentifier());

				//
				if (getMyAgent().getSimulationParameters()._usedProtocol.equals(NegotiationParameters.key4CentralisedstatusProto)){
					//I aggregate agents reliability
					ag.addObserver(this.getIdentifier(), ObservingStatusService.reliabilityObservationKey);//this.addObserver(ag.getIdentifier(),ObservingStatusService.reliabilityObservationKey);???
					reliabilityStatusLog.add(ag.getIdentifier());
					//I forward my opinion to every agents
					this.addObserver(ag.getIdentifier(), SimpleOpinionService.opinionObservationKey);
					opinionsLog.add(ag.getId(), this.getIdentifier());
				} else if (getMyAgent().getSimulationParameters()._usedProtocol.equals(NegotiationParameters.key4statusProto)) {
					//This agent observe every agents that it knows
					for (final AgentIdentifier h :	((Replica)ag).getMyInformation().getKnownAgents()){
						this.getMyAgent().getAgent(h).addObserver(ag.getId(), SimpleOpinionService.opinionObservationKey);
						opinionsLog.add(ag.getId(), h);
					}
				} else if (getMyAgent().getSimulationParameters()._usedProtocol.equals(NegotiationParameters.key4mirrorProto)){
					//no observation
				} else {
					throw new RuntimeException("impossible : ");
				}
			}else if (ag instanceof Host || ag instanceof CollaborativeHost){
				//Observation de l'évolution des états de l'hpte
				ag.addObserver(this.getIdentifier(), ActivityLog.class);
				observedHostResultLog.add(ag.getIdentifier());
				// this.myFaultService.addObserver(h.getId(), FaultEvent.class);
				// this.myFaultService.addObserver(h.getId(), RepairEvent.class)
			} else if (ag instanceof ReplicationLaborantin) {
				this.logMonologue("C'est moi!!!!!!!!!! =D",LogService.onFile);
			} else {
				throw new RuntimeException("impossible");
			}
		}

		String mono = "Setting observation :"
				+"\n * I observe results of "+observedHostResultLog
				+"\n * I observe results of      "+observedRepResultLog
				+"\n * I observe reliability of  "+reliabilityStatusLog;
		for (final AgentIdentifier id : opinionsLog.keySet()) {
			mono += "\n * "+id+" observe opinon of "+opinionsLog.get(id);
		}
		this.logMonologue(mono,LogService.onFile);

	}

	//
	// Termination
	//


	boolean endRequestSended= false;
	public boolean simulationHasEnded(){
		if (this.getAliveAgents().size()==0){
			this.logMonologue("Every agent has finished!!",LogService.onBoth);
			return true;
		}else {
			if (!this.endRequestSended){
				if (getMyAgent().getUptime()>10*ReplicationExperimentationParameters._maxSimulationTime){
					this.signalException("i should have end!!!!(rem ag, rem host)="+getAliveAgents());
					for (final AgentIdentifier r : this.getAliveAgents()) {
						sendMessage(r, new SimulationEndedMessage());
					}
					this.endRequestSended=true;
				} else if (getAliveAgents().size()==remainingHost){
					this.logMonologue("all agents lost! ending ..",LogService.onBoth);
					for (final AgentIdentifier r : this.getAliveAgents()) {
						this.sendMessage(r, new SimulationEndedMessage());
					}
					this.endRequestSended=true;
				}
			}

			return false;
		}
	}
}
