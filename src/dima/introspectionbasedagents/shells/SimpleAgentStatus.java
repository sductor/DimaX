package dima.introspectionbasedagents.shells;

import dima.basiccommunicationcomponents.AbstractMessage;
import dima.basicinterfaces.DimaComponentInterface;
import dima.support.GimaObject;


public class SimpleAgentStatus extends GimaObject {

	//
	// Fields
	//

	/**
	 *
	 */
	private static final long serialVersionUID = -7905474551780027665L;
	/**
	 * Holds the reference of the executor if a method is being executed, holds
	 * null otherwise
	 */
	private DimaComponentInterface currentlyExecutedAgent = null;
	/**
	 * Holds the reference of the method if a method is being executed, holds
	 * null otherwise
	 */
	private MethodHandler currentlyExecutedMethod = null;
	/**
	 * Holds the reference of the message in process, holds null otherwise
	 */
	private AbstractMessage currentlyReadedMail = null;

	//
	// Methods
	//

	/*
	 *
	 */

	/**
	 * @param object
	 *            the currentlyExecutedAgent to set
	 */
	public void setCurrentlyExecutedAgent(
			final DimaComponentInterface agent) {
		this.currentlyExecutedAgent = agent;
	}

	/**
	 * @param mt
	 *            the currentlyExecutedMethod to set
	 */
	public void setCurrentlyExecutedMethod(final MethodHandler mt) {
		this.currentlyExecutedMethod = mt;
	}

	/**
	 * @param currentlyReadedMail
	 *            the currentlyReadedMail to set
	 */
	public void setCurrentlyReadedMail(
			final AbstractMessage currentlyReadedMail) {
		this.currentlyReadedMail = currentlyReadedMail;
	}

	/*
	 *
	 */

	/**
	 * Return the reference of the executor if a method is being executed,
	 * Return null otherwise
	 */
	public DimaComponentInterface getCurrentlyExecutedAgent() {
		return this.currentlyExecutedAgent;
	}

	/**
	 * Return the reference of the method if a method is being executed, Return
	 * null otherwise
	 */
	public MethodHandler getCurrentlyExecutedMethod() {
		return this.currentlyExecutedMethod;
	}

	/**
	 * Return the reference of the message in process if a message is in
	 * process, Return null otherwise
	 */
	public AbstractMessage getCurrentlyReadedMail() {
		return this.currentlyReadedMail;
	}

	/*
	 *
	 */

	public void resetCurrentlyExecutedMethod() {
		this.currentlyExecutedMethod = null;
	}

	public void resetCurrentlyExecutedAgent() {
		this.currentlyExecutedAgent = null;
	}

	public void resetCurrentlyReadedMail() {
		this.currentlyReadedMail = null;
	}

	//
	//
	//

	@Override
	public String toString() {
		return 		"Agent Status :"
		+	"\n    *** agent : " + this.getCurrentlyExecutedAgent()
		+ 	"\n    *** method : " + this.getCurrentlyExecutedMethod()
		+ 	"\n    *** mail  : " + this.getCurrentlyReadedMail().getClass();
	}

}
