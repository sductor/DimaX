package negotiation.faulttolerance.experimentation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.ExperimentationProtocol;
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



	public String get_hostSelection() {
		return this._hostSelection;
	}




	//
	//	public void setkAccessible(final int kAccessible) {
	//		this.kAccessible = kAccessible;
	//	}


}

// return "Simulation with parameters :\n"
// +"nbrAgents="+this.nbAgents
// +"; nbrHosts="+this.nbHosts
// +"; k="+this.kAccessible
// +"; agent load="+this.agentLoadMean
// +"; host faults="+this.hostFaultProbabilityMean
// +" nbrRepPerHostMoyen="+this.kAccessible/(this.nbAgents*this.agentLoadMean.getNumericValue());
// }