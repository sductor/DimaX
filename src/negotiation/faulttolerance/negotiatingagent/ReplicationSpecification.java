package negotiation.faulttolerance.negotiatingagent;

import negotiation.negotiationframework.agent.AgentState;
import negotiation.negotiationframework.interaction.contracts.AbstractActionSpecification;


public interface ReplicationSpecification extends AbstractActionSpecification, AgentState {

}

// private static final long serialVersionUID = -3242409289534793592L;
//
// public HostSpecification actionHost=null;
// public AgentSpecification actionAgent=null;
//
// public ReplicationSpecification() {
// super();
// }
//
// public ReplicationSpecification(final AgentSpecification actionAgent) {
// super();
// this.actionAgent = actionAgent;
// }
//
// public ReplicationSpecification(final HostSpecification actionHost) {
// super();
// this.actionHost = actionHost;
// }
//
// //
// //
// //
//
// public HostSpecification getActionHost() {
// return this.actionHost;
// }
//
// public AgentSpecification getActionAgent() {
// return this.actionAgent;
// }
//
// //
// //
// //
//
// public String toString(){
// return "("+actionHost+","+actionAgent+")";
// }