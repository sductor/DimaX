package dima.introspectionbasedagents.services.core.observingagent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.CommunicatingCompetentComponent;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityFinalisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.services.BasicAgentCommunicatingCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dimaxx.tools.mappedcollections.HashedHashSet;
/**
 * This is the service that provide pattern observing Ductor Sylvain
 */
public abstract class PatternObserverService extends BasicAgentCommunicatingCompetence<BasicCompetentAgent> {


	private static final long serialVersionUID = 7666491868939922910L;

	class ObservationProtocol extends Protocol<CommunicatingCompetentComponent> {
		/**
		 *
		 */
		private static final long serialVersionUID = -5272736195092770051L;
		public static final String Observe = "Observe this agent";
		public static final String DontObserve = "Observe this agent";
		public static final String Notify = "Notification";
//		public ObservationProtocol(final CommunicatingCompetentComponent com) {super(com);}
	}

	public static final int notificationExpirationTime= 3000;

	//
	// Fields
	//

	private final Collection<NotificationMessage<?>> notificationsToSend =
		new ArrayList<NotificationMessage<?>>();
	public final HashedHashSet<String, AgentIdentifier> registeredObservers =
		new HashedHashSet<String, AgentIdentifier>();
	private final Collection<AgentIdentifier> observerBlackList =
		new ArrayList<AgentIdentifier>();

	//
	// Constructors
	//

	//
	// Accessors
	//


	public PatternObserverService(BasicCompetentAgent ag) throws UnrespectedCompetenceSyntaxException {
		super(ag);
	}

	private  boolean iGiveObservation(final AgentIdentifier observer) {
		return !this.observerBlackList.contains(observer);
	}
	
	public Collection<AgentIdentifier> getObserver(String key){
		return registeredObservers.get(key);
	}

	public Collection<String> getKeys(){
		return registeredObservers.keySet();
	}
	//
	// Competences
	//

	/**
	 * Adding/Removing observer from black list
	 *
	 * @param m
	 */
	@Override
	public Boolean addToBlackList(final AgentIdentifier o, final Boolean add) {
		if (add)
			return this.observerBlackList.add(o);
		else
			return this.observerBlackList.remove(o);
	}

	/**
	 * Add a new notification to send. The Pattern Observer Service will
	 * automatically send it to all the registered agent
	 *
	 * @param notification
	 * @return true if success
	 */
	@Override
	public <Notification extends Serializable> Boolean notify(final Notification notification) {
		return this.notify(notification, notification.getClass().getName());
	}

	/**
	 * Add a new notification to send. The Pattern Observer Service will
	 * automatically send it to all the registered agent
	 *
	 * @param notification
	 * @return true if success
	 */
	@Override
	public <Notification extends Serializable> Boolean notify(final Notification notification, final String key) {
		return this.notificationsToSend.add(
				new NotificationMessage<Notification>(key, notification));
	}
	/**
	 * Used to make <@param observedAgent> send automatically all this
	 * notifications that have the key <@param notificationToObserve>
	 *
	 * @param observedAgent
	 * @param notificationToObserve
	 * @return true if success
	 */
	@Override
	public void observe(final AgentIdentifier observedAgent,
			final String notificationToObserve) {
		final FipaACLMessage m = new FipaACLMessage(
				Performative.Request,
				ObservationProtocol.Observe,
				ObservationProtocol.class);
		m.setAttachement(new Object[] {
				notificationToObserve },
				new Class[] {
				String.class });
		this.sendMessage(observedAgent, m);
	}

	@Override
	public void stopObservation(final AgentIdentifier observedAgent,
			final String notificationToObserve) {
		final FipaACLMessage m = new FipaACLMessage(
				Performative.Request,
				ObservationProtocol.DontObserve,
				ObservationProtocol.class);
		m.setAttachement(new Object[] {
				notificationToObserve },
				new Class[] {
				String.class });
		this.sendMessage(observedAgent, m);
	}
	/**
	 * Used to make <@param observedAgent> send automatically all this
	 * notifications that have the key <@param notificationToObserve>
	 *
	 * @param observedAgent
	 * @param notificationToObserve
	 * @return true if success
	 */
	@Override
	public void observe(final AgentIdentifier observedAgent,
			final Class<?> notificationKey) {
		this.observe(observedAgent, notificationKey.getName());
	}

	/**
	 * Used to make <@param observedAgent> send automatically all this
	 * notifications that have the key <@param notificationToObserve>
	 *
	 * @param observedAgent
	 * @param notificationToObserve
	 * @return true if success
	 */
	@Override
	public void stopObservation(final AgentIdentifier observedAgent,
			final Class<?> notificationKey) {
		this.stopObservation(observedAgent, notificationKey.getName());
	}

	//
	// Behaviors
	//

	/**
	 * Register new observer
	 *
	 * @param m
	 */
	@MessageHandler
	@FipaACLEnvelope(
			performative = Performative.Request,
			protocol = ObservationProtocol.class,
			attachementSignature = {String.class })
			public void registrationOfNewObserver(final FipaACLMessage m) {
		final String key = (String) m.getArgs()[0];
		if (m.getContent().equals(ObservationProtocol.Observe))
			this.registeredObservers.add(key, m.getSender());
		else if (m.getContent().equals(ObservationProtocol.DontObserve))
			this.registeredObservers.remove(key, m.getSender());
		else
			this.getMyAgent().signalException("unappropriate message");

		//				getMyAgent().logMonologue(
		//						" : I've registered observer:'" + m.getSender() + "'\n"
		//						+ " i will send notifications of "
		//						+ m.getAttachement()[0]);
	}

	@StepComposant()//ticker=250)//@Transient(ticker=500)
	public void autoSendOfNotifications() {
		final Collection<NotificationMessage<?>> sendedNotif=new ArrayList<NotificationMessage<?>>();
		if (!this.registeredObservers.isEmpty())
			for (final NotificationMessage<?> n : this.notificationsToSend){
//				if (getIdentifier().toString().equals("fault agent of simu_0"))
//					logMonologue("registered obs : "+registeredObservers
//							+"\n sending "+n+"\n to "+this.registeredObservers
//							.get(n.getKey()));
				for (final AgentIdentifier obs : this.registeredObservers.get(n.getKey()))
					if (this.iGiveObservation(obs))
						//						n.setReceiver(obs);
						this.sendMessage(obs,n);
				//						NotificationMessage<?> c = n;AgentIdentifier r = n.getReceiver();
				//						System.out.println("i've sended "+n+" to "+n.getReceiver());
				//						logMonologue("i've sended "+n+" to "+n.getReceiver());//+" from "+n.getSender());
				//						AgentIdentifier r = n.getReceiver();//n.getSender();
				sendedNotif.add(n);
			}
		this.notificationsToSend.removeAll(sendedNotif);
	}

	@StepComposant(ticker=PatternObserverService.notificationExpirationTime)
	public void clean() {
		this.autoSendOfNotifications();
		this.notificationsToSend.clear();
	}
	
	@ProactivityFinalisation
	public void terminate(){
		autoSendOfNotifications();
	}
	//
	// Primitives
	//

	@Override
	public String toString() {
		return "Pattern Observer of " + this.getMyAgent().getIdentifier();
	}
}