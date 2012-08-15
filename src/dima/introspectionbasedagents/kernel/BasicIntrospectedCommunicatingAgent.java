package dima.introspectionbasedagents.kernel;

import java.util.Date;
import java.util.Map;

import dima.basicagentcomponents.AgentAddress;
import dima.basicagentcomponents.AgentIdentifier;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;

public class BasicIntrospectedCommunicatingAgent extends BasicCommunicatingAgent{

	/**
	 *
	 */
	private static final long serialVersionUID = -7910893993695270592L;
	protected BasicCommunicatingShell myShell;

	public Date creation;

	public BasicIntrospectedCommunicatingAgent() {
		super();
		this.creation = new Date();
	}

	public BasicIntrospectedCommunicatingAgent(final AgentIdentifier newId) {
		super(newId);
		this.creation = new Date();
	}

	public BasicIntrospectedCommunicatingAgent(final Map<String, AgentAddress> mp, final AgentIdentifier newId) {
		super(mp, newId);
		this.creation = new Date();
	}

	public BasicIntrospectedCommunicatingAgent(final Map<String, AgentAddress> mp) {
		super(mp);
		this.creation = new Date();
	}

	public BasicIntrospectedCommunicatingAgent(final String newId) {
		super(newId);
		this.creation = new Date();
	}

	/*
	 *
	 */

	public BasicIntrospectedCommunicatingAgent(final Date horloge) {
		super();
		this.creation = horloge;
	}

	public BasicIntrospectedCommunicatingAgent(final AgentIdentifier newId, final Date horloge) {
		super(newId);
		this.creation = horloge;
	}

	public BasicIntrospectedCommunicatingAgent(final Map<String, AgentAddress> mp, final AgentIdentifier newId, final Date horloge) {
		super(mp, newId);
		this.creation = horloge;
	}

	public BasicIntrospectedCommunicatingAgent(final Map<String, AgentAddress> mp, final Date horloge) {
		super(mp);
		this.creation = horloge;
	}

	public BasicIntrospectedCommunicatingAgent(final String newId, final Date horloge) {
		super(newId);
		this.creation = horloge;
	}

	
	//
	// Proactivity
	//


	@Override
	public final void proactivityInitialize() {
		this.myShell =this.initiateMyShell();
		assert myShell!=null;
		this.myShell.proactivityInitialize(this.creation);
		Thread.yield();
	}

	@Override
	public final void preActivity() {
		this.myShell.preActivity(this.creation);
		Thread.yield();
	}

	@Override
	public final void step() {
		this.myShell.step(this.creation);
	}

	@Override
	public final void postActivity(){
		this.myShell.postActivity(this.creation);
		Thread.yield();
	}

	@Override
	public  void tryToResumeActivity() {
		this.myShell.resumeActivity(this.creation);
		Thread.yield();
	}

	@Override
	public final  void proactivityTerminate() {
		this.myShell.proactivityTerminate(this.creation);
		this.myShell=null;
		Thread.yield();
	}

	//
	// Primitive
	//

	protected BasicCommunicatingShell initiateMyShell(){
		return  new BasicCommunicatingShell(this);
	}


}
