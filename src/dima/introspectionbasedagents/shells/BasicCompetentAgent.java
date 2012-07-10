package dima.introspectionbasedagents.shells;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.AbstractMessage;
import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.ActiveComponentInterface;
import dima.introspectionbasedagents.CommunicatingCompetentComponent;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ResumeActivity;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.DuplicateCompetenceException;
import dima.introspectionbasedagents.services.UnInstanciedCompetenceException;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.loggingactivity.LogCommunication.MessageStatus;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.PatternObserverWithHookservice;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.shells.APIAgent.EndLiveMessage;
import dima.introspectionbasedagents.shells.APIAgent.SigKillOrder;
import dima.introspectionbasedagents.shells.APIAgent.StartActivityMessage;
import dimaxx.kernel.DimaXTask;
import dimaxx.server.HostIdentifier;

public class BasicCompetentAgent extends BasicIntrospectedCommunicatingAgent implements CommunicatingCompetentComponent{

	private static final long serialVersionUID = 1137235428764719916L;

	//
	// Fields
	//

	public static int nbCompetentAgent=0;
	DimaXTask<BasicCompetentAgent> darxEngine=null;
	boolean isActive=true;

	//
	// Constructor
	//

	public BasicCompetentAgent(final AgentIdentifier newId)  throws CompetenceException {
		super(newId);
		this.log= new LogService<BasicCompetentAgent>(this);
		this.observer=	new PatternObserverWithHookservice(this);
		this.apiService = new ApiLaunchService(this);
		BasicCompetentAgent.nbCompetentAgent++;
	}

	public BasicCompetentAgent(final String newId) throws CompetenceException {
		super(newId);
		this.log= new LogService<BasicCompetentAgent>(this);
		this.observer=	new PatternObserverWithHookservice(this);
		this.apiService = new ApiLaunchService(this);
		BasicCompetentAgent.nbCompetentAgent++;
	}

	//	/*
	//	 *
	//	 */
	//
	//	public BasicCompetentAgent(final AgentIdentifier newId, final Date horloge)  throws CompetenceException {
	//		super(newId, horloge);
	//		log= new LogService(this);
	//		observer=	new PatternObserverWithHookservice(this);
	//	this.apiService = new ApiLaunchService(this);
	//		nbCompetentAgent++;
	//	}
	//
	//
	//	public BasicCompetentAgent(final String newId, final Date horloge) throws CompetenceException {
	//		super(newId, horloge);
	//		log= new LogService(this);
	//		observer=	new PatternObserverWithHookservice(this);
	//	this.apiService = new ApiLaunchService(this);
	//		nbCompetentAgent++;
	//	}

	//
	// Accessors
	//

	@Override
	public BasicCompetenceShell<BasicCompetentAgent> getMyShell() {
		return (BasicCompetenceShell<BasicCompetentAgent>) this.myShell;
	}

	public Date getCreationTime() {
		return this.creation;
	}

	public long getUptime() {
		return new Date().getTime()-this.creation.getTime();
	}
	//
	//	public void resetUptime(){
	//		this.creation = new Date();
	//	}

	public void load(final BasicAgentCompetence<BasicCompetentAgent> newComp)
			throws UnInstanciedCompetenceException, DuplicateCompetenceException, UnrespectedCompetenceSyntaxException{
		this.getMyShell().load(newComp);
	}

	public void unload(final BasicAgentCompetence<BasicCompetentAgent> newComp)
			throws UnInstanciedCompetenceException, DuplicateCompetenceException{
		this.getMyShell().unload(newComp);
	}

	@Override
	public boolean isActive() {
		return apiService.hasAppliStarted()&&isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setDarxEngine(final DimaXTask darxEngine) {
		this.darxEngine=darxEngine;
	}

	public Collection<Class<? extends AgentCompetence<BasicCompetentAgent>>> getCompetences(){
		return this.getMyShell().loadedCompetence;
	}

	//
	// Hook
	//

	@Override
	public boolean when(
			final AgentCompetence comp,
			final String compMethodToTest, final String agMethodToExecute){
		return this.when(comp, compMethodToTest, null, new Object[]{}, agMethodToExecute, null, new Object[]{});
	}

	@Override
	public boolean when(
			final AgentCompetence comp,
			final String compMethodToTest,  final Object[] compargs,
			final String agMethodToExecute, final Object[] agargs){
		return this.when(comp, compMethodToTest, null, compargs, agMethodToExecute, null, agargs);
	}

	@Override
	public boolean when(
			final AgentCompetence comp,
			final String compMethodToTest,
			final String agMethodToExecute, final Object[] agargs){
		return this.when(comp, compMethodToTest, null, new Object[]{}, agMethodToExecute, null, agargs);
	}

	@Override
	public boolean when(
			final AgentCompetence comp,
			final String compMethodToTest,  final Object[] compargs,
			final String agMethodToExecute){
		return this.when(comp, compMethodToTest, null, compargs, agMethodToExecute, null, new Object[]{});
	}

	@Override
	public boolean when(
			final AgentCompetence comp,
			final String compMethodToTest,  final Class<?>[] compSignature, final Object[] compargs,
			final String agMethodToExecute,  final Class<?>[] agSignature, final Object[] agargs){
		try {
			return this.getMyShell().addHook(comp,
					compMethodToTest, compSignature, compargs,
					this, agMethodToExecute, agSignature, agargs);
		} catch (final Exception e) {
			this.signalException("Impossible to add the hook", e);
			return false;
		}
	}

	/**
	 * retryWhen order the reexecution of the method it is called when the competence boolean method is verified.
	 * !!!! The caller method must not take any argument !!!!
	 * @param comp
	 * @param methodToTest
	 * @param objects
	 * @return
	 */
	private static final int nombreAdaptePourRecupererlaMethodeKiVa_bouhPasBo = 3;

	@Override
	public boolean retryWhen(final AgentCompetence comp,
			final String methodToTest, final ActiveComponentInterface methodComponent, final Object[] testArgs, final Object[] methodsArgs){
		try {
			return this.getMyShell().addHook(
					comp,
					methodToTest,
					null, testArgs,
					methodComponent,
					SimpleMethodHandler.getCurrentlyExecutedMethod(BasicCompetentAgent.nombreAdaptePourRecupererlaMethodeKiVa_bouhPasBo),
					null,	methodsArgs);
		} catch (final Exception e) {
			this.signalException("Impossible to add the hook", e);
			return false;
		}
	}

	@Override
	public boolean retryWhen(final AgentCompetence comp,
			final String methodToTest, final Object[] testArgs, final Object[] methodsArgs){
		try {
			return this.getMyShell().addHook(
					comp,
					methodToTest,
					null, testArgs,
					this,
					SimpleMethodHandler.getCurrentlyExecutedMethod(BasicCompetentAgent.nombreAdaptePourRecupererlaMethodeKiVa_bouhPasBo),
					null,	methodsArgs);
		} catch (final Exception e) {
			this.signalException("Impossible to add the hook", e);
			return false;
		}
	}

	/*
	 *
	 */

	@Override
	public boolean whenIsReady(final NotReadyException e){
		try {
			return this.getMyShell().addHook(e.comp,
					e.compMethodToTest, e.compSignature, e.compargs,
					this, e.agMethodToExecute, e.agSignature, e.agargs);
		} catch (final Exception e2) {
			this.signalException("Impossible to add the hook", e);
			return false;
		}
	}

	//
	// Competence
	//


	/*
	 * Launch
	 */

	@Competence()
	public
	final ApiLaunchService apiService;

	public boolean hasAppliStarted() {
		return apiService.hasAppliStarted();
	}

	boolean launchWith(APILauncherModule api) {
		return apiService.launchWith(api);
	}

	boolean launchWith(APILauncherModule api, HostIdentifier h) {
		return apiService.launchWith(api, h);
	}

	boolean start(StartActivityMessage m) {
		return apiService.start(m);
	}

	boolean endLive(EndLiveMessage m) {
		return apiService.endLive(m);
	}

	boolean endLive() {
		return apiService.endLive();
	}
	/*
	 * Pattern Observer
	 */

	@Competence()
	public
	final PatternObserverWithHookservice observer;

	/**/

	@Override
	public <Notification extends Serializable> Boolean notify(
			final Notification notification, final String key) {
		return this.observer.notify(notification, key);
	}

	@Override
	public <Notification extends Serializable> Boolean notify(
			final Notification notification) {
		return this.observer.notify(notification);
	}

	/**/

	@Override
	public void observe(final AgentIdentifier observedAgent, final Class<?> notificationKey) {
		this.observer.observe(observedAgent, notificationKey);
	}

	@Override
	public void observe(final AgentIdentifier observedAgent,
			final String notificationToObserve) {
		this.observer.observe(observedAgent, notificationToObserve);
	}


	@Override
	public void stopObservation(final AgentIdentifier observedAgent,
			final Class<?> notificationKey) {
		this.observer.stopObservation(observedAgent, notificationKey);
	}

	@Override
	public void stopObservation(final AgentIdentifier observedAgent,
			final String notificationToObserve) {
		this.observer.stopObservation(observedAgent, notificationToObserve);
	}

	@Override
	public void autoObserve(final Class<?> notificationKey) {
		this.observer.addObserver(this.getIdentifier(), notificationKey.getName());
	}

	@Override
	public void addObserver(final AgentIdentifier observerAgent, final Class<?> notificationKey) {
		this.observer.addObserver(observerAgent, notificationKey.getName());
	}

	@Override
	public void addObserver(final AgentIdentifier observerAgent, final String notificationKey) {
		this.observer.addObserver(observerAgent, notificationKey);
	}

	@Override
	public void removeObserver(final AgentIdentifier observerAgent, final Class<?> notificationKey) {
		this.observer.removeObserver(observerAgent, notificationKey.getName());
	}

	@Override
	public void removeObserver(final AgentIdentifier observerAgent, final String notificationKey) {
		this.observer.removeObserver(observerAgent, notificationKey);
	}

	@Override
	public Boolean isObserved(final Class<?> notificationKey) {
		return this.observer.isObserved(notificationKey);
	}

	@Override
	public Collection<AgentIdentifier> getObservers(final Class<?> notificationKey) {
		return this.observer.getObservers(notificationKey);
	}

	@Override
	public void sendNotificationNow(){
		this.observer.autoSendOfNotifications();
	}
	/*
	 *
	 */
	/**/

	@Override
	public Boolean addToBlackList(final AgentIdentifier o, final Boolean add) {
		return this.observer.addToBlackList(o, add);
	}

	/*
	 * Log
	 */

	@Competence()
	public
	final LogService<BasicCompetentAgent> log;



	@Override
	public Boolean signalException(final String text, final Throwable e) {
		return this.log.signalException(text, e);
	}

	//	@Override
	//	public Boolean logException(final String text, final String details, final Throwable e) {
	//		return this.log.logException(text, details, e);
	//	}
	//
	//	@Override
	//	public Boolean logException(final String text, final String details) {
	//		return this.log.logException(text, details);
	//	}

	@Override
	public Boolean signalException(final String text) {
		return this.log.signalException(text);
	}

	@Override
	public Boolean logMonologue(final String text, final String details) {
		return this.log.logMonologue(text, details);
	}

	@Override
	public Boolean logMonologue(final String text) {
		return this.log.logMonologue(text);
	}
	
	//	 @Override
	//	 public Boolean logMonologue(final String text) {
	//		 return this.log.logMonologue(text);
	//	 }
	//
	//	 @Override
	//	 public Boolean logWarning(final String text, final Throwable e) {
	//		 return this.log.logWarning(text, e);
	//	 }
	//
	@Override
	public Boolean logWarning(final String text, final Throwable e, final String details) {
		return this.log.logWarning(text, e, details);
	}

	@Override
	public Boolean logWarning(final String text, final String details) {
		return this.log.logWarning(text, details);
	}

	@Override
	public Boolean logWarning(final String text, final Throwable e) {
		return this.log.logWarning(text, e);
	}

	@Override
	public Boolean logWarning(final String text) {
		return this.log.logWarning(text);
	}
	//	 @Override
	//	 public Boolean logWarning(final String text) {
	//		 return this.log.logWarning(text);
	//	 }

	@Override
	public void addLogKey(final String key, final boolean toScreen, final boolean toFile) {
		this.log.addLogKey(key, toScreen, toFile);
	}

	@Override
	public void addLogKey(String key, String logType) {
		this.log.addLogKey(key, logType);
	}
	@Override
	public void setLogKey(final String key, final boolean toScreen, final boolean toFile) {
		this.log.setLogKey(key, toScreen, toFile);
	}

	/*
	 * Message
	 */

	@Override
	public void sendMessage(final AgentIdentifier agentId, final Message am) {
		super.sendMessage(agentId, am);
		assert am.getSender().equals(this.getIdentifier());
		this.log.logCommunication(am, MessageStatus.MessageSended);
	}


	@Override
	public void receive(final Message m) {
		//		assert m.getReceiver().equals(this.getIdentifier());
		this.log.logCommunication(m, MessageStatus.MessageReceived);
		super.receive(m);
	}
	//
	// Primitive
	//

	@Override
	protected BasicCompetenceShell<BasicCompetentAgent> initiateMyShell(){
		try {
			return new BasicCompetenceShell<BasicCompetentAgent>(this, this.creation);
		} catch (final Exception e) {
			throw new RuntimeException(this+" ("+this.getClass()+") can not instanciate the competence shield!", e);
		}
	}

	@Override
	public void finalize(){
		this.logMonologue("I'm dead : I won't bother the cpu and the ram anymore... farewell my friends =,(",LogService.onBoth);
	}

	@Override
	public String toString(){
		return this.getIdentifier().toString();
	}


}



//	public void activateCompetence(final AgentCompetence competenceIdentifier) {
//		try {
//			this.myShell.activateCompetence(competenceIdentifier, true);
//		} catch (final UnknownCompetenceException e) {
//			LoggerManager.writeException(this,
//					"Trying to activate an unknown competence", e);
//		}
//	}
//
//	public void desActivateCompetence(final AgentCompetence competenceIdentifier) {
//		try {
//			this.myShell.activateCompetence(competenceIdentifier, false);
//		} catch (final UnknownCompetenceException e) {
//			this.log.logException(
//					"Trying to desactivate an unknown competence", e);
//		}
//	}
