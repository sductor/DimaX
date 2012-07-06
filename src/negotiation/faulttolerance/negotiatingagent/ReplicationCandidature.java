package negotiation.faulttolerance.negotiatingagent;

import negotiation.negotiationframework.NegotiationParameters;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.AgentState;
import dima.basicagentcomponents.AgentIdentifier;

public class ReplicationCandidature extends
MatchingCandidature {
	private static final long serialVersionUID = -313913132536347399L;

	//
	// Fields
	//


	//
	// Constructors
	//

	public ReplicationCandidature(final ResourceIdentifier r,
			final AgentIdentifier a, final boolean creation, final boolean isAgentCreator) {
		super(isAgentCreator ? a : r, a, r,
				NegotiationParameters._contractExpirationTime);
		this.setCreation(creation);
	}

	//
	// Methods
	//

	
	@Override
	public <State extends AgentState>  State computeResultingState(final State s) throws IncompleteContractException {
		if (s instanceof ReplicaState) {
			return (State) this.getAgentResultingState((ReplicaState)s);
		} else if (s instanceof HostState) {
			return (State) this.getResourceResultingState((HostState) s);
		} else {
			throw new RuntimeException("arrrggghhhh!!!!"+s);
		}
	}
	

	public ReplicaState getAgentResultingState() throws IncompleteContractException{
		return (ReplicaState) this.computeResultingState(this.getAgent());
	}

	public HostState getResourceResultingState() throws IncompleteContractException{
		return (HostState) this.computeResultingState(this.getResource());
	}

	/*
	 *
	 */

	public ReplicaState getAgentInitialState() throws IncompleteContractException{
		return (ReplicaState) this.getInitialState(this.getAgent());
	}
	
	public HostState getResourceInitialState() throws IncompleteContractException{
		return (HostState) this.getInitialState(this.getResource());
	}

	private ReplicaState getAgentResultingState(final ReplicaState fromState) throws IncompleteContractException {

		assert this.getInitialState(this.getResource()) != null:"wtf? " + this;
		assert fromState.getMyResourceIdentifiers().contains(this.getResource()) || this.creation == true:
			"aaahhhhhhhhhhhhhhhhh  =(  ALREADY CREATED" + this.getResource()+" \n contract : "+this	+ "\n --> fromState " + fromState;
		assert !fromState.getMyResourceIdentifiers().contains(this.getResource()) || this.creation == false:
			"aaaahhhhhhhhhhhhhhhhh   =(  CAN NOT DESTRUCT" + this.getResource()+" \n contract : "+this	+ "\n --> fromState " + fromState;

		if (!fromState.getMyAgentIdentifier().equals(this.getAgent())) {
			return fromState;
		} else {
			ReplicaState r =fromState.allocate(getResourceInitialState());
			assert r.getMyResourceIdentifiers().size()==fromState.getMyResourceIdentifiers().size()+(this.creation?1:-1):
				r.getMyResourceIdentifiers()+"\n --------------- \n"+fromState.getMyResourceIdentifiers();
			return r;
		}
	}

	private HostState getResourceResultingState(final HostState fromState) throws IncompleteContractException {

		assert this.getInitialState(this.getAgent()) != null:"wtf? " + this;
		assert fromState.Ihost(this.getAgent()) || this.creation == true:
			" : oohhhhhhhhhhhhhhhhh  =( ALREADY CREATED"+ this.getAgent()+" \n contract : "+this	+ "\n --> fromState " + fromState;
		assert !fromState.Ihost(this.getAgent()) || this.creation == false:
			" : ooohhhhhhhhhhhhhhhhh  =( "+(this.creation?"agent already created!":"CAN NOT DESTRUCT ")+" \n contract : "+this	+ "\n --> fromState " + fromState
			+"\n CONTRACT CAN DESTRUCT INITIALLY? "+this.getResourceInitialState().Ihost(this.getAgent());

		
		if (!fromState.getMyAgentIdentifier().equals(this.getResource())) {
			return fromState;
		} else {
			HostState r = fromState.allocate(getAgentInitialState());
			assert r.getMyResourceIdentifiers().size()==fromState.getMyResourceIdentifiers().size()+(this.creation?1:-1):
				r.getMyResourceIdentifiers()+"\n --------------- \n"+fromState.getMyResourceIdentifiers();
			return r;
		}
	}

}

// ancien    getAgentResultingState
//			ReplicaState result = new ReplicaState(fromState,
//					this.getResourceInitialState()//, this.getCreationTime()
//					);
//			//on cree n nouveau state a partir de r
//			final HostState h = new HostState(this.getResourceInitialState(), result//, this.getCreationTime()
//					);
//			//On supprime le state qu'on vient d'ajouter dans le but de le mettre a jour
//			result = new ReplicaState(result,
//					this.getResourceInitialState()//, this.getCreationTime()
//					);
//			//on remet ce nouveau state dans r
//			result = new ReplicaState(result, h//, this.getCreationTime()
//					);



//	ancien	getResourceResultingState	
//			HostState h =
//					new HostState(fromState,
//							this.getAgentInitialState()//, this.getCreationTime()
//							);
//			//on cree n nouveau state a partir de h
//			final ReplicaState r1 =
//					new ReplicaState(
//							this.getAgentInitialState(), h//, this.getCreationTime()
//					);
//			//On supprime le state qu'on vient d'ajouter dans le but de le mettre a jour
//			h = new HostState(h,
//					this.getAgentInitialState()//, this.getCreationTime()
//					);
//			//on remet ce nouveau state dans h
//			h = new HostState(h, r1//, this.getCreationTime()
//					);





//			System.out.print(fromState+"\n to "+h+" \n to ");
//			System.err.println("here for "+getAgent());
//			System.out.println("state generated with rinit : "+h);
//			System.out.println("state with r result : "+r1);
//			h.update(r1);
//			System.out.println("final state"+h+"\n");




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