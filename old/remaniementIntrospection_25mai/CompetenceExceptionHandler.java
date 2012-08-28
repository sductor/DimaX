package dima.introspectionbasedagents.kernel.tools;

import dima.basiccommunicationcomponents.AbstractMessage;
import dima.introspectionbasedagents.coreservices.loggingactivity.LoggerManager;
import dima.introspectionbasedagents.kernel.shells.BasicCommunicatingMethodTrunk;
import dima.introspectionbasedagents.kernel.shells.BasicCommunicatingMethodTrunk.UnHandledMessageException;

public class CompetenceExceptionHandler extends SimpleExceptionHandler {

	/**
	 *
	 */
	private static final long serialVersionUID = -8790943513105560743L;
	public boolean isCompetence;
	public BasicCommunicatingMethodTrunk myAgent;

	public CompetenceExceptionHandler() {
		super();
	}

	//
	// Accessors
	//

	public void setMyAgent(final BasicCommunicatingMethodTrunk myAgent) {
		this.myAgent = myAgent;
	}

	//
	// Methods
	//

	@Override
	public String handleUnhandledMessage(
			final AbstractMessage mess,
			final SimpleAgentStatus status) {
		if (!status.getCurrentlyExecutedAgent().equals(this.myAgent.getMyComponent()))
			try {
				this.myAgent.parseMail(mess, status);
				return "réussite? A vérifier";
			} catch (final UnHandledMessageException e) {
				return super.handleUnhandledMessage(mess, status);
			} catch (final Exception e) {
				return super.handleException(e, status);
			}
			else
				return super.handleUnhandledMessage(mess, status);
	}

	public void handleExceptionOnHooks(final Exception e,
			final SimpleAgentStatus status) {
		LoggerManager.writeException(this,
				"Hook"
				+"\n(" + status+")"
				+"\n has raised EXCEPTION :\n" , e.getCause());


	}
}
