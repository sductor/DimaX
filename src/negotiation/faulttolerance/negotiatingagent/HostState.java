package negotiation.faulttolerance.negotiatingagent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.SimpleAgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
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
	public HostState(final ResourceIdentifier myAgent, final double hostMaxProc, final double hostMaxMem, final double lambda, final int stateNumber) {
		this(myAgent,
				new HashSet<ReplicaState>(), lambda,
				hostMaxProc, 0., hostMaxMem, 0.,
				false, //new Date().getTime(),
				stateNumber);
	}

	// take previous state
	public HostState(final HostState init, final ReplicaState newRep){//, final Long creationTime) {
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
								init.isFaulty(),// creationTime,
								init.getStateCounter()+1);

		//		assert newRep.getMyResourceIdentifiers().contains(this.getMyAgentIdentifier());

		this.myReplicatedAgents.addAll(init.myReplicatedAgents);
		if (this.myReplicatedAgents.contains(newRep))
			this.myReplicatedAgents.remove(newRep);
		else
			this.myReplicatedAgents.add(newRep);
	}

	// private universal constructor
	private HostState(final ResourceIdentifier myAgent,
			final Set<ReplicaState> myReplicatedAgents,
			final double lambda, final Double procChargeMax,
			final Double procCurrentCharge, final Double memChargeMax,
			final Double memCurrentCharge, final boolean faulty,// final Long creationTime,
			final int stateNumber) {
		super(myAgent, stateNumber);
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

	public Collection<ReplicaState> getMyAgentsCollec() {
		return this.myReplicatedAgents;
	}

	@Override
	public Collection<AgentIdentifier> getMyResourceIdentifiers(){
		final Collection<AgentIdentifier> result = new ArrayList();
		for (final ReplicaState r : this.myReplicatedAgents)
			result.add(r.getMyAgentIdentifier());
				return result;
	}

	@Override
	public Class<? extends Information> getMyResourcesClass() {
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

	Double getCurrentProcCharge() {
		return this.procCurrentCharge;
	}

	Double getCurrentMemCharge() {
		return this.memCurrentCharge;
	}

	public Double getProcChargeMax() {
		return this.procChargeMax;
	}

	public Double getMemChargeMax() {
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

	//	@Override
	//	public int compareTo(final Information o) {
	//		if (o instanceof HostState) {
	//			final HostState e = (HostState) o;
	//			return this.getMyCharge().compareTo(e.getMyCharge());
	//		} else
	//			throw new RuntimeException("melange d'infos!!!"+this+" "+o);
	//	}

	@Override
	public Double getNumericValue(final Information o) {
		if (o instanceof HostState) {
			final HostState e = (HostState) o;
			return e.getMyCharge();
		} else
			throw new RuntimeException("melange d'infos!!!"+this+" "+o);
	}

	@Override
	public AbstractCompensativeAggregation<Information> fuse(
			final Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
		throw new RuntimeException("should not be called!");
	}

	@Override
	public Information getRepresentativeElement(
			final Collection<? extends Information> elems) {
		final LightAverageDoubleAggregation
		meanProcCu = new LightAverageDoubleAggregation(),
		meanProcMax = new LightAverageDoubleAggregation(),
		meanMemCu = new LightAverageDoubleAggregation(),
		meanMemMax = new LightAverageDoubleAggregation(),
		meanLambda = new LightAverageDoubleAggregation();

		for (final Information o : elems)
			if (o instanceof HostState) {
				final HostState e = (HostState) o;
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
				false,// this.getCreationTime(),
				-1);
	}

	@Override
	public Information getRepresentativeElement(
			final Map<? extends Information, Double> elems) {
		final LightWeightedAverageDoubleAggregation
		meanProcCu = new LightWeightedAverageDoubleAggregation(),
		meanProcMax = new LightWeightedAverageDoubleAggregation(),
		meanMemCu = new LightWeightedAverageDoubleAggregation(),
		meanMemMax = new LightWeightedAverageDoubleAggregation(),
		meanLambda = new LightWeightedAverageDoubleAggregation();

		for (final Information o : elems.keySet())
			if (o instanceof HostState) {
				final HostState e = (HostState) o;
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
				false, //this.getCreationTime(),
				-1);
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
		return this.getMyAgentIdentifier().hashCode();
	}

	@Override
	public String toString() {
		return "\nHOST="+ this.getMyAgentIdentifier()
				//				+ "\n Date "+ this.getCreationTime()
				+ "\n --> charge : "+ this.getCurrentProcCharge()+", "+this.getCurrentMemCharge()
				+ "\n --> capacity : "+ this.getProcChargeMax()+", "+this.getMemChargeMax()
				//				+ "\n * dispo  : "
				//				+ NegotiatingHost.this.myFaultAwareService
				//						.getDisponibility(this.getMyAgentIdentifier())
				+ "\n --> lambda : "+this.lambda+
				"\n --> agents : " + this.getMyResourceIdentifiers()//myReplicatedAgents//getMyAgentIdentifiers()//
				+ "\n --> faulty? : " + this.isFaulty()
				+"\n valid ? : "+this.isValid()
				+"\n --> creation time : "+this.getCreationTime();
	}



}