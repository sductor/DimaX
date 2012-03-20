package dima.kernel.INAF.InteractionAgents;


/**
 * Insert the type's description here.
 * Creation date: (23/03/03 14:52:54)
 * @author: Tarek JARRAYA
 */
import java.util.Hashtable;

import dima.basicagentcomponents.AgentIdentifier;
import dima.kernel.INAF.InteractionProtocols.AbstractRole;
import dima.ontologies.basicFIPAACLMessages.FIPAACLMessage;





public abstract class MultiRolesAgent extends InteractiveAgent
{
	/**
	 *
	 */
	private static final long serialVersionUID = -158527781774176259L;
	public Hashtable roles; // c'est une map de la forme (conversationId, AbstractRole)
	/**
	 * CompetenceBasedReactiveCommunicatingAgent constructor comment.
	 */
	public MultiRolesAgent()
	{
		super();
		this.roles = new Hashtable();
	}
	/**
	 * CompetenceBasedReactiveCommunicatingAgent constructor comment.
	 * @param newId Gdima.basicagentcomponents.AgentIdentifier
	 */
	public MultiRolesAgent(final AgentIdentifier newId)
	{
		super(newId);
		this.roles = new Hashtable();
	}
	/**
	 * CompetenceBasedReactiveCommunicatingAgent constructor comment.
	 * @param newId java.lang.String
	 */
	public MultiRolesAgent(final String newId)
	{
		super(newId);
		this.roles = new Hashtable();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (31/08/2003 19:46:00)
	 * @param convId java.lang.String
	 */
	public void activateRole(final String convId)
	{
		this.getRole(convId).activate();
		// System.out.println("ROLE POUR "+convId+"A ETE ACTIVE ....");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 16:06:22)
	 * @param convId java.lang.String
	 * @param role dima.kernel.communicatingAgent.interaction.AbstractRole
	 */
	public void addRole(final AbstractRole role)
	{
		role.setConversationId(role.getConversationId());

		this.roles.put(role.getConversationId(),role);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 12:30:41)
	 * @return java.lang.String
	 */
	public String buildNewConversationId()
	{
		final int i = this.roles.size()+1;

		return new String(this.getIdentifier()+"conv"+i);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/04/2003 15:41:20)
	 * @return dima.kernel.communicatingAgent.interaction.AbstractRole
	 * @param convId java.lang.String
	 */
	@Override
	public AbstractRole getRole(final String convId)
	{
		return (AbstractRole)this.roles.get(convId);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 18:35:53)
	 * @param conv java.lang.String
	 */
	@Override
	public boolean hasRole(final String convId)
	{
		return this.roles.containsKey(convId);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 19:04:47)
	 * @return Gdima.kernel.INAF2.InteractionProtocols.AbstractRole
	 * @param m Gdima.basicFIPAACLMessages.FIPAACLMessage
	 */
	public AbstractRole initParticipantRole(final FIPAACLMessage m)
	{
		// System.out.println("CREATION PARTICIPANT ......");
		return InteractiveAgent.defaultInitParticipantRole(this,m);
	}
	/**
	 * isActive method comment.
	 */
	@Override
	public abstract boolean competenceIsActive();
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 18:43:23)
	 * @param m Gdima.basicFIPAACLMessages.FIPAACLMessage
	 */
	@Override
	public void processCFPMessage(final FIPAACLMessage m)
	{
		// System.out.println("ENTREE DANS PROCESSCFPMsg");
		this.addRole(this.initParticipantRole(m));
		this.activateRole(m.getConversationId());
		this.getRole(m.getConversationId()).addNewMessage(m);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/04/2003 09:57:56)
	 * @param convId java.lang.String
	 */
	@Override
	public void removeRole(final String convId)
	{
		this.roles.remove(convId);
	}
}
