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
import java.util.Random;

import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;


import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.kernel.CompetentComponent;
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
import frameworks.faulttolerance.negotiatingagent.HostCore;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaCore;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.solver.ChocoReplicationAllocationSolver;
import frameworks.faulttolerance.solver.SolverFactory;
import frameworks.negotiation.NegotiationParameters;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.rationality.RationalAgent;
import frameworks.negotiation.rationality.SimpleRationalAgent;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;
import frameworks.negotiation.selection.GreedySelectionModule;
import frameworks.negotiation.selection.OptimalSelectionModule;
import frameworks.negotiation.selection.SimpleSelectionCore;
import frameworks.negotiation.selection.GreedySelectionModule.GreedySelectionType;

public class ReplicationExperimentationParameters extends
ExperimentationParameters<ReplicationLaborantin> implements Comparable {
	private static final long serialVersionUID = -7191963637040889163L;

	//	final AgentIdentifier experimentatorId;
	public enum DCOPGraphType {Classical, Replication};

	/**
	 * Instance
	 */

	ReplicationInstanceGraph rig;

	/***
	 * Variables &  Constantes
	 */

	long randSeed;
	public  long maxComputingTime = 120000;//2 min
	
	public int nbAgents;
	public int nbHosts;

	public String _usedProtocol;
	public SocialChoiceType _socialWelfare;
	
	public int agentAccessiblePerHost;
	public int maxHostAccessibleParAgent=30;
	
	public  int simultaneousCandidature = 100;
	public  int simultaneousAcceptation = 50;
	public  int	opinionDiffusionDegree = 50;
//	this.simultaneousCandidature = Math.min(nbHosts,this.simultaneousCandidature);
	//		simultaneousAcceptation = (int) Math.min(nbAgents,Math.max(simultaneousAcceptation,(int)((double)startingNbAgents)/((double)startingNbHosts)+1));

	
	public String _agentSelection;
	public String _hostSelection;

	public Double hostFaultProbabilityMean;
	public  DispersionSymbolicValue hostFaultProbabilityDispersion;

	public  Double agentLoadMean;
	public DispersionSymbolicValue agentLoadDispersion;

	public Double hostCapacityMean;
	public DispersionSymbolicValue hostCapacityDispersion;

	public Double agentCriticityMean;
	public DispersionSymbolicValue agentCriticityDispersion;

	//
	// Quantile
	//

//	public static final long _statusObservationFrequency = 250;//10 * ReplicationExperimentationProtocol._timeToCollect;// (long)
	// (0.25*_contractExpirationTime);
	public double alpha_low, alpha_high;


	//
	// System Dynamicity
	//

	public Boolean dynamicCriticity;
	public Boolean faultOccurs;
	
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

	public enum DisponibilityComputationType {Static, Weibull, Poisson}
	public static final DisponibilityComputationType choosenType = DisponibilityComputationType.Static;//Poisson;//

	private static final Double host_maxSimultaneousFailure=20./100.;
	public static final long _host_maxFaultfrequency = 500;//10 * ReplicationExperimentationProtocol._timeToCollect;// 2*_simulationTime;//
	public static final long _timeScale = 10 * ReplicationExperimentationParameters._host_maxFaultfrequency;
	public static final double _lambdaRepair = 1;

	//	WEIBULL : OLD
	public static final double _kValue = 7;
	public static final double _kRepair = .001;
	public static final double _theta = 0;// _host_maxFaultfrequency;//0.2;


	/*
	 * Criticité
	 */

	public static final double _criticityMin = 0.1;
	public static final double _criticityVariationProba = 20. / 100.;// 20%
	public static final double _criticityVariationAmplitude = 30. / 100.;// 10%
	public static final long _criticity_update_frequency = 4*NegotiationParameters._timeToCollect;// (long)


	//
	// Constructor
	//


	ReplicationExperimentationParameters(
			final int nbAgents, final int nbHosts, final double agentAccessiblePerHost,
			final Double hostFaultProbabilityMean,
			final DispersionSymbolicValue hostFaultProbabilityDispersion,
			final Double agentLoadMean,
			final DispersionSymbolicValue agentLoadDispersion,
			final Double hostCapacityMean,
			final DispersionSymbolicValue hostcapacityDispersion,
			final Double agentCriticityMean,
			final DispersionSymbolicValue agentCriticityDispersion,
			final String usedProtocol,
			final SocialChoiceType socialWelfare,
			final String agentSelection,
			final String hostSelection,
			double alpha_low,
			double alpha_high,
			final boolean dynamicCriticty,
			final boolean faultOccurs) {
		super(new AgentName("ziReplExp"),
				ReplicationExperimentationGenerator.getProtocolId()
				);
		this.nbAgents = nbAgents;
		this.nbHosts= nbHosts;
		this.setkAccessible(agentAccessiblePerHost);
		//		assert this.agentAccessiblePerHost>0;
		this.hostFaultProbabilityMean = hostFaultProbabilityMean;
		this.hostFaultProbabilityDispersion=hostFaultProbabilityDispersion;
		this.agentLoadMean = agentLoadMean;
		this.agentLoadDispersion=agentLoadDispersion;
		this.agentCriticityMean=agentCriticityMean;
		this.agentCriticityDispersion=agentCriticityDispersion;
		this.hostCapacityMean=hostCapacityMean;//41.5;//0.30*nbAgents;//hostCapacityMean;
		this.hostCapacityDispersion=hostcapacityDispersion;
		this._usedProtocol = usedProtocol;
		this._socialWelfare=socialWelfare;
		this._agentSelection = this.setAgentSelection(agentSelection);
		this._hostSelection=hostSelection;
		this.dynamicCriticity = dynamicCriticty;
	}

	//
	// Accessors
	//

	public long getRandomSeed() {
		return randSeed;
	}

	public String setAgentSelection(final String agentSelection){
		return this._usedProtocol.equals(NegotiationParameters.key4mirrorProto)?NegotiationParameters.key4greedySelect:agentSelection;
	}
	
	public Collection<AgentIdentifier> getReplicasIdentifier() {
		return this.rig.getAgentsIdentifier();
	}

	public Collection<ResourceIdentifier> getHostsIdentifier() {
		return this.rig.getHostsIdentifier();
	}
	//	//	// final NormalLaw numberOfKnownHosts = new NormalLaw(this.p.kAccessible,
	//	//	// 0);
	//	private int getNumberOfKnownHosts() {
	//		// return numberOfKnownHosts.nextValue();
	//		return kAccessible;
	//	}

	public void setkAccessible(final double k) {
		this.agentAccessiblePerHost =(int) (k * this.nbAgents);
	}

	public Double getRealkAccessible() {
		return (double)this.agentAccessiblePerHost/
				(double)this.nbAgents;
	}

//	public void setMaxSimultFailure(final Double host_maxSimultaneousFailurePercent){
//		this.host_maxSimultaneousFailure = this.agentAccessiblePerHost*host_maxSimultaneousFailurePercent;
//
//	}
//
//	public Double getRealMaxSimultFailure(){
//		return this.host_maxSimultaneousFailure/this.agentAccessiblePerHost;
//	}
	public Double getMaxSimultFailure(){
		return this.agentAccessiblePerHost*host_maxSimultaneousFailure;
	}

	//
	//
	//

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
				+256*this.hostFaultProbabilityDispersion.hashCode()
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
				this.nbAgents,
				this.nbHosts,
				this.getRealkAccessible(),
				this.hostFaultProbabilityMean,
				this.hostFaultProbabilityDispersion,
				this.agentLoadMean,
				this.agentLoadDispersion,
				this.hostCapacityMean,
				this.hostCapacityDispersion,
				this.agentCriticityMean,
				this.agentCriticityDispersion,
				this._usedProtocol,
				this._socialWelfare,
				this._agentSelection,
				this._hostSelection,
				this.alpha_low,
				this.alpha_high,
				this.dynamicCriticity,
				this.faultOccurs);

	}
	@Override
	public int compareTo(final Object o) {
		final ReplicationExperimentationParameters that = (ReplicationExperimentationParameters) o;
		final double fixedResources=
				(double)ReplicationExperimentationGenerator.startingNbAgents/
				(double)ReplicationExperimentationGenerator.startingNbHosts;
		final boolean thisIsFixed=this.hostCapacityMean==fixedResources;
		final boolean thatIsFixed=that.hostCapacityMean==fixedResources;
		final double thisHostCapacityPercent=this.hostCapacityMean/this.nbAgents;
		final double thatHostCapacityPercent=that.hostCapacityMean/that.nbAgents;

		if  (thisIsFixed && thatIsFixed) {
			return this.nbAgents-that.nbAgents;
		} else if (thisIsFixed && !thatIsFixed){
			return -1;
		} else if (thatIsFixed && !thisIsFixed){
			return 1;
		} else {//!thisIsFixed && !thatIsFixed
			if (thisHostCapacityPercent!=thatHostCapacityPercent){
				return (int) (thisHostCapacityPercent-thatHostCapacityPercent);
			} else {
				return this.nbAgents-that.nbAgents;
			}
		}
	}

	//
	// Génération
	//
	
	ReplicationExperimentationGenerator reg = null;
	
	private void instanciateReg(){
		if (reg==null)
		reg = new ReplicationExperimentationGenerator(
				nbAgents, nbHosts, 
				getRealkAccessible(), 
				hostFaultProbabilityMean, hostFaultProbabilityDispersion, 
				agentLoadMean, agentLoadDispersion, 
				hostCapacityMean, hostCapacityDispersion, 
				agentCriticityMean, agentCriticityDispersion, 
				_usedProtocol, _socialWelfare, 
				_agentSelection, _hostSelection, 
				dynamicCriticity, faultOccurs);
	}
	
	@Override
	public void initiateParameters() throws IfailedException {
		instanciateReg();
		reg.initiateParameters();
	}

	@Override
	protected Collection<? extends CompetentComponent> instanciateAgents()
			throws CompetenceException {
		instanciateReg();
		return reg.instanciateAgents();
	}

	@Override
	public LinkedList<ExperimentationParameters<ReplicationLaborantin>> generateSimulation() {
		instanciateReg();
		return reg.generateSimulation();
	}

	@Override
	public Laborantin createLaborantin(APILauncherModule api)
			throws CompetenceException, IfailedException,
			NotEnoughMachinesException {
		instanciateReg();
		return reg.createLaborantin(api);
	}

	@Override
	public Integer getMaxNumberOfAgent(HostIdentifier h) {
		instanciateReg();
		return reg.getMaxNumberOfAgent(h);
	}
}






//			for (final AgentIdentifier h : rep.getMyCurrentState().getMyResourceIdentifiers()){
//				rep.addObserver(h,
//						SimpleObservationService.informationObservationKey);
//				rep.getMyInformation().add(this.rig.getHostState((ResourceIdentifier) h));
//			}
//}else if (){ //Status
//
//rep = new StatusReplica(
//		replicaId,
//		this.rig.getAgentState(replicaId),
//		this.getCore(true, this._usedProtocol, this._socialWelfare),
//		this.getSelectionCore(this._agentSelection),
//		this.getProposerCore(true, this._usedProtocol),
//		this.getInformationService(true, this._usedProtocol),
//		new ReverseCFPProtocol(),
//		this.dynamicCriticity,
//		opinionDiffusionDegree);

//			for (final AgentIdentifier h : rep.getMyCurrentState().getMyResourceIdentifiers()){
//				rep.addObserver(h,
//						SimpleObservationService.informationObservationKey);
//				rep.getMyInformation().add(this.rig.getHostState((ResourceIdentifier) h));
//			}
//
////
//// Creation de laborantin
//// /////////////////////////////////
//
//@Override
//public ReplicationLaborantin createNewLaborantin(
//		final ExperimentationParameters para, final APILauncherModule api)
//				throws NotEnoughMachinesException, CompetenceException, IfailedException {
//	ReplicationLaborantin l = null;
//	final ReplicationExperimentationParameters p = (ReplicationExperimentationParameters) para;
//	//		boolean erreur = true;
//	//		while (erreur)
//	//			try {
//	l = new ReplicationLaborantin(p, api,this.getMaxNumberOfAgentPerMachine(null));
//	//				erreur = false;
//	//			} catch (final IfailedException e) {
//	//				LogService.writeException(
//	//						"retrying to launch simu " + p.getName()
//	//						+ " failure caused by : ", e.e);
//	//				erreur = true;
//	//			}
//
//	return l;
//}

//public String getDescription(){
//	return super.getDescription()+" simus of "
//			+ExperimentationProtocol._simulationTime/1000.+
//			"secs  on "+this.getApi().getAvalaibleHosts().size()+" machine";//+ReplicationExperimentationProtocol.nbSimuPerMAchine+" simu per machine"
//}

//	public final static String simulationResultStateObservationKey = "observe the state!";

// return "Simulation with parameters :\n"
// +"nbrAgents="+this.nbAgents
// +"; nbrHosts="+this.nbHosts
// +"; k="+this.kAccessible
// +"; agent load="+this.agentLoadMean
// +"; host faults="+this.hostFaultProbabilityMean
// +" nbrRepPerHostMoyen="+this.kAccessible/(this.nbAgents*this.agentLoadMean.getNumericValue());
// }