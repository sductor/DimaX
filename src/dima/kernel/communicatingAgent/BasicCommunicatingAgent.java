package dima.kernel.communicatingAgent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import dima.basicagentcomponents.AgentAddress;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.AbstractMailBox;
import dima.basiccommunicationcomponents.AbstractMessage;
import dima.basiccommunicationcomponents.CometCommunicationComponent;
import dima.basiccommunicationcomponents.CommunicationComponent;
import dima.basiccommunicationcomponents.Message;
import dima.basiccommunicationcomponents.SimpleMailBox;
import dima.basicinterfaces.MailBoxBasedCommunicatingComponentInterface;
import dima.introspectionbasedagents.coreservices.loggingactivity.LogCompetence;
import dima.kernel.BasicAgents.AgentEngine;
import dima.kernel.BasicAgents.BasicReactiveAgent;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import dimaxx.exceptions.UninstanciableMonitorException;
import dimaxx.kernel.DimaXCommunicationComponent;
import dimaxx.kernel.DimaXTask;
import dimaxx.monitoring.AgentMonitor;
import dimaxx.monitoring.MonitoredTask;


public abstract class BasicCommunicatingAgent extends BasicReactiveAgent implements MailBoxBasedCommunicatingComponentInterface {
	/**
	 *
	 */
	private static final long serialVersionUID = -8436736742806220406L;
	protected Map aquaintances;
	private AbstractMailBox mailBox;

	private dima.basiccommunicationcomponents.CommunicationComponent com;
	/**
	 * CommunicationBehavior constructor comment.
	 */
	public BasicCommunicatingAgent() {
		super();
		this.initialize();
		this.com = new CommunicationComponent(this);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/05/00 00:24:58)
	 * @param param Gdima.kernel.AgentAddress
	 */
	public BasicCommunicatingAgent(final AgentIdentifier newId) {
		super(newId);
		this.initialize();
		this.com = new CommunicationComponent(this);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/05/00 00:24:58)
	 * @param param Gdima.kernel.AgentAddress
	 */
	public BasicCommunicatingAgent(final String newId) {
		super(newId);
		this.initialize();
		this.com = new CommunicationComponent(this);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/06/00 16:07:49)
	 * @param param Gdima.kernel.AgentAddress
	 * @param mp java.util.Map
	 */
	public BasicCommunicatingAgent(final Map mp) {
		super();
		this.initialize();
		this.aquaintances = mp;
		this.com = new CommunicationComponent(this);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/06/00 16:07:49)
	 * @param param Gdima.kernel.AgentAddress
	 * @param mp java.util.Map
	 */
	public BasicCommunicatingAgent(final Map mp, final AgentIdentifier newId) {
		super(newId);
		this.initialize();
		this.aquaintances = mp;
		this.com = new CommunicationComponent(this);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/05/00 00:24:58)
	 * @param param Gdima.kernel.AgentAddress
	 */
	@Override
	public void activate() {
		final AgentEngine engine = new AgentEngine(this);
		engine.startUp();

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (07/05/00 00:24:58)
	 * @param param Gdima.kernel.AgentAddress
	 */
	public void activateWithFipa() {
		final AgentEngine engine = new AgentEngine(this);
		AgentManagementSystem.getDIMAams().addAquaintance(this);
		engine.startUp();

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/05/00 00:24:58)
	 * @param param Gdima.kernel.AgentAddress
	 */
	public void activateWithComet() {
		final AgentEngine engine = new AgentEngine(this);
		this.com = new CometCommunicationComponent();
		engine.startUp();
	}




	public void activateWithDarx(final int PortNb)
	{
		DimaXTask<BasicCommunicatingAgent> darxEngine;

		darxEngine = new  DimaXTask<BasicCommunicatingAgent>(this);
		this.com = new DimaXCommunicationComponent<BasicCommunicatingAgent>(darxEngine);
		try	{
			darxEngine.activateTask(PortNb);
		}  catch (final java.rmi.RemoteException e){
			LogCompetence.writeException(this,"Error during Activation : ",e);
		}
	}


	public void activateWithDarx(final String url,final int PortNb)
	{
		DimaXTask<BasicCommunicatingAgent> darxEngine;

		darxEngine = new  DimaXTask<BasicCommunicatingAgent>(this);
		this.com = new DimaXCommunicationComponent<BasicCommunicatingAgent>(darxEngine);
		try	{
			darxEngine.activateTask(url,PortNb);
		}  catch (final java.rmi.RemoteException e){
			LogCompetence.writeException(this,"Error during Activation : ",e);
		}
	}

	public void activateWithMonitor(final String url,final int PortNb, final Class<? extends AgentMonitor>... monitors) throws UninstanciableMonitorException
	{
		MonitoredTask<BasicCommunicatingAgent> darxEngine;

		darxEngine = new  MonitoredTask<BasicCommunicatingAgent>(this, monitors);
		this.com = new DimaXCommunicationComponent<BasicCommunicatingAgent>(darxEngine);
		try	{
			darxEngine.activateTask(url,PortNb);
		}  catch (final java.rmi.RemoteException e){
			LogCompetence.writeException(this,"Error during Activation : ",e);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (31/03/00 12:45:29)
	 * @return java.util.Vector
	 */
	public void addAquaintance(final AgentAddress ad) {
		System.out.println("Aquaintance " + " " + ad);
		this.aquaintances.put(ad.getId().toString(), ad);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (31/03/00 12:45:29)
	 * @return java.util.Vector
	 */
	public void addAquaintance(final BasicCommunicatingAgent ag) {
		//System.out.println("Aquaintance " + ad.getId() + " " + ad);
		this.aquaintances.put(ag.getId().toString(), ag.getAddress());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (31/03/00 12:45:29)
	 * @return java.util.Vector
	 */
	public void addAquaintances(final Vector l) {
		//System.out.println("Aquaintance " + ad.getId() + " " + ad);
		AgentAddress ag;
		for (int i = 0; i < l.size(); i++) {
			ag = (AgentAddress) l.get(i);
			this.addAquaintance(ag);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28/04/00 16:30:21)
	 * @return int
	 */
	public int aquaintancesNumber() {
		return this.aquaintances.size();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (06/05/00 23:59:23)
	 * @return Gdima.kernel.AgentAddress
	 */
	public AgentAddress getAddress() {
		return this.com;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/04/2003 15:25:50)
	 * @return java.util.Map
	 */
	public java.util.Map getAquaintances() {
		return this.aquaintances;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (06/05/00 23:59:23)
	 * @return Gdima.kernel.AgentAddress
	 */
	protected AgentAddress getCom() {
		return this.com;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (06/05/00 23:59:23)
	 * @return Gdima.kernel.AgentAddress
	 */
	public CommunicationComponent getCommunicationComponent() {
		return this.com;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 11:01:12)
	 * @return boolean
	 */
	public AbstractMessage getFirstMessage() {
		// Test hasMail first ...
		return this.mailBox.getFirstMessage();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/04/01 16:46:55)
	 * @return Gdima.behaviors.communication.AbstractMailBox
	 */
	@Override
	public AbstractMailBox getMailBox() {
		return this.mailBox;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 11:01:12)
	 * @return boolean
	 */
	public AbstractMessage getMessage() {
		// Test hasMail first ...
		return this.mailBox.readMail();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/04/01 16:46:55)
	 * @return Gdima.behaviors.communication.AbstractMailBox
	 */
	public boolean hasMail() {
		return this.getMailBox().hasMail();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 10:54:27)
	 */
	public void initialize() {
		this.aquaintances = new HashMap();
		this.aquaintances.clear();
		this.mailBox = new SimpleMailBox();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/06/00 18:10:07)
	 * @return boolean
	 */
	@Override
	public boolean isActive() {
		return true;
	}
	/**
	 * Describe the basic cycle of the agent. Itcan be readMailBox();
	 * Creation date: (07/05/00 09:28:47)
	 */
	public void noAction() {
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/04/01 16:46:55)
	 * @return Gdima.behaviors.communication.AbstractMailBox
	 */
	public boolean noMail() {
		return !this.getMailBox().hasMail();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 12:07:21)
	 */
	public void processAclMessage(final Message m) {
		//to implement
		m.process(this);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 12:07:21)
	 */
	public void processNextMessage() {
		final Message m = (Message) this.getMessage();
		if (m.getType().equals("java"))
			m.process(this);
		else
			// System.out.println("TROUVE ACL MSG ....VERIF"+m.toString());
			this.processAclMessage(m);
	}

	public Object processMessage(final Message m) {
		if (m.getType().equals("java"))
			return m.process(this);
		else
			this.processAclMessage(m);
		return null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 11:01:12)
	 * @return boolean
	 */
	public boolean put(final AbstractMessage m) {
		return this.mailBox.writeMail(m);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (06/05/00 23:59:23)
	 * @return Gdima.kernel.AgentAddress
	 */
	public void readAllMessages() {
		while (this.hasMail())
			this.readMailBox();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/05/00 09:28:47)
	 */
	public void readMailBox() {
		if (this.hasMail())
			this.processNextMessage();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28/04/00 16:09:42)
	 * @param m Gdima.competences.communication.AbstractMessage
	 */
	@Override
	public void receive(final Message m) {

		this.put(m);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 11:40:48)
	 * @param replica java.util.List
	 * @param am Gdima.competences.communication.AbstractMessage
	 */
	public void sendAll(final Message am) {
		this.sendMessage(this.aquaintances.values(), am);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 11:40:48)
	 * @param replica java.util.List
	 * @param am Gdima.competences.communication.AbstractMessage
	 */

	@Override
	public void sendMessage(final AgentIdentifier agentId, final Message am) {
		am.setSender(this.getIdentifier());
		am.setReceiver(agentId);
		if (this.aquaintances.containsKey(agentId.toString()))
			this.com.sendMessage(
					((AgentAddress) this.aquaintances.get(agentId.toString())),
					am);
		else
			this.com.sendMessage(am);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 11:40:48)
	 * @param agents java.util.List
	 * @param am Gdima.competences.communication.AbstractMessage
	 */

	public void sendMessage(final Collection agents, final Message am) {
		final Iterator iter = agents.iterator();
		am.setSender(this.getIdentifier());
		while (iter.hasNext())
			this.sendMessage(((AgentAddress) iter.next
					()).getId(), am);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (02/04/2003 15:25:50)
	 * @param newAquaintances java.util.Map
	 */
	void setAquaintances(final java.util.Map newAquaintances) {
		this.aquaintances = newAquaintances;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (06/05/00 23:59:23)
	 * @return Gdima.kernel.AgentAddress
	 */
	public void setCommunicationComponent(final CommunicationComponent a) {
		this.com = a;
	}
	/**
	 * Describe the basic cycle of the agent. Itcan be readMailBox();
	 * Creation date: (07/05/00 09:28:47)
	 */
	@Override
	public abstract void step();
	/**
	 * Insert the method's description here.
	 * Creation date: (07/05/00 09:28:47)
	 */
	public AbstractMessage removeFirstMessage() {
		return ((SimpleMailBox) this.getMailBox()).removeFirstMessage();
	}
}



/*public void activateWithDarx(String url, int PortNb) {
	DarxTaskEngine darxEngine;
	darx.RemoteTask remote = null;

	darxEngine = new DarxTaskEngine(this);
	com = new DARXCommunicationComponent(darxEngine);
	try {
		remote = darxEngine.activateTask(url, PortNb);
	} catch (java.rmi.RemoteException e) {
		System.err.println("Error during Activation : " + e);
	}
	}*/
//public void activateWithDarx(
//		String url,
//		int portNb,
//		int RepDegree,
//		String RepUrl,
//		int ReplicationPort) {
//		DarxTaskEngine darxEngine;
//		darx.RemoteTask remote = null;
//
//		darxEngine = new DarxTaskEngine(this);
//		com = new DARXCommunicationComponent(darxEngine);
//
//		try {
//			remote = darxEngine.activateTask(url, portNb);
//		} catch (java.rmi.RemoteException e) {
//			System.err.println("Error during Activation : " + e);
//		}
//	}
/* public void activateWithDarxObs(String url,int PortNb)
{
DarxTaskEngine darxEngine;
darx.RemoteTask remote=null;

String st= this.getId().toString();
Integer t= new Integer(PortNb);
// System.out.println("ENTRER DANS ACTIVATE WITH DARX OBS....");
	darxEngine = new  DarxTaskEngine(this,"Observateur "+url+":"+t.toString());
com = new ObservedDARXCommunicationComponent(darxEngine);
try
{
remote=darxEngine.activateTask(url,PortNb);
}  catch (java.rmi.RemoteException e)
{
System.err.println("Error during Activation : "+e);
}
}*/
// public void activateWithDarxObs(String url,int PortNb)
//{
//    DarxTaskEngine darxEngine;
//    darx.RemoteTask remote=null;
//
//    String st= this.getId().toString();
//    Integer t= new Integer(PortNb);
//	/* */ System.out.println("ENTRER DANS ACTIVATE WITH DARX OBS....");
//    if ( ((st.substring(0,11).equals("Observateur"))) || ((st.substring(0,11)).equals("FaultySimul")) || ((st.substring(0,10)).equals("AgentSimul"))) {
//    darxEngine = new  DarxTaskEngine(this);}
//    else {
//    darxEngine = new  DarxTaskEngine(this,"Observateur "+url+":"+t.toString());
//    }
//    com = new ObservedDARXCommunicationComponent(darxEngine);
//    try
//	{
//	    remote=darxEngine.activateTask(url,PortNb);
//	}  catch (java.rmi.RemoteException e)
//	    {
//		System.err.println("Error during Activation : "+e);
//	    }
// }
