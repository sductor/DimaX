package negotiation.faulttolerance.negotiatingagent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



import negotiation.faulttolerance.ReplicationSpecification;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.negotiationframework.agent.SimpleAgentState;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.coreservices.information.ObservationService.Information;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;
import dimaxx.tools.aggregator.LightWeightedAverageDoubleAggregation;

public class HostState extends SimpleAgentState implements ReplicationSpecification {
	private static final long serialVersionUID = 4107771452086657790L;

	private  final Set<ReplicaState> myReplicatedAgents;

	private final Double procChargeMax;
	private final Double procCurrentCharge;

	private final Double memChargeMax;
	private final Double memCurrentCharge;

	private final double lambda;
	private  boolean faulty;

	// Take all fields
	public HostState(final ResourceIdentifier myAgent, double lambda) {
		this(myAgent,
				new HashSet<ReplicaState>(), lambda, ReplicationExperimentationProtocol.hostMaxProc, 0., ReplicationExperimentationProtocol.hostMaxMem, 0.,
				false);
	}

	// take previous state
	public HostState(final HostState init, final ReplicaState newRep) {
		this(init.getMyAgentIdentifier(),
				new HashSet<ReplicaState>(),
				init.getLambda(),
				init.getProcChargeMax(),
				init.getCurrentProcCharge()
				+ (init.myReplicatedAgents.contains(newRep) ? 
						-newRep.getMyProcCharge() : newRep	.getMyProcCharge()),
						init.getMemChargeMax(),
						init.getCurrentMemCharge()
						+ (init.myReplicatedAgents.contains(newRep) ? 
								-newRep.getMyMemCharge() : newRep.getMyMemCharge()),
								init.isFaulty());

		this.myReplicatedAgents.addAll(init.myReplicatedAgents);
		if (this.myReplicatedAgents.contains(newRep))
			this.myReplicatedAgents.remove(newRep);
		else 
			this.myReplicatedAgents.add(newRep);
	}

	// private universal constructor
	private HostState(final ResourceIdentifier myAgent, 
			final Set<ReplicaState> myReplicatedAgents,
			double lambda, final Double procChargeMax,
			final Double procCurrentCharge, final Double memChargeMax,
			final Double memCurrentCharge, final boolean faulty) {
		super(myAgent);
		this.myReplicatedAgents = myReplicatedAgents;
		this.procChargeMax = procChargeMax;
		this.procCurrentCharge = procCurrentCharge;
		this.memChargeMax = memChargeMax;
		this.memCurrentCharge = memCurrentCharge;
		this.lambda = lambda;
		this.faulty = faulty;
	}

	//
	// Accessors
	//

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
		return Math.max(this.getCurrentMemCharge() / this.getMemChargeMax(),
				this.getCurrentProcCharge() / this.getProcChargeMax());
	}

	public boolean ImSurcharged() {
		return this.getMyCharge() > 1.;
	}

	public Iterator<ReplicaState> getMyAgents() {
		return myReplicatedAgents.iterator();
	}

	public Collection<AgentIdentifier> getMyAgentIdentifiers(){
		Collection<AgentIdentifier> result = new ArrayList();
		for (ReplicaState r : myReplicatedAgents)
			result.add(r.getMyAgentIdentifier());
				return result;
	}

	public boolean Ihost(AgentIdentifier id){
		return getMyAgentIdentifiers().contains(id);
	}

	/*
	 * 
	 */

	Double getCurrentProcCharge() {
		return this.procCurrentCharge;
	}

	Double getCurrentMemCharge() {
		return this.memCurrentCharge;
	}

	Double getProcChargeMax() {
		return this.procChargeMax;
	}

	Double getMemChargeMax() {
		return this.memChargeMax;
	}

	public double getLambda() {
		return this.lambda;
	}

	/*
	 * 
	 */

	public boolean isFaulty() {
		return this.faulty;
	}

	public void setFaulty(final boolean faulty) {
		this.faulty = faulty;
	}

	/*
	 *
	 */

	@Override
	public boolean setLost(final ResourceIdentifier h, final boolean isLost) {
		if (h.equals(this.getMyAgentIdentifier()))
			this.setFaulty(isLost);
		else {
			// Do nothing
		}
		return false;
	}	

	//
	// Opinion Handling
	//

	@Override
	public int compareTo(Information o) {
		if (o instanceof HostState) {
			HostState e = (HostState) o;
			return this.getMyCharge().compareTo(e.getMyCharge());
		} else
			throw new RuntimeException("melange d'infos!!!"+this+" "+o);
	}

	@Override
	public Double getNumericValue(Information o) {
		if (o instanceof HostState) {
			HostState e = (HostState) o;
			return e.getMyCharge();
		} else
			throw new RuntimeException("melange d'infos!!!"+this+" "+o);
	}

	@Override
	public AbstractCompensativeAggregation<Information> fuse(
			Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
		throw new RuntimeException("should not be called!");
	}

	@Override
	public Information getRepresentativeElement(
			Collection<? extends Information> elems) {
		LightAverageDoubleAggregation 
		meanProcCu = new LightAverageDoubleAggregation(),
		meanProcMax = new LightAverageDoubleAggregation(), 
		meanMemCu = new LightAverageDoubleAggregation(), 
		meanMemMax = new LightAverageDoubleAggregation(),
		meanLambda = new LightAverageDoubleAggregation();

		for (Information o : elems)
			if (o instanceof HostState) {
				HostState e = (HostState) o;
				meanProcCu.add(e.getCurrentProcCharge());
				meanProcMax.add(e.getProcChargeMax());
				meanMemCu.add(e.getCurrentMemCharge());
				meanMemMax.add(e.getMemChargeMax());
				meanLambda.add(e.getLambda());
			} else
				throw new RuntimeException("melange d'infos!!!"+this+" "+o);

		return new HostState(
				this.getMyAgentIdentifier(), 
				null,
				meanProcMax.getRepresentativeElement(), 
				meanProcCu.getRepresentativeElement(), 
				meanMemMax.getRepresentativeElement(), 
				meanMemCu.getRepresentativeElement(), 
				meanLambda.getRepresentativeElement(),
				false);
	}

	@Override
	public Information getRepresentativeElement(
			Map<? extends Information, Double> elems) {
		LightWeightedAverageDoubleAggregation 
		meanProcCu = new LightWeightedAverageDoubleAggregation(),
		meanProcMax = new LightWeightedAverageDoubleAggregation(), 
		meanMemCu = new LightWeightedAverageDoubleAggregation(), 
		meanMemMax = new LightWeightedAverageDoubleAggregation(),
		meanLambda = new LightWeightedAverageDoubleAggregation();

		for (Information o : elems.keySet())
			if (o instanceof HostState) {
				HostState e = (HostState) o;
				meanProcCu.add(e.getCurrentProcCharge(),elems.get(e));
				meanProcMax.add(e.getProcChargeMax(),elems.get(e));
				meanMemCu.add(e.getCurrentMemCharge(),elems.get(e));
				meanMemMax.add(e.getMemChargeMax(),elems.get(e));
				meanLambda.add(e.getLambda(),elems.get(e));
			} else
				throw new RuntimeException("melange d'infos!!!"+this+" "+o);

		return new HostState(
				this.getMyAgentIdentifier(), 
				null,
				meanProcMax.getRepresentativeElement(), 
				meanProcCu.getRepresentativeElement(), 
				meanMemMax.getRepresentativeElement(), 
				meanMemCu.getRepresentativeElement(), 
				meanLambda.getRepresentativeElement(),
				false);
	}

	//
	// Primitives
	//

	@Override
	public boolean equals(final Object o) {
		if (o instanceof HostState) {
			final HostState that = (HostState) o;
			return that.getMyAgentIdentifier().equals(
					this.getMyAgentIdentifier());
		} else
			return false;
	}

	@Override
	public int hashCode(){
		return getMyAgentIdentifier().hashCode();
	}

	@Override
	public String toString() {
		return "\nHOST="+ this.getMyAgentIdentifier()
//				+ "\n Date "+ this.getCreationTime()
				+ "\n --> charge : "+ this.getMyCharge()
				//				+ "\n * dispo  : "
				//				+ NegotiatingHost.this.myFaultAwareService
				//						.getDisponibility(this.getMyAgentIdentifier())
				+ "\n --> lambda : "+this.lambda+
				"\n --> agents : " + this.myReplicatedAgents//getMyAgentIdentifiers()//
				+ "\n --> faulty? : " + this.isFaulty()
				+"\n --> creation time : "+this.getCreationTime();
	}

}