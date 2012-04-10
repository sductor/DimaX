package negotiation.faulttolerance.experimentation;

import java.util.ArrayList;
import java.util.Collection;

import negotiation.negotiationframework.contracts.ResourceIdentifier;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dimaxx.experimentation.ExperimentationResults;
import dimaxx.experimentation.ObservingGlobalService;
import dimaxx.experimentation.SimulationEndedMessage;
import dimaxx.tools.aggregator.HeavyAggregation;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;
import dimaxx.tools.aggregator.LightWeightedAverageDoubleAggregation;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ReplicationObservingGlobalService extends ObservingGlobalService{


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

	//
	// Constructor
	//

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
			ReplicationLaborantin.this.getSimulationParameters();
			int i = ObservingGlobalService.getTimeStep(ag);


			this.updateAnAgentValue(ag, i);


			if (ag.isLastInfo()) {
				ReplicationLaborantin.this.getSimulationParameters();
				ReplicationLaborantin.this
				.getSimulationParameters();
				for (i = ObservingGlobalService.getTimeStep(ag) + 1; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
					this.updateAnAgentValue(ag, i);
				}
			}
		} else if (notification instanceof ReplicationResultHost){
			final ReplicationResultHost h = (ReplicationResultHost) notification;
			ReplicationLaborantin.this.getSimulationParameters();
			int i = ObservingGlobalService.getTimeStep(h);
			this.updateAnHostValue(h, i);


			if (h.isLastInfo()) {
				ReplicationLaborantin.this.getSimulationParameters();
				ReplicationLaborantin.this.getSimulationParameters();
				for (i = ObservingGlobalService.getTimeStep(h) + 1;
						i < ObservingGlobalService.getNumberOfTimePoints();
						i++) {
					this.updateAnHostValue(h, i);
				}
			}
		} else
			assert 1<0;			
	}private void updateAnAgentValue(final ReplicationResultAgent ag, final int i) {
		ReplicationLaborantin.this.getSimulationParameters();
		if (i < ObservingGlobalService.getNumberOfTimePoints()) {
			this.agentsSaturationEvolution[i].add(
					(double)ag.getNumberOfAllocatedResources()/
					((ReplicationExperimentationParameters)ReplicationLaborantin.this.getSimulationParameters()).kAccessible);
			this.agentsReliabilityEvolution[i].add(ag.getReliability());
			this.agentsDispoEvolution[i].add(ag.getDisponibility());
			this.criticite[i].add(ag.disponibility==0. ? 0. : 1., ag.criticity);
			if (ReplicationLaborantin.this.myStatusObserver.iObserveStatus()) {
				ReplicationLaborantin.this.myStatusObserver.incr(ag,i);
			}
		}
		// firstReplicationtime.put(ag.id, );
		// lifeTime.put(ag.id, );
		// lastAction.put(ag.id, );
		// protocoleExecutiontime.put(ag.id, );
	}private void updateAnHostValue(final ReplicationResultHost h, final int i) {
		ReplicationLaborantin.this.getSimulationParameters();
		/**/
		if (i < ObservingGlobalService.getNumberOfTimePoints()) {
			this.hostsChargeEvolution[i].add(h.charge);
			this.faulty[i].add(h.isFaulty ? 0. : 1.);
		}
	}

	@Override
	protected synchronized void writeResult() {
		LogService.logOnFile(
				ReplicationLaborantin.this.getSimulationParameters().getResultPath(),
				"launched :\n--> " + new Date().toString() + "\n "
						+ ReplicationLaborantin.this.getSimulationParameters().getSimulationName()
						+ ReplicationLaborantin.this.getSimulationParameters() + "\n results are :",
						true, false);
		LogService.logOnFile(ReplicationLaborantin.this.getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs(ReplicationLaborantin.this.getSimulationParameters(),"reliability",
						this.agentsReliabilityEvolution, 0.75 * (ReplicationLaborantin.this
								.getAliveAgentsNumber() / ReplicationLaborantin.this
								.getSimulationParameters().nbAgents), ReplicationLaborantin.this
								.getSimulationParameters().nbAgents), true,
								false);
		LogService.logOnFile(ReplicationLaborantin.this.getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs(ReplicationLaborantin.this.getSimulationParameters(),"disponibilite",
						this.agentsDispoEvolution, 0.75 * (ReplicationLaborantin.this
								.getAliveAgentsNumber() / ReplicationLaborantin.this
								.getSimulationParameters().nbAgents), ReplicationLaborantin.this
								.getSimulationParameters().nbAgents), true,
								false);
		// Taux de survie = moyenne pond��r�� des (wi, li) | li ��� {0,1} agent
		// mort/vivant
		LogService.logOnFile(ReplicationLaborantin.this.getSimulationParameters().getResultPath(), ObservingGlobalService
				.getMeanTimeEvolutionObs(ReplicationLaborantin.this.getSimulationParameters(),"survie : moyenne ponderee des (wi, mort/vivant)", this.criticite,
						0.75 * (ReplicationLaborantin.this.getAliveAgentsNumber() / ReplicationLaborantin.this
								.getSimulationParameters().nbAgents), ReplicationLaborantin.this
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
		LogService.logOnFile(ReplicationLaborantin.this.getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs(ReplicationLaborantin.this.getSimulationParameters(),"charge",
						this.hostsChargeEvolution, 0.75,
						ReplicationLaborantin.this.getSimulationParameters().nbHosts), true, false);
		LogService.logOnFile(ReplicationLaborantin.this.getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs(ReplicationLaborantin.this.getSimulationParameters(),"agentSaturation",
						this.agentsSaturationEvolution, 0.75,
						ReplicationLaborantin.this.getSimulationParameters().nbAgents), true, false);
		LogService.logOnFile(ReplicationLaborantin.this.getSimulationParameters().getResultPath(), ObservingGlobalService
				.getMeanTimeEvolutionObs(ReplicationLaborantin.this.getSimulationParameters(),"percent of hosts that are alive",
						this.faulty, 0.75,
						ReplicationLaborantin.this.getSimulationParameters().nbHosts), true, false);
		LogService.logOnFile(ReplicationLaborantin.this.getSimulationParameters().getResultPath(), "Optimal? "+this.analyseOptimal(), true, false);
		if (ReplicationLaborantin.this.myStatusObserver.iObserveStatus()) {
			ReplicationLaborantin.this.myStatusObserver.writeStatusResult();
		}
		this.logWarning(this.getIdentifier()+" OOOOOOOOOKKKKKKKKKKKK?????????"+
				this.analyseOptimal()+" for protocol "+ReplicationLaborantin.this.getSimulationParameters()._usedProtocol,
				LogService.onBoth);

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
		if (ReplicationLaborantin.this.getSimulationParameters()._usedProtocol.equals(ReplicationExperimentationProtocol.getKey4centralisedstatusproto())
				|| ReplicationLaborantin.this.getSimulationParameters()._usedProtocol.equals(ReplicationExperimentationProtocol.getKey4statusproto())) {
			this.getMyAgent().myStatusObserver.activateCompetence(true);
		} else {
			this.getMyAgent().myStatusObserver.activateCompetence(false);
		}


		for (final BasicCompetentAgent ag : this.getMyAgent().agents.values()) {
			//Observation about agent
			if (ag instanceof Replica || ag instanceof CollaborativeReplica){
				//Observation de l'évolution des états de l'agent
				ag.addObserver(this.getIdentifier(), ActivityLog.class);
				observedRepResultLog.add(ag.getIdentifier());

				//
				if (ReplicationLaborantin.this.getSimulationParameters()._usedProtocol.equals(ReplicationExperimentationProtocol.getKey4centralisedstatusproto())){
					//I aggregate agents reliability
					ag.addObserver(this.getIdentifier(), ObservingStatusService.reliabilityObservationKey);//this.addObserver(ag.getIdentifier(),ObservingStatusService.reliabilityObservationKey);???
					reliabilityStatusLog.add(ag.getIdentifier());
					//I forward my opinion to every agents
					this.addObserver(ag.getIdentifier(), SimpleOpinionService.opinionObservationKey);
					opinionsLog.add(ag.getId(), this.getIdentifier());
				} else if (ReplicationLaborantin.this.getSimulationParameters()._usedProtocol.equals(ReplicationExperimentationProtocol.getKey4statusproto())) {
					//This agent observe every agents that it knows
					for (final AgentIdentifier h :	((Replica)ag).getMyInformation().getKnownAgents()){
						this.getMyAgent().getAgent(h).addObserver(ag.getId(), SimpleOpinionService.opinionObservationKey);
						opinionsLog.add(ag.getId(), h);
					}
				} else if (ReplicationLaborantin.this.getSimulationParameters()._usedProtocol.equals(ReplicationExperimentationProtocol.getKey4mirrorproto())){
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
		if (this.getUptime()>10*this.getSimulationParameters().getMaxSimulationTime() && (this.remainingAgent.size()>0 || this.remainingHost.size()>0)){
			this.signalException("i should have end!!!!(rem ag, rem host)="
					+this.remainingAgent+","+this.remainingHost);
			for (final AgentIdentifier r : this.remainingHost) {
				this.sendMessage(r, new SimulationEndedMessage());
			}
			for (final AgentIdentifier r : this.remainingAgent) {
				this.sendMessage(r, new SimulationEndedMessage());
			}
			this.remainingAgent.clear();
			this.remainingHost.clear();
			return false;
		} else if (this.remainingAgent.size()<=0){
			//			this.logMonologue("Every agent has finished!!",onBoth);
			if (this.remainingHost.size()<=0){
				return true;
			} else if (!this.endRequestSended){
				this.logMonologue("all agents lost! ending ..",LogService.onBoth);
				for (final ResourceIdentifier r : this.getSimulationParameters().getHostsIdentifier()) {
					this.sendMessage(r, new SimulationEndedMessage());
				}
				this.endRequestSended=true;
				return false;
			} else {
				return false;
			}
		} else{
			this.observer.autoSendOfNotifications();
			return false;
		}
	}
}
