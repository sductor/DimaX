package frameworks.negotiation.negotiationframework.rationality;

import java.util.Date;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.core.information.ObservationService.Information;

public abstract class SimpleAgentState implements AgentState {
	private static final long serialVersionUID = -1317496111744783996L;

	final AgentIdentifier myAgent;
	private final Long creationTime;
	private final int stateCounter;

	public SimpleAgentState(final AgentIdentifier myAgent, final int stateNumber) {
		super();
		this.myAgent = myAgent;
		this.creationTime = new Date().getTime();
		this.stateCounter=stateNumber;
	}

	//	public SimpleAgentState(final AgentIdentifier myAgent,
	//			final Long creationTime, int stateNumber) {
	//		super();
	//		this.myAgent = myAgent;
	//		this.creationTime = creationTime;
	//		stateCounter=stateNumber;
	//
	//	}

	@Override
	public int getStateCounter(){
		return this.stateCounter;
	}
	@Override
	public AgentIdentifier getMyAgentIdentifier() {
		return this.myAgent;
	}

	@Override
	public Long getCreationTime() {
		return this.creationTime;
	}

	@Override
	public long getUptime() {
		return new Date().getTime() - this.creationTime;
	}

	//	@Override
	//	public void resetUptime() {
	//		this.creationTime = new Date().getTime();
	//	}

	@Override
	public abstract AgentState clone();

	@Override
	public String toString() {
		return "State of agent " + this.myAgent;// +" generated on "+creationTime;
	}

	@Override
	public int isNewerThan(final Information i) {
		if (i instanceof AgentState){
			final SimpleAgentState that = (SimpleAgentState) i;
//			assert this.equals(that) || this.getStateCounter()!=that.getStateCounter():this.getStateCounter()+" "+that.getStateCounter();
//			assert this.equals(that) || (this.getStateCounter()>that.getStateCounter()?
//					this.creationTime>=that.creationTime:this.creationTime<= that.creationTime)
//					:this.getStateCounter()+" "+this.creationTime+" * "+that.getStateCounter()+" "+that.creationTime;

					return this.getStateCounter()-that.getStateCounter();
		} else {
			throw new RuntimeException("wtf");
		}
	}
}
