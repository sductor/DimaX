package frameworks.faulttolerance.candidaturewithstatus;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import frameworks.faulttolerance.Host;
import frameworks.faulttolerance.negotiatingagent.HostCore;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicaStateOpinionHandler;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.opinion.SimpleOpinionService;
import frameworks.negotiation.protocoles.InactiveProposerCore;
import frameworks.negotiation.protocoles.status.StatusObservationCompetence;
import frameworks.negotiation.protocoles.status.StatusProtocol;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;
import frameworks.negotiation.selection.OptimalSelectionModule;
import frameworks.negotiation.selection.SimpleSelectionCore;

public class StatusHost extends Host {
	private static final long serialVersionUID = 1891276000545412915L;

	@Competence
	public StatusObservationCompetence soc;

	public StatusHost(
			final ResourceIdentifier id,
			final HostState myState,
			final SimpleSelectionCore participantCore,
			final SocialChoiceType _socialWelfare,
			final AgentIdentifier myLaborantin,
			final double alpha_low, final double alpha_high) throws CompetenceException {
		this(id, myState, participantCore, _socialWelfare,true);
		this.soc=new StatusObservationCompetence(myLaborantin,false, ReplicaState.class, alpha_low, alpha_high);
		this.soc.setActive(false);
	}

	public StatusHost(
			final ResourceIdentifier id,
			final HostState myState,
			final SimpleSelectionCore participantCore,
			final SocialChoiceType _socialWelfare,
			final int numberToContact,
			final double alpha_low, final double alpha_high) throws CompetenceException {
		this(id, myState, participantCore, _socialWelfare,false);
		this.soc=new StatusObservationCompetence(numberToContact, false, ReplicaState.class, alpha_low, alpha_high);
	}


	private StatusHost(
			final ResourceIdentifier id,
			final HostState myState,
			final SimpleSelectionCore participantCore,
			final SocialChoiceType _socialWelfare,
			final boolean centralised) throws UnrespectedCompetenceSyntaxException, CompetenceException{
		super(id,
				myState,
				new HostCore(_socialWelfare,true,true),
				participantCore,
				new InactiveProposerCore(),
				centralised?new SimpleObservationService():new SimpleOpinionService(new ReplicaStateOpinionHandler(_socialWelfare, id)),
						new StatusProtocol(participantCore.getSelectionModule() instanceof OptimalSelectionModule));

	}

	//	@Override
	//	public ReplicationCandidature generateDestructionContract(AgentIdentifier id) {
	//		return new ReplicationCandidature((ResourceIdentifier) this.getIdentifier(),id,  false, false);
	//	}
	//	@Override
	//	public ReplicationCandidature generateCreationContract(AgentIdentifier id) {
	//		return new ReplicationCandidature((ResourceIdentifier)  this.getIdentifier(),id, true, false);
	//	}
}
