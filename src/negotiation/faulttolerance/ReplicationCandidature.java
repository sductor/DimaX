package negotiation.faulttolerance;

import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.interaction.MatchingCandidature;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;

public class ReplicationCandidature extends
MatchingCandidature<ReplicationSpecification> {
	private static final long serialVersionUID = -313913132536347399L;

	//
	// Fields
	//


	//
	// Constructors
	//

//	public ReplicationCandidature(final ResourceIdentifier r,
//			final AgentIdentifier a, final boolean creation) {
//		super(creation ? a : r, a, r,
//				ReplicationExperimentationProtocol._contractExpirationTime);
//		this.setCreation(creation);
//
//	}

	public ReplicationCandidature(final ResourceIdentifier r,
			final AgentIdentifier a, final boolean creation, final boolean isAgentCreator) {
		super(isAgentCreator ? a : r, a, r,
				ReplicationExperimentationProtocol._contractExpirationTime);
		this.setCreation(creation);
	}

	/*
	 *
	 */

	@Override
	public <State extends ReplicationSpecification>  State computeResultingState(final State s) {
		if (s instanceof ReplicaState)
			return (State) this.getAgentResultingState((ReplicaState)s);
		else if (s instanceof HostState)
			return (State) this.getResourceResultingState((HostState) s);
		else
			throw new RuntimeException("arrrggghhhh!!!!"+s);
	}
	@Override
	public ReplicationSpecification computeResultingState(final AgentIdentifier id) {
		return this.computeResultingState(this.getSpecificationOf(id));
	}

	public ReplicaState getAgentResultingState(){
		return (ReplicaState) this.computeResultingState(this.getAgent());
	}

	public HostState getResourceResultingState(){
		return (HostState) this.computeResultingState(this.getResource());
	}

	/*
	 *
	 */

	public ReplicaState getAgentInitialState(){
		return (ReplicaState) this.getSpecificationOf(this.getAgent());
	}
	public HostState getResourceInitialState(){
		return (HostState) this.getSpecificationOf(this.getResource());
	}

	private ReplicaState getAgentResultingState(final ReplicaState fromState) {

		if (this.getSpecificationOf(this.getResource()) == null)
			throw new RuntimeException("wtf? " + this);
		else if (!fromState.getMyAgentIdentifier().equals(this.getAgent()))
			return fromState;
		else if (fromState.getMyReplicas().contains(this.getSpecificationOf(this.getResource())) && this.creation == true)
			// logException("aaahhhhhhhhhhhhhhhhh  =(  ALREADY CREATED"+id);
			//				this.getMyAgent().sendMessage(
			//						id,
			//						new ShowYourPocket(this.getMyAgent().getIdentifier(),
			//								"replicacore:getmyresultingstate"));
			throw new RuntimeException(
					"aaahhhhhhhhhhhhhhhhh  =(  ALREADY CREATED" + this.getResource()
					+ "\n ----> current state"
					//						+ this.getMyAgent().getMyCurrentState()
					+ "\n --> fromState " + fromState);
			// return this;
		else if (!fromState.getMyReplicas().contains(this.getSpecificationOf(this.getResource())) && this.creation == false)
			// logException("aaaahhhhhhhhhhhhhhhhh  =(  CAN NOT DESTRUCT"+id);
			//			this.getMyAgent().sendMessage(
			//					id,
			//					new ShowYourPocket(this.getMyAgent().getIdentifier(),
			//							"replicacore:getmyresultingstate"));
			throw new RuntimeException(
					"aaaahhhhhhhhhhhhhhhhh  =(  CAN NOT DESTRUCT" + this.getResource()
					//					+ "\n ----> current state"
					//					+ this.getMyAgent().getMyCurrentState()
					+ "\n --> fromState " + fromState);
		else {
			ReplicaState result = new ReplicaState(fromState,
					this.getResourceInitialState(), this.getCreationTime());
			//on cree n nouveau state a partir de r
			final HostState h = new HostState(this.getResourceInitialState(), result, this.getCreationTime());
			//On supprime le state qu'on vient d'ajouter dans le but de le mettre a jour
			result = new ReplicaState(result,
					this.getResourceInitialState(), this.getCreationTime());
			//on remet ce nouveau state dans r
			result = new ReplicaState(result, h, this.getCreationTime());
			return result;
		}

	}

	private HostState getResourceResultingState(final HostState fromState) {

		if (this.getSpecificationOf(this.getAgent()) == null)
			throw new NullPointerException();
		else if (!fromState.getMyAgentIdentifier().equals(this.getResource()))
			return fromState;
		else if (fromState.Ihost(this.getAgent()) && this.creation == true)
			//				this.getMyAgent().sendMessage(
			//						replica.getMyAgentIdentifier(),
			//						new ShowYourPocket(this.getMyAgent().getIdentifier(),
			//								"hostcore:getmyresultingstate"));
			throw new RuntimeException(
					fromState.getMyAgentIdentifier()+" : oohhhhhhhhhhhhhhhhh  =( ALREADY CREATED"
							+ this.getAgent()
							+ "\n ----> current state"
							+ this);
		else if (!fromState.Ihost(this.getAgent()) && this.creation == false)
			//				this.getMyAgent().sendMessage(
			//						replica.getMyAgentIdentifier(),
			//						new ShowYourPocket(this.getMyAgent().getIdentifier(),
			//								"hostcore:getmyresultingstate"));
			throw new RuntimeException(
					fromState.getMyAgentIdentifier()+" : ooohhhhhhhhhhhhhhhhh  =( CAN NOT DESTRUCT " + this.getAgent()
					+ "\n ----> current state" +fromState+"\n current contract"+ this+"\n CONTRACT CAN DESTRUCT INITIALLY? "+this.getResourceInitialState().Ihost(this.getAgent()));
		else {
			HostState h =
					new HostState(fromState,
							this.getAgentInitialState(), this.getCreationTime());
			//on cree n nouveau state a partir de h
			final ReplicaState r1 = new ReplicaState(this.getAgentInitialState(), h, this.getCreationTime());
			//On supprime le state qu'on vient d'ajouter dans le but de le mettre a jour
			h = new HostState(h,
					this.getAgentInitialState(), this.getCreationTime());
			//on remet ce nouveau state dans h
			h = new HostState(h, r1, this.getCreationTime());


			//			System.out.print(fromState+"\n to "+h+" \n to ");
			//			System.err.println("here for "+getAgent());
			//			System.out.println("state generated with rinit : "+h);
			//			System.out.println("state with r result : "+r1);
			//			h.update(r1);
			//			System.out.println("final state"+h+"\n");
			return h;
		}
	}
}
//
// Agent candidates to be replicated and host to destroy
//


// @Override
// public AgentIdentifier getInitiator() {
// return this.creation?this.a:this.getResource();
// }
//
//
// @Override
// public Collection<AgentIdentifier> getNotInitiatingParticipants() {
// return this.creation?Arrays.asList(new
// AgentIdentifier[]{this.getResource()}):Arrays.asList(new
// AgentIdentifier[]{this.a});
// }
//public ReplicaState getAgentSpecification() {
//	return this.computeSpecification(this.getAgent());
//}
//
//public HostState getResourceSpecification() {
//	return getHostResultingState(
//			(HostState) this.computeSpecification(this.getResource()),
//			(ReplicaState) 		this.computeSpecification(this.getAgent()),
//							this.isMatchingCreation());
//}

//
// @Override
// public void setSpecification(final ReplicationSpecification s) {
// if ( s.getMyAgentIdentifier().equals(this.getAgent()) && s instanceof
// ReplicaInfo
// || s.getMyAgentIdentifier().equals(this.getResource()) && s instanceof
// HostInfo)
// this.specs.put(s.getMyAgentIdentifier(), s);
// else
// throw new
// RuntimeException("unappropriate specification set"+this+"\n----> given "+s+","+s.getMyAgentIdentifier());
// }