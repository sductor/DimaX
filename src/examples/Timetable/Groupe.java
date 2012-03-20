package examples.Timetable;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.kernel.INAF.InteractionAgents.SingleRoleAgent;
import dima.kernel.INAF.InteractionDomain.AbstractService;
import dima.kernel.INAF.InteractionDomain.Constraint;
import dima.kernel.INAF.InteractionDomain.EvaluationStrategyWithConstraints;
import dima.kernel.INAF.InteractionProtocols.BargainingInitiator;
import dima.kernel.INAF.InteractionProtocols.ContractNetInitiator;
import dima.kernel.INAF.InteractionTools.Operator;
import dima.ontologies.basicFIPAACLMessages.FIPAACLMessage;


public class Groupe extends SingleRoleAgent
{
	/**
	 *
	 */
	private static final long serialVersionUID = 920821509235872566L;
	public Vector needs;
	public Vector constraints;
	public Hashtable contracts;
	/**
	 * Groupe constructor comment.
	 */
	public Groupe()
	{
		super();
		this.needs = new Vector();
		this.constraints = new Vector();
		this.contracts = new Hashtable();
	}
	/**
	 * Groupe constructor comment.
	 * @param newId Gdima.basicagentcomponents.AgentIdentifier
	 */
	public Groupe(final dima.basicagentcomponents.AgentIdentifier newId)
	{
		super(newId);
		this.needs = new Vector();
		this.constraints = new Vector();
		this.contracts = new Hashtable();
	}
	/**
	 * Groupe constructor comment.
	 * @param newId java.lang.String
	 */
	public Groupe(final String newId)
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
	public java.util.Vector getNeeds()
	{
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

		if (this.isFree()) // si le r�le n'est pas instanci�, alors on cr�e un role initiateur
		{

			if(this.needs.size() == 1)  // il reste une seule seance � fixer ==> on utilise le protocole Bargaining
			{
				System.out.println(this.getIdentifier()+" --> "+this.needs.get(0)+" : I want to negotiate with you ...");

				final EvaluationStrategyWithConstraints strategy = new EvaluationStrategyWithConstraints();
				strategy.setConstraints(this.constraints);

				final BargainingInitiator b = new BargainingInitiator(this,this.buildNewConversationId(),(AgentIdentifier)this.needs.get(0),new Seance(),strategy);

				this.setRole(b);
			}
			else  // par d�faut, on utilise le protocole Contract Net
			{
				System.out.println(this.getIdentifier()+" --> "+this.needs+" : I send a call for proposal...");

				final Date time = new Date(System.currentTimeMillis() + 5000); // le time out est de 2 minutes

				final EvaluationStrategyWithConstraints strategy = new EvaluationStrategyWithConstraints();
				strategy.setConstraints(this.constraints);

				final ContractNetInitiator r = new ContractNetInitiator(this,this.buildNewConversationId(),this.needs,new Seance(),time,strategy);

				this.setRole(r);
			}

			this.getRole().activate(); // activer le r�le
			this.readMailBox();
		} else if(this.hasMail() && this.getFirstMessage() instanceof FIPAACLMessage)
		{
			final FIPAACLMessage m = (FIPAACLMessage)this.getFirstMessage();

			if  (m.getPerformative().equals("AcceptProposal") ||  m.getPerformative().equals("InformDone") )
			{
				this.addContract(m.getSender(),(AbstractService)m.getContent());
				this.addConstraint(new Constraint(m.getContent(),new Operator("!="))); // ajouter une nouvelle contrainte
				this.removeNeed(m.getSender());
			}

			this.readMailBox();

		}
		this.wwait();
	}
}
