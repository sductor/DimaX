package frameworks.faulttolerance.negotiatingagent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.aggregator.AbstractCompensativeAggregation;
import dima.introspectionbasedagents.modules.aggregator.LightAverageDoubleAggregation;
import dima.introspectionbasedagents.modules.aggregator.LightWeightedAverageDoubleAggregation;
import dima.introspectionbasedagents.modules.distribution.PoissonLaw;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.faulttolerance.experimentation.ReplicationExperimentationParameters;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.opinion.SimpleOpinionService;
import frameworks.negotiation.opinion.OpinionService.Opinion;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SimpleAgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class ReplicaState  extends SimpleAgentState  {

	private static final long serialVersionUID = 1557592274895646282L;

	//
	// Fields (all must stay final for clone validity)
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

	private final Set<ResourceIdentifier> myReplicas;
	private final Double myFailureProb;

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
			final int stateCounter) {
		this(myAgent,
				myCriticity,
				new HashSet<ResourceIdentifier>(),
				myProcCharge,
				myMemCharge,
				1.,
				socialWelfare,
				stateCounter);
	}

	public ReplicaState(
			final AgentIdentifier myAgent,
			final Double myCriticity,
			final Double myProcCharge,
			final Double myMemCharge,
			final SocialChoiceType socialWelfare) {
		this(myAgent,
				myCriticity,
				new HashSet<ResourceIdentifier>(),
				myProcCharge,
				myMemCharge,
				1.,
				socialWelfare,
				-1);
	}

	// Clone with modification of criticity
	public ReplicaState(
			final ReplicaState init,
			final double criti) {
		this(init.getMyAgentIdentifier(),
				criti,
				init.myReplicas,
				init.myProcCharge,
				init.myMemCharge,
				init.myFailureProb,
				init.socialWelfare,
				init.getStateCounter()+1);
	}

	// Clone with modification of replicas
	public ReplicaState allocate (final HostState newRep) {
		final HashSet<ResourceIdentifier> rep = new HashSet<ResourceIdentifier>(this.myReplicas);
		double newFailureProb = this.myFailureProb;
		if (rep.contains(newRep.getMyAgentIdentifier())) {
			rep.remove(newRep.getMyAgentIdentifier());
			newFailureProb/=newRep.getFailureProb();
		} else {
			rep.add(newRep.getMyAgentIdentifier());
			newFailureProb*=newRep.getFailureProb();
		}

		return new ReplicaState(this.getMyAgentIdentifier(),
				this.myCriticity,
				rep,
				this.myProcCharge,
				this.myMemCharge,
				newFailureProb,
				this.socialWelfare,
				this.getStateCounter()+1);
	}

	// private constructor for opinions
	ReplicaState(
			final AgentIdentifier myAgent,
			final Double myCriticity,
			final Set<ResourceIdentifier> myReplicas,
			final Double myProcCharge,
			final Double myMemCharge,
			final Double myFailureProb,
			final SocialChoiceType socialWelfare,
			final int stateNumber) {
		super(myAgent,// creationTime,
				stateNumber);
		this.myReplicas = myReplicas;
		this.myCriticity = myCriticity;
		this.myProcCharge = myProcCharge;
		this.myMemCharge = myMemCharge;
		this.myFailureProb=myFailureProb;
		this.socialWelfare = socialWelfare;
	}

	//
	// Accessors
	//

	public Double getMyReliability(){
		return ReplicationSocialOptimisation.getReliability(
				this.getMyDisponibility(),
				this.getMyCriticity(),
				this.socialWelfare);
	}

	public Double getMyDisponibility() {
		return 1 - myFailureProb;
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
	@Override
	public Collection<ResourceIdentifier> getMyResourceIdentifiers() {
		return this.myReplicas;
	}


	@Override
	public Class<HostState> getMyResourcesClass() {
		return HostState.class;
	}

	public SocialChoiceType getSocialWelfare() {
		return this.socialWelfare;
	}
	/*
	 *
	 */

	@Override
	public boolean isValid() {
		return this.getMyResourceIdentifiers()==null || !this.getMyResourceIdentifiers().isEmpty();
	}

	/*
	 *
	 */

	//	@Override
	//	public boolean setLost(final ResourceIdentifier h, final boolean isLost) {
	//		if (isLost) {
	//			return this.myReplicas.remove(h);
	//		} else {
	//			throw new RuntimeException("impossible!!");
	//		}
	//	}
	//
	/*
	 * Double
	 */



	//
	// Primitives
	//

	@Override
	public ReplicaState clone(){
		return this;
	}

	@Override
	public int hashCode() {
		return this.getMyAgentIdentifier().hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof ReplicaState) {
			final ReplicaState that = (ReplicaState) o;
			return that.getMyAgentIdentifier().equals(
					this.getMyAgentIdentifier())&&this.getStateCounter()==that.getStateCounter();
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
				+ "\n * creation time "+ this.getCreationTime()+"#"+this.getStateCounter()
				+"\n valid ? : "+this.isValid();
		// +"\n status "+this.getMyStateStatus();
	}

	public Double getMyFailureProb() {
		return myFailureProb;
	}

}


//} else if (o instanceof Opinion && ((Opinion)o).getRepresentativeElement() instanceof ReplicaState) {
//	final ReplicaState that = (ReplicaState) ((Opinion)o).getRepresentativeElement();
//	return that.getMyAgentIdentifier().equals(
//			this.getMyAgentIdentifier())&&this.getStateCounter()==that.getStateCounter();


//	@Override
//	public int compareTo(final Information<AgentState> o) {
//		if (o instanceof ReplicaState) {
//			final ReplicaState e = (ReplicaState) o;
//			return this.getMyReliability().compareTo(e.getMyReliability());
//		} else
//			throw new RuntimeException("melange d'infos!!!"+this+" "+o);
//	}

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
//		if (myReplicas==null)
//			return new ArrayList<HostState>();
//		else
//		return this.myReplicas;
//	}/
//if (this.myReplicas==null) {
//	return new ArrayList<ResourceIdentifier>();
//} else{
//	final Collection<ResourceIdentifier> result = new ArrayList();
//	for (final HostState h : this.myReplicas) {
//		result.add(h.getMyAgentIdentifier());
//	}
//	return result;
//}
//}

//pas propre!!! camarche uniquement pcq le h ne change pas de lambda
//public boolean update(HostState h){
//	if (myReplicas.contains(h)){
//		//removing previous h
//		myReplicas.remove(h);
//		//adding new h
//		return 	myReplicas.add(h);
//	} else
//		throw new RuntimeException();
//}