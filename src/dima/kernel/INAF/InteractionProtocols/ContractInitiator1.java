/*
 * Created on 31 mai 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package dima.kernel.INAF.InteractionProtocols;

/**
 * @author faci
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * Insert the type's description here.
 * Creation date: (19/03/03 22:06:31)
 */

import java.util.Date;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.kernel.INAF.InteractionAgents.InteractiveAgent;
import dima.kernel.INAF.InteractionDomain.AbstractService;
import dima.kernel.INAF.InteractionDomain.EvaluationStrategy;
import dima.kernel.INAF.InteractionDomain.EvaluationStrategyWithConstraintsMultiProp;
import dima.ontologies.basicFIPAACLMessages.ACLAcceptProposal;




public class ContractInitiator1 extends ContractNetInitiator
{
	/**
	 *
	 */
	private static final long serialVersionUID = 2470075674981117291L;
	private Vector acceptedProposals=new Vector();
	private final Vector acceptedProposers=new Vector();
	/**
	 * InitiatorContractNetProtocol constructor comment.
	 */
	public ContractInitiator1()
	{
		super();

		this.setRoleName("ContractNetInitiator1");
	}
	/**
	 * InitiatorContractNetProtocol constructor comment.
	 */
	public ContractInitiator1(final InteractiveAgent agent,final String convId,final Vector agents,final AbstractService c,final Date time)
	{
		super();
		this.setRoleName("ContractNetInitiator1");

		this.setAgent(agent);
		this.setConversationId(convId);
		this.setParticipants(agents);
		this.setContract(c);
		this.setTimeOut(time);
	}
	/**
	 * InitiatorContractNetProtocol constructor comment.
	 */
	public ContractInitiator1(final InteractiveAgent agent,final String convId,final Vector agents,final AbstractService c,final Date time, final EvaluationStrategy strg)
	{
		super();
		this.setRoleName("ContractNetInitiator1");

		this.setAgent(agent);
		this.setConversationId(convId);
		this.setParticipants(agents);
		this.setContract(c);
		this.setTimeOut(time);
		this.setEvaluationStrategy(strg);

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (31/03/03 01:25:08)
	 * @return boolean
	 */
	public boolean acceptProposals()
	{
		/* */ System.out.println("ENTRER DANS ACCEPTED PROPOSALS...");

		return this.getAcceptedProposals()!=null;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (15/04/2003 12:05:31)
	 */
	public void addContract()
	{
		final AbstractService proposal = (AbstractService)this.getCurrentMessage().getContent();
		final AgentIdentifier proposer = this.getCurrentMessage().getSender();

		this.getAgent().addContract(proposer,proposal);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (31/03/03 01:11:35)
	 */
	public void addReceivedProposal(final AgentIdentifier proposer, final AbstractService proposal)
	{
		/* */ System.out.println("AJOUT DE PROP RECUE.....OK POUR ID CONV.."+this.getConversationId());
		/* */ System.out.println("VALEUR DE PROPOSER...."+proposer.toString());
		if (proposal!=null) /* */ System.out.println("VALEUR DE PROPOSAL....."+proposal.toString());
		else /* */ System.out.println("ATTENTION UNE PROPOSITION VIDE......;");
		this.receivedProposals.put(proposer,proposal);
		/* */ System.out.println("RECEPTION DE PROPOSITION OK.....");
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (07/04/2003 10:56:03)
	 */



	// version multi proposition avec contrainte budgetaire

	@Override
	public void evaluateProposals()
	{
		final InteractiveAgent agent = this.getAgent();

		System.out.println(agent.getIdentifier() + " (" + this.getConversationId() + ") : evaluate proposals....");

		this.getEvaluationStrategy().setProposals(new Vector(this.getReceivedProposals().values()));


		final Vector v= ((EvaluationStrategyWithConstraintsMultiProp) this.getEvaluationStrategy()).execute1();
		/* */ System.out.println("NBRE DE PROPOSITIONS ACCEPTEES PAR ...."+agent.getId().toString()+" "+v.size());

		try
		{

			if (v.size()!=0) {

				this.setAcceptedProposals(v);

				final Vector v1 = new Vector(this.getReceivedProposals().keySet());

				for (int j=0;j<v.size();j++ ){
					final AbstractService proposal= (AbstractService) v.elementAt(j);
					int i = 0;
					while (i < v1.size())
					{

						{

							if (proposal.equals(this.getReceivedProposals().get(v1.get(i))))
							{
								/* */ System.out.println("VOICI L'IDENTITE DE PROP ACCEPTEE.."+((AgentIdentifier) v1.get(i)).toString());
								this.setAcceptedProposers((AgentIdentifier) v1.get(i));
								break;
							}
						}
						i++;
					}
				}
			}

		}
		catch (final NullPointerException e)
		{
			System.out.println(agent.getIdentifier() + " (" + this.getConversationId() + ") : negotiation fail....");
		}
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (23/04/2003 00:28:05)
	 * @return dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
	 */
	public Vector getAcceptedProposals()
	{
		return this.acceptedProposals;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (07/04/2003 10:57:51)
	 */
	@Override
	public void sendAcceptProposal()
	{
		/* */ System.out.println("ENTRER DANS SEND ACCEPT PROPOSALS ....");
		final InteractiveAgent agent = this.getAgent();

		final ACLAcceptProposal msg = new ACLAcceptProposal(this.getConversationId());
		/* */ System.out.println("ESSAI ENVOI D'UN ACCEPT...");
		// envoi des propositions acc�pt�es aux concern�s
		for (int i=0;i<this.acceptedProposals.size();i++){
			//msg.setContent(getAcceptedProposal());
			msg.setContent(this.acceptedProposals.elementAt(i));
			msg.setProtocol("FIPAContractNetProtocol");
			// agent.sendMessage(getAcceptedProposer(), msg);
			agent.sendMessage((AgentIdentifier) this.acceptedProposers.elementAt(i), msg);

			// System.out.println(agent.getIdentifier() + " --> " + getAcceptedProposer() + " : I accept your Proposal...");
			System.out.println(agent.getIdentifier() + " --> " + ((AgentIdentifier) this.acceptedProposers.elementAt(i)).toString() + " : I accept your Proposal...");

		}
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (23/04/2003 00:28:05)
	 * @param newAcceptedProposal dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
	 */
	public void setAcceptedProposals(final Vector newAcceptedProposals)
	{
		this.acceptedProposals=newAcceptedProposals;
		this.acceptedProposal= (AbstractService) newAcceptedProposals.elementAt(0);
	}


	public void setAcceptedProposers(final dima.basicagentcomponents.AgentIdentifier newAcceptedProposer)
	{
		this.acceptedProposer=newAcceptedProposer;
		this.acceptedProposers.add(newAcceptedProposer);
	}

}


