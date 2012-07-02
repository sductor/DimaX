package dima.introspectionbasedagents.services;

import java.io.Serializable;
import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.ActiveComponentInterface;
import dima.introspectionbasedagents.CompetentComponent;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.NotReadyException;

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

	public BasicAgentCompetence(final Agent ag) throws UnrespectedCompetenceSyntaxException{
		this.setMyAgent(ag);
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
	public void setMyAgent(final Agent ag)  {
		this.myAgent=ag;
	}

	@Override
	public Agent getMyAgent() {
		assert this.myAgent!=null;
		return this.myAgent;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void activateCompetence(final boolean active) {
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
	public boolean retryWhen(final AgentCompetence comp, final String methodToTest,
			final ActiveComponentInterface methodComponent, final Object[] testArgs,
			final Object[] methodsArgs) {
		return this.myAgent.retryWhen(comp, methodToTest, methodComponent, testArgs,
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
	public boolean whenIsReady(final NotReadyException e) {
		return this.myAgent.whenIsReady(e);
	}

	/*
	 * loggage
	 */

	@Override
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

	@Override
	public Boolean signalException(final String text) {
		return this.myAgent.signalException(text);
	}

	@Override
	public Boolean logMonologue(final String text, final String details) {
		return this.myAgent.logMonologue(text, details);
	}

	public Boolean logMonologue(final String text) {
		return this.myAgent.logMonologue(text, LogService.onBoth);
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

	@Override
	public Boolean logWarning(final String text, final Throwable e, final String details) {
		return this.myAgent.logWarning(text, e, details);
	}

	@Override
	public Boolean logWarning(final String text, final String details) {
		return this.myAgent.logWarning(text, details);
	}

	public Boolean logWarning(final String text, final Throwable e) {
		return this.myAgent.logWarning(text, e, LogService.onBoth);
	}

	public Boolean logWarning(final String text) {
		return this.myAgent.logWarning(text, LogService.onBoth);
	}
	
	//	@Override
	//	public Boolean logWarning(final String text) {
	//		return this.myAgent.logWarning(text);
	//	}

	@Override
	public void addLogKey(final String key, final boolean toString, final boolean toFile) {
		this.myAgent.addLogKey(key, toString, toFile);
	}
	@Override
	public void setLogKey(final String key, final boolean toScreen, final boolean toFile) {
		this.myAgent.setLogKey(key, toScreen, toFile);
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
	public void autoObserve(final Class<?> notificationKey) {
		this.myAgent.autoObserve(notificationKey);
	}

	@Override
	public void addObserver(final AgentIdentifier observerAgent,
			final Class<?> notificationKey) {
		this.myAgent.addObserver(observerAgent, notificationKey);
	}

	@Override
	public void addObserver(final AgentIdentifier observerAgent,
			final String notificationKey) {
		this.myAgent.addObserver(observerAgent, notificationKey);
	}

	@Override
	public void removeObserver(final AgentIdentifier observerAgent,
			final Class<?> notificationKey) {
		this.myAgent.removeObserver(observerAgent, notificationKey);
	}

	@Override
	public void removeObserver(final AgentIdentifier observerAgent,
			final String notificationKey) {
		this.myAgent.removeObserver(observerAgent, notificationKey);
	}

	@Override
	public void sendNotificationNow(){
		this.myAgent.sendNotificationNow();
	}
	/*
	 *
	 */

	@Override
	public Boolean isObserved(final Class<?> notificationKey) {
		return this.myAgent.isObserved(notificationKey);
	}

	@Override
	public Collection<AgentIdentifier> getObservers(final Class<?> notificationKey) {
		return this.myAgent.getObservers(notificationKey);
	}




}
