package dimaxx.kernel;

import java.io.Serializable;
import java.rmi.RemoteException;

import darx.DarxTask;
import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.CommunicatingComponentInterface;
import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.basicinterfaces.ProactiveComponentInterface;
import dima.introspectionbasedagents.CompetentComponent;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dimaxx.hostcontrol.LocalHost;

public class DimaXTask<Component extends ProactiveComponentInterface & IdentifiedComponentInterface> extends DarxTask {

	//
	// Fields
	//

	/**
	 *
	 */
	private static final long serialVersionUID = -7518760102401713109L;
	Component dimaComponent;

	//This activity is at darx level, it is different of the internal activity status of the agent
	Boolean dimaxTaskActive = false;


	/** this field controls the activation of replicas */
	transient DimaXTaskEngine thread;

	//
	// Constructor
	//

	public DimaXTask(final Component component) {
		super(component.getIdentifier().toString());
		this.dimaComponent = component;
	}

	//
	// Accessor
	//

	/**
	 * @return true if this DarXTask is leader
	 */
	public boolean isLeader() {
		return this.leader;
	}

	public Boolean dimaxTaskIsActive() {
		return this.dimaxTaskActive;
		//		&& (this.isLeader() ||
		//		this.getHandle().getReplicationPolicy().getReplicationType().equals(ReplicationStrategy.ACTIVE_STRATEGY));
	}

	protected Component getComponent() {
		return this.dimaComponent;
	}
	//
	// Primitives
	//

	/*
	 * Activation
	 */


	public void activateTask(final int port) throws RemoteException {
		this.activateTask(LocalHost.getUrl(), port);
	}


	public void setdimaxTaskActive(final Boolean active) {
		this.dimaxTaskActive = active;
	}


	/*
	 * Run
	 */

	/**
	 * Create and launch the thread that manages the agent
	 */
	@Override
	public void start() {

		this.setdimaxTaskActive(true);
		if (this.dimaComponent instanceof CompetentComponent) {
			((CompetentComponent) this.dimaComponent).logMonologue("Starting "+this.getTaskName(), LogService.darxKey);
		} else {
			LogService.write(this, "Starting "+this.getTaskName());
		}
		if (this.thread == null) {
			this.thread = new DimaXTaskEngine(this);
		}

		this.thread.start();
	}




	/**
	 * Suspend the execution of the agents
	 */
	@Override
	public void suspend() {
		if (this.dimaComponent instanceof CompetentComponent) {
			((CompetentComponent) this.dimaComponent).logMonologue("Suspending "+this.getTaskName(), LogService.darxKey);
		} else {
			LogService.write(this, "Suspending "+ this.getTaskName());
		}
		this.setdimaxTaskActive(false);
	}

	/**
	 * Resume the execution of all agents This method controls the replication
	 * strategy of one task, makes decision to start it or no
	 *
	 * @see DarxAgentExecutor
	 */
	@Override
	public void resume() {
		if (this.dimaComponent instanceof CompetentComponent) {
			((CompetentComponent) this.dimaComponent).logMonologue("Resuming "+this.getTaskName(), LogService.darxKey);
		} else {
			LogService.write(this, "Resuming "+ this.getTaskName());
		}
		this.setdimaxTaskActive(true);
	}

	/**
	 * Terminate the execution of the agent
	 */
	@Override
	public void terminate() {
		this.dimaComponent.proactivityTerminate();
		this.setdimaxTaskActive(false);
	}

	/*
	 * Message Handling
	 */

	/**
	 * Put the message
	 * received from DarX in the agent mailbox
	 *
	 * @param msg
	 *            the message, that should be cast in Message
	 * @see Message
	 */
	@Override
	public void receiveAsyncMessage(final Object msg) {
		if (this.dimaComponent instanceof CommunicatingComponentInterface){
			if (msg instanceof Message) {
				((CommunicatingComponentInterface) this.dimaComponent).receive((Message) msg);
			} else if (this.dimaComponent instanceof CompetentComponent) {
				((CompetentComponent) this.dimaComponent).signalException(msg+" is not a message : can not be added to mail box!");
			} else {
				LogService.writeException(this, msg+" is not a message : can not be added to mail box!");
			}
		} else if (this.dimaComponent instanceof CompetentComponent) {
			((CompetentComponent) this.dimaComponent).signalException(this.dimaComponent+" does not communicate!");
		} else {
			LogService.writeException(this, this.dimaComponent+" does not communicate!");
		}

	}

	/**
	 * UNIMPLEMENTED : Execute the task and return the results
	 *
	 * @param msg
	 *            the message, that should be cast in Message
	 * @see Message
	 */
	@Override
	public Serializable receiveSyncMessage(final Object msg) {
		this.receiveAsyncMessage(msg);
		return null;
	}
}



//TODO :

//		if (dimaComponent instanceof CommunicatingComponentInterface){
//			if (msg instanceof SyncMessage<?>  && dimaComponent instanceof CommunicatingComponentInterface)
//				return ((CommunicatingComponentInterface) dimaComponent).receiveSyncMessage((SyncMessage<?>) msg);
//			else{
//				LoggerManager.writeException(this, msg+" is not a sync message : added to mail box");
//				((CommunicatingComponentInterface) dimaComponent).receiveAsyncMessage((Message) msg);
//				return null;
//			}
//		} else{
//			LoggerManager.writeException(this, dimaComponent+" does not communicate!");
//			return null;
//		}

//private void yield() {
//	try {
//		Thread.sleep(50, 10);
//	} catch (final InterruptedException e) {
//		e.printStackTrace();
//	}
//}
////
//// Methods
////
//
//protected void logWarning(String text){
//	LoggerManager.writeWarning(myAgent, text);
//}
//
//
//protected void logException(String text){
//	LoggerManager.writeException(myAgent, text);
//}
//
//protected void logException(String text, Throwable e){
//	LoggerManager.writeException(myAgent, text, e);
//}
//
//protected  void logMonologue(String text){
//	LoggerManager.writeMonologue(this,text);
//}
//
//
////
//// Methodes abstraites
////
//
//protected abstract void logWarning(String text);
//protected abstract void logException(String text);
//protected abstract void logException(String text, Throwable e);
//protected abstract void logMonologue(String text);
//
//protected abstract void initiateAgent();
//protected abstract void runAgent();
//protected abstract void terminateAgent();
//
//public abstract void receiveAsyncMessage(final Message msg);
//public abstract <K extends Serializable> K receiveSyncMessage(final SyncMessage<K> msg);