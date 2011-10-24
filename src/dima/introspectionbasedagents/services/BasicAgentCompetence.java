package dima.introspectionbasedagents.services;

import java.io.Serializable;
import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.ActiveComponentInterface;
import dima.introspectionbasedagents.CompetentComponent;
import dima.introspectionbasedagents.NotReadyException;

public class BasicAgentCompetence<Agent extends CompetentComponent> implements AgentCompetence<Agent>, CompetentComponent{
	private static final long serialVersionUID = -8166804401339182512L;

	//
	// Fields
	//

	Agent myAgent;
	
	boolean active=true;
	
	//
	// Constructors
	//

	public BasicAgentCompetence(Agent ag) throws UnrespectedCompetenceSyntaxException{
		setMyAgent(ag);
	}
	
	public BasicAgentCompetence(){
	}

	//
	// Accessors
	//

	@Override
	public AgentIdentifier getIdentifier(){
		return this.getMyAgent().getIdentifier();
	}
	
	@Override
	public void setMyAgent(final Agent ag) throws UnrespectedCompetenceSyntaxException {
		this.myAgent=ag;
	}

	@Override
	public Agent getMyAgent() {
		return this.myAgent;
	}

	@Override
	public boolean isActive() {
		return active;
	}
	
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
	@Override
	public void die(){
		this.myAgent=null;
	}

	//
	// Methods
	//
	
	/*
	 * Hook
	 */

	@Override
	public boolean retryWhen(AgentCompetence comp, String methodToTest,
			ActiveComponentInterface methodComponent, Object[] testArgs,
			Object[] methodsArgs) {
		return myAgent.retryWhen(comp, methodToTest, methodComponent, testArgs,
				methodsArgs);
	}
	
	@Override
	public boolean when(final AgentCompetence comp, final String compMethodToTest,
			final Class<?>[] compSignature, final Object[] compargs,
			final String agMethodToExecute, final Class<?>[] agSignature, final Object[] agargs) {
		return this.myAgent.when(comp, compMethodToTest, compSignature, compargs,
				agMethodToExecute, agSignature, agargs);
	}

	@Override
	public boolean when(final AgentCompetence comp, final String compMethodToTest,
			final Object[] compargs, final String agMethodToExecute, final Object[] agargs) {
		return this.myAgent.when(comp, compMethodToTest, compargs,
				agMethodToExecute, agargs);
	}

	@Override
	public boolean when(final AgentCompetence comp, final String compMethodToTest,
			final Object[] compargs, final String agMethodToExecute) {
		return this.myAgent
				.when(comp, compMethodToTest, compargs, agMethodToExecute);
	}

	@Override
	public boolean when(final AgentCompetence comp, final String compMethodToTest,
			final String agMethodToExecute, final Object[] agargs) {
		return this.myAgent.when(comp, compMethodToTest, agMethodToExecute, agargs);
	}

	@Override
	public boolean when(final AgentCompetence comp, final String compMethodToTest,
			final String agMethodToExecute) {
		return this.myAgent.when(comp, compMethodToTest, agMethodToExecute);
	}

	@Override
	public boolean retryWhen(final AgentCompetence comp, final String methodToTest,
			final Object[] testArgs, final Object[] methodsArgs) {
		return this.myAgent.retryWhen(comp, methodToTest, this, testArgs, methodsArgs);
	}

	@Override
	public boolean whenIsReady(NotReadyException e) {
		return this.myAgent.whenIsReady(e);
	}
	
	/*
	 * loggage
	 */

	@Override
	public Boolean logException(final String text, final Throwable e) {
		return this.myAgent.logException(text, e);
	}

	@Override
	public Boolean logException(final String text, final String details, final Throwable e) {
		return this.myAgent.logException(text, details, e);
	}

	@Override
	public Boolean logException(final String text, final String details) {
		return this.myAgent.logException(text, details);
	}

	@Override
	public Boolean logException(final String text) {
		return this.myAgent.logException(text);
	}

	@Override
	public Boolean logMonologue(final String text, final String details) {
		return this.myAgent.logMonologue(text, details);
	}

	@Override
	public Boolean logMonologue(final String text) {
		return this.myAgent.logMonologue(text);
	}

	@Override
	public Boolean logWarning(final String text, final Throwable e) {
		return this.myAgent.logWarning(text, e);
	}

	@Override
	public Boolean logWarning(final String text, final String details, final Throwable e) {
		return this.myAgent.logWarning(text, details, e);
	}

	@Override
	public Boolean logWarning(final String text, final String details) {
		return this.myAgent.logWarning(text, details);
	}

	@Override
	public Boolean logWarning(final String text) {
		return this.myAgent.logWarning(text);
	}

	@Override
	public void addLogKey(String key, boolean toString, boolean toFile) {
		this.myAgent.addLogKey(key, toString, toFile);
	}
	/*
	 * Observation
	 */
	
	@Override
	public <Notification extends Serializable> Boolean notify(final Notification notification, final String key) {
		return this.myAgent.notify(notification, key);
	}

	@Override
	public <Notification extends Serializable> Boolean notify(final Notification notification) {
		return this.myAgent.notify(notification);
	}

	@Override
	public Boolean addToBlackList(final AgentIdentifier o, final Boolean add) {
		return this.myAgent.addToBlackList(o, add);
	}

	@Override
	public void observe(final AgentIdentifier observedAgent, final Class<?> notificationKey) {
		this.myAgent.observe(observedAgent, notificationKey);
	}

	@Override
	public void observe(final AgentIdentifier observedAgent, final String notificationToObserve) {
		this.myAgent.observe(observedAgent, notificationToObserve);
	}

	@Override
	public void stopObservation(final AgentIdentifier observedAgent, final Class<?> notificationKey) {
		this.myAgent.stopObservation(observedAgent, notificationKey);
	}

	@Override
	public void stopObservation(final AgentIdentifier observedAgent, final String notificationToObserve) {
		this.myAgent.stopObservation(observedAgent, notificationToObserve);
	}

	@Override
	public void autoObserve(Class<?> notificationKey) {
		myAgent.autoObserve(notificationKey);
	}

	@Override
	public void addObserver(AgentIdentifier observerAgent,
			Class<?> notificationKey) {
		myAgent.addObserver(observerAgent, notificationKey);
	}

	@Override
	public void addObserver(AgentIdentifier observerAgent,
			String notificationKey) {
		myAgent.addObserver(observerAgent, notificationKey);
	}

	@Override
	public void removeObserver(AgentIdentifier observerAgent,
			Class<?> notificationKey) {
		myAgent.removeObserver(observerAgent, notificationKey);
	}

	@Override
	public void removeObserver(AgentIdentifier observerAgent,
			String notificationKey) {
		myAgent.removeObserver(observerAgent, notificationKey);
	}

	public void sendNotificationNow(){
		myAgent.sendNotificationNow();
	}
	/*
	 * 
	 */
	
	@Override
	public Boolean isObserved(Class<?> notificationKey) {
		return myAgent.isObserved(notificationKey);
	}

	@Override
	public Collection<AgentIdentifier> getObservers(Class<?> notificationKey) {
		return myAgent.getObservers(notificationKey);
	}




}
