package dima.kernel.INAF.InteractionProtocols;

/**
 * Insert the type's description here.
 * Creation date: (11/04/2003 13:05:34)
 * @author: Tarek JARRAYA
 */

import dima.basiccommunicationcomponents.Message;
import dima.tools.automata.State;

public class RoleEngine implements Runnable
{
	public AbstractRole role;
	public Thread thread = new Thread(this);
/**
 * RoleInterpreter constructor comment.
 */
public RoleEngine()
{
	super();
}
/**
 * RoleInterpreter constructor comment.
 */
public RoleEngine(final AbstractRole newRole)
{
	super();
	this.setRole(newRole);
}
/**
 * Insert the method's description here.
 * Creation date: (11/04/2003 13:21:10)
 * @return dima.kernel.communicatingAgent.interaction.AbstractRole
 */
public AbstractRole getRole()
{
	return this.role;
}
/**
 * Insert the method's description here.
 * Creation date: (20/04/2003 11:56:51)
 */
@Override
public void run()
{
    //System.out.println("CYCLE DU ROLE ACTIVE ....");
	this.role.setRunState();

	State currentState = this.role.getInitialState();

	while (!currentState.isFinal())
	{
	    // System.out.println("ENTRER DANS CYCLE EXECUTION DU ROLE .....");
		currentState = currentState.crossTransition2(this.role);

		try	{ Thread.sleep(200,10);
			}
		catch (final InterruptedException e){}
	}

	this.stopRole(currentState);
}

public void startUp() {

	this.thread.start();
}

/**
 * Insert the method's description here.
 * Creation date: (11/04/2003 13:21:10)
 * @param newRole dima.kernel.communicatingAgent.interaction.AbstractRole
 */
public void setRole(final AbstractRole newRole)
{
	this.role = newRole;
}



/**
 * Insert the method's description here.
 * Creation date: (16/09/2003 18:17:56)
 */
public void stopRole(final State finalS)
{
	// ajouter un message evenement dans le mailbox de l'agent

    final Message m = new Message("endRoleEvent",new String(this.role.getConversationId()));//,role.getAgent().getId());
    m.setReceiver(this.role.getAgent().getId());
    m.setSender(this.role.getAgent().getId());

	this.role.getAgent().getMailBox().writeMail(m);

	//if(finalS.getStateName().equals("failure"))
	//{
		//if (role.isInitiatorRole())
			////role.getAgent().setRoleState(role.getConversationId(),"fail");
			//role.getAgent().initiatorFail(role.getConversationId());
		//else
			////role.getAgent().setRoleState(role.getConversationId(),"fail");
			//role.getAgent().participantFail(role.getConversationId());
	//}
	//else
	//{
		//if (role.isInitiatorRole())
			////role.getAgent().setRoleState(role.getConversationId(),"Success");
			//role.getAgent().initiatorSuccess(role.getConversationId());
		//else
			////role.getAgent().setRoleState(role.getConversationId(),"Success");
			//role.getAgent().participantSuccess(role.getConversationId());
	//}

	//role.getAgent().removeRole(role.getConversationId());
}
}
