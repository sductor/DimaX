package frameworks.faulttolerance.dcoprealloc;

import dima.introspectionbasedagents.services.CompetenceException;
import frameworks.faulttolerance.Host;
import frameworks.faulttolerance.negotiatingagent.HostCore;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.protocoles.dcopProtocol.DCOPLeaderProtocol;
import frameworks.negotiation.protocoles.dcopProtocol.DcopLeaderProposerCore;
import frameworks.negotiation.protocoles.dcopProtocol.DcopLeaderSelectionCore;
import frameworks.negotiation.protocoles.dcopProtocol.LocalViewInformationService;
import frameworks.negotiation.rationality.AltruistRationalCore;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class DcopHost extends Host {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5486075821387499417L;

	public DcopHost(
			final ResourceIdentifier id,
			final HostState myState,
			final SocialChoiceType socialWelfare,
			final int k,
			final ResourceAllocationSolver solver,
			final long maxComputingTime,
			Double collectiveSeed) throws CompetenceException {
		super(id, myState,
				new AltruistRationalCore(new ReplicationSocialOptimisation(socialWelfare),
						new HostCore(socialWelfare,false,false)),
						new DcopLeaderSelectionCore(),
						new DcopLeaderProposerCore(),
						new LocalViewInformationService(),
						new DCOPLeaderProtocol(k,1,solver,maxComputingTime),collectiveSeed);
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