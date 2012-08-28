package negotiation.faulttolerance.negotiatingagent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import dima.basicagentcomponents.AgentIdentifier;
import negotiation.faulttolerance.experimentation.HostInfo;
import negotiation.faulttolerance.experimentation.ReplicaInfo;
import negotiation.negotiationframework.interaction.ResourceIdentifier;

public class ReplicaState extends ReplicaInfo implements ReplicationSpecification {
	private static final long serialVersionUID = 1557592274895646282L;

	//
	// Fields
	//

	NegotiatingReplica myRep;
	private final Double myCriticity;
	final Map<ResourceIdentifier, Double> myReplicas;

	//
	// Constructors
	//

	// Default creation without replicas
	public ReplicaState(NegotiatingReplica myRep, 
			final Double myCriticity, final Double myProcCharge,
			final Double myMemCharge) {
		this(myRep, myCriticity, myProcCharge, myMemCharge,
				new HashMap<ResourceIdentifier, Double>());
	}

	// Clone with modification of replicas
	public ReplicaState(final ReplicaState init, final HostInfo newRep) {
		this(init.myRep, init.getMyCriticity(), init
				.getMyProcCharge(), init.getMyMemCharge(),
				new HashMap<ResourceIdentifier, Double>());
		this.myReplicas.putAll(init.myReplicas);
		if (this.myReplicas.containsKey(newRep.getMyAgentIdentifier()))
			this.myReplicas.remove(newRep.getMyAgentIdentifier());
		else
			this.myReplicas.put(newRep.getMyAgentIdentifier(),
					newRep.getMyCharge());
	}

	// private universal constructor
	protected ReplicaState(NegotiatingReplica myRep, 
			final Double myCriticity, final Double myProcCharge,
			final Double myMemCharge,
			final Map<ResourceIdentifier, Double> myReplicas) {
		super(myRep.getIdentifier(),
				NegotiatingReplica.getReliability(
						myRep.myFaultAwareService
						.getDisponibility(myReplicas.keySet()),
						myCriticity), myProcCharge, myMemCharge);
		this.myRep = myRep;
		this.myReplicas = myReplicas;
		this.myCriticity = myCriticity;
	}

	//
	// Accessors
	//

	@Override
	public Double getMyReliability() {
		return NegotiatingReplica.getReliability(
				myRep.myFaultAwareService
				.getDisponibility(this.myReplicas.keySet()),
				this.myCriticity);
	}

	public Double getMyDisponibility() {
		return myRep.myFaultAwareService
		.getDisponibility(myRep.getMyReplicas());
	}

	public Double getMyCriticity() {
		return this.myCriticity;
	}

	public Collection<ResourceIdentifier> getMyReplicas() {
		return this.myReplicas.keySet();
	}

	public Double getCharge(final ResourceIdentifier r) {
		return this.myReplicas.get(r);
	}

	public ResourceIdentifier getOneReplica() {
		final int num = new Random().nextInt(this.getMyReplicas().size());
		final Iterator<ResourceIdentifier> itRep = this.getMyReplicas()
		.iterator();
		ResourceIdentifier h = itRep.next();
		for (int i = 1; i < num; i++)
			h = itRep.next();
		// logMonologue("removing wastefull host!!!"+h);//+"\n"+getMyAgent().getMyCurrentState());
		if (h == null)
			throw new RuntimeException("aarrrggh");
		return h;
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
		+ "\n my criticity " + this.getMyCriticity() + "\n dispo "
		+ this.getMyDisponibility() + "\n charge "
		+ this.getMyProcCharge() + " " + this.getMyMemCharge()
		+ "\n relia " + this.getMyReliability() + "\n replicas "
		+ this.getMyReplicas();
		// +"\n status "+this.getMyStateStatus();
	}

	@Override
	public boolean setLost(final ResourceIdentifier h, final boolean isLost) {
		if (isLost)
			return this.myReplicas.remove(h) != null;
		else
			throw new RuntimeException("impossible!!");
	}
}