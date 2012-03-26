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

	//
	// Fields
	// ///////////////////////////////////////////

	//	public final static String simulationResultStateObservationKey = "observe the state!";

	HostDisponibilityComputer dispos;


	//
	// Competences
	// ///////////////////////////////////////////


	@Competence
	protected FaultTriggeringService myFaultService;

	@Competence
	protected ObservingStatusService myStatusObserver;
	
	@Competence
	final ObservingGlobalService<ReplicationLaborantin> myGlobalObservationService = new ObservingGlobalService<ReplicationLaborantin>(this){

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
		HeavyAggregation<Double>[] agentsSaturationEvolution;
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
		HeavyAggregation<Double>[] hostsChargeEvolution;
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

	};


	// ///////////////////////////////////////////
	// Constructor
	// ///////////////////////////////////////////


	public ReplicationLaborantin(final ReplicationExperimentationParameters p,final APILauncherModule api, final int numberOfAgentPerMAchine)
			throws CompetenceException, IfailedException, NotEnoughMachinesException {
		super(p, api, numberOfAgentPerMAchine);

//		this.myInformationService = new SimpleObservationService();
//		this.myInformationService.setMyAgent(this);


		this.myGlobalObservationService.initiate();

		this.initialisation();

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

	@Override
	public ReplicationExperimentationParameters getSimulationParameters() {
		return (ReplicationExperimentationParameters) super
				.getSimulationParameters();
	}

	@Override
	public SimpleRationalAgent getAgent(final AgentIdentifier id) {
		return (SimpleRationalAgent) super.getAgent(id);
	}

	// final NormalLaw numberOfKnownHosts = new NormalLaw(this.p.kAccessible,
	// 0);
	private int getNumberOfKnownHosts() {
		// return numberOfKnownHosts.nextValue();
		return this.getSimulationParameters().kAccessible;
	}

	public int getAliveAgentsNumber(){
		return this.remainingAgent.size();
	}

	// ///////////////////////////////////////////
	// Methods
	// ///////////////////////////////////////////


	/*
	 * Instanciation
	 */

	@Override
	protected void instanciate(final ExperimentationParameters par)
			throws IfailedException, CompetenceException {
		final ReplicationExperimentationParameters p = (ReplicationExperimentationParameters) par;

		this.logMonologue("Initializing agents... ",LogService.onBoth);

		/*
		 * Host instanciation
		 */


		final DistributionParameters<ResourceIdentifier> fault = new DistributionParameters<ResourceIdentifier>(
				this.getSimulationParameters().getHostsIdentifier(),
				this.getSimulationParameters().hostFaultProbabilityMean,
				this.getSimulationParameters().hostDisponibilityDispersion);

		for (int i = 0; i < this.getSimulationParameters().nbHosts; i++) {
			final SimpleRationalAgent host = this.constructHost(this
					.getSimulationParameters().getHostsIdentifier().get(i),
					fault);
			this.addAgent(host);
			//			this.setHostObservation(host);
			this.myInformationService.add(host.getMyCurrentState());
		}

		this.logMonologue("Those are my dispos!!!!! :\n" + this.myInformationService.show(HostState.class),LogService.onFile);

		/*
		 * Agent instanciation
		 */

		this.logMonologue("INITIALISING FIRST REPLICA",LogService.onFile);
		for (int i = 0; i < this.getSimulationParameters().nbAgents; i++) {
			/* Adding acquaintance for host within latence */
			final Collection<HostIdentifier> hostsIKnow = new ArrayList<HostIdentifier>();
			Collections.shuffle(this.getSimulationParameters().getHostsIdentifier());
			for (int j = 0;
					j < Math.min(Math.max(2, this.getNumberOfKnownHosts()), this
							.getSimulationParameters().getHostsIdentifier()
							.size()); j++) {
				hostsIKnow.add(this.getSimulationParameters()
						.getHostsIdentifier().get(j));
			}

			final SimpleRationalAgent ag = this.constructAgent(this
					.getSimulationParameters().getReplicasIdentifier().get(i),
					hostsIKnow,
					this.getSimulationParameters().agentCriticity,
					this.getSimulationParameters().agentProcessor,
					this.getSimulationParameters().agentMemory);
			this.addAgent(ag);
			//			this.setAgentObservation(ag);
			this.myInformationService.add(ag.getMyCurrentState());

			/*
			 * First rep
			 */

			try {
				final Iterator<ResourceIdentifier> itHost =
						this.getSimulationParameters().getHostsIdentifier().iterator();
				if (!itHost.hasNext()) {
					throw new RuntimeException("no host? impossible!");
				}

				SimpleRationalAgent firstReplicatedOnHost = this.getAgent(itHost.next());
				MatchingCandidature c = this.generateInitialAllocationCandidature(firstReplicatedOnHost,ag);



				while (!c.computeResultingState(firstReplicatedOnHost.getMySpecif(c)).isValid()) {
					if (!itHost.hasNext()) {
						throw new IfailedException("can not create at least one rep for each agent\n"
								+this.getSimulationParameters().getHostsIdentifier());
					} else {
						firstReplicatedOnHost = this.getAgent(itHost.next());
						c = this.generateInitialAllocationCandidature(firstReplicatedOnHost,ag);
					}
				}

				this.executeFirstRep(c,ag,firstReplicatedOnHost);

			} catch (final Exception e) {
				throw new IfailedException(e);

			}
		}
		this.logMonologue("Initializing agents done!",LogService.onFile);
	}

	//	private void executeFirstRep(
	//			final SimpleRationalAgent<ReplicationSpecification,ReplicationSpecification,MatchingCandidature<ReplicationSpecification>> host,
	//			final MatchingCandidature<ReplicationSpecification> c,
	//			final SimpleRationalAgent ag) {
	//
	//
	//		host.setNewState(
	//				c.computeResultingState(
	//						host.getMyCurrentState()));
	//		host.getMyInformation().add(c.getAgentResultingState());
	//
	//		/*
	//		 *
	//		 */
	//
	//		if (c.isMatchingCreation()) {
	//		} else
	//			throw new RuntimeException();
	//
	//	}

	private MatchingCandidature generateInitialAllocationCandidature(
			final SimpleRationalAgent firstReplicatedOnHost,
			final SimpleRationalAgent ag){

		MatchingCandidature c;

		if (this.getSimulationParameters()._usedProtocol
				.equals(ReplicationExperimentationProtocol.getKey4mirrorproto())){
			final ReplicationCandidature temp = new ReplicationCandidature(
					(ResourceIdentifier) firstReplicatedOnHost.getIdentifier(),
					ag.getIdentifier(),
					true,true);
			//			temp.setSpecification(
			//					(ReplicationSpecification)
			//					((InformedCandidatureRationality) ag.getMyCore())
			//					.getMySimpleSpecif(ag.getMyCurrentState(), temp));
			//			temp.setSpecification(
			//					(ReplicationSpecification)
			//					((InformedCandidatureRationality) firstReplicatedOnHost.getMyCore())
			//					.getMySimpleSpecif(firstReplicatedOnHost.getMyCurrentState(), temp));
			//
			c = new InformedCandidature(temp);
		} else {
			c =
					new ReplicationCandidature(
							(ResourceIdentifier) firstReplicatedOnHost.getIdentifier(),
							ag.getIdentifier(),
							true,true);
		}

		c.setSpecification(ag.getMySpecif(c));
		c.setSpecification(firstReplicatedOnHost.getMySpecif(c));

		return c;
	}

	private void executeFirstRep(
			final MatchingCandidature c,
			final SimpleRationalAgent agent,
			final SimpleRationalAgent host) {
		try {
			assert c.isViable();

			//		logMonologue("Executing first rep!!!!!!!!!!!!!!!!\n"+getMyAgent().getMyCurrentState(), LogService.onScreen);
			if (c.isMatchingCreation()){

				host.addObserver(agent.getIdentifier(),
						SimpleObservationService.informationObservationKey);
				agent.addObserver(host.getIdentifier(),
						SimpleObservationService.informationObservationKey);

				ReplicationHandler.replicate(c.getAgent());

				this.logMonologue(c.getResource() + "  ->I have initially replicated "
						+ c.getAgent(),LogService.onBoth);
			} else {
				throw new RuntimeException();
			}

			host.setNewState(
					c.computeResultingState(host.getMyCurrentState()));
			agent.setNewState(
					c.computeResultingState(agent.getMyCurrentState()));

			agent.getMyInformation().add(c.computeResultingState(host.getIdentifier()));
			host.getMyInformation().add(c.computeResultingState(agent.getIdentifier()));
		} catch (final IncompleteContractException e) {
			throw new RuntimeException();
		}
	}



	protected SimpleRationalAgent constructAgent(final AgentIdentifier replicaId,
			final Collection<HostIdentifier> hostsIKnow,
			final DistributionParameters<AgentIdentifier> agentCriticity,
			final DistributionParameters<AgentIdentifier> agentProcessor,
			final DistributionParameters<AgentIdentifier> agentMemory)
					throws CompetenceException {

		if (this.getSimulationParameters()._usedProtocol
				.equals(ReplicationExperimentationProtocol.getKey4mirrorproto())) { //Collaborative

			final CollaborativeReplica rep = new CollaborativeReplica(
					replicaId,
					Math.min(
							ReplicationExperimentationProtocol._criticityMin
							+ agentCriticity.get(replicaId), 1),
							agentProcessor.get(replicaId), agentMemory.get(replicaId),
							this.getSimulationParameters()._socialWelfare,
							this.getSimulationParameters().dynamicCriticity);

			rep.getMyInformation().addAll(hostsIKnow);
			return rep;
		}else { //Status


			SimpleSelectionCore select;
			if (this.getSimulationParameters()._agentSelection
					.equals(ReplicationExperimentationProtocol.getKey4greedyselect())) {
				select = new SimpleSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false, GreedySelectionType.Greedy);
			} else if (this.getSimulationParameters()._agentSelection
					.equals(ReplicationExperimentationProtocol.getKey4roulettewheelselect())) {
				select = new SimpleSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false, GreedySelectionType.RooletteWheel);
			} else if (this.getSimulationParameters()._agentSelection
					.equals(ReplicationExperimentationProtocol.getKey4allocselect())) {
				throw new RuntimeException(
						"todo!!! "
								+ this.getSimulationParameters()._agentSelection);
				//				select = new AllocationSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false);
			} else {
				throw new RuntimeException(
						"Static parameters est mal conf : agentSelection = "
								+ this.getSimulationParameters()._agentSelection);
			}

			RationalCore core;
			ProposerCore proposer;
			ObservationService informations;

			if (this.getSimulationParameters()._usedProtocol
					.equals(ReplicationExperimentationProtocol.getKey4centralisedstatusproto())){
				core = new CandidatureReplicaCoreWithStatus();
				proposer = new CandidatureReplicaProposerWithStatus();
				informations = new SimpleOpinionService();
				/**/
				if (!this.myStatusObserver.iObserveStatus()) {
					throw new RuntimeException("unappropriate laborantin!");
				}

			} else if (this.getSimulationParameters()._usedProtocol
					.equals(ReplicationExperimentationProtocol.getKey4statusproto())) {
				core = new CandidatureReplicaCoreWithStatus();
				proposer = new CandidatureReplicaProposerWithStatus();
				final Map<AgentIdentifier, Class<? extends Information>> registration = new HashMap<AgentIdentifier, Class<? extends Information>>();
				informations = new SimpleOpinionService();

			} else 	if (this.getSimulationParameters()._usedProtocol
					.equals(ReplicationExperimentationProtocol.getKey4multilatproto())) {
				throw new RuntimeException("unimplemented!");
			} else {
				throw new RuntimeException(
						"Static parameters est mal conf : _usedProtocol = "
								+ this.getSimulationParameters()._usedProtocol);
			}


			final Replica rep = new Replica(replicaId, Math.min(
					ReplicationExperimentationProtocol._criticityMin
					+ agentCriticity.get(replicaId), 1),
					agentProcessor.get(replicaId), agentMemory.get(replicaId), core,
					select, proposer, informations, this.getSimulationParameters().dynamicCriticity);

			rep.getMyInformation().addAll(hostsIKnow);
			return rep;
		}



	}

	protected SimpleRationalAgent constructHost(final ResourceIdentifier hostId,
			final DistributionParameters<ResourceIdentifier> fault)
					throws CompetenceException {


		if (this.getSimulationParameters()._usedProtocol
				.equals(ReplicationExperimentationProtocol.getKey4mirrorproto())) {
			return new CollaborativeHost(
					hostId,
					this.getSimulationParameters().kAccessible * this.getSimulationParameters().hostProcCapacity.get(hostId),
					this.getSimulationParameters().kAccessible * this.getSimulationParameters().hostMemCapacity.get(hostId),
					fault.get(hostId),
					this.getSimulationParameters()._socialWelfare,
					this.dispos);
		} else {

			SimpleSelectionCore select;
			if (this.getSimulationParameters().get_hostSelection()
					.equals(ReplicationExperimentationProtocol.getKey4greedyselect())) {
				select = new SimpleSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false, GreedySelectionType.Greedy);
			} else if (this.getSimulationParameters().get_hostSelection()
					.equals(ReplicationExperimentationProtocol.getKey4roulettewheelselect())) {
				select = new SimpleSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false, GreedySelectionType.RooletteWheel);
			} else if (this.getSimulationParameters().get_hostSelection()
					.equals(ReplicationExperimentationProtocol.getKey4allocselect())) {
				throw new RuntimeException(
						"todo!!! "
								+ this.getSimulationParameters()._agentSelection);
				//				select = new AllocationSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false);
			} else {
				throw new RuntimeException(
						"Static parameters est mal conf : agentSelection = "
								+ this.getSimulationParameters()._agentSelection);
			}

			HostCore core;
			ProposerCore proposer;
			ObservationService informations;


			if (this.getSimulationParameters()._usedProtocol
					.equals(ReplicationExperimentationProtocol.getKey4centralisedstatusproto())) {
				if (!ReplicationLaborantin.this.myStatusObserver.iObserveStatus()) {
					throw new RuntimeException("unappropriate laborantin!"
							+ this.myStatusObserver);
				}
				core = new HostCore(this.getSimulationParameters()._socialWelfare);
				proposer = new InactiveProposerCore();
				informations = new SimpleObservationService();

			} else if (this.getSimulationParameters()._usedProtocol
					.equals(ReplicationExperimentationProtocol.getKey4statusproto())) {
				core = new HostCore(this.getSimulationParameters()._socialWelfare);
				proposer = new InactiveProposerCore();
				informations = new SimpleOpinionService();

			} else 	if (this.getSimulationParameters()._usedProtocol
					.equals(ReplicationExperimentationProtocol.getKey4multilatproto())) {
				throw new RuntimeException("unimplemented!");
			} else {
				throw new RuntimeException(
						"Static parameters est mal conf : _usedProtocol = "
								+ this.getSimulationParameters()._usedProtocol);
			}

			final Host hostAg = new Host(
					hostId,
					this.getSimulationParameters().kAccessible * this.getSimulationParameters().hostProcCapacity.get(hostId),
					this.getSimulationParameters().kAccessible * this.getSimulationParameters().hostMemCapacity.get(hostId),
					fault.get(hostId),
					core, select, proposer, informations, this.dispos);

			return hostAg;
		}
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