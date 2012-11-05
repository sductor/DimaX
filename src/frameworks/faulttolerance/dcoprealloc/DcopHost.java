package frameworks.faulttolerance.dcoprealloc;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.Host;
import frameworks.faulttolerance.negotiatingagent.HostCore;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.faulttolerance.solver.SolverFactory;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.protocoles.dcopProtocol.DCOPLeaderProtocol;
import frameworks.negotiation.protocoles.dcopProtocol.DcopLeaderProposerCore;
import frameworks.negotiation.protocoles.dcopProtocol.DcopLeaderSelectionCore;
import frameworks.negotiation.protocoles.dcopProtocol.LocalViewInformationService;
import frameworks.negotiation.rationality.AltruistRationalCore;
import frameworks.negotiation.rationality.RationalCore;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class DcopHost extends Host {

	public DcopHost(
			ResourceIdentifier id, 
			HostState myState,
			SocialChoiceType socialWelfare,
			int k,
			ResourceAllocationSolver solver,
			final long maxComputingTime) throws CompetenceException {
		super(id, myState, 
				new AltruistRationalCore(new ReplicationSocialOptimisation(socialWelfare),
						new HostCore(socialWelfare,false,true)),
				new DcopLeaderSelectionCore(), 
		new DcopLeaderProposerCore(), 
		new LocalViewInformationService(),
		new DCOPLeaderProtocol(k,1,solver,maxComputingTime));
	}
//	@StepComposant(ticker=1000)
//	public void sayAlive() {
//		logMonologue("I'M STILL ALIVE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",LogService.onBoth);
//	}
}
//{
//
//	@Override
//	public ReplicationCandidature generateDestructionContract(
//			AgentIdentifier a1, AgentIdentifier a2) {
//
//		final AgentIdentifier ag;
//		final ResourceIdentifier h;
//		if (a1 instanceof ResourceIdentifier){
//			assert !(a2 instanceof ResourceIdentifier);
//			ag = a2;
//			h = (ResourceIdentifier) a1;
//		} else {
//			assert (a2 instanceof ResourceIdentifier);
//			ag = a1;
//			h = (ResourceIdentifier) a2;			
//		}
//		return new ReplicationCandidature(h, ag, false, false);
//	}
//
//	@Override
//	public ReplicationCandidature generateCreationContract(
//			AgentIdentifier a1, AgentIdentifier a2) {
//
//		final AgentIdentifier ag;
//		final ResourceIdentifier h;
//		if (a1 instanceof ResourceIdentifier){
//			assert !(a2 instanceof ResourceIdentifier);
//			ag = a2;
//			h = (ResourceIdentifier) a1;
//		} else {
//			assert (a2 instanceof ResourceIdentifier);
//			ag = a1;
//			h = (ResourceIdentifier) a2;			
//		}
//		return new ReplicationCandidature(h, ag, true, false);
//	}
//}