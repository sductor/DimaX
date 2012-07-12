package negotiation.faulttolerance.candidaturewithstatus;

import java.util.Collection;

import negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.negotiationframework.NegotiationParameters;
import negotiation.negotiationframework.protocoles.status.AgentStateStatus;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.information.OpinionService;
import dima.introspectionbasedagents.services.information.OpinionService.Opinion;




//
//public void beNotified(final SystemInformationMessage notification) {
//	this.lowerThreshold = notification.lowerThreshold;
//	this.higherThreshold = notification.higherThreshold;
//	// logMonologue("update!!!!!!!!!!\n * myReliab="+getMyAgent().getMyCurrentState().getMyReliability()+notification+"\n * my status "+getMyStatus());
//}