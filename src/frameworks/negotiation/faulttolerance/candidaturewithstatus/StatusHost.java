package frameworks.negotiation.faulttolerance.candidaturewithstatus;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.core.information.SimpleObservationService;
import dima.introspectionbasedagents.services.core.opinion.SimpleOpinionService;
import frameworks.negotiation.faulttolerance.Host;
import frameworks.negotiation.faulttolerance.negotiatingagent.HostCore;
import frameworks.negotiation.faulttolerance.negotiatingagent.HostState;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicaStateOpinionHandler;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;
import frameworks.negotiation.negotiationframework.protocoles.InactiveProposerCore;
import frameworks.negotiation.negotiationframework.protocoles.ReverseCFPProtocol;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.negotiationframework.protocoles.status.StatusObservationCompetence;
import frameworks.negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;

public class StatusHost extends Host {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1891276000545412915L;
	@Competence
	public StatusObservationCompetence soc;

	public StatusHost(
			final ResourceIdentifier id,
			final HostState myState,
			final SelectionCore participantCore,
			final SocialChoiceType _socialWelfare,
			final AgentIdentifier myLaborantin) throws CompetenceException {
		this(id, myState, participantCore, _socialWelfare,true);
		this.soc=new StatusObservationCompetence(myLaborantin,false, ReplicaState.class);
		this.soc.setActive(false);
	}

	public StatusHost(
			final ResourceIdentifier id,
			final HostState myState,
			final SelectionCore participantCore,
			final SocialChoiceType _socialWelfare,
			final int numberToContact) throws CompetenceException {
		this(id, myState, participantCore, _socialWelfare,false);
		this.soc=new StatusObservationCompetence(numberToContact, false, ReplicaState.class);
	}


	private StatusHost(
			final ResourceIdentifier id,
			final HostState myState,
			final SelectionCore participantCore,
			final SocialChoiceType _socialWelfare,
			final boolean centralised) throws UnrespectedCompetenceSyntaxException, CompetenceException{
		super(id,
				myState,
				new HostCore(_socialWelfare,true,true),
				participantCore,
				new InactiveProposerCore(),
				centralised?new SimpleObservationService():new SimpleOpinionService(new ReplicaStateOpinionHandler(_socialWelfare, id)),
						new ReverseCFPProtocol());
		
	}
}
