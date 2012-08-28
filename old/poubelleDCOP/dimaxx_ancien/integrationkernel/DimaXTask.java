package dimaxx.integrationkernel;

import java.io.Serializable;
import java.rmi.RemoteException;

import darx.DarxTask;
import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.CommunicatingComponentInterface;
import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.basicinterfaces.ProactiveComponentInterface;
import dima.introspectionbasedagents.libraries.loggingactivity.LoggerManager;
import dimaxx.deploymentAndExecution.LocalHost;

public class DimaXTask<Component extends ProactiveComponentInterface & IdentifiedComponentInterface>
extends DarxTask {

	//
	// Fields
	//

	/**
	 *
	 */
	private static final long serialVersionUID = -7518760102401713109L;
	Component dimaComponent;
	Boolean active = false;


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

	public Boolean isActive() {
		return this.active;
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


	public void setActive(final Boolean active) {
		this.active = active;
	}


	/*
	 * Run
	 */

	/**
	 * Create and launch the thread that manages the agent
	 */
	@Override
	public void start() {

		this.active = true;
		if (this.isActive()){
			LoggerManager.write(this, "Starting "+this.getTaskName());
			if (this.thread == null)
				this.thread = new DimaXTaskEngine(this);

			this.thread.start();
		}
	}




	/**
	 * Suspend the execution of the agents
	 */
	@Override
	public void suspend() {
		LoggerManager.write(this, "Suspending "+ this.getTaskName());
		this.active = false;
	}

	/**
	 * Resume the execution of all agents This method controls the replication
	 * strategy of one task, makes decision to start it or no
	 *
	 * @see DarxAgentExecutor
	 */
	@Override
	public void resume() {
		LoggerManager.write(this, "Resuming "+ this.getTaskName());
		this.active = true;
	}

	/**
	 * Terminate the execution of the agent
	 */
	@Override
	public void terminate() {
		this.dimaComponent.proactivityTerminate();
		this.setActive(false);
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
			if (msg instanceof Message)
				((CommunicatingComponentInterface) this.dimaComponent).receive((Message) msg);
			else
				LoggerManager.writeException(this, msg+" is not a message : can not be added to mail box!");
		} else
			LoggerManager.writeException(this, this.dimaComponent+" does not communicate!");

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