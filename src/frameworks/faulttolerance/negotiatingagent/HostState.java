package frameworks.faulttolerance.negotiatingagent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.distribution.PoissonLaw;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.faulttolerance.experimentation.ReplicationExperimentationParameters;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.SimpleAgentState;

public class HostState extends SimpleAgentState{
	private static final long serialVersionUID = 4107771452086657790L;

	/*
	 * Fields  (all must stay final for clone validity)
	 */

	private  final Set<AgentIdentifier> myReplicatedAgents;

	private final Double procChargeMax;
	private final Double procCurrentCharge;

	private final Double memChargeMax;
	private final Double memCurrentCharge;

	final double lambda;
	private boolean isFaulty;


	// Take all fields


	public HostState(
			final ResourceIdentifier myAgent,
			final double hostMaxProc,
			final double hostMaxMem,
			final double lambda) {
		this(myAgent,
				hostMaxProc, 0.,
				hostMaxMem, 0.,
				new HashSet<AgentIdentifier>(),
				lambda,
				-1);
	}

	public HostState allocate (final ReplicaState newRep, final boolean creation) {
		final boolean ok = creation &&!this.myReplicatedAgents.contains(newRep.getMyAgentIdentifier())
				|| !creation && this.myReplicatedAgents.contains(newRep.getMyAgentIdentifier());
		if (ok) {
			return this.allocate(newRep);
		} else {
			return this;
		}
	}
	public HostState allocate(final ReplicaState newRep){
		final HashSet<AgentIdentifier> rep =new HashSet<AgentIdentifier>(this.myReplicatedAgents);
		Double procCurrentCharge, memCurrentCharge;

		if (rep.contains(newRep.getMyAgentIdentifier())) {
			rep.remove(newRep.getMyAgentIdentifier());
			procCurrentCharge=this.procCurrentCharge-newRep.getMyProcCharge();
			memCurrentCharge=this.memCurrentCharge-newRep.getMyMemCharge();
		} else {
			rep.add(newRep.getMyAgentIdentifier());
			procCurrentCharge=this.procCurrentCharge+newRep.getMyProcCharge();
			memCurrentCharge=this.memCurrentCharge+newRep.getMyMemCharge();
		}

		return new HostState(
				this.getMyAgentIdentifier(),
				this.procChargeMax,
				procCurrentCharge,
				this.memChargeMax,
				memCurrentCharge,
				rep,
				this.lambda,
				this.getStateCounter()+1);
	}

	public HostState allocateAll(final Collection<ReplicaState> toAllocate){
		HostState s = this;
		for (final ReplicaState ress : toAllocate)	{
			s =  s.allocate(ress);
		}
		return s;
	}

	public HostState freeAllResources() {
		return new HostState(
				this.getMyAgentIdentifier(),
				this.procChargeMax,0.,
				this.memChargeMax,0.,
				new HashSet<AgentIdentifier>(),
				this.lambda,
				this.getStateCounter()+1);
	}
	// private universal constructor
	HostState(
			final ResourceIdentifier myAgent,
			final Double procChargeMax,
			final Double procCurrentCharge,
			final Double memChargeMax,
			final Double memCurrentCharge,
			final Set<AgentIdentifier> myReplicatedAgents,
			final double lambda,
			final int stateNumber) {
		super(myAgent, stateNumber);
		this.myReplicatedAgents = myReplicatedAgents;
		this.procChargeMax = procChargeMax;
		this.procCurrentCharge = procCurrentCharge;
		this.memChargeMax = memChargeMax;
		this.memCurrentCharge = memCurrentCharge;
		this.lambda = lambda;
	}

	//
	// Accessors
	//

	public boolean isFaulty() {
		return this.isFaulty;
	}

	public void setFaulty(final boolean isFaulty) {
		this.isFaulty = isFaulty;
	}
	//pas propre!!! camarche uniquement pcq le h ne change pas de charge
	//	public boolean update(ReplicaState h){
	//		if (myReplicatedAgents.contains(h)){
	//			//remove previous h :
	//			myReplicatedAgents.remove(h);
	//			//adding new h
	//			return myReplicatedAgents.add(h);
	//			//			return true;
	//		} else
	//			throw new RuntimeException();
	//	}
	@Override
	public ResourceIdentifier getMyAgentIdentifier() {
		return (ResourceIdentifier) super.getMyAgentIdentifier();
	}

	public Double getMyCharge() {
		//		if (ReplicationExperimentationParameters.multiDim) {
		return Math.max(this.getCurrentMemCharge() / this.getMemChargeMax(),
				this.getCurrentProcCharge() / this.getProcChargeMax());
		//		} else {
		//			return this.getCurrentMemCharge() / this.getMemChargeMax();
		//		}
	}

	public boolean ImSurcharged() {
		return this.getMyCharge() > 1.;
	}


	@Override
	public Collection<AgentIdentifier> getMyResourceIdentifiers(){
		return  Collections.unmodifiableSet(this.myReplicatedAgents);
	}
	@Override
	public boolean hasResource(final AgentIdentifier id) {
		return  this.myReplicatedAgents.contains(id);
	}
	@Override
	public Class<ReplicaState> getMyResourcesClass() {
		return ReplicaState.class;
	}

	public boolean Ihost(final AgentIdentifier id){
		return this.getMyResourceIdentifiers().contains(id);
	}

	@Override
	public boolean isValid() {
		return !this.ImSurcharged();
	}

	/*
	 *
	 */

	public double getFailureProb() {
		switch (ReplicationExperimentationParameters.choosenType) {
		case Static:
			return this.lambda;
		case Poisson:
			final long nbInterval = ExperimentationParameters._maxSimulationTime / ReplicationExperimentationParameters._host_maxFaultfrequency;
			return PoissonLaw.getPoissonLaw(this.lambda * nbInterval, 1);
		default:
			throw new RuntimeException("impossible");
		}
	}

	public Double getCurrentProcCharge() {
		return this.procCurrentCharge;
	}

	public Double getCurrentMemCharge() {
		return this.memCurrentCharge;
	}

	public Double getProcChargeMax() {
		return this.procChargeMax;
	}

	public Double getMemChargeMax() {
		return this.memChargeMax;
	}

	/*
	 *
	 */

	//	public void setFaulty(final boolean faulty) {
	//		this.faulty = faulty;
	//	}

	/*
	 *
	 */

	//	@Override
	//	public boolean setLost(final ResourceIdentifier h, final boolean isLost) {
	//		if (h.equals(this.getMyAgentIdentifier())) {
	//			this.setFaulty(isLost);
	//		} else {
	//			// Do nothing
	//		}
	//		return false;
	//	}

	//
	// Opinion Handling
	//

	//	@Override
	//	public int compareTo(final Information o) {
	//		if (o instanceof HostState) {
	//			final HostState e = (HostState) o;
	//			return this.getMyCharge().compareTo(e.getMyCharge());
	//		} else
	//			throw new RuntimeException("melange d'infos!!!"+this+" "+o);
	//	}

	//
	// Primitives
	//

	@Override
	public HostState clone(){
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof HostState ) {
			final HostState that = (HostState) o;
			return that.getMyAgentIdentifier().equals(
					this.getMyAgentIdentifier())&&this.getStateCounter()==that.getStateCounter();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode(){
		return this.getMyAgentIdentifier().hashCode();
	}

	@Override
	public String toString() {
		return "\nHOST="+ this.getMyAgentIdentifier()
				+ "\n --> current charge % = "+100*this.getMyCharge()
				+ "\n --> charge : "+ this.getCurrentProcCharge()+", "+this.getCurrentMemCharge()
				+ "\n --> capacity : "+ this.getProcChargeMax()+", "+this.getMemChargeMax()
				+ "\n --> lambda : "+this.lambda+
				"\n --> agents : " + this.getMyResourceIdentifiers()
				+"\n --> creation time : "+this.getCreationTime()+"#"+this.getStateCounter()
				+"\n valid ? : "+this.isValid();
	}

	public Double getLambda() {
		return this.lambda;
	}




}