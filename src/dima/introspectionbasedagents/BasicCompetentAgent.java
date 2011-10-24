package dima.introspectionbasedagents;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.ActiveComponentInterface;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.competences.AgentCompetence;
import dima.introspectionbasedagents.competences.BasicAgentCompetence;
import dima.introspectionbasedagents.competences.CompetenceException;
import dima.introspectionbasedagents.competences.DuplicateCompetenceException;
import dima.introspectionbasedagents.competences.UnInstanciedCompetenceException;
import dima.introspectionbasedagents.competences.UnknownCompetenceException;
import dima.introspectionbasedagents.competences.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.coreservices.loggingactivity.LogCommunication.MessageStatus;
import dima.introspectionbasedagents.coreservices.loggingactivity.LogCompetence;
import dima.introspectionbasedagents.coreservices.loggingactivity.LogCompetence;
import dima.introspectionbasedagents.coreservices.observingagent.PatternObserverCompetence;
import dima.introspectionbasedagents.coreservices.observingagent.PatternObserverWithHookCompetence;
import dima.introspectionbasedagents.shells.BasicCommunicatingShell;
import dima.introspectionbasedagents.shells.BasicCompetenceShell;
import dima.introspectionbasedagents.shells.MethodHandler;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;

public class BasicCompetentAgent extends BasicIntrospectedCommunicatingAgent implements CommunicatingCompetentComponent{

	private static final long serialVersionUID = 1137235428764719916L;

	//
	// Fields
	//

	private boolean active=true;
	private BasicCompetenceShell myShell;
	private Date creation;

	//
	// Constructor
	//


	public BasicCompetentAgent(final AgentIdentifier newId)  throws CompetenceException {
		super(newId);
		this.creation = new Date();
		log= new LogCompetence(this);
		observer=	new PatternObserverWithHookCompetence(this);
	}

	public BasicCompetentAgent(final Map<?, ?> mp, final AgentIdentifier newId)  throws CompetenceException {
		super(mp, newId);
		this.creation = new Date();
		log= new LogCompetence(this);
		observer=	new PatternObserverWithHookCompetence(this);
	}

	public BasicCompetentAgent(final Map<?, ?> mp)  throws CompetenceException  {
		super(mp);
		this.creation = new Date();
		log= new LogCompetence(this);
		observer=	new PatternObserverWithHookCompetence(this);
	}

	public BasicCompetentAgent(final String newId) throws CompetenceException {
		super(newId);
		this.creation = new Date();
		log= new LogCompetence(this);
		observer=	new PatternObserverWithHookCompetence(this);
	}

	/*
	 *
	 */

	public BasicCompetentAgent(final AgentIdentifier newId, final Date horloge)  throws CompetenceException {
		super(newId);
		this.creation = horloge;
		log= new LogCompetence(this);
		observer=	new PatternObserverWithHookCompetence(this);
	}

	public BasicCompetentAgent(final Map<?, ?> mp, final AgentIdentifier newId, final Date horloge)  throws CompetenceException {
		super(mp, newId);
		this.creation = horloge;
		log= new LogCompetence(this);
		observer=	new PatternObserverWithHookCompetence(this);
	}

	public BasicCompetentAgent(final Map<?, ?> mp, final Date horloge)  throws CompetenceException  {
		super(mp);
		this.creation = horloge;
		log= new LogCompetence(this);
		observer=	new PatternObserverWithHookCompetence(this);
	}

	public BasicCompetentAgent(final String newId, final Date horloge) throws CompetenceException {
		super(newId);
		this.creation = horloge;
		log= new LogCompetence(this);
		observer=	new PatternObserverWithHookCompetence(this);
	}

	//
	// Accessors
	//

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

	public Date getCreationTime() {
		return this.creation;
	}

	public long getUptime() {
		return new Date().getTime()-this.creation.getTime();
	}

	public void resetUptime(){
		this.creation = new Date();
	}

	public void load(BasicAgentCompetence<CompetentComponent> newComp) 
			throws UnInstanciedCompetenceException, DuplicateCompetenceException, UnrespectedCompetenceSyntaxException{
		myShell.load(newComp);
	}

	public void unload(BasicAgentCompetence<CompetentComponent> newComp) 
			throws UnInstanciedCompetenceException, DuplicateCompetenceException{
		myShell.unload(newComp);
	}
	@Override
	public boolean isActive() {
		return active;
	}
	public void setActive( boolean active) {
		this.active=active;
	}
	//
	// Launch
	//


	private boolean appliHasStarted=false;

	public boolean hasAppliStarted() {
		return appliHasStarted;
	}

	public boolean start(){
		this.appliHasStarted=true;
		return true;
	}

	@Override
	public final void proactivityTerminate() {
		//		logMonologue("my job is done! cleaning... ");

		super.proactivityTerminate();
	}


	//
	// Competence
	//

	/*
	 * Hook
	 */

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
			return this.myShell.addHook(comp,
					compMethodToTest, compSignature, compargs,
					this, agMethodToExecute, agSignature, agargs);
		} catch (final Exception e) {
			this.logException("Impossible to add the hook", e);
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
			return this.myShell.addHook(
					comp,
					methodToTest,
					null, testArgs,
					methodComponent,
					MethodHandler.getCurrentlyExecutedMethod(nombreAdaptePourRecupererlaMethodeKiVa_bouhPasBo),
					null,	methodsArgs);
		} catch (final Exception e) {
			this.logException("Impossible to add the hook", e);
			return false;
		}
	}

	@Override
	public boolean retryWhen(final AgentCompetence comp,
			final String methodToTest, final Object[] testArgs, final Object[] methodsArgs){
		try {
			return this.myShell.addHook(
					comp,
					methodToTest,
					null, testArgs,
					this,
					MethodHandler.getCurrentlyExecutedMethod(nombreAdaptePourRecupererlaMethodeKiVa_bouhPasBo),
					null,	methodsArgs);
		} catch (final Exception e) {
			this.logException("Impossible to add the hook", e);
			return false;
		}
	}

	/*
	 * 
	 */

	@Override
	public boolean whenIsReady(NotReadyException e){
		try {
			return this.myShell.addHook(e.comp,
					e.compMethodToTest, e.compSignature, e.compargs,
					this, e.agMethodToExecute, e.agSignature, e.agargs);
		} catch (final Exception e2) {
			this.logException("Impossible to add the hook", e);
			return false;
		}
	}


	/*
	 * Pattern Observer
	 */

	@Competence()
	public final PatternObserverWithHookCompetence observer;

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
		this.observer.registeredObservers.add(notificationKey.getName(), this.getIdentifier());
	}

	@Override
	public void addObserver(final AgentIdentifier observerAgent, final Class<?> notificationKey) {
		this.observer.registeredObservers.add(notificationKey.getName(), observerAgent);
	}

	@Override
	public void addObserver(final AgentIdentifier observerAgent, final String notificationKey) {
		this.observer.registeredObservers.add(notificationKey, observerAgent);
	}

	@Override
	public void removeObserver(final AgentIdentifier observerAgent, final Class<?> notificationKey) {
		this.observer.registeredObservers.remove(notificationKey.getName(), observerAgent);
	}

	@Override
	public void removeObserver(final AgentIdentifier observerAgent, final String notificationKey) {
		this.observer.registeredObservers.remove(notificationKey, observerAgent);
	}

	@Override
	public Boolean isObserved(Class<?> notificationKey) {
		return this.observer.isObserved(notificationKey);
	}

	@Override
	public Collection<AgentIdentifier> getObservers(Class<?> notificationKey) {
		return this.observer.getObservers(notificationKey);
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
	private final LogCompetence log;


	@Override
	public Boolean logException(final String text, final Exception e) {
		return this.log.logException(text, e);
	}

	@Override
	public Boolean logException(final String text, final String details, final Exception e) {
		return this.log.logException(text, details, e);
	}

	@Override
	public Boolean logException(final String text, final String details) {
		return this.log.logException(text, details);
	}

	@Override
	public Boolean logException(final String text) {
		return this.log.logException(text);
	}

	@Override
	public Boolean logMonologue(final String text, final String details) {
		return this.log.logMonologue(text, details);
	}

	@Override
	public Boolean logMonologue(final String text) {
		return this.log.logMonologue(text);
	}

	@Override
	public Boolean logWarning(final String text, final Exception e) {
		return this.log.logWarning(text, e);
	}

	@Override
	public Boolean logWarning(final String text, final String details, final Exception e) {
		return this.log.logWarning(text, details, e);
	}

	@Override
	public Boolean logWarning(final String text, final String details) {
		return this.log.logWarning(text, details);
	}

	@Override
	public Boolean logWarning(final String text) {
		return this.log.logWarning(text);
	}
	
	@Override
	public void sendMessage(final AgentIdentifier agentId, final Message am) {
		super.sendMessage(agentId, am);
		this.log.logCommunication(am, MessageStatus.MessageSended);
	}

	@Override
	public void receive(final Message m) {
		super.receive(m);
		this.log.logCommunication(m, MessageStatus.MessageReceived);
	}
	//
	// Primitive
	//

	@Override
	protected BasicCompetenceShell initiateMyShell(){
		try {
			return new BasicCompetenceShell(this, this.creation);
		} catch (Exception e) {
			throw new RuntimeException(this+" ("+this.getClass()+") can not instanciate the competence shield!", e);
		}
	}

	@Override
	public void finalize(){
		this.logMonologue("I'm dead : I won't bother the cpu and the ram anymore... farewell my friends =,(");
	}

	@Override
	public String toString(){
		return this.getIdentifier().toString();
	}

}
