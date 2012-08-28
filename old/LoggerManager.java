package dima.introspectionbasedagents.coreservices.loggingactivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import darx.Darx;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.CommunicatingCompetentComponent;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.coreservices.observingagent.NotificationMessage;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
/**
 * Le LoggerManager est chargé d'écrire et d'afficher les logs d'activité des
 * agents de l'application. Il charge depuis un fichier xml verifiant log.dtd
 * qui définit qu'elles sont les destination sur lesquelles il peut écrire Il
 * s'abonne
 *
 * @author Sylvain Ductor
 */
public class LoggerManager {


	/////OLD

	/////OLD




	private static final long serialVersionUID = -2334034738070660410L;
	class LogProtocol extends Protocol<CommunicatingCompetentComponent> {
		private static final long serialVersionUID = -9110912414804929695L;
//		public LogProtocol(final CommunicatingCompetentComponent com) {
//			super(com);}

		static final String Activate = "Activate Log";
		static final String NewCreation = "A new agent has been created";
	}

	public enum LogType {
		Monologue, MessageSend, MessageReceived, MessageLocallySend, MessageLocallyReceived, Exception, Warning;

		public String getKey() {
			return "#Logging#" + this.toString();
		}
	}

	//
	// Fields
	//

	private String myPath;
	private boolean mainLogger = false;

	private final boolean printdetails = true;

	private boolean printAllToScreenActivated;
	private boolean printExceptionToScreen;
	private boolean printWarningToScreen;

	//
	// Constructor
	//

	//	public LoggerManager(final AgentIdentifier h)
	//			throws MissingCompetenceException {
	//		super(h);
	//		if (!HostIdentifier.class.isAssignableFrom(h.getClass()))
	//			throw new MissingCompetenceException(this.getClass(),
	//					new IllegalArgumentException(h.toString()));
	//		else {
	//			this.myPath = ServerManager.getLocalDimaXDir() + "/log/"
	//					+ this.getIdentifier() + "#"
	//					+ ServerManager.getCreationTime() + "/";
	//			this.myPath = this.myPath.replaceAll(":", "_");
	//		}
	//		// TODO XML
	//
	//	}

	//
	// Accessors
	//

	public void printLoggerPathInfo() {
		System.out.println("   * LOGGER *");
		System.out.println("Les logs de cette session sont écrit dans : ");
		System.out.println(" ******* " + this.myPath);
		System.out.println();
	}

	public boolean isLogged(final AgentIdentifier ag, final LogType messageType) {
		// TODO
		return true;
	}

	public void setMainLogger() {
		this.mainLogger = true;
	}

	//
	// Competence
	//

	/**
	 * Used when a new agent or host is created
	 *
	 * @param activate
	 * @return true if success
	 */
	public void registerNewAgent(final AgentIdentifier agent) {
		final FipaACLMessage m = new FipaACLMessage(Performative.Inform,
				LogProtocol.NewCreation, LogProtocol.class);
		m.setAttachement(new Object[] { agent }, new Class[] { AgentIdentifier.class });
		//		this.com.sendMessage(ServerManager.getMainServerIdentifier(), m);
	}

	//
	// Behavior
	//
	/**
	 * Activate/Desactivate the service
	 *
	 * @param FipaACLMessage
	 *            m
//	 */
	//	@MessageHandler
	//	@FipaACLEnvellopeHandler(
	//			performative = Performative.Request,
	//			protocol = LogProtocol.class, attachementSignature = { Boolean.class })
	//	public Boolean activate(final FipaACLMessage m) {
	//		final Boolean active = (Boolean) m.getAttachement()[0];
	//		this.setActive(active);
	//		return true;
	//	}

//	@MessageHandler
//	@FipaACLEnvelope(performative = Performative.Inform,
//			protocol = ObservationProtocol.class, attachementSignature = {
//		NotificationMessage.class, AgentIdentifier.class })
		public void receiveLog(final FipaACLMessage m) {
		final NotificationMessage<?> n = (NotificationMessage<?>) m.getArgs()[0];
		final AgentIdentifier agent = (AgentIdentifier) m.getArgs()[1];
		if (n.getNotification() instanceof LogNotification)
			System.out.println(((LogNotification) n.getNotification()).generateLogToScreen(agent,
					this.printdetails));
		//		else
		//			notify(new LogException(
		//					"not supposed to receive this!!:\n" + m));
		// File file = new File(myPath+m.getNotification().getFileName());
		// if (m.getNotification().isException())
		// write(file, m.getNotification().getText(), printExceptionToScreen,
		// m.getNotification().getException());
		// else
		// write(file, m.getNotification().getText(), printAllToScreenActivated,
		// m.getNotification().isWarning());
	}

	//
	// Method
	//

	@MessageHandler
	@FipaACLEnvelope(performative = Performative.Inform,
			protocol = LogProtocol.class, attachementSignature = { AgentIdentifier.class })
			protected void requestLog(final FipaACLMessage m) {
		final AgentIdentifier ag = (AgentIdentifier) m.getArgs()[0];
		//		this.observation.askObservationOf(ag, new LogNotificationKey());
		// for (LogType t : LogType.values())
		// if (isLogged(ag, t))
		// sendMessageToService(new RequestObservationServiceMessage(ag,
		// t.getKey()));;

	}

	//
	// Static Methods
	//


}

// /*
// * Paths
// */
//
// private String myAgentPath;
// private String myServicePath;
// private String myHostPath;
//
// private File myAgentCanalInfoFile;
//
// private File myMessageLogFile;
// private File myExceptionLogFile;
//
// /*
// * Configuration
// */
//
// private boolean agentInfoActivated;
// private boolean serviceInfoActivated;
// private boolean hostInfoActivated;
//
// private boolean agentCanalInfoActivated;
//
// private boolean printDetails;
//
// private boolean messageSendActivated;
// private boolean messageReceiveActivated;
//
// private boolean warningActivated;
// private boolean exceptionActivated;
// private boolean printExceptionDetails;
//
// private boolean printToScreenActivated;
// private boolean printExceptionToScreen;
// private boolean printAllToScreen;