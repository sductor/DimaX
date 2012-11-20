package dima.introspectionbasedagents.kernel;

import java.lang.reflect.InvocationTargetException;

import dima.basiccommunicationcomponents.AbstractMessage;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.ontologies.ClassEnveloppe;
import dima.introspectionbasedagents.ontologies.MessageInEnvelope;
import dima.introspectionbasedagents.services.loggingactivity.DebugProtocol;
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
	private static final long serialVersionUID = -338572470858657248L;

	public String handleException(
			final Throwable e,
			final SimpleAgentStatus status){
		Throwable e2;
		if (e instanceof InvocationTargetException) {
			e2 =((InvocationTargetException) e).getCause();
		} else {
			e2 = e;
		}

		if (status.getCurrentlyReadedMail()!=null) {
			return this.handleExceptionOnMessage(
					status.getCurrentlyExecutedAgent(),
					status.getCurrentlyExecutedBehavior(),
					status.getCurrentlyReadedMail(),
					e2);
		} else {
			return this.handleExceptionOnMethod(
					status.getCurrentlyExecutedAgent(),
					status.getCurrentlyExecutedBehavior(),
					e2);
		}
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
		String result = "This mail has been added to the mail box to be handled by the agent step()"
				+"\n --> The received message' env is :";
		if(mess instanceof MessageInEnvelope) {
			result+= ((MessageInEnvelope) mess).getMyEnvelope().toString();
		} else {
			result+=	new ClassEnveloppe(mess).toString();
		}
		result +="\n"+mess.getProtocolTrace();
		if (status.getCurrentlyExecutedAgent() instanceof CommunicatingCompetentComponent) {
			final CommunicatingCompetentComponent ccc = (CommunicatingCompetentComponent) status.getCurrentlyExecutedAgent();
			DebugProtocol.answerNotUnderstood(ccc, mess, status.getCurrentlyExecutedBehavior().getMethod().toGenericString());
		}
		return result;
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
		final String result =
				"------> Method : "+methodHandler+
				"\n ------> On message "+abstractMessage
				+"\n ------> "+abstractMessage.getProtocolTrace()
				+"\n ------> has raised an EXCEPTION : ";// + e;
		if (dimaComponentInterface instanceof CommunicatingCompetentComponent) {
			final CommunicatingCompetentComponent ccc = (CommunicatingCompetentComponent) dimaComponentInterface;
			DebugProtocol.answerFailure(ccc, abstractMessage,e);
		}

		return result;
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