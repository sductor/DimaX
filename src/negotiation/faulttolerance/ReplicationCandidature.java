package negotiation.faulttolerance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationParameters;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.AllocationSocialWelfares;
import negotiation.negotiationframework.interaction.MatchingCandidature;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;

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
			final AgentIdentifier a, final boolean creation, boolean isAgentCreator) {
		super(isAgentCreator ? a : r, a, r,
				ReplicationExperimentationProtocol._contractExpirationTime);
		this.setCreation(creation);
	}

	/*
	 * 
	 */

	@Override
	public <State extends ReplicationSpecification>  State computeResultingState(State s) {
		if (s instanceof ReplicaState){
			return (State) getAgentResultingState((ReplicaState)s);
		} else if (s instanceof HostState){
			return (State) getResourceResultingState((HostState) s);
		} else
			throw new RuntimeException("arrrggghhhh!!!!"+s);
	}
	@Override
	public ReplicationSpecification computeResultingState(AgentIdentifier id) {		
		return computeResultingState(this.getSpecificationOf(id));
	}

	public ReplicaState getAgentResultingState(){
		return (ReplicaState) computeResultingState(getAgent());
	}

	public HostState getResourceResultingState(){
		return (HostState) computeResultingState(getResource());
	}

	/*
	 * 
	 */

	public ReplicaState getAgentInitialState(){
		return (ReplicaState) this.getSpecificationOf(getAgent());
	}
	public HostState getResourceInitialState(){
		return (HostState) this.getSpecificationOf(getResource());
	}

	private ReplicaState getAgentResultingState(final ReplicaState fromState) {

		if (getSpecificationOf(getResource()) == null)
			throw new RuntimeException("wtf? " + this);
		else if (!fromState.getMyAgentIdentifier().equals(getAgent()))
			return fromState;
		else if (fromState.getMyReplicas().contains(getSpecificationOf(getResource())) && creation == true){
			// logException("aaahhhhhhhhhhhhhhhhh  =(  ALREADY CREATED"+id);
			//				this.getMyAgent().sendMessage(
			//						id,
			//						new ShowYourPocket(this.getMyAgent().getIdentifier(),
			//								"replicacore:getmyresultingstate"));
			throw new RuntimeException(
					"aaahhhhhhhhhhhhhhhhh  =(  ALREADY CREATED" + getResource()
					+ "\n ----> current state"
					//						+ this.getMyAgent().getMyCurrentState()
					+ "\n --> fromState " + fromState);
			// return this;
		} else if (!fromState.getMyReplicas().contains(getSpecificationOf(getResource())) && creation == false) {	
			// logException("aaaahhhhhhhhhhhhhhhhh  =(  CAN NOT DESTRUCT"+id);
			//			this.getMyAgent().sendMessage(
			//					id,
			//					new ShowYourPocket(this.getMyAgent().getIdentifier(),
			//							"replicacore:getmyresultingstate"));
			throw new RuntimeException(
					"aaaahhhhhhhhhhhhhhhhh  =(  CAN NOT DESTRUCT" + getResource()
					//					+ "\n ----> current state"
					//					+ this.getMyAgent().getMyCurrentState()
					+ "\n --> fromState " + fromState);
		} else {
			ReplicaState result = new ReplicaState(fromState, 
					getResourceInitialState(), getCreationTime());
			//on cree n nouveau state a partir de r
			HostState h = new HostState(getResourceInitialState(), result, getCreationTime());
			//On supprime le state qu'on vient d'ajouter dans le but de le mettre a jour
			result = new ReplicaState(result, 
					getResourceInitialState(), getCreationTime());
			//on remet ce nouveau state dans r
			result = new ReplicaState(result, h, getCreationTime());
			return result;
		}

	}

	private HostState getResourceResultingState(final HostState fromState) {

		if (getSpecificationOf(this.getAgent()) == null)
			throw new NullPointerException();
		else if (!fromState.getMyAgentIdentifier().equals(getResource()))
			return fromState;
		else if (fromState.Ihost(getAgent()) && creation == true) {	
			//				this.getMyAgent().sendMessage(
			//						replica.getMyAgentIdentifier(),
			//						new ShowYourPocket(this.getMyAgent().getIdentifier(),
			//								"hostcore:getmyresultingstate"));
			throw new RuntimeException(
					fromState.getMyAgentIdentifier()+" : oohhhhhhhhhhhhhhhhh  =( ALREADY CREATED"
							+ getAgent()
							+ "\n ----> current state"
							+ this); 
		} else if (!fromState.Ihost(getAgent()) && creation == false) {	
			//				this.getMyAgent().sendMessage(
			//						replica.getMyAgentIdentifier(),
			//						new ShowYourPocket(this.getMyAgent().getIdentifier(),
			//								"hostcore:getmyresultingstate"));
			throw new RuntimeException(
					fromState.getMyAgentIdentifier()+" : ooohhhhhhhhhhhhhhhhh  =( CAN NOT DESTRUCT " + getAgent()
					+ "\n ----> current state" +fromState+"\n current contract"+ this+"\n CONTRACT CAN DESTRUCT INITIALLY? "+this.getResourceInitialState().Ihost(getAgent()));
		} else {
			HostState h = 
					new HostState(fromState, 
							getAgentInitialState(), getCreationTime());
			//on cree n nouveau state a partir de h
			ReplicaState r1 = new ReplicaState(getAgentInitialState(), h, getCreationTime());
			//On supprime le state qu'on vient d'ajouter dans le but de le mettre a jour
			h = new HostState(h, 
					getAgentInitialState(), getCreationTime());
			//on remet ce nouveau state dans h
			h = new HostState(h, r1, getCreationTime());


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