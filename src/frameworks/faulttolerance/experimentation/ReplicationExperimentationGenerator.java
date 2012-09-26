package frameworks.faulttolerance.experimentation;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.deployment.server.HostIdentifier;
import dima.introspectionbasedagents.services.launch.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.experimentation.IfailedException;
import frameworks.experimentation.Laborantin;
import frameworks.experimentation.Laborantin.NotEnoughMachinesException;
import frameworks.faulttolerance.candidaturewithstatus.StatusHost;
import frameworks.faulttolerance.candidaturewithstatus.StatusReplica;
import frameworks.faulttolerance.collaborativecandidature.CollaborativeHost;
import frameworks.faulttolerance.collaborativecandidature.CollaborativeReplica;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.solver.SolverFactory;
import frameworks.negotiation.NegotiationParameters;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.RationalAgent;
import frameworks.negotiation.rationality.SimpleRationalAgent;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;
import frameworks.negotiation.selection.GreedySelectionModule;
import frameworks.negotiation.selection.OptimalSelectionModule;
import frameworks.negotiation.selection.SimpleSelectionCore;
import frameworks.negotiation.selection.GreedySelectionModule.GreedySelectionType;

public class ReplicationExperimentationGenerator extends ReplicationExperimentationParameters{

	ReplicationExperimentationGenerator(int nbAgents, int nbHosts,
			double k, Double hostFaultProbabilityMean,
			DispersionSymbolicValue hostFaultProbabilityDispersion,
			Double agentLoadMean, DispersionSymbolicValue agentLoadDispersion,
			Double hostCapacityMean,
			DispersionSymbolicValue hostcapacityDispersion,
			Double agentCriticityMean,
			DispersionSymbolicValue agentCriticityDispersion,
			String usedProtocol, SocialChoiceType socialWelfare,
			String agentSelection, String hostSelection,
			double alpha_low,
			double alpha_high,
			boolean dynamicCriticty, boolean faultOccurs) {
		super(nbAgents, nbHosts, k,
				hostFaultProbabilityMean, hostFaultProbabilityDispersion,
				agentLoadMean, agentLoadDispersion, hostCapacityMean,
				hostcapacityDispersion, agentCriticityMean, agentCriticityDispersion,
				usedProtocol, socialWelfare, agentSelection, hostSelection,
				alpha_low,
				alpha_high,
				dynamicCriticty, faultOccurs);
		// TODO Auto-generated constructor stub
	}

	public static final int startingNbHosts = 3;
	public static int startingNbAgents =8;
	
	//		startingNbAgents =(int)((startingNbHosts * hostCapacityMean)/agentLoadMean);


	public final boolean completGraph = true;
	public static final boolean multiDim=true;

	private  boolean withOptimal = false;
	private final int maxOptimal = 50;
	
	//
	//	public static final int startingNbHosts = 8;
	//	public static int startingNbAgents =15;

	//		public static final int startingNbHosts = 5;
	//		public static int startingNbAgents =10;


	//
	// Methods
	//

	@Override
	public final void initiateParameters() throws IfailedException{
		this.rig = new ReplicationInstanceGraph(_socialWelfare);

		rig.randomInitiaition(getSimulationName(), randSeed,
				nbAgents, nbHosts, startingNbAgents,
				agentCriticityMean, agentCriticityDispersion, 
				agentLoadMean, agentLoadDispersion, hostCapacityMean, 
				hostCapacityDispersion, hostFaultProbabilityMean, hostFaultProbabilityDispersion, 
				maxHostAccessibleParAgent, agentAccessiblePerHost);


		if (this.withOptimal){
			this.logMonologue("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& wtttttttfffffffffffff ", LogService.onBoth);
//			ReplicationOptimalSolver ros=null;
//			ros = new ReplicationOptimalSolver(this.getMyAgent());
//			this.logMonologue("beggining optimal computation &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&", LogService.onBoth);
//			ros.solve();
//			this.logMonologue("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ending optimal computation ", LogService.onBoth);
		}

		String initialisationStatus = "Neighborhoog... :\n";
		if (this.completGraph) {
			initialisationStatus+="using a complete accesiblity graph";
		} else {
			for (final AgentIdentifier r : this.rig.getAgentsIdentifier()){
				initialisationStatus+=r+"  has acces to  "+this.rig.getAccessibleHosts(r)+"\n";
			}
		}
		initialisationStatus += "\n Initializing allocation... :\n";
		for (final HostState h : this.rig.getHostsStates()){
			initialisationStatus+=h.getMyAgentIdentifier()+"  has allocated  "+h.getMyResourceIdentifiers()+"\n";
		}
		this.logMonologue(initialisationStatus, LogService.onFile);
	}


	//
	// Protocol
	//





	/*
	 * Instanciation
	 */

	@Override
	protected Collection<RationalAgent> instanciateAgents()throws CompetenceException {
		//		System.out.println(this.getMyAgent()+" "+this.getMyAgent().
		//				myStatusObserver);
		//		assert !this._usedProtocol
		//		.equals(NegotiationParameters.key4CentralisedstatusProto) ||
		//		this.getMyAgent().
		//		myStatusObserver.iObserveStatus():
		//			this._usedProtocol
		//			.equals(NegotiationParameters.key4CentralisedstatusProto)+" "+this.getMyAgent().myStatusObserver.iObserveStatus();

		//		this.logMonologue("Initializing agents... ",LogService.onBoth);
		final Map<AgentIdentifier,RationalAgent> result = new HashMap<AgentIdentifier, RationalAgent>();

		/*
		 * Agent instanciation
		 */

		for (final AgentIdentifier replicaId : this.getReplicasIdentifier()) {

			final SimpleRationalAgent rep;
			if (this._usedProtocol
					.equals(NegotiationParameters.key4mirrorProto)) { //Collaborative

				rep = new CollaborativeReplica(
						replicaId,
						this.rig.getAgentState(replicaId),
						this._socialWelfare,
						this.simultaneousCandidature,
						this.dynamicCriticity);

			}else if (this._usedProtocol
					.equals(NegotiationParameters.key4CentralisedstatusProto)){
				rep = new StatusReplica(
						replicaId,
						this.rig.getAgentState(replicaId),
						this.getSelectionCore(this._agentSelection),
						this.simultaneousCandidature,
						this.dynamicCriticity,
						this.getMyAgentIdentifier(),
						alpha_low, alpha_high);
			}else  if (this._usedProtocol.equals(NegotiationParameters.key4statusProto)){ //Status

				rep = new StatusReplica(
						replicaId,
						this.rig.getAgentState(replicaId),
						this.getSelectionCore(this._agentSelection),
						this.simultaneousCandidature,
						this.dynamicCriticity,
						this.opinionDiffusionDegree,
						alpha_low, alpha_high);


			} else {
				throw new RuntimeException("impossible : usedProtocol = "+this._usedProtocol);
			}

			//Ajout des acquaintances
			rep.getMyInformation().addAll(this.rig.getAccessibleHosts(replicaId));

			//gestion des état initiaux
			for (final AgentIdentifier host : rep.getMyCurrentState().getMyResourceIdentifiers()){
				if (rep.getMyCore().iMemorizeMyRessourceState()) {
					rep.getMyInformation().add(this.rig.getHostState((ResourceIdentifier)host));
				}
				if (rep.getMyCore().iObserveMyRessourceChanges()) {
					rep.addObserver(host,
							SimpleRationalAgent.stateChangementObservation);
				}
			}
			result.put(rep.getId(),rep);
			getMyAgent().myInformationService.add(rep.getMyCurrentState());
		}

		/*
		 * Host instanciation
		 */

		for (final ResourceIdentifier hostId : this.getHostsIdentifier()) {

			final RationalAgent hostAg;
			if (this._usedProtocol
					.equals(NegotiationParameters.key4mirrorProto)) {
				hostAg = new CollaborativeHost(
						hostId,
						this.rig.getHostState(hostId),
						this._socialWelfare,
						this.simultaneousAcceptation,
						this.getGreedySelectionType(this._hostSelection),
						this.maxComputingTime);
			}else if (this._usedProtocol
					.equals(NegotiationParameters.key4CentralisedstatusProto)){ //Status
				hostAg = new StatusHost(
						hostId,
						this.rig.getHostState(hostId),
						this.getSelectionCore(this._hostSelection),
						this._socialWelfare,
						this.getMyAgentIdentifier(),
						alpha_low, alpha_high);

			} else if (this._usedProtocol
					.equals(NegotiationParameters.key4statusProto)) {
				hostAg = new StatusHost(
						hostId,
						this.rig.getHostState(hostId),
						this.getSelectionCore(this._hostSelection),
						this._socialWelfare,
						this.opinionDiffusionDegree,
						alpha_low, alpha_high);
			}else {
				throw new RuntimeException("impossible : usedProtocol = "+this._usedProtocol);
			}

			//pas d'acquaintance pour les ressources

			//gestion des état initiaux
			for (final AgentIdentifier ag : hostAg.getMyCurrentState().getMyResourceIdentifiers()){
				if ((hostAg.getMyCore()).iMemorizeMyRessourceState()) {
					hostAg.getMyInformation().add(this.rig.getAgentState(ag));
				}
				if ((hostAg.getMyCore()).iObserveMyRessourceChanges()) {
					hostAg.addObserver(ag,
							SimpleRationalAgent.stateChangementObservation);
				}
			}

			result.put(hostAg.getIdentifier(),hostAg);
			getMyAgent().myInformationService.add(hostAg.getMyCurrentState());
		}

		/*
		 *
		 */


		this.logMonologue("Initializing agents done!:\n" + this.getMyAgent().myInformationService.show(HostState.class) + this.getMyAgent().myInformationService.show(ReplicaState.class),LogService.onFile);
		return result.values();
	}

	private SimpleSelectionCore getSelectionCore(final String selection){

		if (selection
				.equals(NegotiationParameters.key4greedySelect)) {
			return new SimpleSelectionCore(
					true, false, new GreedySelectionModule(GreedySelectionType.Greedy));
		} else if (selection
				.equals(NegotiationParameters.key4rouletteWheelSelect)) {
			return new SimpleSelectionCore(
					true, false, new GreedySelectionModule(GreedySelectionType.RooletteWheel));
		} else if (selection
				.equals(NegotiationParameters.key4randomSelect)) {
			return new SimpleSelectionCore(
					true, false, new GreedySelectionModule(GreedySelectionType.Random));
		} else if (selection
				.equals(NegotiationParameters.key4OptSelect)) {
			return new SimpleSelectionCore(
					true, false, new OptimalSelectionModule(SolverFactory.getLocalSolver(_socialWelfare), true, maxComputingTime));
		}else if (selection
				.equals(NegotiationParameters.key4BetterSelect)) {
			return new SimpleSelectionCore(
					true, false, new OptimalSelectionModule(SolverFactory.getLocalSolver(_socialWelfare), false, maxComputingTime));
		} else {
			throw new RuntimeException(
					"Static parameters est mal conf : selection = "+ selection);
		}
	}
	private GreedySelectionType getGreedySelectionType(final String selection){

		if (selection
				.equals(NegotiationParameters.key4greedySelect)) {
			return GreedySelectionType.Greedy;
		} else if (selection
				.equals(NegotiationParameters.key4rouletteWheelSelect)) {
			return GreedySelectionType.RooletteWheel;
		} else if (selection
				.equals(NegotiationParameters.key4OptSelect)) {
			throw new RuntimeException(
					"todo!!! "+ selection);
			//				select = new AllocationSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false);
		} else {
			throw new RuntimeException(
					"Static parameters est mal conf : selection = "+ selection);
		}
	}

	//
	// Protocole
	//

	//
	//  Génération de simulation
	// /////////////////////////////////

	//
	// Set of values
	//

	static List<String> protos = Arrays.asList(new String[]{
			NegotiationParameters.key4mirrorProto,
			NegotiationParameters.key4CentralisedstatusProto
			//			,NegotiationParameters.key4statusProto
	});
	static List<SocialChoiceType> welfare = Arrays.asList(new SocialChoiceType[]{SocialChoiceType.Utility, SocialChoiceType.Leximin,SocialChoiceType.Nash});//
	static List<String> select = Arrays.asList(new String[]{
			NegotiationParameters.key4greedySelect,
			NegotiationParameters.key4randomSelect,
			NegotiationParameters.key4rouletteWheelSelect});//,key4AllocSelect
	static List<DispersionSymbolicValue> dispersion = Arrays.asList(new DispersionSymbolicValue[]{
			DispersionSymbolicValue.Nul,
			DispersionSymbolicValue.Moyen,
			DispersionSymbolicValue.Max});
	static List<Double> doubleParameters = Arrays.asList(new Double[]{
			0.1,
			0.5,
			1.});
	static List<Double> doubleParameters4 = Arrays.asList(new Double[]{
			0.3,
			0.6,
			1.});
	static List<Double> doubleParameters5 = Arrays.asList(new Double[]{
			0.1,
			0.33
			,0.66
			//			,1.
	});
	static List<Double> doubleParameters2 = Arrays.asList(new Double[]{
			0.,
			0.5,
			1.});
	static List<Double> doubleParameters3 = Arrays.asList(new Double[]{
			0.,
			0.25,
			0.5,
			0.75,
			1.});
	static List<Double> doubleParameters6 = Arrays.asList(new Double[]{
			//			0.01,
			0.1,
			//			0.25,
			0.5,
			//			0.75,
			//			1.
	});
	//pref TODO : Non imple chez l'agent!!
	//	Collection<String> agentPref = Arrays.asList(new String[]{
	//			ReplicationExperimentationProtocol.key4agentKey_Relia,
	//			ReplicationExperimentationProtocol.key4agentKey_loadNRelia});
	//	static final String key4agentKey_Relia="onlyRelia";
	//	static final String key4agentKey_loadNRelia="firstLoadSecondRelia";

	//
	// Variation configuration
	//

	public static final int iterationNumber=10;

	static boolean varyProtocol=false;
	static boolean  varyOptimizers=false;

	static boolean varyAgents=false;
	static boolean varyHosts=false;

	static boolean varyAccessibleHost=false;

	static boolean varyAgentSelection=false;
	static boolean varyHostSelection=false;

	static boolean varyHostDispo=false;
	static boolean varyHostFaultDispersion=false;

	static boolean varyAgentLoad=false;
	static boolean varyAgentLoadDispersion=false;

	static boolean varyHostCapacity=false;
	static boolean varyHostCapacityDispersion=false;

	static boolean varyAgentCriticity=false;
	static boolean varyAgentCriticityDispersion=false;

//	static boolean varyFault=false;
	static int dynamicCriticityKey=-1; //-1 never dynamics, 1 always dynamics, 0 both


	//
	// Default values
	//

	static ReplicationExperimentationParameters getDefaultParameters() {
		return new ReplicationExperimentationParameters(
				ReplicationExperimentationGenerator.startingNbAgents,
				ReplicationExperimentationGenerator.startingNbHosts,
				0.5,//ReplicationExperimentationParameters.doubleParameters.get(2),//kaccessible
				0.7,//dispo mean
				DispersionSymbolicValue.Moyen,//dispo dispersion
				1.,//0.25,//ReplicationExperimentationProtocol.doubleParameters.get(1),//load mean
				DispersionSymbolicValue.Nul,//DispersionSymbolicValue.Moyen,//load dispersion
				3.,//(double)ReplicationExperimentationParameters.startingNbAgents/(double)ReplicationExperimentationParameters.startingNbHosts,//ReplicationExperimentationParameters.doubleParameters.get(1),//capacity mean2.5,//
				DispersionSymbolicValue.Nul,//DispersionSymbolicValue.Faible,//capcity dispersion
				ReplicationExperimentationGenerator.doubleParameters.get(1),//criticity mean
				DispersionSymbolicValue.Fort,//criticity dispersion
				NegotiationParameters.key4mirrorProto,//NegotiationParameters.key4statusProto,//NegotiationParameters.key4CentralisedstatusProto,//
				SocialChoiceType.Utility,
				NegotiationParameters.key4greedySelect,//NegotiationParameters.key4rouletteWheelSelect,//
				NegotiationParameters.key4rouletteWheelSelect,//NegotiationParameters.key4BetterSelect,//NegotiationParameters.key4greedySelect,//
				0.3,
				0.6,
				false,
				false);
	}

	public static String getProtocolId() {
		return ExperimentationParameters._maxSimulationTime / 1000
				+ "secs"
				+ (ReplicationExperimentationGenerator.varyAgentSelection==true?"varyAgentSelection":"")
				+ (ReplicationExperimentationGenerator.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationGenerator.varyProtocol?"varyProtocol":"")
				+ (ReplicationExperimentationGenerator.varyHostDispo?"varyHostDispo":"")
				+ (ReplicationExperimentationGenerator.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationGenerator.varyOptimizers?"varyOptimizers":"")
				+ (ReplicationExperimentationGenerator.varyAccessibleHost?"varyAccessibleHost":"")
				+ (ReplicationExperimentationGenerator.varyAgentLoad?"varyAgentLoad":"")
				+ (ReplicationExperimentationGenerator.varyHostCapacity?"varyHostCapacity":"");
	}

	@Override
	public LinkedList<ExperimentationParameters<ReplicationLaborantin>> generateSimulation() {
		//		final String usedProtocol, agentSelection, hostSelection;
		//		f.mkdirs();
		new File(LogService.getMyPath()+"result_"+ReplicationExperimentationGenerator.getProtocolId()+"/").mkdirs();
		Collection<ReplicationExperimentationParameters> simuToLaunch =
				new HashSet<ReplicationExperimentationParameters>();
		simuToLaunch.add(ReplicationExperimentationGenerator.getDefaultParameters());
		if (ReplicationExperimentationGenerator.varyAgents) {
			simuToLaunch = this.varyAgents(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHosts) {
			simuToLaunch = this.varyHosts(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAccessibleHost) {
			simuToLaunch = this.varyAccessibleHost(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHostDispo) {
			simuToLaunch = this.varyHostDispo(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHostFaultDispersion) {
			simuToLaunch = this.varyHostFaultDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAgentLoad) {
			simuToLaunch = this.varyAgentLoad(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAgentLoadDispersion) {
			simuToLaunch = this.varyAgentLoadDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHostCapacity) {
			simuToLaunch = this.varyHostCapacity(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHostCapacityDispersion) {
			simuToLaunch = this.varyHostCapacityDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAgentCriticity) {
			simuToLaunch = this.varyAgentCriticity(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAgentCriticityDispersion) {
			simuToLaunch = this.varyAgentCriticityDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAgentSelection) {
			simuToLaunch = this.varyAgentSelection(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHostSelection) {
			simuToLaunch = this.varyHostSelection(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyOptimizers) {
			simuToLaunch = this.varyOptimizers(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyProtocol) {
			simuToLaunch = this.varyProtocol(simuToLaunch);
		}
//		if (ReplicationExperimentationGenerator.varyFault) {
//			simuToLaunch = this.varyMaxSimultFailure(simuToLaunch);
//		}

		simuToLaunch = this.varyDynamicCriticity(simuToLaunch);

		final Comparator<ExperimentationParameters<ReplicationLaborantin>> comp =
				new Comparator<ExperimentationParameters<ReplicationLaborantin>>() {

			@Override
			public int compare(final ExperimentationParameters<ReplicationLaborantin> o1,
					final ExperimentationParameters<ReplicationLaborantin> o2) {
				return o1.getSimulationName().compareTo(o2.getSimulationName());
			}
		};

		final LinkedList<ExperimentationParameters<ReplicationLaborantin>> simus =
				new LinkedList<ExperimentationParameters<ReplicationLaborantin>>();
		for (final ReplicationExperimentationParameters p : simuToLaunch) {
			if (this.isValid(p)){
				simus.add(p);
			}else{
				//				this.logWarning("ABORTED !!! "+(p.nbHosts*p.agentAccessiblePerHost<p.nbAgents)+" "+(p.agentAccessiblePerHost<=0)+" \n"+p, LogService.onBoth);
			}
		}
		Collections.sort(simus,comp);
		return simus;
	}


	private boolean isValid(final ReplicationExperimentationParameters p) {
		if (p.nbHosts*p.agentAccessiblePerHost<this.nbAgents || p.agentAccessiblePerHost<=0) {
			return false;
		}

		return true;
	}

	@Override
	public Laborantin createLaborantin(final APILauncherModule api)
			throws CompetenceException, IfailedException,
			NotEnoughMachinesException {
		final ReplicationLaborantin l = new ReplicationLaborantin(this, api);
		this.setMyAgent(l);
		return l;
	}


	/*
	 *Distribution
	 */

	final double nbSimuPerMAchine = 1;
	@Override
	public Integer getMaxNumberOfAgent(final HostIdentifier id) {
		return new Integer((int) this.nbSimuPerMAchine*
				//				(nbAgents + nbHosts)+1);
				(ReplicationExperimentationGenerator.startingNbAgents + ReplicationExperimentationGenerator.startingNbHosts)+1);
		//		return new Integer(10);
	}


	/*
	 *
	 */


	//
	// Primitive
	//



	private Collection<ReplicationExperimentationParameters> varyProtocol(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationGenerator.protos){
				final ReplicationExperimentationParameters n =  p.clone();
				n._usedProtocol=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentSelection(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationGenerator.select){
				final ReplicationExperimentationParameters n =  p.clone();
				n.setAgentSelection(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostSelection(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationGenerator.select){
				final ReplicationExperimentationParameters n =  p.clone();
				n._hostSelection=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyOptimizers(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final SocialChoiceType v : ReplicationExperimentationGenerator.welfare){
				final ReplicationExperimentationParameters n =  p.clone();
				n._socialWelfare=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgents(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationGenerator.doubleParameters6){
				final ReplicationExperimentationParameters n =  p.clone();
				n.nbAgents=(int)(v*ReplicationExperimentationGenerator.startingNbAgents);
				//				n.nbAgents=(int)((v  * n.nbHosts * n.hostCapacityMean)/n.agentLoadMean);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgents2(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			final List<Integer> nbAgentsList = Arrays.asList(new Integer[]{10,20,40,80,100,150,200,350,500,750,1000,2000,5000});
			for (final Integer v : nbAgentsList){
				final ReplicationExperimentationParameters n =  p.clone();
				n.nbAgents=v;
				result.add(n);
			}

			//			for (final Double v : ReplicationExperimentationParameters.doubleParameters6){
			//				final ReplicationExperimentationParameters n =  p.clone();
			//				n.nbAgents=(int)(v*ReplicationExperimentationParameters.startingNbAgents);
			////				n.nbAgents=(int)((v  * n.nbHosts * n.hostCapacityMean)/n.agentLoadMean);
			//				result.add(n);
			//			}

			//
			//
			//				final ReplicationExperimentationParameters n1 =  p.clone();
			//				n1.nbAgents=50;
			//				result.add(n1);
			//				final ReplicationExperimentationParameters n2 =  p.clone();
			//				n2.nbAgents=80;
			//				result.add(n2);
			//				final ReplicationExperimentationParameters n3 =  p.clone();
			//				n3.nbAgents=150;
			//				result.add(n3);
			//				final ReplicationExperimentationParameters n4 =  p.clone();
			//				n4.nbAgents=300;
			//				result.add(n4);


		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHosts(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationGenerator.doubleParameters6){
				final ReplicationExperimentationParameters n =  p.clone();
				n.nbHosts=(int)(v*ReplicationExperimentationGenerator.startingNbHosts);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAccessibleHost(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationGenerator.doubleParameters4){
				final ReplicationExperimentationParameters n =  p.clone();
				n.setkAccessible(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostDispo(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationGenerator.doubleParameters5){
				final ReplicationExperimentationParameters n =  p.clone();
				n.hostFaultProbabilityMean=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyHostFaultDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationGenerator.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostFaultProbabilityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoad(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationGenerator.doubleParameters5){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadMean=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoadDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationGenerator.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadDispersion=v;
				result.add(n);
			}
		}
		return result;
	}
	//	private Collection<ReplicationExperimentationParameters> varyHostCapacity2(final Collection<ReplicationExperimentationParameters> exps){
	//		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
	//		for (final ReplicationExperimentationParameters p : exps) {
	//			for (final Double v : ReplicationExperimentationParameters.doubleParameters){
	//				final ReplicationExperimentationParameters n = p.clone();
	//				n.hostCapacityMean=v;
	//				result.add(n);
	//			}
	//		}
	//		return result;
	//	}

	private Collection<ReplicationExperimentationParameters> varyHostCapacity(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		final List<Double> capacitiesList = Arrays.asList(new Double[]{0.10,0.30,0.60,1.});
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : capacitiesList){
				//variant resources capacity
				final ReplicationExperimentationParameters n = p.clone();
				n.hostCapacityMean=v*p.nbAgents;
				if (n.hostCapacityMean>n.nbAgents/n.nbHosts){
					result.add(n);
				}
				//fixed resources capacity
				final ReplicationExperimentationParameters n2 = p.clone();
				n2.hostCapacityMean=(double)ReplicationExperimentationGenerator.startingNbAgents/(double)ReplicationExperimentationGenerator.startingNbHosts;
				if (n2.hostCapacityMean>n2.nbAgents/n2.nbHosts){
					result.add(n2);
				}
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostCapacityDispersion(
			final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationGenerator.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostCapacityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentCriticity(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationGenerator.doubleParameters){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentCriticityMean=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentCriticityDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationGenerator.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentCriticityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}

//	private Collection<ReplicationExperimentationParameters> varyMaxSimultFailure(final Collection<ReplicationExperimentationParameters> exps){
//		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
//		for (final ReplicationExperimentationParameters p : exps) {
//			for (final Double v : ReplicationExperimentationGenerator.doubleParameters2){
//				final ReplicationExperimentationParameters n = p.clone();
//				n.setMaxSimultFailure(v);
//				result.add(n);
//			}
//		}
//		return result;
//	}
	private Collection<ReplicationExperimentationParameters> varyDynamicCriticity(
			final Collection<ReplicationExperimentationParameters> exps) {
		assert ReplicationExperimentationGenerator.dynamicCriticityKey>=-1 && ReplicationExperimentationGenerator.dynamicCriticityKey<=1;
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			if (ReplicationExperimentationGenerator.dynamicCriticityKey==-1){
				p.dynamicCriticity=false;
				result.add(p);
			} else if (ReplicationExperimentationGenerator.dynamicCriticityKey==1){
				p.dynamicCriticity=true;
				result.add(p);
			} else {
				final ReplicationExperimentationParameters n = p.clone();
				n.dynamicCriticity=!p.dynamicCriticity;
				result.add(n);
			}
		}
		return result;
	}

}
