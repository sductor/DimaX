package negotiation.faulttolerance.collaborativecandidature;

import negotiation.faulttolerance.Host;
import negotiation.faulttolerance.negotiatingagent.HostCore;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationHostAllocationSolver;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.collaborative.InformedCandidature;
import negotiation.negotiationframework.protocoles.collaborative.InformedCandidatureRationality;
import negotiation.negotiationframework.protocoles.collaborative.OneDeciderCommunicationProtocol;
import negotiation.negotiationframework.protocoles.collaborative.ResourceInformedProposerCore;
import negotiation.negotiationframework.protocoles.collaborative.ResourceInformedSelectionCore;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
import negotiation.negotiationframework.selection.GreedySelectionModule.GreedySelectionType;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;

public class CollaborativeHost extends Host{
	private static final long serialVersionUID = -8478683967125467116L;


	public CollaborativeHost(
			final ResourceIdentifier myId,
			final HostState myState,
			final SocialChoiceType socialWelfare,
			final int maxCAccepts,
			final GreedySelectionType initialSelectionType,
			final long maxComputingTime)
					throws CompetenceException {
		super(
				myId,
				myState,
				new InformedCandidatureRationality(new HostCore(socialWelfare,true,true),false),
				new ResourceInformedSelectionCore(new ReplicationHostAllocationSolver(socialWelfare),maxCAccepts,initialSelectionType,maxComputingTime){
					private static final long serialVersionUID = -1578866978817500691L;
					@Override
					protected InformedCandidature generateDestructionContract(final AgentIdentifier id) {
						return new InformedCandidature(new ReplicationCandidature(myId,id,false,false));
					}
					@Override
					protected void setSpecif(final AgentState s,
							final InformedCandidature d) {
						//						return new NoActionSpec();
					}
				},//new GreedyBasicSelectionCore(true, false),//
				new ResourceInformedProposerCore(),
				new SimpleObservationService(),
				new OneDeciderCommunicationProtocol(true) );
		this.getMyProtocol().getContracts().setMyAgent(this);
	}
}
