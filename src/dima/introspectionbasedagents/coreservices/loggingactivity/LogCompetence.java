package dima.introspectionbasedagents.coreservices.loggingactivity;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.management.Notification;

import darx.Darx;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.competences.BasicAgentCommunicatingCompetence;
import dima.introspectionbasedagents.competences.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.coreservices.loggingactivity.LogCommunication.MessageStatus;
import dima.introspectionbasedagents.coreservices.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.coreservices.observingagent.NotificationMessage;

/**
 * OLD :
 * Le LoggerManager est chargé d'écrire et d'afficher les logs d'activité des
 * agents de l'application. Il charge depuis un fichier xml verifiant log.dtd
 * qui définit qu'elles sont les destination sur lesquelles il peut écrire Il
 * s'abonne
 *
 * @author Sylvain Ductor
 */
public final class LogCompetence extends BasicAgentCommunicatingCompetence<BasicCompetentAgent>{
	private static final long serialVersionUID = -4511578003487049832L;

	//
	// Fields
	//

	public static final String logKey = "log official key!!";
	public Map<String, Boolean> keysToScreen;
	public Map<String, Boolean> keysToFiles;

	//Order or the log to be written to screen
	public  boolean commtoScreen = false;
	public  boolean exceptoScreen = true;
	public  boolean monotoScreen = false;
	//Order or the log to be written in specific files
	public static boolean toFiles = true;
	public static boolean commtoFiles = false;
	private  boolean printDetails = false;
	//
	// Constructors
	//

	public LogCompetence(BasicCompetentAgent ag) throws UnrespectedCompetenceSyntaxException {
		super(ag);
	}

	//
	// Methods
	//

	/******************
	 * LOG DEMAND
	 */

	// MONOLOGUE

	@Override
	public Boolean  logMonologue(final String text) {
		LogNotification log = new LogMonologue(getIdentifier(),text);
		if (monotoScreen)
			System.out.println(log.generateLogToScreen(printDetails));
		if (toFiles)
			return this.getMyAgent().notify(log,logKey);
		return true;
	}

	@Override
	public Boolean  logMonologue(final String text, final String details) {
		LogNotification log = new LogMonologue(getIdentifier(),text,details);
		if (monotoScreen)
			System.out.println(log.generateLogToScreen(printDetails));
		//			System.out.println("*** * From "+this.getMyAgent().getIdentifier()
		//					+ ":\n       ----> "+text+" ("+details+")");
		if (toFiles)
			return this.getMyAgent().notify(log,logKey);
		return true;
	}

	// Communication

	public Boolean logCommunication(Message am, MessageStatus s){
		if (!(am instanceof NotificationMessage && ((NotificationMessage)am).getNotification() instanceof LogNotification) && (commtoScreen||commtoFiles)){
			LogNotification log = new LogCommunication(getIdentifier(), am, s);
			if (commtoScreen)
				System.out.println(log.generateLogToScreen(printDetails));
			if (commtoFiles)
				return this.notify(log,logKey);
		}
		return true;
	}
	//EXCEPTION


	@Override
	public Boolean logException(final String text) {
		LogNotification log = new LogException(getIdentifier(),text);
		if (exceptoScreen)
			System.err.println(log.generateLogToScreen(printDetails));
		//			System.err.println("*** * From "+this.getMyAgent().getIdentifier()
		//					+"!!!!EXCEPTION!!!!:\n       ----> "+text);
		if (toFiles)
			return this.notify(log,logKey);
		return true;
	}

	@Override
	public Boolean logException(final String text, final Exception e) {
		LogNotification log = new LogException(getIdentifier(),text,e);
		if (exceptoScreen){
			System.err.println(log.generateLogToScreen(printDetails));
			e.printStackTrace();
			//			System.err.println("From "+this.getMyAgent().getIdentifier()
			//					+"!!!!EXCEPTION!!!!:\n       ----> "+text);
			e.printStackTrace();
		} 
		if (toFiles)
			return this.notify(log,logKey);
		return true;
	}

	@Override
	public Boolean logException(final String text, final String details) {
		LogNotification log = new LogException(getIdentifier(),text,details);
		if (exceptoScreen)
			System.err.println(log.generateLogToScreen(printDetails));
		//			System.err.println("From "+this.getMyAgent().getIdentifier()
		//					+"!!!!EXCEPTION!!!!:\n       ----> "+text+" ("+details+")");
		if (toFiles)
			return this.notify(log,logKey);
		return true;
	}

	@Override
	public Boolean  logException(final String text, final String details,
			final Exception e) {
		LogNotification log = new LogException(getIdentifier(),text,details,e);
		if (exceptoScreen){
			System.err.println(log.generateLogToScreen(printDetails));
			e.printStackTrace();
			//			System.err.println("*** * From "+this.getMyAgent().getIdentifier()
			//					+"!!!!EXCEPTION!!!!:\n       ----> "+text);
		}if (toFiles)
			return this.notify(log,logKey);
		return true;
	}

	// WARNING

	@Override
	public Boolean logWarning(final String text) {
		LogNotification log = new LogWarning(getIdentifier(),text);
		if (exceptoScreen)
			System.err.println(log.generateLogToScreen(printDetails));
		//					System.err.println("*** * From "+this.getMyAgent().getIdentifier()
		//							+"!!!!WARNING!!!!:\n       ----> "+text);
		if (toFiles)
			return this.notify(log,logKey);
		return true;
	}
	@Override
	public Boolean logWarning(final String text, final Exception e) {
		LogNotification log = new LogWarning(getIdentifier(),text,e);
		if (exceptoScreen){
			System.err.println(log.generateLogToScreen(printDetails));
			//			System.err.println("*** * From "+this.getMyAgent().getIdentifier()
			//					+"!!!!WARNING!!!!:\n       ----> "+text);
			e.printStackTrace();
		}if (toFiles)
			return this.notify(log,logKey);
		return true;
	}

	@Override
	public Boolean logWarning(final String text, final String details) {
		LogNotification log = new LogWarning(getIdentifier(),text,details);
		if (exceptoScreen)
			System.err.println(log.generateLogToScreen(printDetails));
		//			System.err.println("*** * From "+this.getMyAgent().getIdentifier()
		//					+"!!!!WARNING!!!!:\n       ----> "+text+" ("+details+")");
		if (toFiles)
			return this.notify(log,logKey);
		return true;
	}

	@Override
	public Boolean  logWarning(final String text, final String details,
			final Exception e) {
		LogNotification log = new LogWarning(getIdentifier(),text,details,e);
		if (exceptoScreen){
			System.err.println(log.generateLogToScreen(printDetails));
			//			System.err.println("*** * From "+this.getMyAgent().getIdentifier()
			//					+"!!!!WARNING!!!!:\n       ----> "+text);
			e.printStackTrace();
		}
		if (toFiles)
			return this.notify(log,logKey);
		return true;
	}	

	/******************
	 * LOG WRITING
	 */

	@NotificationEnvelope(logKey)
	@MessageHandler
	public void receiveLogNotif(NotificationMessage<LogNotification> n){
		if (!logSetted)
			setLogConfiguration();

		LogNotification log = n.getNotification();
		if (log instanceof LogMonologue){
			File agentFile = new File(getMyPath()+log.getCaller()+".log");
			logOnFile(agentFile,log.generateLogToWrite(printDetails),false,false);
			logOnFile(myAllLogFile,log.generateLogToWrite(printDetails),false,false);
		} else if (log instanceof LogCommunication){
			logOnFile(myMessageLogFile,log.generateLogToWrite(printDetails),false,false);
			logOnFile(myAllLogFile,log.generateLogToWrite(printDetails),false,false);
		} else if (log instanceof LogException){
			logOnFile(myExceptionLogFile,log.generateLogToWrite(printDetails),false,((LogException) log).getException());
			logOnFile(myAllLogFile,log.generateLogToWrite(printDetails),false,((LogException) log).getException());		
		}

	}

	//
	// Static methods
	//


	public static void write(final Object caller, final String text) {
		System.out.println("\n" + caller + " SAY >>>> \n" + text);
	}
	public synchronized static void write(final String text) {
		System.out.println(text);
	}
	//	public synchronized static void write(final Object component,
	//			final String text) {
	//		System.out.println(component+"on ("+Darx.getMyURL()+" "+Darx.getMyPortNb()+")  >>> "+text); //("+component.getClass()+")\n
	//	}
	public synchronized static void writeWarning(final Object component,
			final String text) {
		System.err.println("!!!!WARNING!!!!\n "+component+" ("+component.getClass()+")\n >>> "+text);
	}
	/*
	 * Exceptions
	 */
	public synchronized static void writeException(final String text) {
		System.err.println("!!!!EXCEPTION!!!!\n"+text);
	}
	public synchronized static void writeException(final String text,
			final Throwable e) {
		System.err.println("!!!!EXCEPTION!!!!\n"+text);
		e.printStackTrace();
	}
	public synchronized static void writeException(final Object component,
			final String text) {
		System.err.println("!!!!EXCEPTION!!!!\n "+component+" ("+component.getClass()+")\n >>> "+text);
	}
	public synchronized static void writeException(final Object component, final String text,
			final Throwable e) {
		System.err.println("!!!!EXCEPTION!!!!\n "+component+" ("+component.getClass()+")\n >>> "+text+ " caused by " + e);
		e.printStackTrace();
//		if (e!=null)
//			e.printStackTrace();
	}
	public synchronized static void flush(){
		System.out.flush();
		System.err.flush();
	}
	//
	// Writing Primitives
	//

	public static String getDimaXDir() {
		if (System.getProperty("user.dir").endsWith("/bin") || System.getProperty("user.dir").endsWith("/src"))
			return
					System.getProperty("user.dir").substring
					(0,	System.getProperty("user.dir").length() - 4)+"/";
		else
			return
					System.getProperty("user.dir")+"/";
	}

	public static synchronized void logOnFile(final File output, final String text,
			final boolean toScreen, final boolean red) {
		FileWriter logOut;
		PrintWriter pw;
		// BufferedWriter pw;
		try {
			logOut = new FileWriter(output, true);
			pw = new PrintWriter(logOut);// , true); => true pour flusher
			// pw = new BufferedWriter(logOut);//, true); => true pour flusher

			pw.println(text);
			pw.println();

			pw.flush();
			pw.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		if (toScreen)
			if (!red)
				System.out.println("\n"+text);
			else
				System.err.println("\n"+text);

	}

	public static synchronized void logOnFile(final File output, final String text, final boolean toScreen,
			final Throwable e) {
		FileWriter logOut;
		PrintWriter pw;
		// BufferedWriter pw;
		try {
			logOut = new FileWriter(output, true);
			pw = new PrintWriter(logOut);// , true); => true pour flusher
			// pw = new BufferedWriter(logOut);//, true); => true pour flusher

			pw.println(text);
			pw.println();

			pw.flush();
			pw.close();
			if (e!=null)
				e.printStackTrace(pw);
		} catch (final IOException io) {
			io.printStackTrace();
		}
		if (toScreen){
			System.err.println("\n"+text);
			e.printStackTrace();
		}
	}

	private boolean logSetted=false;
	private static String myPath =(getDimaXDir()+"log/"+(new Date()).toString().replace(" ", "_").replace(":", "-") +"/").replaceAll(":", "_");
	//myPath = getDimaXDir()+"log/"+getHostIdentifier()+"#"+DimaXServer.getCreationTime()+"/";	
	private  File myExceptionLogFile;
	private  File myMessageLogFile;
//	private  File myInfoLogFile;
	private  File myAllLogFile;

	protected void setLogConfiguration() {
		new File(getMyPath()).mkdirs();	

//		myInfoLogFile = new File(getMyPath() + "__INFO.log");
		myMessageLogFile = new File(getMyPath() + "__Messages.log");
		myExceptionLogFile = new File(getMyPath() + "__ERREUR.log");
		myAllLogFile = new File(getMyPath() + "__ALL.log");

		logSetted=true;
	}

	public static String getMyPath() {
		return myPath;
	}

}







//
//
//
//
///**********************************************************************
// *                                                                    *
// *                                                                    *
// *  Ces attributs permettent d'activer/désactiver l'écriture du log   *
// *                                                                    *
// *                                                                    *
// **********************************************************************/
//private static boolean globalInfoActivated = false;
//private static boolean dimaInfoActivated = true;
//private static boolean agentInfoActivated = true;
//private static boolean monitorInfoActivated = true;
//private static boolean managerInfoActivated = true;
//private static boolean observerInfoActivated = false;
//private static boolean messageSendActivated = false;
//private static boolean messageReceiveActivated = false;
//private static boolean exceptionActivated = true;
//private static boolean printExceptionDetails = true;
//private static boolean printExceptionToScreen = true;
//private static boolean printToScreen = true;
//private static boolean printDetails = false;
//private static boolean printAllToScreen = true;
//
//
//public void observeLogOf(HostIdentifier host) {
//	observe(host, LogMessage.class);
//}
//
///**********************************************************************
// *                                                                    *
// *                                                                    *
// *                Logger                                              *
// *                                                                    *
// *                                                                    *
// **********************************************************************/
//private static final long serialVersionUID = 5314450495396254287L;
//
//static HostIdentifier localhost = DimaXServer.getServerManager().getHostIdentifier();
//
//protected static boolean log = false;
//private static String myPath;
//private static String myAgentPath;
//private static String myMonitorPath;
//private static String myHostPath;
//private static String myObserverPath;
//private static String myMonitorInfoPath;
//private static File myMonitorInfoFile;
//private static String myInfoLogPath;
//private static File myInfoLogFile;
//private static String myDIMAInfoLogPath;
//private static File myDIMAInfoLogFile;
//private static String myMessageLogPath;
//private static File myMessageLogFile;
//private static String myExceptionLogPath;
//private static File myExceptionLogFile;
//private static File myToScreenLogFile;
//
//
//public Logger(HostIdentifier agent) {
//	super(agent);
//	this.setLogConfiguration();
//}
//
//public static String getPath() {
//	return myPath;
//}
//
//protected void setLogConfiguration() {
//	myPath = getDimaXDir()+"log/"+getHostIdentifier()+"#"+DimaXServer.getCreationTime()+"/";
//	myPath = myPath.replaceAll(":", "_");
//	myPath = myPath.replaceAll(HostIdentifier.managerPrefixName, "");
//	myAgentPath = myPath + "Agents/";
//	myMonitorPath = myPath + "Agents/Monitor/";
//	myHostPath = myPath + "Host/";
//	myObserverPath = myPath + "Observer/";
//
//	myMonitorInfoPath = myMonitorPath + "MONITOR_INFO.log";
//	myInfoLogPath = myPath + "INFO.log";
//	myDIMAInfoLogPath = myPath + "DIMA_INFO.log";
//	myMessageLogPath = myPath + "Messages.log";
//	myExceptionLogPath = myPath + "ERREUR.log";
//
//	myMonitorInfoFile = new File(myMonitorInfoPath);
//	myInfoLogFile = new File(myInfoLogPath);
//	myDIMAInfoLogFile = new File(myDIMAInfoLogPath);
//	myMessageLogFile = new File(myMessageLogPath);
//	myExceptionLogFile = new File(myExceptionLogPath);
//	myToScreenLogFile = new File(myPath + "ToScreen.log");
//
//	if (exceptionActivated || globalInfoActivated || messageSendActivated || messageReceiveActivated || dimaInfoActivated || printToScreen)
//		new File(myPath).mkdir();
//	if (agentInfoActivated)
//		new File(myAgentPath).mkdir();
//	if (monitorInfoActivated)
//		new File(myMonitorPath).mkdirs();
//	if (managerInfoActivated)
//		new File(myHostPath).mkdirs();
//	if (observerInfoActivated)
//		new File(myObserverPath).mkdirs();
//
//	//		 System.out.println(System.getProperty("user.dir"));
//	//		 System.out.println(myPath);
//	// System.out.println(myConversationPath);
//	// System.out.println(myDarXPath);
//	//		if (!ok) {
//	//			System.err
//	//			.println("Classe tools.Logger : Erreur dans la construction du Logger");
//	//			 System.out.println(System.getProperty("user.dir"));
//	//			 System.out.println(myPath);
//	//			 System.out.println(myConversationPath);
//	//			 System.out.println(myMonitorPath);
//	//			 System.out.println(myHostPath);
//	//			System.exit(-1);
//	//		}
//}
//
//public static String getDimaXDir() {
//	if (System.getProperty("user.dir").endsWith("/bin") || System.getProperty("user.dir").endsWith("/src"))
//		return
//		System.getProperty("user.dir").substring
//		(0,	System.getProperty("user.dir").length() - 4)+"/";
//	else
//		return
//		System.getProperty("user.dir")+"/";
//}
//
//public static void printLoggerPathInfo() {
//	System.out.println("   * LOGGER *");
//	System.out.println("Les logs de cette session sont écrit dans : ");
//	System.out.println(" ******* " + myPath);
//	System.out.println();
//}
//
//public static void printEndInfo() {}
//
//public enum MessageStatus{Send , Received}
//
///**
// * Permet d'activer ou de désactiver les différent log
// *
// * @param conversationInfoActivated
// * @param darXInfoActivated
// * @param infoActivated
// * @param dimaInfoActivated
// * @param exceptionActivated
// */
//public static void setLogActivated(boolean conversationInfoActivated, boolean darXInfoActivated,
//		boolean infoActivated, boolean dimaInfoActivated, boolean exceptionActivated) {
//	Logger.agentInfoActivated = conversationInfoActivated;
//	Logger.managerInfoActivated = darXInfoActivated;
//	Logger.globalInfoActivated = infoActivated;
//	Logger.dimaInfoActivated = dimaInfoActivated;
//	Logger.exceptionActivated = exceptionActivated;
//}
//
///**
// * Permet d'activer ou de désactiver l'impression des détails et des piles
// * d'appel des exceptions
// *
// * @param details
// * @param exceptionDetails
// */
//public static void setDetailActivated(boolean details, boolean exceptionDetails) {
//	Logger.printDetails = details;
//	Logger.printExceptionToScreen = exceptionDetails;
//}
//
//@MessageHandler
//public void parseLog(LogMessage m){
//	if (m.isException())
//		log(m.getOutput(), m.getText(), m.isOnScreen(), m.getException());
//	else
//		log(m.getOutput(), m.getText(), m.isOnScreen(), m.isRed());
//}
//
///*
// *
// *
// *
// *  Static Methods
// *
// *
// *
// *
// *
// */
//
///**
// * Imprime des infos envoyé par les agent sur un canal commun (permet de
// * visualiser la séquencialité des actions)
// *
// * @param caller
// * @param text
// */
//public static void info(Object caller,  String text) {
//	if (globalInfoActivated)
//		log(myInfoLogFile, createTemplate1(caller, localhost, text, ""), printAllToScreen, false);
//}
//
///**
// * Imprime des infos envoyé par les monitor sur un canal commun (permet de
// * visualiser la séquencialité des actions)
// *
// * @param caller
// * @param text
// */
//public static void infoFromMonitor(Object caller, String text) {
//	if (monitorInfoActivated)
//		log(myMonitorInfoFile, createTemplate1(caller, localhost, text, ""), printAllToScreen, false);
//}
//
///**
// * Imprime des infos envoyé par DimaX sur un canal commun (permet de
// * visualiser la séquencialité des actions)
// *
// * @param caller
// * @param text
// */
//public static void fromDimaX(String text) {
//	if (dimaInfoActivated)
//		log(myDIMAInfoLogFile, createTemplate4Monologue(text, localhost, ""), printAllToScreen, false);
//}
//
//public static void message(Message m, MessageStatus s) {
//	switch(s){
//	case Send :
//		if (messageSendActivated)
//			log(myMessageLogFile, createTemplate4Message(m,s), printAllToScreen, false);
//		break;
//	case Received :
//		if (messageReceiveActivated)
//			log(myMessageLogFile, createTemplate4Message(m,s), printAllToScreen, false);
//		break;
//	default :
//		break;
//	}
//}
//
//public static void fromObserver(Object caller, String text) {
//	if (observerInfoActivated) {
//		File agentFile = new File(
//				myObserverPath+
//				caller+".log");
//		log(agentFile, createTemplate1(caller,localhost, "OBSERVER AGENT:\n"+text, ""), printAllToScreen, false);
//	}
//}
//
///**
// * Imprime des infos envoyé par les hôtes das un fichier par hôtes
// *
// * @param caller
// * @param text
// */
//public static void fromHost(HostManager caller,  String text) {
//	if (managerInfoActivated) {
//		File hostFile =
//			new File(myHostPath + caller.getHostIdentifier().getFullId() + ".log");
//		log(
//				hostFile, createTemplate4Monologue(text, localhost, ""),
//				printAllToScreen, false);
//	}
//}
//
///**
// * Imprime des infos envoyé par les agent dans un fichier par agent NB : Il
// * faut réécrire écriture pour écrire beaucoup moins d'informations
// *
// * @param basicMonitoredAgent
// * @param text
// */
//public static void fromAgent(BasicMonitoredAgent basicMonitoredAgent,  String text) {
//	if (agentInfoActivated) {
//		File agentFile = new File(
//				myAgentPath+
//				basicMonitoredAgent.getIdentifier()+".log");
//		log(agentFile,
//				createTemplate4Monologue(
//						basicMonitoredAgent.getIdentifier().getFullId()+":\n"+
//						text, localhost, ""), printAllToScreen, false);
//	}
//}
//
//public static void fromAgentMonitor(Object caller, String text) {
//	if (monitorInfoActivated) {
//		File agentFile = new File(
//				myMonitorPath+
//				caller.toString()+".log");
//		log(agentFile, createTemplate4Monologue(text, localhost, ""), printAllToScreen, false);
//	}
//}
//
///**
// * Imprime les exception dans un fichier à part Si printExceptionDetails est
// * à true, la pile d'appel de l'exception est sauvegarder aussi
// *
// * @param classeAppelante
// * @param text
// * @param e
// */
//public static void exception(Object classeAppelante, String text, Throwable e) {
//	if (exceptionActivated)
//		if (printExceptionDetails)
//			Logger.log(myExceptionLogFile,
//					"EXCEPTION from "+classeAppelante+" ("+new Date()+"):\n"+text, printExceptionToScreen, e);
//		else
//			Logger.log(myExceptionLogFile,
//					"EXCEPTION from "+classeAppelante+" ("+new Date()+"):\n"+text, printExceptionToScreen, true);
//}
//
///**
// * Imprime un message dans le fichier d'exception
// *
// * @param caller
// * @param text
// */
//public static void exception(Object caller, String text) {
//	if (exceptionActivated)
//		log(myExceptionLogFile, createTemplate4Exception(caller, localhost, text, ""), printExceptionToScreen, true);
//}
//
//public static void soFar() {
//	toScreen("So Far ...");
//}
//
//public static void soFar(String comment) {
//	toScreen("So Far ... (from " + comment + ")");
//}
//
//public static void soFar(int number) {
//	toScreen("So Far (" + number + ") ... ");
//}
//
//public static void soGood() {
//	toScreen("... So Good!");
//}
//
//public static void toScreen(String text) {
//	if (printToScreen)
//		log(myToScreenLogFile,createTemplate4Monologue(text, localhost, ""), true, false);
//}
//
///*
// *
// *
// *
// * WRITING PRIMITIVES
// *
// *
// */
//
//private static synchronized void log(File output, String text,
//		boolean toScreen, boolean red) {
//	if (log){
//		FileWriter logOut;
//		PrintWriter pw;
//		// BufferedWriter pw;
//		try {
//			logOut = new FileWriter(output, true);
//			pw = new PrintWriter(logOut);// , true); => true pour flusher
//			// pw = new BufferedWriter(logOut);//, true); => true pour flusher
//
//			pw.println(text);
//			pw.println();
//
//			pw.flush();
//			pw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		if (toScreen)
//			if (!red)
//				System.out.println("\n"+text);
//			else
//				System.err.println("\n"+text);
//	} else{
//		LogMessage m = new LogMessage(output, text, toScreen, red);
//		DimaXServer.getServerManager().addNotificationToSend(m);
//	}
//}
//
//private static synchronized void log(File output, String text, boolean toScreen,
//		Throwable e) {
//	if (log) {
//		FileWriter logOut;
//		PrintWriter pw;
//		// BufferedWriter pw;
//		try {
//			logOut = new FileWriter(output, true);
//			pw = new PrintWriter(logOut);// , true); => true pour flusher
//			// pw = new BufferedWriter(logOut);//, true); => true pour flusher
//
//			pw.println(text);
//			pw.println();
//
//			pw.flush();
//			pw.close();
//			e.printStackTrace(pw);
//		} catch (IOException io) {
//			io.printStackTrace();
//		}
//		if (toScreen){
//			System.err.println("\n"+text);
//			e.printStackTrace();
//		}
//	} else{
//		LogMessage m = new LogMessage(output, text, toScreen, e);
//		DimaXServer.getServerManager().addNotificationToSend(m);
//	}
//}
//
///*
// *
// * TEMPLATE
// *
// *
// */
//
//private static String createTemplate1(Object caller, HostIdentifier h, String text, String detail) {
//	Date date = new Date();
//
//	String result =
//		"** On " + date.toString()+" ("+date.getTime()+"):\n"+
//		"* FROM HOST " + h.getFullId()+" :"+"\n"+
//		"* ===> " +caller.toString()+"("+caller.getClass()+ ") :\n"
//		+"* -------> "+text;
//	if (printDetails)
//		result+="\n"+detail;
//	return result;
//}
//
//private static String createTemplate4Monologue(String text, HostIdentifier h, String detail) {
//	Date date = new Date();
//	return
//	"** On " + date.toString()+" ("+date.getTime()+"):\n"+
//	"* FROM HOST " +h.getFullId()+" :"+"\n"+
//	text+(!detail.equals("")&&printDetails?" : ("+detail+")":"")+"\n";
//}
//
//private static String createTemplate4Exception(Object caller, HostIdentifier h, String text,
//		String detail) {
//	Date date = new Date();
//
//	String result =
//		"** On " + date.toString()+" ("+date.getTime()+"):\n"+
//		"* FROM HOST " + h.getFullId()+" :"+"\n"+
//		"* EXCEPTION FROM " + caller.getClass() + ",\n* * "+caller.toString()+" :\n* -------> "+text;
//	if (printDetails)
//		result+="\n"+detail;
//	return result;
//}
//
//private static String createTemplate4Message(Message m, MessageStatus s) {
//	Date date = new Date();
//	AgentIdentifier agent = s.equals(MessageStatus.Send)?m.getSender():m.getReceiver();
//	String result =
//		"*** On "+date+" ("+date.getTime()+"):\n"+
//		" * "+agent.getFullId()+" :\n * New Message "+s+" *\n"+
//		m.toString();
//	return result;
//}

