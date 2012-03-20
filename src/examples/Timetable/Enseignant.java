package examples.Timetable;

import java.util.Hashtable;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.kernel.INAF.InteractionAgents.InteractiveAgent;
import dima.kernel.INAF.InteractionAgents.MultiRolesAgent;
import dima.kernel.INAF.InteractionDomain.AbstractService;
import dima.kernel.INAF.InteractionDomain.Constraint;
import dima.kernel.INAF.InteractionTools.Operator;
import dima.ontologies.basicFIPAACLMessages.FIPAACLMessage;


public class Enseignant extends MultiRolesAgent
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3912616643582180802L;
	public Vector needs;
	public Vector constraints;
	public Hashtable contracts;
	/**
	 * Enseignant constructor comment.
	 */
	public Enseignant()
	{
		super();
		this.needs = new Vector();
		this.constraints = new Vector();
		this.contracts = new Hashtable();
	}
	/**
	 * Enseignant constructor comment.
	 * @param newId Gdima.basicagentcomponents.AgentIdentifier
	 */
	public Enseignant(final dima.basicagentcomponents.AgentIdentifier newId)
	{
		super(newId);
		this.needs = new Vector();
		this.constraints = new Vector();
		this.contracts = new Hashtable();
	}
	/**
	 * Enseignant constructor comment.
	 * @param newId java.lang.String
	 */
	public Enseignant(final String newId)
	{
		super(newId);
		this.needs = new Vector();
		this.constraints = new Vector();
		this.contracts = new Hashtable();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/04/2003 12:26:44)
	 * @param c dima.kernel.communicatingAgent.domainOfInteraction.Constraint
	 */
	public void addConstraint(final Constraint c)
	{
		this.constraints.add(c);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/04/2003 13:40:29)
	 * @param c dima.kernel.communicatingAgent.domain.Contract
	 */
	@Override
	public void addContract(final AgentIdentifier contractant, final AbstractService proposal)
	{
		this.contracts.put(contractant,proposal);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/04/2003 13:39:21)
	 * @param n dima.kernel.communicatingAgent.exemple.emploiDuTemps.Need
	 */
	public void addNeed(final AgentIdentifier agentId)
	{
		this.needs.add(agentId);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/04/2003 13:38:12)
	 * @return java.util.Vector
	 */
	public java.util.Vector getNeeds() {
		return this.needs;
	}
	/**
	 * isActive method comment.
	 */
	@Override
	public boolean competenceIsActive()
	{
		return !this.needs.isEmpty();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/04/2003 13:36:44)
	 * @return boolean
	 * @param c dima.kernel.communicatingAgent.domain.Contract
	 */
	public boolean isNeeded(final AgentIdentifier agentId)
	{
		//Enumeration e = getNeeds().elements();

		//while (e.hasMoreElements())
		//if (((AgentIdentifier)e.nextElement()).equals(c))
		//return true;

		//return false;	// il n'existe aucun need qui correspond � cette demande de participation

		return this.needs.contains(agentId);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (15/04/2003 11:48:54)
	 * @param c dima.kernel.communicatingAgent.domain.Contract
	 */
	public void removeNeed(final AgentIdentifier agentId)
	{
		//Enumeration e = needs.elements();

		//while(e.hasMoreElements())
		//{
		//if( ((AgentIdentifier)e.nextElement()).equals(agentId))
		//{
		//needs.remove(agentId);
		//break;
		//}
		//}
		if (this.needs.contains(agentId)) {
			this.needs.remove(agentId);
		}

	}
	/**
	 * step method comment.
	 */
	@Override
	public void step()
	{
		if (this.hasMail() && this.getFirstMessage() instanceof FIPAACLMessage )
		{
			final FIPAACLMessage m = (FIPAACLMessage)this.getFirstMessage();

			if (m.isCallForParticipationMessage())
			{
				System.out.println(this.getIdentifier()+" --> "+m.getSender()+" : Yes, I accept to participate to ("+m.getConversationId()+")");

				this.addRole(InteractiveAgent.defaultInitParticipantRole(this,m));

				/*if (getRole() instanceof ContractNetParticipant)
					((ContractNetParticipant)getRole()).setEvaluationStrategy(new RandomEvaluationStrategy());

				if (getRole() instanceof BargainingParticipant)
					((BargainingParticipant)getRole()).setEvaluationStrategy(new RandomEvaluationStrategy());
				 */
				this.activateRole(m.getConversationId());

				this.readMailBox();
			}
			else
			{
				if  (m.getPerformative().equals("AcceptProposal"))
				{
					this.addContract(m.getSender(),(AbstractService)m.getContent());
					this.addConstraint(new Constraint(m.getContent(),new Operator("!="))); // ajouter une nouvelle contrainte
					this.removeNeed(m.getSender());
				}

				if  (m.getPerformative().equals("InformDone"))
				{
					this.addContract(m.getSender(),(AbstractService)m.getContent());
					this.addConstraint(new Constraint(m.getContent(),new Operator("!="))); // ajouter une nouvelle contrainte
					this.removeNeed(m.getSender());
				}
				this.readMailBox();
			}
		} else {
			this.readMailBox();
		}

		this.wwait();
	}
}
