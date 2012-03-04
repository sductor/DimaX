package dima.introspectionbasedagents.services;

import java.io.Serializable;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.CompetentComponent;

public class BasicAgentModule<Agent extends CompetentComponent> implements DimaComponentInterface{
	private static final long serialVersionUID = -8166804401339182512L;

	//
	// Fields
	//

	Agent myAgent;

	//
	// Constructors
	//


	public BasicAgentModule(final Agent ag){
		this.myAgent = ag;
	}

	public BasicAgentModule(){
	}

	//
	// Accessors
	//

	public AgentIdentifier getMyAgentIdentifier(){
		return this.getMyAgent().getIdentifier();
	}

	public void setMyAgent(final Agent ag) {
		this.myAgent=ag;
	}

	public Agent getMyAgent() {
		return this.myAgent;
	}
	/*
	 * loggage
	 */

	public Boolean signalException(final String text, final Throwable e) {
		return this.myAgent.signalException(text, e);
	}

	//	@Override
	//	public Boolean logException(final String text, final String details, final Throwable e) {
	//		return this.myAgent.logException(text, details, e);
	//	}
	//
	//	@Override
	//	public Boolean logException(final String text, final String details) {
	//		return this.myAgent.logException(text, details);
	//	}

	public Boolean signalException(final String text) {
		return this.myAgent.signalException(text);
	}

	public Boolean logMonologue(final String text, final String details) {
		return this.myAgent.logMonologue(text, details);
	}

	//	@Override
	//	public Boolean logMonologue(final String text) {
	//		return this.myAgent.logMonologue(text);
	//	}
	//
	//	@Override
	//	public Boolean logWarning(final String text, final Throwable e) {
	//		return this.myAgent.logWarning(text, e);
	//	}

	public Boolean logWarning(final String text, final Throwable e, final String details) {
		return this.myAgent.logWarning(text, e, details);
	}

	public Boolean logWarning(final String text, final String details) {
		return this.myAgent.logWarning(text, details);
	}

	//	@Override
	//	public Boolean logWarning(final String text) {
	//		return this.myAgent.logWarning(text);
	//	}

	public void addLogKey(final String key, final boolean toString, final boolean toFile) {
		this.myAgent.addLogKey(key, toString, toFile);
	}
	public void setLogKey(final String key, final boolean toScreen, final boolean toFile) {
		this.myAgent.setLogKey(key, toScreen, toFile);
	}
	/*
	 * Observation
	 */

	public <Notification extends Serializable> Boolean notify(final Notification notification, final String key) {
		return this.myAgent.notify(notification, key);
	}

	public <Notification extends Serializable> Boolean notify(final Notification notification) {
		return this.myAgent.notify(notification);
	}

}
