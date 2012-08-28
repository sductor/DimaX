package dima.introspectionbasedagents.kernel.shells;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dima.basiccommunicationcomponents.AbstractMailBox;
import dima.basiccommunicationcomponents.AbstractMessage;
import dima.basiccommunicationcomponents.Message;
import dima.basiccommunicationcomponents.SimpleMailBox;
import dima.basicinterfaces.DimaComponentInterface;
import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.CompetenceProtocol;
import dima.introspectionbasedagents.coreservices.loggingactivity.LoggerManager;
import dima.introspectionbasedagents.kernel.competences.AgentCompetence;
import dima.introspectionbasedagents.kernel.competences.DuplicateCompetenceException;
import dima.introspectionbasedagents.kernel.competences.UnInstanciableCompetenceException;
import dima.introspectionbasedagents.kernel.competences.UnknownCompetenceException;
import dima.introspectionbasedagents.kernel.tools.CompetenceExceptionHandler;
import dima.introspectionbasedagents.ontologies.MessageWithProtocol;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
import dima.support.GimaObject;

/**
 * The competence shell adds the handle of competences to an agent introspective shell
 *
 * @author Sylvain Ductor
 */
public class BasicCompetenceShell  extends
BasicCommunicatingShell {
	private static final long serialVersionUID = 87670953420119714L;


	//
	// Fields
	//

//	BasicCommunicatingMethodTrunk myAgentMethods;
//	final Date horloge;
//
//	Map<CompetenceIdentifier, IntrospectedMethodsTrunk> myCompetenceMethods =
//		new HashMap<CompetenceIdentifier, IntrospectedMethodsTrunk>();
//	Map<Class<? extends Protocol>, RoleBasedCommunicationMethodsHandler> CompetenceMessageMatcher =
//		new HashMap<Class<? extends Protocol>, RoleBasedCommunicationMethodsHandler>();
	
	
	Map<CompetenceIdentifier, Boolean> activatedCompetence =
		new HashMap<CompetenceIdentifier, Boolean>();
	Map<MethodHandler, MethodHandler> methodsHook =
		new HashMap<MethodHandler, MethodHandler>();

	//
	// Constructor
	//

	public BasicCompetenceShell(
			final IdentifiedComponentInterface myComponent, final Date horloge,
			final AbstractMailBox mailbox,
			final CompetenceExceptionHandler exceptionHandler) throws UnInstanciableCompetenceException, DuplicateCompetenceException{
		super(myComponent, horloge, mailbox, new CompetenceExceptionHandler());
//		this.myAgentMethods = this.getMyMethods();
		this.init(myComponent);
//		this.horloge = horloge;
		//		System.out.println();
		//		System.out.println();
		//		System.out.println();
		//		System.out.println("##########COMPETENCE SHELL INIT##################");
		//		System.out.println("############################");
		//		System.out.println("############################");
		//		System.out.println("Agent "+myComponent.getIdentifier());
		//		for (Class<? extends Protocol> p : CompetenceMessageMatcher.keySet()){
		//			System.out.println("------> Protocol "+p.getSimpleName());
		//			System.out.println(CompetenceMessageMatcher.get(p));
		//		}
	}

	public BasicCompetenceShell(final BasicCommunicatingAgent myComponent, final Date horloge) throws UnInstanciableCompetenceException, DuplicateCompetenceException {
		this(myComponent, horloge, myComponent.getMailBox(), new CompetenceExceptionHandler());
	}

	private void init(final IdentifiedComponentInterface myComponent) throws UnInstanciableCompetenceException, DuplicateCompetenceException{
		//System.out.println("Initiating shell of : "+myComponent+":\n"+IntrospectionPrimitives.getAllFields(myComponent.getClass()));
		for (final Field comp : IntrospectionStaticPrimitivesLibrary.getAllFields(myComponent.getClass()))
			if (this.fieldIsACompetence(comp)){
				//				System.out.println(comp.getName());

				///// IMPORTANT : CODE A REMETTRE
				//				if (!Modifier.isFinal(comp.getModifiers())){
				//					LoggerManager.writeWarning(getMyComponent(),
				//							"This competence '"+comp.getName()+"' is not final, it can not be used");
				//					throw new UnInstanciableCompetenceException(comp.getName());
				//				}

				AgentCompetence competence = null;
				try {
					competence = (AgentCompetence) comp.get(myComponent);
				} catch (final Exception e) {
					LoggerManager.writeException(myComponent,
							"Impossible!!", e);
					throw new UnInstanciableCompetenceException(comp.getName());
				}
				if (competence==null){
					LoggerManager.writeException(this.getMyComponent(),
							"This competence '"+comp.getName()
							+"' has not been instanciated, it can not be used");
					throw new UnInstanciableCompetenceException(comp.getName());
				}


				final CompetenceIdentifier competenceId = new CompetenceIdentifier(competence);
				//				System.out.println("Handling comp "+comp.getName()+" "+competence.getClass());

				final IntrospectedMethodsTrunk methods = new BasicIntrospectedMethodsTrunk(competence, this.horloge);
				methods.init();
				if (this.myCompetenceMethods.containsKey(competenceId)){
					System.err.println("duplicate comp : "+competenceId
					);
					throw new DuplicateCompetenceException();
				} else
					this.myCompetenceMethods.put(competenceId, methods);

				//				System.out.println("type de : " +comp.getName()+" : "+comp.getType());
				if (comp.getType().isAnnotationPresent(CompetenceProtocol.class))
					//					System.out.println(getMyComponent()+"  ----> protocole trouv� pour " +comp.getName());
					if (this.CompetenceMessageMatcher.containsKey(competenceId))//Un role est déja présent
						this.CompetenceMessageMatcher.get(competenceId).add(competence, this.horloge);
					else
						this.CompetenceMessageMatcher.put(competenceId.getProtocol(), new RoleBasedCommunicationMethodsHandler(competence, this.horloge));
				//				if (competence extends comm comp mais na pas de proto ajouté direct dans l'agent)
				this.activatedCompetence.put(competenceId, true);

				this.getExceptionHandler().setMyAgent(this.myAgentMethods);
			}
	}



	public void activateCompetence(final AgentCompetence competence, final boolean active) throws UnknownCompetenceException{
		try {
			if (this.activatedCompetence.containsKey(new CompetenceIdentifier(competence)))
				this.activatedCompetence.put(new CompetenceIdentifier(competence), active);
			else
				throw new UnknownCompetenceException();
		} catch (final Exception e) {
			LoggerManager.writeException(this.getMyComponent(), "ajout comp! impossible");
		}
	}

	/**
	 * Used to add a hook
	 *
	 * @param testComp
	 * @param testMethod
	 * @param testMethodSignature
	 * @param testMethodArguments
	 * @param executionMethod
	 * @param executionMethodSignature
	 * @param executionMethodArgument
	 * @return false if a hook already existed for this key
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 */
	public Boolean addHook(
			final DimaComponentInterface testComp,
			final String testMethod,
			final Class<?>[] testMethodSignature,
			final Object[] testMethodArguments,
			final DimaComponentInterface execComp,
			final String executionMethod,
			final Class<?>[] executionMethodSignature,
			final Object[] executionMethodArgument)
	throws SecurityException, IllegalArgumentException, NoSuchMethodException
	{

		final MethodHandler compTestMethod = new MethodHandler(
				testComp,//				getCompetenceMethods(comp),
				testMethod, testMethodSignature, testMethodArguments);
		final MethodHandler agExecMethod = new MethodHandler(
				execComp,
				executionMethod, executionMethodSignature, executionMethodArgument);

		return this.methodsHook.put(
				compTestMethod,
				agExecMethod) == null;
	}

	private DimaComponentInterface getCompetenceMethods(final AgentCompetence comp) {
		try{
			return this.myCompetenceMethods.get(new CompetenceIdentifier(comp)).getMyComponent();
		}catch (final Exception e){
			LoggerManager.writeException(this.getMyComponent(),  "unknown comp :"+ comp+"\n known comp :"+this.myCompetenceMethods.keySet(),e);
			return null;
		}
	}

	//
	// Methods
	//


	public void preActivity(){
		this.sortMails();
	}

	@Override
	public void step(){
		this.competencesStep();
		super.step();
	}

	private void competencesStep(){
		this.parseCompetenceMails();
		this.executeCompetences();
	}

	@Override
	public void postActivity(){
		this.executeHooks();
		super.postActivity();
	}

	public void proactivityTerminate(){
		for (final Field comp : IntrospectionStaticPrimitivesLibrary.getAllFields(this.getMyComponent().getClass()))
			if (this.fieldIsACompetence(comp)){
				AgentCompetence competence = null;
				try {
					competence = (AgentCompetence) comp.get(this.getMyComponent());
				} catch (final Exception e) {
					LoggerManager.writeException(this.getMyComponent(),
							"Impossible!!", e);
				}
				if (competence==null)
					LoggerManager.writeException(this.getMyComponent(),
							"This competence '"+comp.getName()
							+"' has not been instanciated, it can not be used");
				competence.die();
			}

		this.myCompetenceMethods.clear();
		this.CompetenceMessageMatcher.clear();
		this.myAgentMethods=null;
	}
	/*
	 * Step
	 */

	public void executeCompetences(){

		for (final CompetenceIdentifier comp : this.myCompetenceMethods.keySet())
			if (this.activatedCompetence.get(comp)){
				final IntrospectedMethodsTrunk componentMethods = this.myCompetenceMethods.get(comp);
				this.setMyMethods(componentMethods);
				this.getStatus().setCurrentlyExecutedAgent(componentMethods.getMyComponent());
				super.executeBehaviors();
				this.getStatus().resetCurrentlyExecutedAgent();
			}
		this.setMyMethods(this.myAgentMethods);
	}

	/*
	 * Message
	 */

	private Boolean parsingCompetenceMails = null;
	private final CompetenceMailBox sortedMailBox = new CompetenceMailBox();

	private void sortMails(){
		this.sortedMailBox.load(this.mailBox);
	}

	@Override
	public AbstractMailBox getMailBox() {
		if (this.parsingCompetenceMails == null){
			LoggerManager.writeException(this.getMyComponent(), "Impossible : not parsing mail ");
			return this.mailBox;
		} else if (this.parsingCompetenceMails)
			return this.sortedMailBox.competenceMails;
		else
			return this.sortedMailBox.agentMails;
	}

	@Override
	protected AbstractMessage getNextMail(){
		final Message mess = (Message) this.getMailBox().readMail();

		if (this.parsingCompetenceMails){
			//The message is delivered  to a competence:

			final CommunicationMethodsTrunk methods=
				this.CompetenceMessageMatcher.get(
						((MessageWithProtocol) mess).getProtocol()).getAgentOf(mess);

			if (methods!=null){
				this.setMyMethods(methods);
				this.getStatus().setCurrentlyExecutedAgent(methods.getMyComponent());
			}else
				System.err.println("SHELL : IMPOSSIBLE I've received a message of a unknown protocol : "
						+mess);

		}

		return mess;
	}

	@Override
	protected void parseMails() {
		this.parsingCompetenceMails=false;
		//The message is delivered to the agent:
		this.setMyMethods(this.myAgentMethods);
		this.getStatus().setCurrentlyExecutedAgent(this.myAgentMethods.getMyComponent());
		super.parseMails();//grace a la surcharge de get next mail
		this.getStatus().resetCurrentlyExecutedAgent();
		this.parsingCompetenceMails=null;
	}

	/**
	 * Les méthodes de la compétence approprié sont chargé par getNextMail()
	 */
	protected void parseCompetenceMails() {
		this.parsingCompetenceMails = true;
		super.parseMails();//grace a la surcharge de get next mail
		this.getStatus().resetCurrentlyExecutedAgent();
		this.parsingCompetenceMails=null;
	}

	//
	// Primitives
	//

	/*
	 * Hooks
	 */

	public void executeHooks(){
		try {
			for (final MethodHandler compM : this.methodsHook.keySet()){
				final Object resultatTest = compM.execute();
				if (resultatTest instanceof Boolean && (Boolean) resultatTest){
					final Object resultatAg = this.methodsHook.get(compM).execute();
					if (resultatAg instanceof Boolean && (Boolean) resultatAg)
						this.methodsHook.remove(compM);
				}
			}
		} catch (final InvocationTargetException e) {
			this.getExceptionHandler().handleExceptionOnHooks(e, this.getStatus());
		}
	}

	//
	// Subclass
	//

	private class CompetenceIdentifier extends GimaObject{//extends AgentName {
		private static final long serialVersionUID = -252244526609107537L;

		//Non communicating competence :
		private final Class<? extends AgentCompetence> className;

		//Communicating competence :
		private final Class<? extends Protocol> protocolName;
		//		private final Class<? extends ProtocolRole> roleName;

		public CompetenceIdentifier(final AgentCompetence comp) {
			//			super(comp.getClass().toString());
			if (comp.getClass().isAnnotationPresent(CompetenceProtocol.class)){
				this.protocolName = comp.getClass().getAnnotation(CompetenceProtocol.class).value();
				//				this.roleName= comp.getClass().getAnnotation(CompetenceProtocol.class).role();
				this.className=comp.getClass();
			}else {
				this.protocolName = null;
				//				this.roleName = null;
				this.className=comp.getClass();
			}
		}

		@Override
		public boolean equals(final Object o){
			if (o instanceof CompetenceIdentifier) {
				final CompetenceIdentifier that = (CompetenceIdentifier) o;


				if (this.protocolName==null)// && this.roleName==null)that.roleName ==null && 	&& that.roleName!=null && that.roleName.equals(this.roleName)
					return that.protocolName==null && this.className.equals(that.className);
				else
					return that.protocolName!=null && that.protocolName.equals(this.protocolName);

			} else
				return false;
		}

		//		public Class<? extends ProtocolRole> getRole(){
		//			return roleName;
		//		}

		public Class<? extends Protocol> getProtocol(){
			return this.protocolName;
		}

		@Override
		public int hashCode(){
			// On choisit les deux nombres impairs
			int result = 7;
			final int multiplier = 17;

			// Pour chaque attribut, on calcule le hashcode
			// que l'on ajoute au résultat après l'avoir multiplié
			// par le nombre "multiplieur" :
			result = multiplier*result + this.className.hashCode();
			result = multiplier*result + (this.protocolName==null ? 0 : this.protocolName.hashCode());
			//			result = multiplier*result + (this.roleName==null ? 0 : this.roleName.hashCode());

			// On retourne le résultat :
			return result;
		}

		@Override
		public String toString(){
			return "Competence "+this.className.getSimpleName()+ (this.protocolName==null ? "" :" of protocol "+this.protocolName.getSimpleName());
		}
	}

	private class CompetenceMailBox implements Serializable{

		/**
		 *
		 */
		private static final long serialVersionUID = 2428661829475771765L;
		SimpleMailBox competenceMails = new SimpleMailBox();
		SimpleMailBox agentMails = new SimpleMailBox();

		public void load(final AbstractMailBox b){
			while (b.hasMail()){
				final AbstractMessage mess = b.readMail();
				if (mess instanceof MessageWithProtocol
						&& BasicCompetenceShell.this.CompetenceMessageMatcher.containsKey(((MessageWithProtocol) mess).getProtocol()))
					//The message is delivered  to a competence:
					this.competenceMails.writeMail(mess);
				else
					//The message is delivered to the agent:
					this.agentMails.writeMail(mess);
			}
		}
	}
}




//
//	/**
//	 * Used to add a hook
//	 *
//	 * @param comp
//	 * @param competenceTestMethod
//	 * @param competenceTestMethodSignature
//	 * @param competenceTestMethodArguments
//	 * @param agentExecutionMethod
//	 * @param agentExecutionMethodSignature
//	 * @param agentExecutionMethodArgument
//	 * @return false if a hook already existed for this key
//	 * @throws SecurityException
//	 * @throws IllegalArgumentException
//	 * @throws NoSuchMethodException
//	 */
//	public Boolean when(
//			AgentCompetence comp,
//			String competenceTestMethod,
//			Class<?>[] competenceTestMethodSignature,
//			Object[] competenceTestMethodArguments,
//			AgentCompetence callerComp,
//			String agentExecutionMethod,
//			Class<?>[] agentExecutionMethodSignature,
//			Object[] agentExecutionMethodArgument)
//	throws SecurityException, IllegalArgumentException, NoSuchMethodException
//	{
//		final ConfiguredMethodHandler compTestMethod = new ConfiguredMethodHandler(
//				this.myCompetenceMethods.get(new CompetenceIdentifier(comp)).getMyComponent(),
//				competenceTestMethod, competenceTestMethodSignature, competenceTestMethodArguments);
//		final ConfiguredMethodHandler agExecMethod = new ConfiguredMethodHandler(
//				callerComp,
//				agentExecutionMethod, agentExecutionMethodSignature, agentExecutionMethodArgument);
//
//		return this.methodsHook.put(
//				compTestMethod,
//				agExecMethod) == null;
//	}
//
//	public Boolean when(
//			AgentCompetence comp,
//			String competenceTestMethod,
//			Object[] competenceTestMethodArguments,
//			AgentCompetence callerComp,
//			String agentExecutionMethod,
//			Class<?>[] agentExecutionMethodSignature,
//			Object[] agentExecutionMethodArgument)
//	{
//		try {
//		return when(comp, competenceTestMethod, null,
//				competenceTestMethodArguments, callerComp,
//				agentExecutionMethod, agentExecutionMethodSignature,
//				agentExecutionMethodArgument);
//	} catch (final Exception e) {
//		LoggerManager.writeException("Impossible to add the hook", e);
//		return false;
//	}
//	}
//	public Boolean when(
//			AgentCompetence comp,
//			String competenceTestMethod,
//			Object[] competenceTestMethodArguments,
//			AgentCompetence callerComp,
//			String agentExecutionMethod)
//	{
//		try {
//		return when(comp, competenceTestMethod, null,
//				competenceTestMethodArguments, callerComp,
//				agentExecutionMethod, null,
//				null);
//	} catch (final Exception e) {
//		LoggerManager.writeException("Impossible to add the hook", e);
//		return false;
//	}
//	}
//	public Boolean when(
//			AgentCompetence comp,
//			String competenceTestMethod,
//			Object[] competenceTestMethodArguments,
//			AgentCompetence callerComp,
//			String agentExecutionMethod,
//			Object[] agentExecutionMethodArgument)
//	{
//		try {
//		return when(comp, competenceTestMethod, null,
//				competenceTestMethodArguments, callerComp,
//				agentExecutionMethod, null,
//				agentExecutionMethodArgument);
//	} catch (final Exception e) {
//		LoggerManager.writeException("Impossible to add the hook", e);
//		return false;
//	}
//
//	}