package negotiation.faulttolerance.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import negotiation.experimentationframework.ExperimentationParameters;
import negotiation.experimentationframework.ExperimentationProtocol;
import negotiation.experimentationframework.ExperimentationResults;
import negotiation.experimentationframework.IfailedException;
import negotiation.experimentationframework.Laborantin;
import negotiation.experimentationframework.ObservingGlobalService;
import negotiation.faulttolerance.candidaturenegotiation.mirrordestruction.CandidatureReplicaCoreWithDestruction;
import negotiation.faulttolerance.candidaturenegotiation.mirrordestruction.HostDestructionCandidatureProposer;
import negotiation.faulttolerance.candidaturenegotiation.statusdestruction.CandidatureReplicaCoreWithStatus;
import negotiation.faulttolerance.candidaturenegotiation.statusdestruction.CandidatureReplicaProposerWithStatus;
import negotiation.faulttolerance.faulsimulation.FaultTriggeringService;
import negotiation.faulttolerance.faulsimulation.HostDisponibilityComputer;
import negotiation.faulttolerance.negotiatingagent.HostCore;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.Host;
import negotiation.faulttolerance.negotiatingagent.Replica;
import negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.agent.RationalCore;
import negotiation.negotiationframework.agent.SimpleRationalAgent;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import negotiation.negotiationframework.interaction.candidatureprotocol.CandidatureReplicaProposer;
import negotiation.negotiationframework.interaction.consensualnegotiation.AbstractProposerCore;
import negotiation.negotiationframework.interaction.consensualnegotiation.InactiveProposerCore;
import negotiation.negotiationframework.interaction.selectioncores.AbstractSelectionCore;
import negotiation.negotiationframework.interaction.selectioncores.AllocationSelectionCore;
import negotiation.negotiationframework.interaction.selectioncores.GreedyBasicSelectionCore;
import negotiation.negotiationframework.interaction.selectioncores.GreedyRouletteWheelSelectionCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.APILauncherModule;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.library.information.ObservationService;
import dima.introspectionbasedagents.services.library.information.ObservationService.Information;
import dima.introspectionbasedagents.services.library.information.SimpleObservationService;
import dima.introspectionbasedagents.services.library.information.SimpleOpinionService;
import dimaxx.server.HostIdentifier;
import dimaxx.tools.aggregator.HeavyAggregation;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;
import dimaxx.tools.aggregator.LightWeightedAverageDoubleAggregation;
import dimaxx.tools.distribution.DistributionParameters;

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
	final ObservingGlobalService myGlobalObservationService = new ObservingGlobalService(this, this.getSimulationParameters()){

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
			this.agentsReliabilityEvolution = new HeavyDoubleAggregation[this.getSimulationParameters().getNumberOfTimePoints()];
			this.criticite = new LightWeightedAverageDoubleAggregation[this.getSimulationParameters().getNumberOfTimePoints()];
			this.hostsChargeEvolution = new HeavyDoubleAggregation[this.getSimulationParameters().getNumberOfTimePoints()];
			this.faulty = new LightAverageDoubleAggregation[this.getSimulationParameters().getNumberOfTimePoints()];

			for (int i = 0; i < this.getSimulationParameters().getNumberOfTimePoints(); i++) {
				this.hostsChargeEvolution[i] = new HeavyDoubleAggregation();
				this.agentsReliabilityEvolution[i] = new HeavyDoubleAggregation();
				this.criticite[i] = new LightWeightedAverageDoubleAggregation();
				this.faulty[i] = new LightAverageDoubleAggregation();
			}
		}

		//
		// Methods
		//
		
	

		@Override
		public void updateAgentInfo(final ExperimentationResults agent) {
			final ReplicationResultAgent ag = (ReplicationResultAgent) agent;
			int i = this.getSimulationParameters().getTimeStep(ag);
		
			
			this.updateAnAgentValue(ag, i);
		

			if (ag.isLastInfo())
				for (i = this.getSimulationParameters().getTimeStep(ag) + 1; i < this
						.getSimulationParameters().getNumberOfTimePoints(); i++)
					this.updateAnAgentValue(ag, i);
		}private void updateAnAgentValue(final ReplicationResultAgent ag, final int i) {
			if (i < this.getSimulationParameters().getNumberOfTimePoints()) {
				this.agentsReliabilityEvolution[i].add(ag.getReliability());
				this.criticite[i].add(ag.disponibility==0. ? 0. : 1., ag.criticity);
				if (ReplicationLaborantin.this.myStatusObserver.iObserveStatus())
					ReplicationLaborantin.this.myStatusObserver.incr(ag,i);
			}
			// firstReplicationtime.put(ag.id, );
			// lifeTime.put(ag.id, );
			// lastAction.put(ag.id, );
			// protocoleExecutiontime.put(ag.id, );
		}

		@Override
		public void updateHostInfo(final ExperimentationResults host) {
			final ReplicationResultHost h = (ReplicationResultHost) host;
			int i = this.getSimulationParameters().getTimeStep(h);
			this.updateAnHostValue(h, i);

			
			if (h.isLastInfo())
				for (i = this.getSimulationParameters().getTimeStep(h) + 1;
						i < this.getSimulationParameters().getNumberOfTimePoints();
						i++)
					this.updateAnHostValue(h, i);
		}private void updateAnHostValue(final ReplicationResultHost h, final int i) {
			/**/
			if (i < this.getSimulationParameters().getNumberOfTimePoints()) {
				this.hostsChargeEvolution[i].add(h.charge);
				this.faulty[i].add(h.isFaulty ? 0. : 1.);
			}
		}

		@Override
		protected synchronized void writeResult() {
			LogService.logOnFile(
					this.getSimulationParameters().getF(),
					"launched :\n--> " + new Date().toString() + "\n "
							+ this.getSimulationParameters().getName()
							+ this.getSimulationParameters() + "\n results are :",
							true, false);
			LogService.logOnFile(this.getSimulationParameters().getF(), ObservingGlobalService
					.getQuantileTimeEvolutionObs(this.getSimulationParameters(),"reliability",
							this.agentsReliabilityEvolution, 0.75 * (ReplicationLaborantin.this
									.getAliveAgentsNumber() / this
									.getSimulationParameters().nbAgents), this
									.getSimulationParameters().nbAgents), true,
									false);
			// Taux de survie = moyenne pond��r�� des (wi, li) | li ��� {0,1} agent
			// mort/vivant
			LogService.logOnFile(ReplicationLaborantin.this.getSimulationParameters().getF(), ObservingGlobalService
					.getMeanTimeEvolutionObs(this.getSimulationParameters(),"criticity", this.criticite,
							0.75 * (ReplicationLaborantin.this.getAliveAgentsNumber() / this
									.getSimulationParameters().nbAgents), this
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
			LogService.logOnFile(this.getSimulationParameters().getF(), ObservingGlobalService
					.getQuantileTimeEvolutionObs(this.getSimulationParameters(),"charge",
							this.hostsChargeEvolution, 0.75,
							this.getSimulationParameters().nbHosts), true, false);
			LogService.logOnFile(this.getSimulationParameters().getF(), ObservingGlobalService
					.getMeanTimeEvolutionObs(this.getSimulationParameters(),"percent of hosts that are alive",
							this.faulty, 0.75,
							this.getSimulationParameters().nbHosts), true, false);

			if (ReplicationLaborantin.this.myStatusObserver.iObserveStatus())
				ReplicationLaborantin.this.myStatusObserver.writeStatusResult();
			this.logWarning(this.getIdentifier()+" OOOOOOOOOKKKKKKKKKKKK?????????"+
					this.analyseOptimal()+" for protocol "+this.getSimulationParameters()._usedProtocol,
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
						prev.criticity>reliaStates.getFirst().criticity)
					return false;

				prev = reliaStates.removeFirst();
			}
			return true;
		}


	};


	// ///////////////////////////////////////////
	// Constructor
	// ///////////////////////////////////////////


	public ReplicationLaborantin(final ReplicationExperimentationParameters p,final APILauncherModule api, final int numberOfAgentPerMAchine)
			throws CompetenceException, IfailedException, NotEnoughMachinesException {
		super(p, api, numberOfAgentPerMAchine);

		final Collection<AgentIdentifier> everyone = new ArrayList<AgentIdentifier>();
		everyone.addAll(this.getSimulationParameters().getHostsIdentifier());
		everyone.addAll(this.getSimulationParameters().getReplicasIdentifier());

		this.myInformationService = new SimpleObservationService();
		this.myInformationService.setMyAgent(this);

		this.myFaultService = new FaultTriggeringService(this
				.getSimulationParameters().getName(),  everyone);

		this.myGlobalObservationService.initiate();

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
		return this.getSimulationParameters().getkAccessible();
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
			this.myInformationService.add(((Host) host).getMyCurrentState());
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
							.size()); j++)
				hostsIKnow.add(this.getSimulationParameters()
						.getHostsIdentifier().get(j));

			final SimpleRationalAgent ag = this.constructAgent(this
					.getSimulationParameters().getReplicasIdentifier().get(i),
					hostsIKnow,
					this.getSimulationParameters().agentCriticity,
					this.getSimulationParameters().agentProcessor,
					this.getSimulationParameters().agentMemory);
			this.addAgent(ag);
			//			this.setAgentObservation(ag);

			/*
			 * First rep
			 */

			try {
				final Iterator<ResourceIdentifier> itHost = this.getSimulationParameters().getHostsIdentifier().iterator();
				if (!itHost.hasNext())
					throw new RuntimeException("no host? impossible!");

				SimpleRationalAgent firstReplicatedOnHost = this.getAgent(itHost.next());
				ReplicationCandidature c =
						new ReplicationCandidature(
								(ResourceIdentifier) firstReplicatedOnHost.getIdentifier(),
								ag.getIdentifier(),
								true,true);
				c.setSpecification((ReplicationSpecification) ag.getMySpecif(c));
				c.setSpecification((ReplicationSpecification) firstReplicatedOnHost.getMySpecif(c));


				while (!firstReplicatedOnHost
						.respectMyRights(c.computeResultingState((HostState) firstReplicatedOnHost.getMySpecif(c))))
					if (!itHost.hasNext())
						throw new IfailedException("can not create at least one rep for each agent\n"
								+this.getSimulationParameters().getHostsIdentifier());
					else {
						firstReplicatedOnHost = this.getAgent(itHost.next());
						c = new ReplicationCandidature(
								(ResourceIdentifier) firstReplicatedOnHost
								.getIdentifier(),
								ag.getIdentifier(), true,true);
						c.setSpecification((ReplicationSpecification) ag.getMySpecif(c));
						c.setSpecification((ReplicationSpecification) firstReplicatedOnHost
								.getMySpecif(c));
					}

				((ReplicaCore) ag.myCore).executeFirstRep(c,firstReplicatedOnHost);
				((HostCore) firstReplicatedOnHost.myCore).executeFirstRep(c, ag);
			} catch (final Exception e) {
				throw new IfailedException(e);
			}
		}
		this.logMonologue("Initializing agents done!",LogService.onFile);
	}

	protected Replica constructAgent(final AgentIdentifier replica,
			final Collection<HostIdentifier> hostsIKnow,
			final DistributionParameters<AgentIdentifier> agentCriticity,
			final DistributionParameters<AgentIdentifier> agentProcessor,
			final DistributionParameters<AgentIdentifier> agentMemory)
					throws CompetenceException {
		AbstractSelectionCore select;
		if (this.getSimulationParameters()._agentSelection
				.equals(ExperimentationProtocol.key4greedySelect))
			select = new GreedyBasicSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false);
		else if (this.getSimulationParameters()._agentSelection
				.equals(ExperimentationProtocol.key4rouletteWheelSelect))
			select = new GreedyRouletteWheelSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false);
		else if (this.getSimulationParameters()._agentSelection
				.equals(ExperimentationProtocol.key4AllocSelect))
			select = new AllocationSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false);
		else
			throw new RuntimeException(
					"Static parameters est mal conf : agentSelection = "
							+ this.getSimulationParameters()._agentSelection);

		RationalCore core;
		AbstractProposerCore proposer;
		ObservationService informations;

		if (this.getSimulationParameters()._usedProtocol
				.equals(ExperimentationProtocol.key4mirrorProto)) {
			core = new CandidatureReplicaCoreWithDestruction(this.getSimulationParameters()._socialWelfare);
			proposer = new CandidatureReplicaProposer();
			informations = new SimpleObservationService();

		} else if (this.getSimulationParameters()._usedProtocol
				.equals(ExperimentationProtocol.key4CentralisedstatusProto)){
			core = new CandidatureReplicaCoreWithStatus();
			proposer = new CandidatureReplicaProposerWithStatus();
			informations = new SimpleOpinionService();
			/**/
			if (!this.myStatusObserver.iObserveStatus())
				throw new RuntimeException("unappropriate laborantin!");

		} else if (this.getSimulationParameters()._usedProtocol
				.equals(ExperimentationProtocol.key4statusProto)) {
			core = new CandidatureReplicaCoreWithStatus();
			proposer = new CandidatureReplicaProposerWithStatus();
			final Map<AgentIdentifier, Class<? extends Information>> registration = new HashMap<AgentIdentifier, Class<? extends Information>>();
			informations = new SimpleOpinionService();

		} else 	if (this.getSimulationParameters()._usedProtocol
				.equals(ExperimentationProtocol.key4multiLatProto))
			throw new RuntimeException("unimplemented!");
		else
			throw new RuntimeException(
					"Static parameters est mal conf : _usedProtocol = "
							+ this.getSimulationParameters()._usedProtocol);

		final Replica rep = new Replica(replica, Math.min(
				ReplicationExperimentationParameters._criticityMin
				+ agentCriticity.get(replica), 1),
				agentProcessor.get(replica), agentMemory.get(replica), core,
				select, proposer, informations);

		rep.getMyInformation().addAll(hostsIKnow);

		return rep;

	}

	protected Host constructHost(final ResourceIdentifier host,
			final DistributionParameters<ResourceIdentifier> fault)
					throws CompetenceException {
		AbstractSelectionCore select;
		if (this.getSimulationParameters().get_hostSelection()
				.equals(ExperimentationProtocol.key4greedySelect))
			select = new GreedyBasicSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false);
		else if (this.getSimulationParameters().get_hostSelection()
				.equals(ExperimentationProtocol.key4rouletteWheelSelect))
			select = new GreedyRouletteWheelSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false);
		else if (this.getSimulationParameters().get_hostSelection()
				.equals(ExperimentationProtocol.key4AllocSelect))
			select = new AllocationSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false);
		else
			throw new RuntimeException(
					"Static parameters est mal conf : agentSelection = "
							+ this.getSimulationParameters()._agentSelection);

		HostCore core;
		AbstractProposerCore proposer;
		ObservationService informations;
		if (this.getSimulationParameters()._usedProtocol
				.equals(ExperimentationProtocol.key4mirrorProto)) {
			core = new HostCore(true,this.getSimulationParameters()._socialWelfare);
			proposer = new HostDestructionCandidatureProposer();
			informations = new SimpleObservationService();
		} else if (this.getSimulationParameters()._usedProtocol
				.equals(ExperimentationProtocol.key4CentralisedstatusProto)) {
			if (!ReplicationLaborantin.this.myStatusObserver.iObserveStatus())
				throw new RuntimeException("unappropriate laborantin!"
						+ this.myStatusObserver);
			core = new HostCore(false,this.getSimulationParameters()._socialWelfare);
			proposer = new InactiveProposerCore();
			informations = new SimpleObservationService();

		} else if (this.getSimulationParameters()._usedProtocol
				.equals(ExperimentationProtocol.key4statusProto)) {
			core = new HostCore(false,this.getSimulationParameters()._socialWelfare);
			proposer = new InactiveProposerCore();
			informations = new SimpleOpinionService();

		} else 	if (this.getSimulationParameters()._usedProtocol
				.equals(ExperimentationProtocol.key4multiLatProto))
			throw new RuntimeException("unimplemented!");
		else
			throw new RuntimeException(
					"Static parameters est mal conf : _usedProtocol = "
							+ this.getSimulationParameters()._usedProtocol);

		final Host hostAg = new Host(host, fault.get(host),
				core, select, proposer, informations, this.dispos);

		return hostAg;
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