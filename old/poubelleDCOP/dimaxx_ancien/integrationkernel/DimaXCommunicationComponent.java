package dimaxx.integrationkernel;

import darx.DarxCommInterface;
import darx.DarxException;
import darx.RemoteTask;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.CommunicationComponent;
import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.CommunicatingComponentInterface;
import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.basicinterfaces.ProactiveComponentInterface;
import dima.introspectionbasedagents.libraries.loggingactivity.LoggerManager;

public class DimaXCommunicationComponent<Component extends ProactiveComponentInterface & IdentifiedComponentInterface & CommunicatingComponentInterface>
extends CommunicationComponent {

    /**
	 *
	 */
	private static final long serialVersionUID = 4142959262808107106L;
	protected  DarxCommInterface comm;
    protected  DimaXTask<Component> task;


	public DimaXCommunicationComponent(final DimaXTask<Component> task) {
		super();
		this.task = task;
		this.comm = new DarxCommInterface(task.getTaskName());
	}


	@Override
	public void sendMessage(final Message m) {

		final AgentIdentifier id = m.getReceiver();
		RemoteTask remote = null;
		try {
			remote = this.task.findTask(id.toString());
		}
		catch(final DarxException e) {
			System.out.println("Getting " + id + " from nameserver failed : " + e);
			return;
		}
		System.out.println(task.getTaskName()+" I want to send a message!");

		if(remote != null)
			this.comm.sendAsyncMessage(remote, m);
		else
			LoggerManager.writeException(this, "Echec de l'envoi du message"+m);

	}
}