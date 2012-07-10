package negotiation.faulttolerance.negotiatingagent;

import java.util.ArrayList;
import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.rationality.AltruistRationalCore;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

public  class ReplicaCore
extends
BasicAgentCompetence<SimpleRationalAgent<ReplicaState, ReplicationCandidature>>
implements
RationalCore<ReplicaState, ReplicationCandidature>  {
	private static final long serialVersionUID = 3436030307737036668L;

	final boolean observeResourceChanges;
	final boolean memorizeRessourceState;
	
	//
	// Constructor
	//

	 public ReplicaCore(boolean observeResourceChanges, boolean memorizeRessourceState) {
	 super();
	 this.observeResourceChanges=observeResourceChanges;
		this.memorizeRessourceState=memorizeRessourceState;
	 }

		@Override
		public boolean iObserveMyRessourceChanges() {
			return observeResourceChanges;
		}
		@Override
		public boolean iMemorizeMyRessourceState() {
			return memorizeRessourceState;
		}
		public void handleResourceInformation(AgentState c){
				if (iMemorizeMyRessourceState()) 
					getMyAgent().getMyInformation().add(c);
				if (iObserveMyRessourceChanges())
					observe(c.getMyAgentIdentifier(), SimpleObservationService.informationObservationKey);
		}
	//
	// Method
	//

	@Override
	public int getAllocationPreference(
			final Collection<ReplicationCandidature> c1,
			final Collection<ReplicationCandidature> c2) {
		//La mise a jour des spec actualise les contrats mais ne modifie pas l'ordre!!!
//		for (final ReplicationCandidature c : c1) {
//			c.setInitialState(getMyAgent().getMyCurrentState());
//		}
//		for (final ReplicationCandidature c : c2) {
//			c.setInitialState(getMyAgent().getMyCurrentState());
//		}
		//		return this.getFirstLoadSecondReliabilitAllocationPreference(s, c1, c2);
		AltruistRationalCore.verifyStateConsistency(getMyAgent(), c1, c2);
		return this.getAllocationReliabilityPreference(c1, c2);
	}



	@Override
	public void execute(final Collection<ReplicationCandidature> cs) {
		try {
			//			assert ContractTransition.allViable(cs):cs;
			//		logMonologue(
			//				"executing "+c+" from state "
			//		+this.getMyAgent().getMyCurrentState()
			//		+" to state "+c.computeResultingState(
			//						this.getMyAgent().getMyCurrentState()));


			final Collection<ReplicationCandidature> creation = new ArrayList<ReplicationCandidature>();
			final Collection<ReplicationCandidature> destruction = new ArrayList<ReplicationCandidature>();

			for (final ReplicationCandidature c : cs) {
				if (c.isMatchingCreation()) {
					creation.add(c);
				} else {
					destruction.add(c);
				}
			}

			for (final ReplicationCandidature c : creation){
				handleResourceInformation(c.getResourceResultingState());
				this.getMyAgent().setNewState(
						c.computeResultingState(
								this.getMyAgent().getMyCurrentState()));
				//			System.out.println(c.getResource() + " " + new Date().toString()
				//					+ "  ->I have replicated " + c.getAgent());//+" new State is "+this.getMyAgent().getMyCurrentState());
				this.logMonologue("  -> i have been replicated by "+c.getResource(),LogService.onFile);
			}

			for (final ReplicationCandidature c : destruction){
				handleResourceInformation(c.getResourceResultingState());
				this.getMyAgent().setNewState(
						c.computeResultingState(
								this.getMyAgent().getMyCurrentState()));
				//			System.out.println(c.getResource() + " " + new Date().toString()
				//					+ "  ->I have killed " + c.getAgent());//+" new State is "+this.getMyAgent().getMyCurrentState());
				this.logMonologue("  -> i have been killed by "+c.getResource(),LogService.onFile);
			}
		} catch (final IncompleteContractException e) {
			throw new RuntimeException(e);
		}

	}




	@Override
	public void setMySpecif(
			final ReplicaState s,
			final ReplicationCandidature c) {
//		return new NoActionSpec();
	}



	//
	//
	// Primitives
	//

	private Double getLoadEtendue(final ReplicaState s) {
		Double min = Double.POSITIVE_INFINITY;
		Double max = Double.NEGATIVE_INFINITY;

		for (final AgentState r : this.getMyAgent().getMyResources()) {
			min = Math.min(min, ((HostState) r).getMyCharge());
			max = Math.max(max, ((HostState) r).getMyCharge());
		}

		return max - min;
	}

	protected int getAllocationReliabilityPreference(
			final Collection<ReplicationCandidature> c1,
			final Collection<ReplicationCandidature> c2) {
		Double r1, r2;
		final ReplicaState s1 = this.getMyAgent().getMyResultingState(getMyAgent().getMyCurrentState(), c1);
		final ReplicaState s2 = this.getMyAgent().getMyResultingState(getMyAgent().getMyCurrentState(), c2);
		r1 = s1.getMyDisponibility();
		r2 = s2.getMyDisponibility();
		return r1.compareTo(r2);
	}

	protected int getAllocationLoadPreference(
			final Collection<ReplicationCandidature> c1,
			final Collection<ReplicationCandidature> c2) {
		Double e0, e1, e2;
		final ReplicaState s1 = this.getMyAgent().getMyResultingState(getMyAgent().getMyCurrentState(), c1);
		final ReplicaState s2 = this.getMyAgent().getMyResultingState(getMyAgent().getMyCurrentState(), c2);
		e0 = this.getLoadEtendue(getMyAgent().getMyCurrentState());
		e1 = this.getLoadEtendue(s1);
		e2 = this.getLoadEtendue(s2);

		if (e1 < e0 && e2 >= e0) {
			return 1;
		} else if (e2 < e0 && e1 >= e0) {
			return -1;
		} else {
			return 0;
		}
	}

	// Double e0, e1, e2;
	// e0 = getLoadEtendue(s);
	// e1 = getLoadEtendue(s1);
	// e2 = getLoadEtendue(s2);
	//
	// if (e1.equals(e2))
	// return 0;
	// else if (!e1.equals(e0) && !e2.equals(e0))
	// return e2.compareTo(e0);
	// else if (e1.equals(e0))
	// return -1;
	// else //e2.equals(e0)
	// return 1;

	protected int getFirstLoadSecondReliabilitAllocationPreference(final Collection<ReplicationCandidature> c1,
			final Collection<ReplicationCandidature> c2) {
		final int loadPreference = this.getAllocationLoadPreference(c1, c2);
		if (loadPreference == 0) {
			return this.getAllocationReliabilityPreference(c1, c2);
		} else {
			return loadPreference;
		}
	}

	@Override
	public Double evaluatePreference(final Collection<ReplicationCandidature> cs) {
		return this.getMyAgent().getMyResultingState(cs).getMyReliability();
	}



}

//// N��c��ssit�� de faire une fonction a part car celle ci est appell�� avant
//// le lancement des agent : le message envoy�� par observe ne parviendrait
//// pas! =(
//public void executeFirstRep(final ReplicationCandidature c,
//		final SimpleRationalAgent host) {
//	assert this.getMyAgent().respectRights(c);
//
//	//		logMonologue("Executing first rep!!!!!!!!!!!!!!!!\n"+getMyAgent().getMyCurrentState(), LogService.onScreen);
//	if (c.isMatchingCreation())
//		host.addObserver(c.getResource(), SimpleObservationService.informationObservationKey);
//	else
//		host.removeObserver(c.getResource(),SimpleObservationService.informationObservationKey);
//
//	this.getMyAgent().setNewState(
//			c.computeResultingState(
//					this.getMyAgent().getMyCurrentState()));
//	this.getMyAgent().getMyInformation().add(c.getResourceResultingState());
//}
//
//@Override
//public ReplicaState getMyResultingState(final ReplicaState fromState,
//		final ReplicationCandidature c) {
//	final ResourceIdentifier id = c.getResource();
//	final boolean creation = c.isMatchingCreation();
//	final HostState host = c.getResourceSpecification();
//	if (host == null)
//		throw new RuntimeException("wtf? " + c);
//	// final Set<ResourceIdentifier> reps = new
//	// HashSet<ResourceIdentifier>();
//	// reps.addAll(fromState.getMyReplicas());
//
//	getMyAgent().getMyInformation().add(host);
//	if (fromState.getMyReplicas().contains(id)) {
//		if (creation == true) {
//			// logException("aaahhhhhhhhhhhhhhhhh  =(  ALREADY CREATED"+id);
//			this.getMyAgent().sendMessage(
//					id,
//					new ShowYourPocket(this.getMyAgent().getIdentifier(),
//							"replicacore:getmyresultingstate"));
//			throw new RuntimeException(
//					"aaahhhhhhhhhhhhhhhhh  =(  ALREADY CREATED" + id
//					+ "\n ----> current state"
//					+ this.getMyAgent().getMyCurrentState()
//					+ "\n --> fromState " + fromState);
//			// return this;
//		} else
//			// reps.remove(id);
//			return new ReplicaState(fromState, host, ((NegotiatingReplica) getMyAgent()).myFaultAwareService);
//	} else if (creation == false) {
//		// logException("aaaahhhhhhhhhhhhhhhhh  =(  CAN NOT DESTRUCT"+id);
//		this.getMyAgent().sendMessage(
//				id,
//				new ShowYourPocket(this.getMyAgent().getIdentifier(),
//						"replicacore:getmyresultingstate"));
//		throw new RuntimeException(
//				"aaaahhhhhhhhhhhhhhhhh  =(  CAN NOT DESTRUCT" + id
//				+ "\n ----> current state"
//				+ this.getMyAgent().getMyCurrentState());
//		// return this;
//	} else
//		// reps.add(id);
//		return new ReplicaState(fromState, host, ((NegotiatingReplica) getMyAgent()).myFaultAwareService);
//}

// private int getAgentLoadsPreference(final ReplicaState s1, final ReplicaState
// s2){
// if (this.getLoadEtendue(s2)==this.getLoadEtendue(s1)
// ||
// this.getLoadEtendue(s2)<this.getLoadEtendue(this.getMyAgent().getMyCurrentState())
// &&
// this.getLoadEtendue(s1)<this.getLoadEtendue(this.getMyAgent().getMyCurrentState())
// ||
// this.getLoadEtendue(s2)>this.getLoadEtendue(this.getMyAgent().getMyCurrentState())
// &&
// this.getLoadEtendue(s1)>this.getLoadEtendue(this.getMyAgent().getMyCurrentState()))
// return 0;
// else if
// (this.getLoadEtendue(s2)<=this.getLoadEtendue(this.getMyAgent().getMyCurrentState())
// &&
// this.getLoadEtendue(s1)>=this.getLoadEtendue(this.getMyAgent().getMyCurrentState()))
// return 1;
// else //if
// (getLoadEtendue(s2)>=getLoadEtendue(getMyOpinionService().getMyCurrentState())
// &&
// //getLoadEtendue(s1)<=getLoadEtendue(getMyOpinionService().getMyCurrentState()))
// return -1;
// }

// @Override
// public int getAllocationPreference(ReplicaState s, final
// Collection<ReplicationCandidature> c1,
// final Collection<ReplicationCandidature> c2) {
// Boolean creation = null;
// for (ReplicationCandidature c : c1)
// if (creation==null)
// creation=c.isCreation();
// else if (creation!=c.isCreation())
// throw new
// RuntimeException("agent can not compare a mix of creation and destruction");
// for (ReplicationCandidature c : c2)
// if (creation==null)
// creation=c.isCreation();
// else if (creation!=c.isCreation())
// throw new
// RuntimeException("agent can not compare a mix of creation and destruction");
//
//
// final ReplicaState s1 = this.getMyResultingState(s, c1);
// final ReplicaState s2 = this.getMyResultingState(s, c2);
// if (creation)
// return s1.getMyReliability().compareTo(s2.getMyReliability());
// else
// return getAgentLoadsPreference(s1, s2);
//
//
// // final int i = this.getAgentLoadsPreference(s1,s2);
// //
// // if (i == 0)
// // return s1.getMyReliability().compareTo(s2.getMyReliability());
// // else
// // return i;
// }
// @Override
// public int getAllocationPreference(ReplicaState s, final
// Collection<ReplicationCandidature> c1,
// final Collection<ReplicationCandidature> c2) {
// minKnownReliability=Double.POSITIVE_INFINITY;
// Boolean creation = null;
// for (ReplicationCandidature c : c1){
// if (!c.isCreation())
// minKnownReliability = Math.min(minKnownReliability,
// c.getMinHostedReliability());
// if (creation==null)
// creation=c.isCreation();
// else if (creation!=c.isCreation())
// throw new
// RuntimeException("agent can not compare a mix of creation and destruction");
// }
// for (ReplicationCandidature c : c2){
// if (!c.isCreation())
// minKnownReliability = Math.min(minKnownReliability,
// c.getMinHostedReliability());
// if (creation==null)
// creation=c.isCreation();
// else if (creation!=c.isCreation())
// throw new
// RuntimeException("agent can not compare a mix of creation and destruction");
// }
//
// final ReplicaState s1 = this.getMyResultingState(s, c1);
// final ReplicaState s2 = this.getMyResultingState(s, c2);
// int result;
//
// // if (!respectRights(s1) && !respectRights(s2))
// // result = 0;
// // else if (!respectRights(s2))
// // result = 1;
// // else if (!respectRights(s1))
// // result = -1;
// // else {
// if (creation)
// result = s1.getMyReliability().compareTo(s2.getMyReliability());
// else
// result = getAgentLoadsPreference(s1, s2);
// // }
// minKnownReliability=Double.NEGATIVE_INFINITY;
// return result;
//
// // final int i = this.getAgentLoadsPreference(s1,s2);
// //
// // if (i == 0)
// // return s1.getMyReliability().compareTo(s2.getMyReliability());
// // else
// // return i;
// }
// minKnownReliability=Double.POSITIVE_INFINITY;
// for (ReplicationCandidature c : c1)
// if (!c.isCreation())
// minKnownReliability = Math.min(minKnownReliability,
// c.getMinHostedReliability());
// for (ReplicationCandidature c : c2)
// if (!c.isCreation())
// minKnownReliability = Math.min(minKnownReliability,
// c.getMinHostedReliability());
// if (!respectRights(s1) && !respectRights(s2))
// result = 0;
// else if (!respectRights(s2))
// result = 1;
// else if (!respectRights(s1))
// result = -1;
// else {
// minKnownReliability=Double.NEGATIVE_INFINITY;

// boolean supp = false;
// if
// (this.getMyAgent().getMyCurrentState().getMyReplicas().contains(c.getResource()))//Suppression
// supp = true;
// if (supp)
// specifs.remove(c.getResource());// pour econoiser la consommation m���moire

//
// if
// (!this.getMyAgent().getMyCurrentState().getMyReplicas().contains(c.getResource())){//Ajout
// // if (c.getActionSpec().getActionHost()==null) throw new
// RuntimeException("specif null");
// // this.specifs.put(c.getResource(), c.getActionSpec().getActionHost());
// }

// private final Double myDisponiblity;
// myDisponiblity = computeDisponibility(myReplicas);
// private ReplicaState(
// final AgentIdentifier myAgent,
// final Double myCriticity,
// final Double myProcCharge,
// final Double myMemCharge,
// final Double myDisponibility) {
// super(myAgent);
// this.myCriticity = myCriticity;
// this.myDisponiblity = myDisponibility;
// this.myProcCharge = myProcCharge;
// this.myMemCharge = myMemCharge;
// this.myReplicas = null;
// }

//
// public ReplicaState(final ReplicaState init) {
// this(
// init.getMyAgentIdentifier(),
// init.myCriticity,
// init.myProcCharge,
// init.myMemCharge,
// init.myReplicas);
// }
//
// Si observation des hotes
// @Override
// public int getPreference(final AgentIdentifier id, final HostState s1, final
// HostState s2) {
// return myCore.getHostPreference(s1, s2);
// }

// @Override
// public Boolean respectRights(final HostState s) {
// return myCore.respectHostRights(s);
// }

// @Override
// public HostState getResultingState(final HostState s, final Candidature c){
// return myCore.getHostResultingState(s, c.getActionSpec().getActionAgent());
// }
