package negotiation.faulttolerance.negotiatingagent;

import java.util.Collection;

import negotiation.faulttolerance.experimentation.ReplicationSocialOptimisation;
import negotiation.negotiationframework.AllocationSocialWelfares;
import negotiation.negotiationframework.agent.RationalCore;
import negotiation.negotiationframework.agent.SimpleRationalAgent;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.library.information.SimpleObservationService;
import dima.introspectionbasedagents.services.library.replication.ReplicationHandler;

/**
 * This class contains the core evaluation, decision and execution methods of an
 * host
 *
 * @author Sylvain Ductor
 *
 * @param <Contract>
 */
public class HostCore
extends	BasicAgentCompetence<SimpleRationalAgent<ReplicationSpecification, HostState, ReplicationCandidature>>
implements RationalCore<ReplicationSpecification, HostState, ReplicationCandidature> {
	private static final long serialVersionUID = -179565544489478368L;

	private final boolean iWanToNegotiate;
	private final ReplicationSocialOptimisation myOptimiser;

	//
	// Constructor
	//

	public HostCore(final boolean mirrorNegotiating, final String socialWelfare) {
		this.iWanToNegotiate = mirrorNegotiating;
		this.myOptimiser = new ReplicationSocialOptimisation(this, socialWelfare);

	}


	//
	// Methods
	//

	@Override
	public int getAllocationPreference(final HostState s,
			final Collection<ReplicationCandidature> c1,
			final Collection<ReplicationCandidature> c2) {
		for (final ReplicationCandidature c : c1)
			c.setSpecification(s);
		for (final ReplicationCandidature c : c2)
			c.setSpecification(s);
		final int pref = this.myOptimiser.getSocialPreference(c1, c2);
		this.logMonologue("Preference : "+pref+" for \n "+c1+"\n"+c2, AllocationSocialWelfares.log_socialWelfareOrdering);
		return pref;
	}



	@Override
	public void execute(final ReplicationCandidature c) {
		assert this.getMyAgent().respectMyRights(c);
		//		logMonologue(
		//				"executing "+c+" from state "
		//		+this.getMyAgent().getMyCurrentState()
		//		+" to state "+c.computeResultingState(
		//						this.getMyAgent().getMyCurrentState()));

		/*
		 *
		 */

		if (c.isMatchingCreation()) {
			ReplicationHandler.replicate(c.getAgent());
			this.observe(c.getAgent(), SimpleObservationService.informationObservationKey);
			//			System.out.println(c.getResource() + " " + new Date().toString()
			//					+ "  ->I have replicated " + c.getAgent());//+" new State is "+this.getMyAgent().getMyCurrentState());
			this.logMonologue( "  ->I have replicated " + c.getAgent(),LogService.onBoth);
		} else {
			ReplicationHandler.killReplica(c.getAgent());
			this.stopObservation(c.getAgent(), SimpleObservationService.informationObservationKey);
			//			System.out.println(c.getResource() + " " + new Date().toString()
			//					+ "  ->I have killed " + c.getAgent());//+" new State is "+this.getMyAgent().getMyCurrentState());
			this.logMonologue( "  ->I have killed " + c.getAgent(),LogService.onBoth);
		}

		this.getMyAgent().setNewState(
				c.computeResultingState(
						this.getMyAgent().getMyCurrentState()));
		this.getMyAgent().getMyInformation().add(c.getAgentResultingState());
	}

	public void executeFirstRep(
			final ReplicationCandidature c,
			final SimpleRationalAgent ag) {
		assert this.getMyAgent().respectMyRights(c);

		this.getMyAgent().setNewState(
				c.computeResultingState(
						this.getMyAgent().getMyCurrentState()));
		this.getMyAgent().getMyInformation().add(c.getAgentResultingState());

		/*
		 *
		 */

		if (c.isMatchingCreation()) {
			ag.addObserver(this.getMyAgent().getIdentifier(),
					SimpleObservationService.informationObservationKey);
			ReplicationHandler.replicate(c.getAgent());
			this.logMonologue(c.getResource() + "  ->I have initially replicated "
					+ c.getAgent(),LogService.onBoth);
		} else
			throw new RuntimeException();

	}

	@Override
	public HostState getMySpecif(
			final HostState s,
			final ReplicationCandidature c) {
		return s;
	}

	@Override
	public boolean IWantToNegotiate(final HostState s) {
		return this.iWanToNegotiate;
	}

	@Override
	public Double evaluatePreference(final HostState s1) {
		return this.myOptimiser.getUtilitaristEvaluator().getUtilityValue(s1);
	}

}
//


//
//@Override
//public HostState getMyResultingState(final HostState fromState,
//		final ReplicationCandidature c) {
//	if (c == null)
//		this.logException("contrat null!!!!!");
//	else if (c.getAgentSpecification() == null)
//		this.logException("un defined agent!!!");
//	final ReplicaState replica = c.getAgentSpecification();
//	final boolean creation = c.isMatchingCreation();
//	if (replica == null)
//		throw new NullPointerException();
//
//	if (fromState.Ihost(replica.getMyAgentIdentifier())) {
//		if (creation == false) {
//			final HostState h = new HostState(fromState, replica);
//			if (!h.Ihost(replica.getMyAgentIdentifier()))
//				return h;
//			else
//				throw new RuntimeException("error while predicting " + c
//						+ "\n   _-> from state :" + fromState
//						+ "\n  _-> result state " + h);
//
//		} else {
//			this.getMyAgent().sendMessage(
//					replica.getMyAgentIdentifier(),
//					new ShowYourPocket(this.getMyAgent().getIdentifier(),
//							"hostcore:getmyresultingstate"));
//			throw new RuntimeException(
//					"oohhhhhhhhhhhhhhhhh  =( ALREADY CREATED"
//							+ replica
//							+ "\n ----> current state"
//							+ this
//							+ ((SimpleNegotiatingAgent) this.getMyAgent())
//							.getMyProtocol().getContracts());
//		}
//
//	} else {
//		if (creation == true) {
//
//			final HostState h = new HostState(fromState, replica);
//			if (h.Ihost(replica.getMyAgentIdentifier()))
//				return h;
//			else
//				throw new RuntimeException("error while predicting " + c
//						+ "\n   _-> from state :" + fromState
//						+ "\n  _-> result state " + h);
//		} else {
//			this.getMyAgent().sendMessage(
//					replica.getMyAgentIdentifier(),
//					new ShowYourPocket(this.getMyAgent().getIdentifier(),
//							"hostcore:getmyresultingstate"));
//			throw new RuntimeException(
//					"ooohhhhhhhhhhhhhhhhh  =( CAN NOT DESTRUCT " + replica
//					+ "\n ----> current state" + this);
//		}
//	}
//}


// if
// (getMyAgent().getIdentifier().toString().equals("#HOST_MANAGER##simu_0#HostManager_1:77"))
// logMonologue("!!!!!!!!!!!!!!!!!!!!executing "+c+" my state "+getMyAgent().getMyCurrentState());
// this.stopObservation(c.getAgent(), AgentInfo.class);
// getMyAgent().getMyCurrentState().myReplicatedAgents.remove(c.getAgent());
// this.observe(c.getAgent(), AgentInfo.class);
// getMyAgent().getMyCurrentState().myReplicatedAgents.add(c.getAgent());
// if
// (getMyAgent().getIdentifier().toString().equals("#HOST_MANAGER##simu_0#HostManager_1:77"))
// logMonologue("new state "+getMyAgent().getMyCurrentState());

// /*
// *
// */
//
// public void predictUpdate(final AgentIdentifier agent) {
// if (this.myState.getMyReplicatedAgents().contains(agent))
// this.myState.freeReplica(agent);
// else
// allocateReplica(this.myAgent.getMyInformation().getBelievedState(agent));
// }
//
// public void update(final AgentIdentifier agent) {
// try {
// if (this.myState.getMyReplicatedAgents().contains(agent))
// this.myState.freeReplica(agent);
// else
// allocateReplica(this.myAgent.getMyInformation().getAgentState(agent));
// } catch (final MissingInformationException e) {
// this.myAgent.getMyInformation().obtainInformation(e);
// this.myAgent.retryWhen(
// this.myAgent.getMyInformation(),
// "hasInformation",
// new Object[]{e}, new Object[]{agent});
// }
// }
//
// public void reset() {
// final Collection<AgentIdentifier> agentToRemove = new
// ArrayList<AgentIdentifier>();
// agentToRemove.addAll(this.myState.getMyReplicatedAgents());
// for (final AgentIdentifier r : agentToRemove)
// this.myState.freeReplica(r);
// }
//
//
// public void allocateReplica(final AgentIdentifier replica){
// this.myReplicatedAgents.add(replica);
// this.procCurrentCharge+= getMreplica.getProcCharge();
// this.memCurrentCharge+=replica.getMemCharge();
// }
//
// public void freeReplica(final AgentIdentifier agentIdentifier){
// final ReplicaState agentState =
// this.myReplicatedAgents.remove(agentIdentifier);
// this.procCurrentCharge-= agentState.getProcCharge();
// this.memCurrentCharge-=agentState.getMemCharge();
// }