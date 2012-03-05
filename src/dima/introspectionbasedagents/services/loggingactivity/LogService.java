package dima.introspectionbasedagents.services.loggingactivity;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.AbstractMessage;
import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.ActiveComponentInterface;
import dima.basicinterfaces.DimaComponentInterface;
import dima.basicinterfaces.MailBoxBasedCommunicatingComponentInterface;
import dima.introspectionbasedagents.CommunicatingCompetentComponent;
import dima.introspectionbasedagents.CompetentComponent;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.loggingactivity.LogCommunication.MessageStatus;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.shells.BasicCompetenceShell;
import dima.introspectionbasedagents.shells.MethodHandler;
import dima.introspectionbasedagents.shells.NotReadyException;
import dima.introspectionbasedagents.shells.SimpleAgentStatus;
import dima.introspectionbasedagents.shells.SimpleExceptionHandler;
/**
 * OLD :
 * Le LoggerManager est chargé d'écrire et d'afficher les logs d'activité des
 * agents de l'application. Il charge depuis un fichier xml verifiant log.dtd
 * qui définit qu'elles sont les destination sur lesquelles il peut écrire Il
 * s'abonne
 *
 * @author Sylvain Ductor
 */
public final class LogService<Agent extends CommunicatingCompetentComponent & MailBoxBasedCommunicatingComponentInterface> extends SimpleExceptionHandler
implements AgentCompetence<Agent>, CompetentComponent{
	private static final long serialVersionUID = -4511578003487049832L;


	/***********************************************************************
	 * *********************************************************************
	 *//***********************************************************************
	 * *********************************************************************
	 */

	//Order or the log to be written to screen
	public  boolean activateCommtoScreen = false;
	public  boolean activateExceptoScreen = true;
	public static  Boolean activateMonotoScreen = null;
	//Order or the log to be written in specific files
	public static Boolean activateMonoToFiles = null;
	public boolean activateCommtoFiles = false;
	public  boolean activateExceptoFile = true;

	/***********************************************************************
	 * *********************************************************************
	 *//***********************************************************************
	 * *********************************************************************
	 */


	//
	// Fields
	//

	public static final String logNotificationKey = "log notification for the writer";
	public Map<String, Boolean> keysToScreen=new HashMap<String, Boolean>();
	public Map<String, Boolean> keysToFiles=new HashMap<String, Boolean>();

	public static final String onScreen = "print on screen";
	public static final String onFile = "print on file";
	public static final String onBoth = "print on screen and on file";
	public static final String onNone = "print on none";

	public static final String darxKey = "print from darx!!!";

	//
	// Constructors
	//

	public LogService(final Agent myComponent) throws UnrespectedCompetenceSyntaxException {
		this.setMyAgent(myComponent);
		this.addLogKey(LogService.onScreen,true,false);
		this.addLogKey(LogService.onFile,false,true);
		this.addLogKey(LogService.onBoth,true,true);
		this.addLogKey(LogService.onNone,false,false);
		this.addLogKey(LogService.darxKey,false,true);
	}
	static {
		LogService.setLogConfiguration();
	}
	//
	// Accessors
	//

	public static void setLog(boolean b) {
		activateMonotoScreen = b;
		activateMonoToFiles = b;
	}
	
	@Override
	public void addLogKey(final String key, final boolean toScreen, final boolean toFile){
		if (this.keysToScreen.put(key,toScreen)!=null || this.keysToFiles.put(key, toFile)!=null)
			this.logWarning("Already known key! "+key,LogService.onBoth);
		//		else
		//			System.out.println("will log "+key+" on : "+(toScreen?"screen ":" ")+(toFile?"file":""));
	}

	@Override
	public void setLogKey(final String key, final boolean toScreen, final boolean toFile) {
		this.keysToScreen.put(key,toScreen);
		this.keysToFiles.put(key, toFile);
	}
	public void setCommtoScreen(final boolean commtoScreen) {
		this.activateCommtoScreen = commtoScreen;
	}

	public void setExceptoScreen(final boolean exceptoScreen) {
		this.activateExceptoScreen = exceptoScreen;
	}

	public void setMonotoScreen(final boolean monotoScreen) {
		this.activateMonotoScreen = monotoScreen;
	}

	public void setToFiles(final boolean toFiles) {
		this.activateMonoToFiles = toFiles;
	}

	public void setCommtoFiles(final boolean commtoFiles) {
		this.activateCommtoFiles = commtoFiles;
	}

	public void setMyMessageLogFile(final File myMessageLogFile) {
		LogService.myMessageLogFile = myMessageLogFile;
	}

	//
	// Methods
	//

	/******************
	 * LOG DEMAND
	 */

	// MONOLOGUE

	//	@Override
	//	public Boolean  logMonologue(final String text) {
	//		LogNotification log = new LogMonologue(getIdentifier(),text);
	//		if (monotoScreen)
	//			System.out.println(log.generateLogToScreen());
	//		if (toFiles)
	//			return this.getMyAgent().notify(log,logNotificationKey);
	//		return true;
	//	}

	@Override
	public Boolean  logMonologue(final String text, final String key) {
		final LogNotification log = new LogMonologue(this.getIdentifier(),text);
		if (this.toScreen(key)&&this.activateMonotoScreen)
			System.out.println(log.generateLogToScreen());
		//			System.out.println("*** * From "+this.getMyAgent().getIdentifier()
		//					+ ":\n       ----> "+text+" ("+details+")");
		if (this.toFile(key)&&this.activateMonoToFiles)
			return this.getMyAgent().notify(log,LogService.logNotificationKey);
		return true;
	}

	// Communication

	public Boolean logCommunication(final Message am, final MessageStatus s){
		//		if (!(am instanceof LogNotification) ||
		//				(!(am instanceof NotificationMessage) && ((NotificationMessage) am).getNotification()  instanceof LogNotification)){
		//			LogNotification log = new LogCommunication(getIdentifier(), am, s);
		//			if (commtoScreen )
		//				System.out.println(log.generateLogToScreen());
		//			if (commtoFiles)
		//				return this.notify(log,logNotificationKey);
		//		}
		return true;
	}
	//EXCEPTION


	@Override
	public Boolean signalException(final String text) {
		final LogNotification log = new LogException(this.getIdentifier(),text);
		if (this.activateExceptoScreen)
			System.err.println(log.generateLogToScreen());
		//			System.err.println("*** * From "+this.getMyAgent().getIdentifier()
		//					+"!!!!EXCEPTION!!!!:\n       ----> "+text);
		if (this.activateExceptoFile)
			return this.notify(log,LogService.logNotificationKey);
		return true;
	}

	@Override
	public Boolean signalException(final String text, final Throwable e) {
		final LogNotification log = new LogException(this.getIdentifier(),text,e);
		if (this.activateExceptoScreen){
			System.err.println(log.generateLogToScreen());
			if (e!=null)
				e.printStackTrace();
			else
				System.err.println("exception is null!!!!");
			//			System.err.println("From "+this.getMyAgent().getIdentifier()
			//					+"!!!!EXCEPTION!!!!:\n       ----> "+text);
		}
		if (this.activateExceptoFile){
			this.notify(log,LogService.logNotificationKey);
			this.sendNotificationNow();
		}
		return true;
	}

	//	@Override
	//	public Boolean logException(final String text, final String key) {
	//		LogNotification log = new LogException(getIdentifier(),text);
	//		if (toScreen(key))
	//			System.err.println(log.generateLogToScreen());
	//		//			System.err.println("From "+this.getMyAgent().getIdentifier()
	//		//					+"!!!!EXCEPTION!!!!:\n       ----> "+text+" ("+details+")");
	//		if (toFile(key))
	//			return this.notify(log,logNotificationKey);
	//		return true;
	//	}
	//
	//	@Override
	//	public Boolean  logException(final String text, final String key,
	//			final Throwable e) {
	//		LogNotification log = new LogException(getIdentifier(),text,e);
	//		if (toScreen(key)){
	//			System.err.println(log.generateLogToScreen());
	//			if (e!=null)
	//			e.printStackTrace();
	//			else
	//				System.err.println("exception is null!!");
	//			//			System.err.println("*** * From "+this.getMyAgent().getIdentifier()
	//			//					+"!!!!EXCEPTION!!!!:\n       ----> "+text);
	//		}if (toFile(key)){
	//			this.notify(log,logNotificationKey);
	//			sendNotificationNow();
	//		}
	//		return true;
	//	}

	// WARNING
	//
	//	@Override
	//	public Boolean logWarning(final String text) {
	//		LogNotification log = new LogWarning(getIdentifier(),text);
	//		if (exceptoScreen)
	//			System.err.println(log.generateLogToScreen());
	//		//					System.err.println("*** * From "+this.getMyAgent().getIdentifier()
	//		//							+"!!!!WARNING!!!!:\n       ----> "+text);
	//		if (toFiles)
	//			return this.notify(log,logNotificationKey);
	//		return true;
	//	}
	//	@Override
	//	public Boolean logWarning(final String text, final Throwable e) {
	//		LogNotification log = new LogWarning(getIdentifier(),text,e);
	//		if (exceptoScreen){
	//			System.err.println(log.generateLogToScreen());
	//			//			System.err.println("*** * From "+this.getMyAgent().getIdentifier()
	//			//					+"!!!!WARNING!!!!:\n       ----> "+text);
	//			e.printStackTrace();
	//		}if (toFiles)
	//			return this.notify(log,logNotificationKey);
	//		return true;
	//	}

	@Override
	public Boolean logWarning(final String text, final String key) {
		final LogNotification log = new LogWarning(this.getIdentifier(),text);
		if (this.toScreen(key)&&this.activateExceptoScreen)
			System.err.println(log.generateLogToScreen());
		//			System.err.println("*** * From "+this.getMyAgent().getIdentifier()
		//					+"!!!!WARNING!!!!:\n       ----> "+text+" ("+details+")");
		if (this.toFile(key)&&this.activateExceptoFile)
			return this.notify(log,LogService.logNotificationKey);
		return true;
	}

	@Override
	public Boolean  logWarning(final String text,
			final Throwable e, final String key) {
		final LogNotification log = new LogWarning(this.getIdentifier(),text,e);
		if (this.toScreen(key)&&this.activateExceptoScreen){
			System.err.println(log.generateLogToScreen());
			//			System.err.println("*** * From "+this.getMyAgent().getIdentifier()
			//					+"!!!!WARNING!!!!:\n       ----> "+text);
			e.printStackTrace();
		}
		if (this.toFile(key)&&this.activateExceptoFile)
			return this.notify(log,LogService.logNotificationKey);
		return true;
	}

	/******************
	 * LOG WRITING
	 */

	@NotificationEnvelope(LogService.logNotificationKey)
	@MessageHandler
	public void receiveLogNotif(final NotificationMessage<LogNotification> n){
		final LogNotification log = n.getNotification();

		if (!LogService.logSetted)
			LogService.setLogConfiguration();

		if (log instanceof LogMonologue){
			final File agentFile = new File(LogService.getMyPath()+log.getCaller()+".log");
			LogService.logOnFile(agentFile,
					log.generateLogToWrite(),false,false);
			LogService.logOnFile(LogService.myAllLogFile,
					log.generateLogToWrite(),false,false);
		} else if (log instanceof LogCommunication){
			LogService.logOnFile(LogService.myMessageLogFile,
					log.generateLogToWrite(),false,false);
			LogService.logOnFile(LogService.myAllLogFile,
					log.generateLogToWrite(),false,false);
		} else if (log instanceof LogWarning){
			final File agentFile = new File(LogService.getMyPath()+log.getCaller()+".log");
			LogService.logOnFile(agentFile,log.generateLogToWrite(),false,((LogException) log).getException());
			LogService.logOnFile(LogService.myWarningLogFile,
					log.generateLogToWrite(),false,((LogException) log).getException());
			LogService.logOnFile(LogService.myAllLogFile,
					log.generateLogToWrite(),false,((LogException) log).getException());
		} else if (log instanceof LogException){
			final File agentFile = new File(LogService.getMyPath()+log.getCaller()+".log");
			LogService.logOnFile(agentFile,log.generateLogToWrite(),false,((LogException) log).getException());
			LogService.logOnFile(LogService.myExceptionLogFile,
					log.generateLogToWrite(),false,((LogException) log).getException());
			LogService.logOnFile(LogService.myAllLogFile,
					log.generateLogToWrite(),false,((LogException) log).getException());
		}

	}

	//
	// Static methods
	//


	public static void write(final Object caller, final String text) {
		System.out.println(caller + " SAY >>>> \n" + text);
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
			else
				pw.println("exception is null!!");
		} catch (final IOException io) {
			io.printStackTrace();
		}
		if (toScreen){
			System.err.println("\n"+text);
			e.printStackTrace();
		}
	}

	private static boolean logSetted=false;
	private static String myPath;
	//myPath = getDimaXDir()+"log/"+getHostIdentifier()+"#"+DimaXServer.getCreationTime()+"/";
	private static  File myExceptionLogFile;
	private static  File myWarningLogFile;
	private static  File myMessageLogFile;
	//	private  File myInfoLogFile;
	private static  File myAllLogFile;

	protected static void setLogConfiguration() {
		if (!LogService.logSetted){
			LogService.myPath =(LogService.getDimaXDir()+"log/"+(new Date()).toString().replace(" ", "_").replace(":", "-") +"/").replaceAll(":", "_");

			new File(LogService.getMyPath()).mkdirs();

			//		myInfoLogFile = new File(getMyPath() + "__INFO.log");
			LogService.myMessageLogFile = new File(LogService.getMyPath() + "__Messages.log");
			LogService.myExceptionLogFile = new File(LogService.getMyPath() + "__ERREUR.log");
			LogService.myWarningLogFile = new File(LogService.getMyPath() + "__WARNING.log");
			LogService.myAllLogFile = new File(LogService.getMyPath() + "__ALL.log");

			LogService.logSetted=true;
		}
	}

	public static String getMyPath() {
		return LogService.myPath;
	}

	//
	// Primitives
	//

	private boolean toScreen(final String key){
		if (this.keysToScreen.containsKey(key))
			return this.keysToScreen.get(key);
		else{
			this.logWarning("Unknown log key!!!!! "+key,LogService.onBoth);
			return false;
		}
	}

	private boolean toFile(final String key){
		if (this.keysToFiles.containsKey(key))
			return this.keysToFiles.get(key);
		else{
			this.logWarning("Unknown log key!!!!! "+key,LogService.onBoth);
			return false;
		}
	}

	//
	// Exception handling override
	//
	BasicCompetenceShell myAgentShell;

	public void setMyAgentShell(final BasicCompetenceShell myAgentShell) {
		this.myAgentShell = myAgentShell;
	}

	@Override
	protected String handleExceptionOnMessage(
			final DimaComponentInterface dimaComponentInterface,
			final MethodHandler methodHandler,
			final AbstractMessage abstractMessage,
			final Throwable e){
		this.signalException(
				"Method "+methodHandler+"\n on message "+abstractMessage.toString()
				+"\n has raised EXCEPTION :" , e);
		this.stopFaultyMethods(methodHandler);
		return super.handleExceptionOnMessage(dimaComponentInterface, methodHandler, abstractMessage, e);
	}

	@Override
	protected String handleExceptionOnMethod(
			final DimaComponentInterface dimaComponentInterface,
			final MethodHandler mt,
			final Throwable e){
		this.signalException(
				"Method "+mt.getMethodName()
				+"\n has raised EXCEPTION :" , e);
		this.stopFaultyMethods(mt);
		return super.handleExceptionOnMethod(dimaComponentInterface, mt, e);

	}

	public String handleExceptionOnHooks(final Throwable e,
			final SimpleAgentStatus status) {
		this.signalException(
				"Hook"
						+"\n(" + status+")"
						+"\n has raised EXCEPTION :" , e);
		return "Hook"
		+"\n(" + status+")"
		+"\n has raised an EXCEPTION :\n";
	}

	private void stopFaultyMethods(final MethodHandler m){
		//		getMyAgent().setActive(false);
		m.setActive(false);
		//		if (m.getMyComponent() instanceof AgentCompetence)
		//			myAgentShell.unload((AgentCompetence) m.getMyComponent());
		//		else
		//			myAgentShell.getMyMethods().removeMethod(m);
	}




	//
	// Competence methods (copy for BasicAgentCompetence et BasicAgentCommunicatingCompetence
	//

	//
	// Fields
	//

	Agent myAgent;

	boolean active=true;

	protected void sendMessage(final AgentIdentifier id, final Message m ){
		this.getMyAgent().sendMessage(id, m);
	}

	protected void sendMessage(final Collection<? extends AgentIdentifier> ids, final Message m){
		for (final AgentIdentifier id :ids)
			if (id!=this.myAgent.getIdentifier())
				this.sendMessage(id, m);
	}
	//
	// Accessors
	//

	@Override
	public AgentIdentifier getIdentifier(){
		return this.getMyAgent().getIdentifier();
	}

	@Override
	public void setMyAgent(final Agent ag) throws UnrespectedCompetenceSyntaxException {
		this.myAgent=ag;
	}

	@Override
	public Agent getMyAgent() {
		return this.myAgent;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void setActive(final boolean active) {
		this.active = active;
	}
	@Override
	public void die(){
		this.myAgent=null;
	}

	//
	// Methods
	//

	/*
	 * Hook
	 */

	@Override
	public boolean retryWhen(final AgentCompetence comp, final String methodToTest,
			final ActiveComponentInterface methodComponent, final Object[] testArgs,
			final Object[] methodsArgs) {
		return this.myAgent.retryWhen(comp, methodToTest, methodComponent, testArgs,
				methodsArgs);
	}

	@Override
	public boolean when(final AgentCompetence comp, final String compMethodToTest,
			final Class<?>[] compSignature, final Object[] compargs,
			final String agMethodToExecute, final Class<?>[] agSignature, final Object[] agargs) {
		return this.myAgent.when(comp, compMethodToTest, compSignature, compargs,
				agMethodToExecute, agSignature, agargs);
	}

	@Override
	public boolean when(final AgentCompetence comp, final String compMethodToTest,
			final Object[] compargs, final String agMethodToExecute, final Object[] agargs) {
		return this.myAgent.when(comp, compMethodToTest, compargs,
				agMethodToExecute, agargs);
	}

	@Override
	public boolean when(final AgentCompetence comp, final String compMethodToTest,
			final Object[] compargs, final String agMethodToExecute) {
		return this.myAgent
				.when(comp, compMethodToTest, compargs, agMethodToExecute);
	}

	@Override
	public boolean when(final AgentCompetence comp, final String compMethodToTest,
			final String agMethodToExecute, final Object[] agargs) {
		return this.myAgent.when(comp, compMethodToTest, agMethodToExecute, agargs);
	}

	@Override
	public boolean when(final AgentCompetence comp, final String compMethodToTest,
			final String agMethodToExecute) {
		return this.myAgent.when(comp, compMethodToTest, agMethodToExecute);
	}

	@Override
	public boolean retryWhen(final AgentCompetence comp, final String methodToTest,
			final Object[] testArgs, final Object[] methodsArgs) {
		return this.myAgent.retryWhen(comp, methodToTest, this, testArgs, methodsArgs);
	}

	@Override
	public boolean whenIsReady(final NotReadyException e) {
		return this.myAgent.whenIsReady(e);
	}

	/*
	 * Observation
	 */

	@Override
	public <Notification extends Serializable> Boolean notify(final Notification notification, final String key) {
		return this.myAgent.notify(notification, key);
	}

	@Override
	public <Notification extends Serializable> Boolean notify(final Notification notification) {
		return this.myAgent.notify(notification);
	}

	@Override
	public Boolean addToBlackList(final AgentIdentifier o, final Boolean add) {
		return this.myAgent.addToBlackList(o, add);
	}

	@Override
	public void observe(final AgentIdentifier observedAgent, final Class<?> notificationKey) {
		this.myAgent.observe(observedAgent, notificationKey);
	}

	@Override
	public void observe(final AgentIdentifier observedAgent, final String notificationToObserve) {
		this.myAgent.observe(observedAgent, notificationToObserve);
	}

	@Override
	public void stopObservation(final AgentIdentifier observedAgent, final Class<?> notificationKey) {
		this.myAgent.stopObservation(observedAgent, notificationKey);
	}

	@Override
	public void stopObservation(final AgentIdentifier observedAgent, final String notificationToObserve) {
		this.myAgent.stopObservation(observedAgent, notificationToObserve);
	}

	@Override
	public void autoObserve(final Class<?> notificationKey) {
		this.myAgent.autoObserve(notificationKey);
	}

	@Override
	public void addObserver(final AgentIdentifier observerAgent,
			final Class<?> notificationKey) {
		this.myAgent.addObserver(observerAgent, notificationKey);
	}

	@Override
	public void addObserver(final AgentIdentifier observerAgent,
			final String notificationKey) {
		this.myAgent.addObserver(observerAgent, notificationKey);
	}

	@Override
	public void removeObserver(final AgentIdentifier observerAgent,
			final Class<?> notificationKey) {
		this.myAgent.removeObserver(observerAgent, notificationKey);
	}

	@Override
	public void removeObserver(final AgentIdentifier observerAgent,
			final String notificationKey) {
		this.myAgent.removeObserver(observerAgent, notificationKey);
	}

	/*
	 *
	 */
	@Override
	public Boolean isObserved(final Class<?> notificationKey) {
		return this.myAgent.isObserved(notificationKey);
	}

	@Override
	public Collection<AgentIdentifier> getObservers(final Class<?> notificationKey) {
		return this.myAgent.getObservers(notificationKey);
	}

	@Override
	public void sendNotificationNow() {
		this.myAgent.sendNotificationNow();
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

