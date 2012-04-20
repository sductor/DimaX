package dima.kernel.INAF.InteractionAgents;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.Message;
import dima.kernel.INAF.InteractionDomain.AbstractService;
import dima.kernel.INAF.InteractionDomain.RandomEvaluationStrategy;
import dima.kernel.INAF.InteractionProtocols.AbstractRole;
import dima.kernel.INAF.InteractionProtocols.BargainingParticipant;
import dima.kernel.INAF.InteractionProtocols.ContractNetParticipant;
import dima.kernel.INAF.InteractionProtocols.EnglishAuctionParticipant;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
import dima.ontologies.basicFIPAACLMessages.FIPAACLMessage;



public  abstract class InteractiveAgent extends BasicCommunicatingAgent //implements RoleStateListener
{
	/**
	 *
	 */
	private static final long serialVersionUID = -5768725437806051838L;
	public Vector services; // c'est les services offerts par l'agent
	public Hashtable contracts;
	/**
	 * CompetenceBasedReactiveCommunicatingAgent constructor comment.
	 */
	public InteractiveAgent()
	{
		super();
		this.services = new Vector();
	}
	/**
	 * CompetenceBasedReactiveCommunicatingAgent constructor comment.
	 * @param newId Gdima.basicagentcomponents.AgentIdentifier
	 */
	public InteractiveAgent(final AgentIdentifier newId)
	{
		super();
		this.setId(newId);

		this.services = new Vector();
		this.contracts=new Hashtable();
	}
	/**
	 * CompetenceBasedReactiveCommunicatingAgent constructor comment.
	 * @param newId java.lang.String
	 */
	public InteractiveAgent(final String newId)
	{
		super(newId);
		this.services = new Vector();
		this.contracts=new Hashtable();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/04/2003 13:17:31)
	 * @param serv dima.kernel.communicatingAgent.domain.Service
	 */
	public void addContract(final AgentIdentifier agentId,final AbstractService contract)
	{
		// System.out.println("ENTRER DANS ADDCONTRACT......");
		// System.out.println("...."+agentId.toString());
		// System.out.println("....."+contract.toString());
		this.contracts.put(agentId.toString(), contract);
		// System.out.println("SORTIR DE ADDCONTRACT....."+contract.toString());
	}

	public void removeContract(final AgentIdentifier agentId)
	{
		this.contracts.remove(agentId.toString());
	}



	public void addService(final AbstractService serv)
	{
		this.services.add(serv);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 19:04:47)
	 * @return Gdima.kernel.INAF2.InteractionProtocols.AbstractRole
	 * @param m Gdima.basicFIPAACLMessages.FIPAACLMessage
	 */
	public static AbstractRole defaultInitParticipantRole(final InteractiveAgent agent, final FIPAACLMessage m)
	{
		if (m.getProtocol().equals("FIPAContractNetProtocol")) {
			return new ContractNetParticipant(agent,m.getConversationId(),m.getSender(),(AbstractService)m.getContent(),m.getReplyBy(),new RandomEvaluationStrategy());
		}

		if (m.getProtocol().equals("BargainingProtocol")) {
			return new BargainingParticipant(agent,m.getConversationId(),m.getSender(),(AbstractService)m.getContent(),new RandomEvaluationStrategy());
		}

		if (m.getProtocol().equals("EnglishAuctionProtocol")) {
			return new EnglishAuctionParticipant(agent,m.getConversationId(),m.getSender(),(AbstractService)m.getContent(),m.getReplyBy());
		}

		return null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/09/2003 06:06:22)
	 * @param convId java.lang.String
	 */
	public void endRoleEvent(final String convId)
	{
		// System.out.println("ENTRER DANS ENDROLE EVENT...");
		// System.out.println("VALEUR DE GETROLE.. "+getRole(convId).getRoleName());
		if(this.getRole(convId).getState().equalsIgnoreCase("failure")) {
			this.failureRoleProcess(this.getRole(convId));
		} else if(this.getRole(convId).getState().equalsIgnoreCase("success"))
		{
			this.successRoleProcess(this.getRole(convId));
			// System.out.println("SORTIR DE END ROLE EVENT ....");
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/09/2003 05:50:08)
	 * @param convId java.lang.String
	 */
	public void failureRoleProcess(final AbstractRole r)
	{
		this.removeRole(r.getConversationId());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 12:25:11)
	 */
	public Vector getAgentsAquaintances()
	{
		final Vector agents = new Vector(this.getAquaintances().keySet());

		for(int i=0;i<agents.size();i++) {
			agents.set(i,new AgentName((String)agents.get(i)));
		}

		return agents;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/04/2003 09:53:08)
	 * @param convId java.lang.String
	 */
	public abstract AbstractRole getRole(String convId);
	/**
	 * Insert the method's description here.
	 * Creation date: (10/09/2003 11:45:34)
	 * @return java.util.Vector
	 */
	public java.util.Vector getServices() {
		return this.services;
	}

	public java.util.Vector getContracts() {

		final Vector temp=new Vector();
		for (final Enumeration e= this.contracts.keys();e.hasMoreElements();){
			final String nom=(String) e.nextElement();
			final AbstractService serv= (AbstractService) this.contracts.get(nom);
			temp.add(serv);
		}
		return temp;
	}

	public java.util.Hashtable getCompContracts() {


		return this.contracts;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (01/09/2003 09:26:46)
	 */
	public boolean hasAclMessage()
	{
		return this.hasMail() && this.getFirstMessage() instanceof FIPAACLMessage;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 18:35:53)
	 * @param conv java.lang.String
	 */
	public abstract boolean hasRole(String conv);
	/**
	 * processAclMessage method comment.
	 */
	@Override
	public void processAclMessage(final Message m)
	{
		final FIPAACLMessage msg = (FIPAACLMessage) m;

		if (this.hasRole(msg.getConversationId())) {
			this.getRole(msg.getConversationId()).addNewMessage(msg);
		} else if(msg.isCFP()) {
			// System.out.println("TROUVE CFP .....");
			this.processCFPMessage(msg); // traiter le cas de la reception d'un nouveau appel de proposition
		} else {
			System.out.println("ERREUR!!! d'envoie de message ("+msg.getPerformative()+") "+this.getId()+" --> "+msg.getSender()+" : Je ne participe pas � la conversation : "+msg.getConversationId());
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 18:30:47)
	 * @param m Gdima.basicFIPAACLMessages.FIPAACLMessage
	 */
	public abstract void processCFPMessage(FIPAACLMessage m);
	/**
	 * Insert the method's description here.
	 * Creation date: (15/04/2003 14:01:46)
	 * @param conId java.lang.String
	 */
	public abstract void removeRole(String conId);
	/**
	 * Insert the method's description here.
	 * Creation date: (06/04/03 12:05:15)
	 * @param service dima.kernel.communicatingAgent.domain.AbstractService
	 */
	public void removeService(final AbstractService service)
	{
		this.services.remove(service);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/09/2003 11:45:34)
	 * @param newServices java.util.Vector
	 */
	public void setServices(final java.util.Vector newServices) {
		this.services = newServices;
	}
	/**
	 * Describe the basic cycle of the agent. Itcan be readMailBox();
	 * Creation date: (07/05/00 09:28:47)
	 */
	@Override
	public void step()
	{
		this.readMailBox();
		this.wwait();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/09/2003 05:50:44)
	 * @param convId java.lang.String
	 */
	public void successRoleProcess(final AbstractRole r)
	{
		this.removeRole(r.getConversationId());
	}
}
