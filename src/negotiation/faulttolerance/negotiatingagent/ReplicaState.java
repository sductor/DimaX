package negotiation.faulttolerance.negotiatingagent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import negotiation.faulttolerance.faulsimulation.HostDisponibilityComputer;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.SimpleAgentState;
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
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

	private final Set<HostState> myReplicas;
	private final Double myDisponibility;

	private final SocialChoiceType socialWelfare;
	//
	// Constructors
	//


	public ReplicaState(
			final AgentIdentifier myAgent,
			final Double myCriticity,
			final Double myProcCharge,
			final Double myMemCharge,
			final SocialChoiceType socialWelfare,
			final int stateNumber) {
		super(myAgent,stateNumber);
		this.myCriticity = myCriticity;
		this.myProcCharge = myProcCharge;
		this.myMemCharge = myMemCharge;
		this.myReplicas = new HashSet<HostState>();
		this.socialWelfare = socialWelfare;
		this.myDisponibility=0.;
	}

	// Clone with modification of criticity
	public ReplicaState(
			final ReplicaState init,
			final double criti
			//			,final Long creationTime
			) {
		this(init.getMyAgentIdentifier(),
				criti,
				init.myReplicas,
				init.myProcCharge,
				init.myMemCharge,
				init.myDisponibility,
				init.socialWelfare,
				init.getStateCounter()+1);
	}

	// Clone with modification of replicas
	public ReplicaState allocate (final HostState newRep) {
		assert newRep.getMyResourceIdentifiers().contains(this.getMyAgentIdentifier());
		HashSet<HostState> rep = new HashSet<HostState>(this.myReplicas);
		
		if (rep.contains(newRep)) {
			rep.remove(newRep);
		} else {
			rep.add(newRep);
		}
		
		double myDisponibility=HostDisponibilityComputer.getDisponibility(this.myReplicas);
		
		return new ReplicaState(this.getMyAgentIdentifier(),
				this.myCriticity,
				rep,
				this.myProcCharge,
				this.myMemCharge,
				myDisponibility,
				this.socialWelfare,
				this.getStateCounter()+1);


		
	}

	// private constructor for opinions
	public ReplicaState(
			final AgentIdentifier myAgent,
			final Double myCriticity,
			final Set<HostState> myReplicas,
			final Double myProcCharge,
			final Double myMemCharge,
			final Double myDispo,
			final SocialChoiceType socialWelfare,
			//			final Long creationTime,
			final int stateNumber) {
		super(myAgent,// creationTime,
				stateNumber);
		this.myReplicas = myReplicas;
		this.myCriticity = myCriticity;
		this.myProcCharge = myProcCharge;
		this.myMemCharge = myMemCharge;
		this.myDisponibility=myDispo;
		this.socialWelfare = socialWelfare;
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
				this,
				this.socialWelfare);
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

	public Collection<HostState> getMyHosts() {
		return this.myReplicas;
	}

	@Override
	public Collection<ResourceIdentifier> getMyResourceIdentifiers() {
		//		if (myReplicas==null)
		//			return new ArrayList<HostState>();
		//		else
		//		return this.myReplicas;
		//	}/
		if (this.myReplicas==null) {
			return new ArrayList<ResourceIdentifier>();
		} else{
			final Collection<ResourceIdentifier> result = new ArrayList();
			for (final HostState h : this.myReplicas) {
				result.add(h.getMyAgentIdentifier());
			}
			return result;
		}
	}

	@Override
	public Class<? extends Information> getMyResourcesClass() {
		return HostState.class;
	}

	public SocialChoiceType getSocialWelfare() {
		return this.socialWelfare;
	}
	/*
	 *
	 */

	//	public ResourceIdentifier getOneReplica() {
	//		final int num = new Random().nextInt(this.getMyReplicas().size());
	//		final Iterator<HostState> itRep = this.getMyReplicas()
	//				.iterator();
	//		HostState h = itRep.next();
	//		for (int i = 1; i < num; i++) {
	//			h = itRep.next();
	//		}
	//		// logMonologue("removing wastefull host!!!"+h);//+"\n"+getMyAgent().getMyCurrentState());
	//		if (h == null) {
	//			throw new RuntimeException("aarrrggh");
	//		}
	//		return h.getMyAgentIdentifier();
	//	}

	@Override
	public boolean isValid() {
		return !this.getMyResourceIdentifiers().isEmpty();
	}

	/*
	 *
	 */

	@Override
	public boolean setLost(final ResourceIdentifier h, final boolean isLost) {
		if (isLost) {
			return this.myReplicas.remove(h);
		} else {
			throw new RuntimeException("impossible!!");
		}
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
		} else {
			throw new RuntimeException("melange d'infos!!!"+this+" "+o);
		}
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

		for (final Information o : elems) {
			if (o instanceof ReplicaState) {
				//				if (!((ReplicaState) o).getMyCriticity().equals(Double.NaN))
				//					throw new RuntimeException();
				final ReplicaState e = (ReplicaState) o;
				meanCrit.add(e.getMyCriticity());
				meanDisp.add(e.getMyDisponibility());
				meanMem.add(e.getMyMemCharge());
				meanProc.add(e.getMyProcCharge());
			} else {
				throw new RuntimeException("melange d'infos!!!"+this+" "+o);
			}
		}

		final ReplicaState rep = new ReplicaState(
				SimpleOpinionService.globaLAgentIdentifer,
				meanCrit.getRepresentativeElement(),
				null,
				meanProc.getRepresentativeElement(),
				meanMem.getRepresentativeElement(),
				meanDisp.getRepresentativeElement(),// this.getCreationTime(),
				this.socialWelfare,
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

		for (final Information o : elems.keySet()) {
			if (o instanceof ReplicaState) {
				final ReplicaState e = (ReplicaState) o;
				meanCrit.add(e.getMyCriticity(),elems.get(e));
				meanDisp.add(e.getMyDisponibility(),elems.get(e));
				meanMem.add(e.getMyMemCharge(),elems.get(e));
				meanProc.add(e.getMyProcCharge(),elems.get(e));
			} else {
				throw new RuntimeException("melange d'infos!!!"+this+" "+o);
			}
		}

		final ReplicaState rep = new ReplicaState(
				SimpleOpinionService.globaLAgentIdentifer,
				meanCrit.getRepresentativeElement(),
				null,
				meanProc.getRepresentativeElement(),
				meanMem.getRepresentativeElement(),
				meanDisp.getRepresentativeElement(), //this.getCreationTime(),
				this.socialWelfare,
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
		} else {
			return false;
		}
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