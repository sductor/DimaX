package dima.kernel.INAF.InteractionProtocols;

/**
 * Insert the type's description here.
 * Creation date: (17/03/2003 11:12:10)
 * @author: Tarek JARRAYA
 */

import java.util.Vector;

import dima.basiccommunicationcomponents.Message;
import dima.kernel.INAF.InteractionAgents.InteractiveAgent;
import dima.kernel.INAF.InteractionDomain.AbstractService;
import dima.ontologies.basicFIPAACLMessages.FIPAACLMessage;
import dima.tools.automata.ATN;




public class AbstractRole extends ATN
{
	/**
	 *
	 */
	private static final long serialVersionUID = -945821104803136140L;
	public String roleName;
	public Vector receivedMessages;
	public AbstractService contract = null; // c'est le contrat initial du CFP
	public InteractiveAgent agent; // c'est l'agent qui joue le r�le
	public String conversationId;
	public FIPAACLMessage currentMessage;
	public java.lang.String state;
	/**
	 * AbstractRole constructor comment.
	 */
	public AbstractRole()
	{
		super();
		this.receivedMessages = new Vector();
	}
	/**
	 * AbstractRole constructor comment.
	 */
	public AbstractRole(final ATN newAtn)
	{
		super(newAtn);

		this.receivedMessages = new Vector();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (20/04/2003 11:39:11)
	 */
	public void activate()
	{
		final RoleEngine engine = new RoleEngine(this);
		engine.startUp();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 16:01:08)
	 * @param msg Gdima.basiccommunicationcomponents.Message
	 */
	public void addNewMessage(final Message msg)
	{
		this.receivedMessages.add(msg);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 10:33:00)
	 * @return dima.kernel.communicatingAgent.interaction.CompetenceBasedReactiveCommunicatingAgent
	 */
	public InteractiveAgent getAgent()
	{
		return this.agent;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 10:02:38)
	 * @return dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
	 */
	public AbstractService getContract() {
		return this.contract;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 11:22:20)
	 * @return java.lang.String
	 */
	public String getConversationId()
	{
		return this.conversationId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (06/04/03 14:35:15)
	 * @return Gdima.basiccommunicationcomponents.Message
	 */
	public  FIPAACLMessage getCurrentMessage()
	{
		return this.currentMessage;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 12:28:03)
	 * @return java.lang.String
	 */
	public java.lang.String getRoleName() {
		return this.roleName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/09/2003 14:33:16)
	 * @return java.lang.String
	 */
	public java.lang.String getState() {
		return this.state;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/06/2003 23:09:36)
	 * @return boolean
	 * @param pref java.lang.String
	 */
	public boolean hasMessage(final String perf)
	{
		int i = 0;

		while (i< this.receivedMessages.size())
		{
			if (((FIPAACLMessage)this.receivedMessages.get(i)).getPerformative().equals(perf)) {
				return true;
			}
			i++;
		}

		return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/09/2003 10:50:45)
	 * @return boolean
	 */
	public boolean isInitiatorRole()
	{
		return this.getRoleName().equalsIgnoreCase("ContractNetInitiator") || this.getRoleName().equalsIgnoreCase("EnglishAuctionInitiator") || this.getRoleName().equalsIgnoreCase("BargainingInitiator");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/09/2003 10:55:04)
	 * @return boolean
	 */
	public boolean isParticipantRole()
	{
		return !this.isInitiatorRole();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 17:40:33)
	 * @return Gdima.basiccommunicationcomponents.Message
	 */
	public boolean readMessage(final String perf)
	{
		int i = 0;

		while (i< this.receivedMessages.size())
		{
			if (((FIPAACLMessage)this.receivedMessages.get(i)).getPerformative().equals(perf))
			{
				this.setCurrentMessage((FIPAACLMessage)this.receivedMessages.get(i));
				this.receivedMessages.remove(i);
				return true;
			}
			i++;
		}
		return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 10:33:00)
	 * @param newAgent dima.kernel.communicatingAgent.interaction.CompetenceBasedReactiveCommunicatingAgent
	 */
	public void setAgent(final InteractiveAgent newAgent)
	{
		this.agent = newAgent;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 10:02:38)
	 * @param newContract dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
	 */
	public void setContract(final AbstractService newContract) {
		this.contract = newContract;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 11:22:20)
	 * @param newConversationId java.lang.String
	 */
	public void setConversationId(final String newConversationId) {
		this.conversationId = newConversationId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (06/04/03 14:35:15)
	 * @param newCurrentMessage Gdima.basiccommunicationcomponents.Message
	 */
	public void setCurrentMessage(final FIPAACLMessage newCurrentMessage) {
		this.currentMessage = newCurrentMessage;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/09/2003 14:36:01)
	 */
	public void setFailureState()
	{
		this.setState("failure");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 12:28:03)
	 * @param newRoleName java.lang.String
	 */
	public void setRoleName(final String newRoleName) {
		this.roleName = newRoleName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/09/2003 14:36:52)
	 */
	public void setRunState()
	{
		this.setState("run");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/09/2003 14:33:16)
	 * @param newState java.lang.String
	 */
	public void setState(final java.lang.String newState)
	{
		this.state = newState;

		//Gdima.basiccommunicationcomponents.MessageSend2.invoke(getAgent(),"addMessage",)
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/09/2003 14:37:19)
	 */
	public void setSuccessState()
	{
		this.setState("success");
	}
}
