package negotiation.faulttolerance.candidaturewithstatus;

import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.OpinionService;
import negotiation.faulttolerance.experimentation.Host;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.NegotiationParameters;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.RationalCore;

public class StatusHost extends Host {
	
	
	public StatusHost(ResourceIdentifier id, HostState myState,
			RationalCore myRationality, SelectionCore participantCore,
			ProposerCore proposerCore, ObservationService myInformation,
			AbstractCommunicationProtocol protocol) throws CompetenceException {
		super(id, myState, myRationality, participantCore, proposerCore, myInformation,
				protocol);
	}

	@StepComposant(ticker = NegotiationParameters._statusObservationFrequency)
	public void notifyMyReliability4Status() {
		// logMonologue("relia send to "+observer.getObserver(ReplicationExperimentationProtocol.reliabilityObservationKey));
		try {
			this.notify(
					((OpinionService) getMyInformation()).getGlobalOpinion(ReplicaState.class),
					CentralisedObservingStatusService.reliabilityObservationKey);
		} catch (NoInformationAvailableException e) {
			//do nothing
		}
	}
	
}
