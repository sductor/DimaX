package frameworks.negotiation.faulttolerance.collaborativecandidature;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import frameworks.negotiation.faulttolerance.Host;
import frameworks.negotiation.faulttolerance.negotiatingagent.HostCore;
import frameworks.negotiation.faulttolerance.negotiatingagent.HostState;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicationHostAllocationSolver;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;
import frameworks.negotiation.negotiationframework.protocoles.collaborative.InformedCandidature;
import frameworks.negotiation.negotiationframework.protocoles.collaborative.InformedCandidatureRationality;
import frameworks.negotiation.negotiationframework.protocoles.collaborative.OneDeciderCommunicationProtocol;
import frameworks.negotiation.negotiationframework.protocoles.collaborative.ResourceInformedProposerCore;
import frameworks.negotiation.negotiationframework.protocoles.collaborative.ResourceInformedSelectionCore;
import frameworks.negotiation.negotiationframework.rationality.AgentState;
import frameworks.negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
import frameworks.negotiation.negotiationframework.selection.GreedySelectionModule.GreedySelectionType;

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
