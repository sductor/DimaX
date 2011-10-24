package negotiation.negotiationframework.interaction.candidaturenegotiation.statusdestruction;

import negotiation.faulttolerance.negotiatingagent.HostCore;

public class CandidatureHostCoreWithStatus extends HostCore {

	/**
	 *
	 */
	private static final long serialVersionUID = 1667555079657235611L;

	public CandidatureHostCoreWithStatus(final boolean mirrorNegotiating) {
		super(mirrorNegotiating);
		throw new RuntimeException("unimplemented!!!!!!!");
		// TODO Auto-generated constructor stub
	}

	//
	// Global Observation
	//
	//
	// HeavyQuantileAggregator<AgentInfo> systemState =
	// new HeavyQuantileAggregator<AgentInfo>();
	//
	// void notifyTercile() {
	// if (this.systemState.getFirstTercile()!=null &&
	// this.systemState.getLastTercile()!=null)
	// this.notify(new SystemInformationMessage());
	// }
	//
	// void receiveAgentInfo(final AgentInfo i){
	// this.systemState.add(i);
	// }
	//
	// public class SystemInformationMessage implements Serializable{
	//
	// /**
	// *
	// */
	// private static final long serialVersionUID = 9097386950633875924L;
	// final Double firstTercile;
	// final Double lastTercile;
	//
	// public SystemInformationMessage() {
	// super();
	// this.firstTercile =
	// HostCore.this.systemState.getFirstTercile().getMyReliability();
	// this.lastTercile =
	// HostCore.this.systemState.getLastTercile().getMyReliability();
	// }
	//
	// public String toString(){
	// return
	// "\n * First="+firstTercile
	// +"\n * Last ="+lastTercile;
	// }
	// }

	//
	// Subclass

}
