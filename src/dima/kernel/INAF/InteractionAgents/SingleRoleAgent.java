

/**
 * Insert the type's description here.
 * Creation date: (23/03/03 14:52:54)
 * @author: Tarek JARRAYA
 */

package dima.kernel.INAF.InteractionAgents;
import dima.basicagentcomponents.AgentIdentifier;
import dima.kernel.INAF.InteractionProtocols.AbstractRole;
import dima.ontologies.basicFIPAACLMessages.FIPAACLMessage;


public abstract class SingleRoleAgent extends InteractiveAgent
{
	/**
	 *
	 */
	private static final long serialVersionUID = -881115648291915016L;
	public String conversationId = new String();
	public AbstractRole role = new AbstractRole();
	/**
	 * CompetenceBasedReactiveCommunicatingAgent constructor comment.
	 */
	public SingleRoleAgent()
	{
		super();
		this.conversationId = new String();
		this.role = new AbstractRole();
	}
	/**
	 * CompetenceBasedReactiveCommunicatingAgent constructor comment.
	 * @param newId Gdima.basicagentcomponents.AgentIdentifier
	 */
	public SingleRoleAgent(final AgentIdentifier newId)
	{
		super();
		this.setId(newId);
		this.role = new AbstractRole();
		this.conversationId = new String();
	}
	/**
	 * CompetenceBasedReactiveCommunicatingAgent constructor comment.
	 * @param newId java.lang.String
	 */
	public SingleRoleAgent(final String newId)
	{
		super(newId);
		this.role = new AbstractRole();
		this.conversationId = new String();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (31/08/2003 19:43:52)
	 */
	public void activateRole()
	{
		this.getRole().activate();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 12:30:41)
	 * @return java.lang.String
	 */
	public String buildNewConversationId()
	{
		return new String(this.getIdentifier()+"conv");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/04/2003 14:24:15)
	 * @return java.lang.String
	 */
	public java.lang.String getConversationId()
	{
		return this.conversationId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25/05/2003 23:26:33)
	 * @return dima.kernel.communicatingAgent.interaction.AbstractRole
	 */
	public AbstractRole getRole()
	{
		return this.role;
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
		if (this.getConversationId().equals(convId)) {
			return this.getRole();
		} else {
			return null;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 18:37:45)
	 * @return boolean
	 * @param c java.lang.String
	 */
	@Override
	public boolean hasRole(final String c)
	{
		return this.getConversationId().equals(c);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 19:04:47)
	 * @return Gdima.kernel.INAF2.InteractionProtocols.AbstractRole
	 * @param m Gdima.basicFIPAACLMessages.FIPAACLMessage
	 */
	public AbstractRole initParticipantRole(final FIPAACLMessage m)
	{
		return InteractiveAgent.defaultInitParticipantRole(this,m);
	}
	/**
	 * isActive method comment.
	 */
	@Override
	public abstract boolean isActive();
	/**
	 * Insert the method's description here.
	 * Creation date: (01/09/2003 09:54:33)
	 * @return boolean
	 */
	public boolean isFree()
	{
		return this.getConversationId().equals(new String());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 18:43:23)
	 * @param m Gdima.basicFIPAACLMessages.FIPAACLMessage
	 */
	@Override
	public void processCFPMessage(final FIPAACLMessage m)
	{
		if (this.isFree())
		{
			this.setRole(this.initParticipantRole(m));
			this.activateRole();
			this.getRole().addNewMessage(m);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/04/2003 09:57:56)
	 * @param convId java.lang.String
	 */
	public void removeRole()
	{
		this.role = new AbstractRole();
		this.setConversationId(new String());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/04/2003 09:57:56)
	 * @param convId java.lang.String
	 */
	@Override
	public void removeRole(final String convId)
	{
		this.removeRole();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/04/2003 14:24:15)
	 * @param newConversationId java.lang.String
	 */
	public void setConversationId(final java.lang.String newConversationId)
	{
		this.conversationId = newConversationId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 16:06:22)
	 * @param convId java.lang.String
	 * @param role dima.kernel.communicatingAgent.interaction.AbstractRole
	 */
	public void setRole(final AbstractRole newRole)
	{
		this.setConversationId(newRole.getConversationId());

		this.role = newRole;
	}
}
