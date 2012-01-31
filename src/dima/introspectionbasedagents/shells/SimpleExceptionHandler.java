package dima.introspectionbasedagents.shells;

import java.lang.reflect.InvocationTargetException;

import dima.basiccommunicationcomponents.AbstractMessage;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.ontologies.ClassEnveloppe;
import dima.introspectionbasedagents.ontologies.MessageInEnvelope;
import dima.support.GimaObject;



/**
 *
 * handleException(Throwable e, AgentStatus s) Is called by a dimaX agent every
 * time an exception occurs
 *
 * @author Sylvain Ductor
 *
 */
public class SimpleExceptionHandler extends GimaObject{

	/**
	 *
	 */
	private static final long serialVersionUID = -338572470858657248L;

	public String handleException(
			final Throwable e,
			final SimpleAgentStatus status){
		Throwable e2;
		if (e instanceof InvocationTargetException)
			e2 =((InvocationTargetException) e).getCause();
		else
			e2 = e;

		if (status.getCurrentlyReadedMail()!=null)
			return this.handleExceptionOnMessage(
					status.getCurrentlyExecutedAgent(),
					status.getCurrentlyExecutedMethod(),
					status.getCurrentlyReadedMail(),
					e2);
		else
			return this.handleExceptionOnMethod(
					status.getCurrentlyExecutedAgent(),
					status.getCurrentlyExecutedMethod(),
					e2);
	}


	/**
	 * Invocated when an agent does not know how to handle the message
	 * @param metToRemove
	 * @param dimaXAgentStatus
	 * @param m
	 */
	public String handleUnhandledMessage(
			final AbstractMessage mess,
			final SimpleAgentStatus status){
		String t;
		if(mess instanceof MessageInEnvelope)
			t = ((MessageInEnvelope) mess).getMyEnvelope().toString();
		else
			t =	new ClassEnveloppe(mess).toString();
		return
				"This mail has been added to the mail box for be handled by the agent step()"
				+"\n --> The received message' env is :"+t;
	}

	//
	// Primitives
	//

	/**
	 * This method aims at handling an exception that arise during an agent
	 * execution
	 * @param methodHandler
	 * @param methodHandler
	 * @param metToRemove
	 * @param dimaComponentInterface
	 *
	 * @param abstractMessage throwable
	 *            the exception to ahndle
	 * @param s up-to-date status of what was being done
	 */
	protected String handleExceptionOnMessage(
			final DimaComponentInterface dimaComponentInterface,
			final MethodHandler methodHandler,
			final AbstractMessage abstractMessage,
			final Throwable e){
		return "Method "+methodHandler+" on message "+abstractMessage
				+"\n(" + e+")"
				+"\n has raised an EXCEPTION";

	}

	/**
	 * This method aims at handling an exception that arise during an agent
	 * execution
	 * @param metToRemove
	 * @param mt
	 *
	 * @param e throwable
	 *            the exception to ahndle
	 * @param s up-to-date status of what was being done
	 */
	protected String handleExceptionOnMethod(
			final DimaComponentInterface dimaComponentInterface,
			final MethodHandler mt,
			final Throwable e){
		return "Method "+mt.getMethodName()
				+"\n has raised an EXCEPTION";

	}

	//	protected boolean handleNotReadyException(
	//			final DimaComponentInterface dimaComponentInterface,Throwable e){
	//		if (e instanceof NotReadyException) {
	//			NotReadyException nre = (NotReadyException) e;
	//			dimaComponentInterface.wh
	//		}
	//		else
	//			return false;
	//	}
}