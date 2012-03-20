package  examples.lg.agent;

import java.util.Date;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.kernel.INAF.InteractionAgents.MultiRolesAgent;
import dima.kernel.INAF.InteractionDomain.Constraint;
import dima.kernel.INAF.InteractionDomain.EvaluationStrategyWithConstraints;
import dima.kernel.INAF.InteractionProtocols.AbstractRole;
import dima.kernel.INAF.InteractionProtocols.ContractNetInitiator;
import dima.kernel.INAF.InteractionProtocols.ContractNetParticipant;
import dima.kernel.INAF.InteractionTools.Operator;
import examples.lg.model.GameException;
import examples.lg.model.Letter;
import examples.lg.model.LetterGame;
import examples.lg.strategy.DropHeuristic;
/**
 * Insert the type's description here.
 * Creation date: (08/09/2003 09:48:56)
 * @author:
 */

public class LoneNegoAgent extends MultiRolesAgent
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3736817688619015831L;
	protected LetterGame game;
	/**
	 * LoneNegoAgent constructor comment.
	 * @param newId Gdima.basicagentcomponents.AgentIdentifier
	 */
	public LoneNegoAgent(final AgentIdentifier newId, final LetterGame g)
	{
		super(newId);
		this.setGame(g);
		this.services = this.game.getDeck().getLetters();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 11:43:58)
	 */
	public void display()
	{
		System.out.println("Word "+this.getId()+" : " + this.game.getWordToComplete().getCurrentSubword());

		//System.out.println("Deck "+getId()+" : "+game.getDeck().getLetters()+" == "+ services+ " => Les lettres qui je peux proposer : "+getServices());
		System.out.println("Deck "+this.getId()+" : "+this.game.getDeck().getLetters());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 11:20:13)
	 * @param l lg.model.Letter
	 */
	public void exchangeLetter(final Letter neededL)
	{
		final Letter dropedL = DropHeuristic.whichToDrop(this.game);
		// il faut dropper une lettre, on se sert d'une heuristique
		if (dropedL != null) {
			try
			{	this.game.getDeck().dropLetter(dropedL);		// retirer la letter du Deck
			this.initInitiatorRole(dropedL,neededL); 	// on lance un appel � proposition
			}
			catch (final GameException e){
				e.printStackTrace(System.out);	}
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/09/2003 05:50:08)
	 * @param convId java.lang.String
	 */
	@Override
	public void failureRoleProcess(final AbstractRole r)
	{
		if(r.isInitiatorRole()) {
			this.addService(r.getContract());
		} else if( ((ContractNetParticipant)r).getProposal() != null) {
			this.addService(((ContractNetParticipant)r).getProposal());
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/09/2003 17:23:49)
	 * @return lg.model.LetterGame
	 */
	public LetterGame getGame() {
		return this.game;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/09/2003 11:45:34)
	 * @return java.util.Vector
	 */
	@Override
	public java.util.Vector getServices()
	{
		return new Vector(this.game.getNoNeededLetters());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/09/2003 14:38:00)
	 * @return boolean
	 */
	public boolean hasInitiatorRole()
	{
		final java.util.Enumeration e = this.roles.elements();

		while(e.hasMoreElements()) {
			if( ((AbstractRole)e.nextElement()).isInitiatorRole()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 16:26:05)
	 */
	public void initInitiatorRole(final Letter dropedL, final Letter neededL)
	{
		final String convId = new String(this.buildNewConversationId()); // cr�er un nouveau identifiant pour la conversation

		final EvaluationStrategyWithConstraints strategy = new EvaluationStrategyWithConstraints();

		//ajout d'une contrainte d'acceptation des propositions : il faut que la lettre propos�e en echange corresponde au neededL
		strategy.addConstraint(new Constraint(neededL,new Operator("==")));

		final ContractNetInitiator  role = new ContractNetInitiator(this,convId,this.getAgentsAquaintances(),dropedL,new Date(System.currentTimeMillis() + 1000),strategy);

		this.addRole(role);
		this.activateRole(convId);
	}
	/**
	 * isActive method comment.
	 */
	@Override
	public boolean competenceIsActive()
	{
		//l'agent est actif tant qu'il n'a pas termin� le mot
		return !this.game.getWordToComplete().isComplete();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/02/2000 23:50:27)
	 * @return boolean
	 */
	@Override
	public void preActivity()
	{
		if(!this.hasInitiatorRole()) // si l'agent est libre
		{
			final Letter needed = this.game.getWordToComplete().getNeededLetter();

			if (this.game.hasLetter(needed)) {
				this.useLetter(needed);
				//System.out.println(getId()+" : Oui j'ai la lettre ("+needed+") dans la main");
			}
			else {
				this.exchangeLetter(needed); // lancer un cfp
			}
		}
		this.display();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/09/2003 17:23:49)
	 * @param newGame lg.model.LetterGame
	 */
	public void setGame(final LetterGame newGame) {
		this.game = newGame;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/09/2003 05:50:44)
	 * @param convId java.lang.String
	 */
	@Override
	public void successRoleProcess(final AbstractRole r)
	{
		if(r.isInitiatorRole()) {
			//addService(((ContractNetInitiator)r).getAcceptedProposal());
			try{
				this.getGame().getWordToComplete().addLetter((Letter)((ContractNetInitiator)r).getAcceptedProposal());
			}catch(final GameException e)
			{ e.printStackTrace(System.out);}
		} else {
			this.addService(((ContractNetParticipant)r).getContract());
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (15/09/2003 13:11:23)
	 * @param l lg.model.Letter
	 */
	public void useLetter(final Letter l)
	{
		try
		{
			this.game.useLetter(l);

		}catch(final GameException e)
		{	e.printStackTrace(System.out);
		}
	}
}
