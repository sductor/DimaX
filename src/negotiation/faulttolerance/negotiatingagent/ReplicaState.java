package negotiation.faulttolerance.negotiatingagent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import negotiation.faulttolerance.faulsimulation.HostDisponibilityComputer;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.SimpleAgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dima.introspectionbasedagents.services.information.SimpleOpinionService;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;
import dimaxx.tools.aggregator.LightWeightedAverageDoubleAggregation;

public class ReplicaState  extends SimpleAgentState implements ReplicationSpecification {
	private static final long serialVersionUID = 1557592274895646282L;

	//
	// Fields
	//


	/*
	 *
	 */

	private final Double myCriticity;
	private final Double myProcCharge;
	private final Double myMemCharge;

	/*
	 *
	 */

	private Set<HostState> myReplicas=null;
	private final Double myDisponibility;

	//
	// Constructors
	//

	// initial
	//	protected ReplicaState(
	//			final AgentIdentifier myAgent,
	//			final Double myCriticity,
	//			final Double myProcCharge,
	//			final Double myMemCharge) {
	//		super(myAgent);
	//		this.myReplicas = new ArrayList<ResourceIdentifier>();
	//		this.myCriticity = myCriticity;
	//		this.myProcCharge = myProcCharge;
	//		this.myMemCharge = myMemCharge;
	////		remainingDispo=0.;
	//	}

	public ReplicaState(
			final AgentIdentifier myAgent,
			final Double myCriticity,
			final Double myProcCharge,
			final Double myMemCharge,
			final Set<HostState> myReps,
			final int stateNumber) {
		super(myAgent,stateNumber);
		this.myCriticity = myCriticity;
		this.myProcCharge = myProcCharge;
		this.myMemCharge = myMemCharge;
		this.myReplicas = myReps;
		this.myDisponibility=HostDisponibilityComputer.getDisponibility(this.myReplicas);
	}

	// Clone with modification of replicas
	public ReplicaState(
			final ReplicaState init,
			final HostState newRep
			//			,final Long creationTime
			) {
		super(init.getMyAgentIdentifier(),// creationTime,
				init.getStateCounter()+1);
		
//		assert newRep.getMyResourceIdentifiers().contains(this.getMyAgentIdentifier());
		
		this.myCriticity = init.getMyCriticity();
		this.myProcCharge = init.getMyProcCharge();
		this.myMemCharge = init.getMyMemCharge();
		//
		this.myReplicas = new HashSet<HostState>();
		this.myReplicas.addAll(init.myReplicas);
		//
		if (this.myReplicas.contains(newRep))
			this.myReplicas.remove(newRep);
		else
			this.myReplicas.add(newRep);
		//
		this.myDisponibility=HostDisponibilityComputer.getDisponibility(this.myReplicas);
	}

	// private constructor for opinions
	private ReplicaState(
			final AgentIdentifier myAgent,
			final Double myCriticity,
			final Double myProcCharge,
			final Double myMemCharge,
			final Double dispo,
			//			final Long creationTime,
			final int stateNumber) {
		super(myAgent,// creationTime,
				stateNumber);
		this.myReplicas = null;
		this.myCriticity = myCriticity;
		this.myProcCharge = myProcCharge;
		this.myMemCharge = myMemCharge;
		this.myDisponibility=dispo;
	}

	//
	// Accessors
	//

	//pas propre!!! camarche uniquement pcq le h ne change pas de lambda
	//	public boolean update(HostState h){
	//		if (myReplicas.contains(h)){
	//			//removing previous h
	//			myReplicas.remove(h);
	//			//adding new h
	//			return 	myReplicas.add(h);
	//		} else
	//			throw new RuntimeException();
	//	}

	public Double getMyReliability(){
		return ReplicationSocialOptimisation.getReliability(
				this.myDisponibility,
				this.myCriticity);
	}


	public Double getMyDisponibility() {
		return this.myDisponibility;
	}

	public Double getMyCriticity() {
		return this.myCriticity;
	}

	public Double getMyProcCharge() {
		return this.myProcCharge;
	}

	public Double getMyMemCharge() {
		return this.myMemCharge;
	}

	/*
	 *
	 */

	
	public Set<HostState> getMyReplicas() {
		//		if (myReplicas==null)
		//			return new ArrayList<HostState>();
		//		else
		return this.myReplicas;
	}
	@Override
	public Collection<ResourceIdentifier> getMyResourceIdentifiers() {
		//		if (myReplicas==null)
		//			return new ArrayList<HostState>();
		//		else
		final Collection<ResourceIdentifier> result = new ArrayList();
		for (final HostState h : this.myReplicas)
			result.add(h.getMyAgentIdentifier());
				return result;
	}

	@Override
	public Class<? extends Information> getMyResourcesClass() {
		return HostState.class;
	}

	/*
	 *
	 */

	public ResourceIdentifier getOneReplica() {
		final int num = new Random().nextInt(this.getMyReplicas().size());
		final Iterator<HostState> itRep = this.getMyReplicas()
				.iterator();
		HostState h = itRep.next();
		for (int i = 1; i < num; i++)
			h = itRep.next();
		// logMonologue("removing wastefull host!!!"+h);//+"\n"+getMyAgent().getMyCurrentState());
		if (h == null)
			throw new RuntimeException("aarrrggh");
		return h.getMyAgentIdentifier();
	}

	@Override
	public boolean isValid() {
		return !this.getMyReplicas().isEmpty();
	}

	/*
	 *
	 */

	@Override
	public boolean setLost(final ResourceIdentifier h, final boolean isLost) {
		if (isLost)
			return this.myReplicas.remove(h);
		else
			throw new RuntimeException("impossible!!");
	}

	/*
	 * Opinion
	 */

	//	@Override
	//	public int compareTo(final Information<AgentState> o) {
	//		if (o instanceof ReplicaState) {
	//			final ReplicaState e = (ReplicaState) o;
	//			return this.getMyReliability().compareTo(e.getMyReliability());
	//		} else
	//			throw new RuntimeException("melange d'infos!!!"+this+" "+o);
	//	}

	@Override
	public Double getNumericValue(final Information o) {
		if (o instanceof ReplicaState) {
			final ReplicaState e = (ReplicaState) o;
			return e.getMyReliability();
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
		meanCrit = new LightAverageDoubleAggregation(),
		meanDisp = new LightAverageDoubleAggregation(),
		meanMem = new LightAverageDoubleAggregation(),
		meanProc = new LightAverageDoubleAggregation();

		for (final Information o : elems)
			if (o instanceof ReplicaState) {
				//				if (!((ReplicaState) o).getMyCriticity().equals(Double.NaN))
				//					throw new RuntimeException();
				final ReplicaState e = (ReplicaState) o;
				meanCrit.add(e.getMyCriticity());
				meanDisp.add(e.getMyDisponibility());
				meanMem.add(e.getMyMemCharge());
				meanProc.add(e.getMyProcCharge());
			} else
				throw new RuntimeException("melange d'infos!!!"+this+" "+o);

		final ReplicaState rep = new ReplicaState(
				SimpleOpinionService.globaLAgentIdentifer,
				meanCrit.getRepresentativeElement(),
				meanProc.getRepresentativeElement(),
				meanMem.getRepresentativeElement(),
				meanDisp.getRepresentativeElement(),// this.getCreationTime(),
				-1);
		return rep;
	}

	@Override
	public Information getRepresentativeElement(
			final Map<? extends Information, Double> elems) {
		final LightWeightedAverageDoubleAggregation
		meanCrit = new LightWeightedAverageDoubleAggregation(),
		meanDisp = new LightWeightedAverageDoubleAggregation(),
		meanMem = new LightWeightedAverageDoubleAggregation(),
		meanProc = new LightWeightedAverageDoubleAggregation();

		for (final Information o : elems.keySet())
			if (o instanceof ReplicaState) {
				final ReplicaState e = (ReplicaState) o;
				meanCrit.add(e.getMyCriticity(),elems.get(e));
				meanDisp.add(e.getMyDisponibility(),elems.get(e));
				meanMem.add(e.getMyMemCharge(),elems.get(e));
				meanProc.add(e.getMyProcCharge(),elems.get(e));
			} else
				throw new RuntimeException("melange d'infos!!!"+this+" "+o);

		final ReplicaState rep = new ReplicaState(
				SimpleOpinionService.globaLAgentIdentifer,
				meanCrit.getRepresentativeElement(),
				meanProc.getRepresentativeElement(),
				meanMem.getRepresentativeElement(),
				meanDisp.getRepresentativeElement(), //this.getCreationTime(),
				-1);
		return rep;
	}

	//
	// Primitives
	//

	@Override
	public int hashCode() {
		return this.getMyAgentIdentifier().hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof ReplicaState) {
			final ReplicaState that = (ReplicaState) o;
			return that.getMyAgentIdentifier().equals(
					this.getMyAgentIdentifier());
		} else
			return false;
	}

	@Override
	public String toString() {
		return "\nAGENT=" + this.getMyAgentIdentifier()
				+ "\n * my criticity " + this.getMyCriticity()
				+ "\n * dispo "+ this.getMyDisponibility()
				+ "\n * charge "+ this.getMyProcCharge() + " " + this.getMyMemCharge()
				+ "\n * relia " + this.getMyReliability()
				+ "\n * replicas "+(this.myReplicas==null?"empty":this.getMyResourceIdentifiers())
				+"\n valid ? : "+this.isValid()
				+ "\n * creation time "+ this.getCreationTime();
		// +"\n status "+this.getMyStateStatus();
	}

}