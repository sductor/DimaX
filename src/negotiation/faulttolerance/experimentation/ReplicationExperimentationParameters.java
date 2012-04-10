package negotiation.faulttolerance.experimentation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.JDOMException;

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
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.protocoles.InactiveProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import negotiation.negotiationframework.rationality.SocialChoiceFunctions;
import negotiation.negotiationframework.selection.SimpleSelectionCore;
import negotiation.negotiationframework.selection.GreedySelectionModule.GreedySelectionType;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.information.SimpleOpinionService;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.replication.ReplicationHandler;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.ExperimentationProtocol;
import dimaxx.experimentation.Experimentator;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.ObservingGlobalService;
import dimaxx.experimentation.Laborantin.NotEnoughMachinesException;
import dimaxx.server.HostIdentifier;
import dimaxx.tools.distribution.DistributionParameters;
import dimaxx.tools.distribution.NormalLaw.DispersionSymbolicValue;

public class ReplicationExperimentationParameters extends
ExperimentationParameters {
	private static final long serialVersionUID = -7191963637040889163L;

	//	final AgentIdentifier experimentatorId;

	private boolean initiated=false;

	/***
	 * Variables
	 */
	
	public int nbAgents;
	public int nbHosts;

	public String _usedProtocol;
	public String _agentSelection;
	public String _hostSelection;
	public String _socialWelfare;

	List<AgentIdentifier> replicasIdentifier  = new ArrayList<AgentIdentifier>();
	List<ResourceIdentifier> hostsIdentifier = new ArrayList<ResourceIdentifier>();

	public int kAccessible;

	public Double hostFaultProbabilityMean;
	public  DispersionSymbolicValue hostDisponibilityDispersion;

	public  Double agentLoadMean;
	public DispersionSymbolicValue agentLoadDispersion;

	public Double hostCapacityMean;
	public DispersionSymbolicValue hostCapacityDispersion;

	public Double agentCriticityMean;
	public DispersionSymbolicValue agentCriticityDispersion;

	public Boolean dynamicCriticity;
	public Double host_maxSimultaneousFailure;

	DistributionParameters<AgentIdentifier> agentCriticity;
	DistributionParameters<AgentIdentifier> agentProcessor;
	DistributionParameters<AgentIdentifier> agentMemory;


	DistributionParameters<ResourceIdentifier> hostProcCapacity;
	DistributionParameters<ResourceIdentifier> hostMemCapacity;


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
	};


	//
	// Constructor
	//

	ReplicationExperimentationParameters(final File resultPath,
			final AgentIdentifier experimentatorId,
			final int nbAgents, final int nbHosts, final double k,
			final Double hostFaultProbabilityMean,
			final DispersionSymbolicValue hostFaultProbabilityDispersion,
			final Double agentLoadMean,
			final DispersionSymbolicValue agentLoadDispersion,
			final Double hostCapacityMean,
			final DispersionSymbolicValue hostcapacityDispersion,
			final Double agentCriticityMean,
			final DispersionSymbolicValue agentCriticityDispersion,
			final String usedProtocol,
			final String socialWelfare,
			final String agentSelection,
			final String hostSelection,
			final boolean dynamicCriticty,
			final Double host_maxSimultaneousFailurePercent) {
		super(experimentatorId, resultPath);
		this.nbAgents = nbAgents;
		this.nbHosts= nbHosts;		
		this.setkAccessible(k);
		this.hostFaultProbabilityMean = hostFaultProbabilityMean;
		this.hostDisponibilityDispersion=hostFaultProbabilityDispersion;
		this.agentLoadMean = agentLoadMean;
		this.agentLoadDispersion=agentLoadDispersion;
		this.agentCriticityMean=agentCriticityMean;
		this.agentCriticityDispersion=agentCriticityDispersion;
		this.hostCapacityMean=hostCapacityMean;
		this.hostCapacityDispersion=hostcapacityDispersion;
		this._usedProtocol = usedProtocol;
		this._socialWelfare=socialWelfare;
		this._agentSelection = agentSelection;
		this.set_hostSelection(hostSelection);
		this.dynamicCriticity = dynamicCriticty;
		this.setMaxSimultFailure(host_maxSimultaneousFailurePercent);
	}

	@Override
	public boolean equals(final Object o){
		if (o instanceof ReplicationExperimentationParameters){
			final ReplicationExperimentationParameters that = (ReplicationExperimentationParameters) o;

			return super.equals(that) && this.getRealkAccessible()==that.getRealkAccessible() &&
					this.hostFaultProbabilityMean.equals( that.hostFaultProbabilityMean) &&
					this.agentLoadMean.equals(that.agentLoadMean) &&
					this.agentLoadDispersion.equals(that.agentLoadDispersion) &&
					this.hostCapacityMean.equals(that.hostCapacityMean) &&
					this.hostCapacityDispersion.equals(that.hostCapacityDispersion) &&
					this._usedProtocol.equals(that._usedProtocol) &&
					this._socialWelfare.equals(that._socialWelfare) &&
					this._agentSelection.equals(that._agentSelection) &&
					this._hostSelection.equals(that._hostSelection);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode(){
		return
				2*this.getRealkAccessible().hashCode()
				+4*this.hostFaultProbabilityMean.hashCode()
				+8*this.agentLoadMean.hashCode()
				+16*this._usedProtocol.hashCode()
				+32*this._socialWelfare.hashCode()
				+64*this._agentSelection.hashCode()
				+128*this._hostSelection.hashCode()
				+256*this.hostDisponibilityDispersion.hashCode()
				+512*this.agentLoadDispersion.hashCode()
				+1024*this.agentCriticityMean.hashCode()
				+2048*this.agentCriticityDispersion.hashCode()
				+4096*this.dynamicCriticity.hashCode()
				+8192*this.host_maxSimultaneousFailure.hashCode()
				+16000*this.hostCapacityMean.hashCode()
				+32000*this.hostCapacityDispersion.hashCode();
	}

	@Override
	public ReplicationExperimentationParameters clone(){
		return new ReplicationExperimentationParameters(
				this.getResultPath(),
				this.experimentatorId,
				this.nbAgents,
				this.nbHosts,
				this.getRealkAccessible(),
				this.hostFaultProbabilityMean,
				this.hostDisponibilityDispersion,
				this.agentLoadMean,
				this.agentLoadDispersion,
				this.hostCapacityMean,
				this.hostCapacityDispersion,
				this.agentCriticityMean,
				this.agentCriticityDispersion,
				this._usedProtocol,
				this._socialWelfare,
				this._agentSelection,
				this.get_hostSelection(),
				this.dynamicCriticity,
				this.getRealMaxSimultFailure());

	}
	
	//
	// Accessors
	//

	@Override
	public boolean isInitiated() {
		return initiated;
	}
	
	public int getNbAgents() {
		return this.nbAgents;
	}

	public int getNbHosts() {
		return this.nbHosts;
	}

	public int getNbAgentsNHosts() {
		return this.nbAgents+this.nbHosts;
	}

	public List<AgentIdentifier> getReplicasIdentifier() {
		return this.replicasIdentifier;
	}

	public List<ResourceIdentifier> getHostsIdentifier() {
		return this.hostsIdentifier;
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
	public void setkAccessible(final double k) {
		this.kAccessible =(int) (k * this.nbHosts);
	}

	public Double getRealkAccessible() {
		return (double)this.kAccessible/
				(double)this.nbHosts;
	}

	public void setMaxSimultFailure(final Double host_maxSimultaneousFailurePercent){
		this.host_maxSimultaneousFailure = this.kAccessible*host_maxSimultaneousFailurePercent;

	}

	public Double getRealMaxSimultFailure(){
		return this.host_maxSimultaneousFailure/this.kAccessible;
	}

	public void set_hostSelection(final String hostSelection) {
		if (this._usedProtocol.equals(ReplicationExperimentationProtocol.key4mirrorProto)) {
			this._hostSelection = ReplicationExperimentationProtocol.key4AllocSelect;
		} else {
			this._hostSelection = hostSelection;
		}
	}

	public DistributionParameters<AgentIdentifier> getAgentCriticity() {
		return this.agentCriticity;
	}

	public DistributionParameters<AgentIdentifier> getAgentProcessor() {
		return this.agentProcessor;
	}

	public DistributionParameters<AgentIdentifier> getAgentMemory() {
		return this.agentMemory;
	}



	//
	// Methods
	//

	public final void initiateParameters(){
		this.initiateAgentsAndHosts();
		this.initiate();
	}

	private void initiateAgentsAndHosts(){
		/*
		 * Agents and hosts names
		 */

		for (int i=0; i<this.nbAgents; i++) {
			this.replicasIdentifier.add(//new AgentName("_--simu="+p.toString()+"--_DomainAgent_"+i));
					new AgentName("#"+this.getSimulationName()+"#DomainAgent_"+i));
		}
		for (int i=0; i<this.nbHosts; i++) {
			this.hostsIdentifier.add(//new ResourceIdentifier("_--simu="+p.toString()+"--_HostManager_"+i,77));
					new ResourceIdentifier("#"+this.getSimulationName()+"#HostManager_"+i,77));
		}
	}

	
	public void initiate() {
		this.agentCriticity = new DistributionParameters<AgentIdentifier>(
				this.getReplicasIdentifier(),
				this.agentCriticityMean,
				this.agentCriticityDispersion);
		this.agentProcessor = new DistributionParameters<AgentIdentifier>(
				this.getReplicasIdentifier(), this.agentLoadMean,
				this.agentLoadDispersion);
		this.agentMemory = new DistributionParameters<AgentIdentifier>(
				this.getReplicasIdentifier(), this.agentLoadMean,
				this.agentLoadDispersion);
		this.hostMemCapacity = new DistributionParameters<ResourceIdentifier>(
				this.getHostsIdentifier(), this.hostCapacityMean,
				this.hostCapacityDispersion);
		this.hostProcCapacity = new DistributionParameters<ResourceIdentifier>(
				this.getHostsIdentifier(), this.hostCapacityMean,
				this.hostCapacityDispersion);
		initiated=true;
	}


	//
	//	public void setkAccessible(final int kAccessible) {
	//		this.kAccessible = kAccessible;
	//	}


	public String get_hostSelection() {
		return this._hostSelection;
	}


	//
	// Protocol
	//


	//
	//  Génération de simulation
	// /////////////////////////////////

	//
	// Set of values
	//

	static List<String> protos = Arrays.asList(new String[]{
			ReplicationExperimentationProtocol.getKey4mirrorproto(),
			ReplicationExperimentationProtocol.getKey4centralisedstatusproto(),
			ReplicationExperimentationProtocol.getKey4statusproto()});
	static List<String> welfare = Arrays.asList(new String[]{
			SocialChoiceFunctions.key4leximinSocialWelfare,
			SocialChoiceFunctions.key4NashSocialWelfare,
			SocialChoiceFunctions.key4UtilitaristSocialWelfare});
	static List<String> select = Arrays.asList(new String[]{
			ReplicationExperimentationProtocol.getKey4greedyselect(),
			ReplicationExperimentationProtocol.getKey4roulettewheelselect()});//,key4AllocSelect
	static List<DispersionSymbolicValue> dispersion = Arrays.asList(new DispersionSymbolicValue[]{
			DispersionSymbolicValue.Nul,
			DispersionSymbolicValue.Moyen,
			DispersionSymbolicValue.Max});
	static List<Double> doubleParameters = Arrays.asList(new Double[]{
			0.1,
			0.5,
			1.});
	//	static List<Double> doubleParameters = Arrays.asList(new Double[]{
	//			0.1,
	//			0.3,
	//			0.6,
	//			1.});
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
	//pref TODO : Non imple chez l'agent!!
	//	Collection<String> agentPref = Arrays.asList(new String[]{
	//			ReplicationExperimentationProtocol.key4agentKey_Relia,
	//			ReplicationExperimentationProtocol.key4agentKey_loadNRelia});
	//	static final String key4agentKey_Relia="onlyRelia";
	//	static final String key4agentKey_loadNRelia="firstLoadSecondRelia";

	//
	// Variation configuration
	//

	static boolean varyProtocol=false;
	static boolean  varyOptimizers=true;

	static boolean varyAgentsAndhosts=false;

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

	static boolean varyFault=false;
	static int dynamicCriticity=-1; //-1 never dynamics, 1 always dynamics, 0 both

	//
	// Default values
	//

	static ReplicationExperimentationParameters getDefaultParameters(final File f) {
		return new ReplicationExperimentationParameters(
				f,
				Experimentator.myId,
				ReplicationExperimentationProtocol.startingNbAgents,
				ReplicationExperimentationProtocol.startingNbHosts,
				ReplicationExperimentationProtocol.doubleParameters.get(2),//kaccessible
				ReplicationExperimentationProtocol.doubleParameters.get(1),//dispo mean
				DispersionSymbolicValue.Fort,//dispo dispersion
				0.5,//ReplicationExperimentationProtocol.doubleParameters.get(1),//load mean
				DispersionSymbolicValue.Fort,//load dispersion
				2*ReplicationExperimentationProtocol.doubleParameters.get(1),//capacity mean
				DispersionSymbolicValue.Nul,//capcity dispersion
				ReplicationExperimentationProtocol.doubleParameters.get(1),//criticity mean
				DispersionSymbolicValue.Fort,//criticity dispersion
				ReplicationExperimentationProtocol.getKey4mirrorproto(),
				SocialChoiceFunctions.key4UtilitaristSocialWelfare,
				ReplicationExperimentationProtocol.getKey4greedyselect(),
				ReplicationExperimentationProtocol.getKey4allocselect(),
				false,
				ReplicationExperimentationProtocol.doubleParameters2.get(0));
	}


	//
	// Primitives
	//

	@Override
	public LinkedList<ExperimentationParameters> generateSimulation(String args[]) {
		final String usedProtocol, agentSelection, hostSelection;
		final File f = new File(ReplicationExperimentationProtocol.resultPath);
		//		f.mkdirs();
		Collection<ReplicationExperimentationParameters> simuToLaunch =
				new LinkedList<ReplicationExperimentationParameters>();
		simuToLaunch.add(ReplicationExperimentationProtocol.getDefaultParameters(f));
		if (ReplicationExperimentationProtocol.varyAgentsAndhosts) {
			simuToLaunch = this.varyAgentsAndhosts(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAccessibleHost) {
			simuToLaunch = this.varyAccessibleHost(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyHostDispo) {
			simuToLaunch = this.varyHostDispo(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyHostFaultDispersion) {
			simuToLaunch = this.varyHostFaultDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAgentLoad) {
			simuToLaunch = this.varyAgentLoad(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAgentLoadDispersion) {
			simuToLaunch = this.varyAgentLoadDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyHostCapacity) {
			simuToLaunch = this.varyHostCapacity(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyHostCapacityDispersion) {
			simuToLaunch = this.varyHostCapacityDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAgentCriticity) {
			simuToLaunch = this.varyAgentCriticity(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAgentCriticityDispersion) {
			simuToLaunch = this.varyAgentCriticityDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAgentSelection) {
			simuToLaunch = this.varyAgentSelection(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyHostSelection) {
			simuToLaunch = this.varyHostSelection(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyOptimizers) {
			simuToLaunch = this.varyOptimizers(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyProtocol) {
			simuToLaunch = this.varyProtocol(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyFault) {
			simuToLaunch = this.varyMaxSimultFailure(simuToLaunch);
		}

		simuToLaunch = this.varyDynamicCriticity(simuToLaunch);

		final Comparator<ExperimentationParameters> comp = new Comparator<ExperimentationParameters>() {

			@Override
			public int compare(final ExperimentationParameters o1,
					final ExperimentationParameters o2) {
				return o1.getSimulationName().compareTo(o2.getSimulationName());
			}
		};

		final LinkedList<ExperimentationParameters> simus = new LinkedList<ExperimentationParameters>(simuToLaunch);
		Collections.sort(simus,comp);
		return simus;
	}



	/*
	 *
	 */

	private Collection<ReplicationExperimentationParameters> varyProtocol(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationProtocol.protos){
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
			for (final String v : ReplicationExperimentationProtocol.select){
				final ReplicationExperimentationParameters n =  p.clone();
				n._agentSelection=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostSelection(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationProtocol.select){
				final ReplicationExperimentationParameters n =  p.clone();
				n.set_hostSelection(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyOptimizers(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationProtocol.welfare){
				final ReplicationExperimentationParameters n =  p.clone();
				n._socialWelfare=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentsAndhosts(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
				final ReplicationExperimentationParameters n =  p.clone();
				n.nbAgents=(int)(v*ReplicationExperimentationProtocol.startingNbAgents);
				n.nbHosts=(int)(v*ReplicationExperimentationProtocol.startingNbHosts);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAccessibleHost(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
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
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
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
			for (final DispersionSymbolicValue v : ReplicationExperimentationProtocol.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostDisponibilityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoad(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
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
			for (final DispersionSymbolicValue v : ReplicationExperimentationProtocol.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadDispersion=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostCapacity(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostCapacityMean=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyHostCapacityDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationProtocol.dispersion){
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
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
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
			for (final DispersionSymbolicValue v : ReplicationExperimentationProtocol.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentCriticityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyMaxSimultFailure(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters2){
				final ReplicationExperimentationParameters n = p.clone();
				n.setMaxSimultFailure(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyDynamicCriticity(
			final Collection<ReplicationExperimentationParameters> exps) {
		assert ReplicationExperimentationProtocol.dynamicCriticity>=-1 && ReplicationExperimentationProtocol.dynamicCriticity<=1;
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			if (ReplicationExperimentationProtocol.dynamicCriticity==-1){
				p.dynamicCriticity=false;
				result.add(p);
			} else if (ReplicationExperimentationProtocol.dynamicCriticity==1){
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

	/*
	 *
	 */

	public static final String resultPath;

	static {
		resultPath= ExperimentationProtocol._simulationTime / 60000
				+ "mins"
				+ (ReplicationExperimentationProtocol.varyAgentSelection==true?"varyAgentSelection":"")
				+ (ReplicationExperimentationProtocol.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationProtocol.varyProtocol?"varyProtocol":"")
				+ (ReplicationExperimentationProtocol.varyHostDispo?"varyHostDispo":"")
				+ (ReplicationExperimentationProtocol.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationProtocol.varyOptimizers?"varyOptimizers":"")
				+ (ReplicationExperimentationProtocol.varyAccessibleHost?"varyAccessibleHost":"")
				+ (ReplicationExperimentationProtocol.varyAgentLoad?"varyAgentLoad":"")
				+ (ReplicationExperimentationProtocol.varyHostCapacity?"varyHostCapacity":"");
	}



	//
	// Distribution
	//

	final Integer maxNumberOfAgentPerMachine  =this.getMaxNumberOfAgentPerMachine(null)  ;
	final double nbSimuPerMAchine = 1;
	@Override
	public Integer getMaxNumberOfAgentPerMachine(final HostIdentifier id) {
		return new Integer((int) this.nbSimuPerMAchine*
				(ReplicationExperimentationProtocol.startingNbAgents + ReplicationExperimentationProtocol.startingNbHosts)+1);
	}
	//	public int getMaxNumberOfAgentPerMachine(HostIdentifier id) {
	//		return new Integer(10);
	//	}

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
	// Creation de laborantin
	// /////////////////////////////////

	@Override
	public ReplicationLaborantin createNewLaborantin(
			final ExperimentationParameters para, final APILauncherModule api)
					throws NotEnoughMachinesException, CompetenceException, IfailedException {
		ReplicationLaborantin l = null;
		final ReplicationExperimentationParameters p = (ReplicationExperimentationParameters) para;
		//		boolean erreur = true;
		//		while (erreur)
		//			try {
		l = new ReplicationLaborantin(p, api,this.getMaxNumberOfAgentPerMachine(null));
		//				erreur = false;
		//			} catch (final IfailedException e) {
		//				LogService.writeException(
		//						"retrying to launch simu " + p.getName()
		//						+ " failure caused by : ", e.e);
		//				erreur = true;
		//			}

		return l;
	}



	/***
	 * Constantes
	 */

	/* FAULTS
	 *
	 * * lambda haut => weibull bas weibull bas => eventOccur haut lambda = prob
	 * de panne disp = 1 - lambda (useStaticDispo = true) disp = weibull
	 * (useStaticDispo = false)
	 *
	 *
	 * **
	 *
	 * k bas => eventOccur tot et pour tout le monde
	 */

	public static final long _host_maxFaultfrequency = 500;//10 * ReplicationExperimentationProtocol._timeToCollect;// 2*_simulationTime;//
	public static final long _timeScale = 10 * ReplicationExperimentationProtocol._host_maxFaultfrequency;
	public static final double _kValue = 7;
	public static final double _lambdaRepair = 1;
	public static final double _kRepair = .001;
	public static final double _theta = 0;// _host_maxFaultfrequency;//0.2;

	//
	// Quantile
	//

	public static final long _reliabilityObservationFrequency = 250;//10 * ReplicationExperimentationProtocol._timeToCollect;// (long)
	// (0.25*_contractExpirationTime);
	public static final int firstTercile = 33;// percent
	public static final int lastTercile = 66;// percent
	public static final double alpha_low = 1;
	public static final double alpha_high = 1;

	//
	// System Dynamicity
	//

	/*
	 * Criticité
	 */

	public static final double _criticityMin = 0.1;
	public static final double _criticityVariationProba = 20. / 100.;// 20%
	public static final double _criticityVariationAmplitude = 30. / 100.;// 10%
	public static final long _criticity_update_frequency = 2*ReplicationExperimentationProtocol._timeToCollect;// (long)

	// public static final double _dispoMax = 0.7;
	// public static final double _dispoVariationProba = 0./100.;
	// public static final double _dispoVariationAmplitude = 10./100.;
	// public static final long _dispo_update_frequency =2*_quantileInfoFrequency;
	

	//
	// Configuration statique
	// /////////////////////////////////

	//
	// Simulation Configuration
	//


	/**
	 * 
	 */

	public static final int startingNbAgents = 10;
	public static final int startingNbHosts = 5;

	//
	// Negotiation Tickers
	//

	public static final long _timeToCollect =50;//500;//
	public static final long _initiatorPropositionFrequency = -1;//(long) (ExperimentationProtocol._timeToCollect*0.5);//(long)
	// public static final long _initiator_analysisFrequency = (long) (_timeToCollect*2);
	public static final long _contractExpirationTime = Long.MAX_VALUE;//10000;//20 * ReplicationExperimentationProtocol._timeToCollect;


	/**
	 * Clés statiques
	 */

	//Protocoles
	public final static String key4mirrorProto = "mirror protocol";
	private final static String key4CentralisedstatusProto = "Centralised status protocol";
	private final static String key4statusProto = "status protocol";
	private final static String key4multiLatProto = "multi lateral protocol";

	//Selection algorithms
	private final static String key4greedySelect = "greedy select";
	private final static String key4rouletteWheelSelect = "roolette wheel select";
	public final static String key4AllocSelect = "alloc select";

	
	
	
	
	public static String getKey4greedyselect() {
		return ReplicationExperimentationProtocol.key4greedySelect;
	}

	public static String getKey4roulettewheelselect() {
		return ReplicationExperimentationProtocol.key4rouletteWheelSelect;
	}

	public static String getKey4allocselect() {
		return ReplicationExperimentationProtocol.key4AllocSelect;
	}

	public static String getKey4mirrorproto() {
		return ReplicationExperimentationProtocol.key4mirrorProto;
	}

	public static String getKey4centralisedstatusproto() {
		return ReplicationExperimentationProtocol.key4CentralisedstatusProto;
	}

	public static String getKey4statusproto() {
		return ReplicationExperimentationProtocol.key4statusProto;
	}

	public static String getKey4multilatproto() {
		return ReplicationExperimentationProtocol.key4multiLatProto;
	}

	//
	// Main
	//
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5800762843932232122L;

	public ReplicationExperimentator()
			throws CompetenceException {
		super(new ReplicationExperimentationProtocol());
	}

	public static void main(final String[] args) throws CompetenceException, IllegalArgumentException, IllegalAccessException, JDOMException, IOException{
		final ReplicationExperimentator exp = new ReplicationExperimentator();
		exp.run(args);
	}

	public String getDescription(){
		return super.getDescription()+" simus of "
				+ExperimentationProtocol._simulationTime/1000.+
				"secs  on "+this.getApi().getAvalaibleHosts().size()+" machine";//+ReplicationExperimentationProtocol.nbSimuPerMAchine+" simu per machine"
	}

}

// return "Simulation with parameters :\n"
// +"nbrAgents="+this.nbAgents
// +"; nbrHosts="+this.nbHosts
// +"; k="+this.kAccessible
// +"; agent load="+this.agentLoadMean
// +"; host faults="+this.hostFaultProbabilityMean
// +" nbrRepPerHostMoyen="+this.kAccessible/(this.nbAgents*this.agentLoadMean.getNumericValue());
// }