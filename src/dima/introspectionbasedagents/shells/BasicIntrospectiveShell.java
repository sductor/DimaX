package dima.introspectionbasedagents.shells;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.lang.annotation.Annotation;


import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.annotations.PostStepComposant;
import dima.introspectionbasedagents.annotations.PreStepComposant;
import dima.introspectionbasedagents.annotations.ProactivityFinalisation;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.support.GimaObject;

public class BasicIntrospectiveShell extends GimaObject {

	private static final long serialVersionUID = -8399072656535198387L;

	//
	// Fields
	//

	//	private final DimaComponentInterface myMainComponent;

	/** The,agent and its methods **/
	private IntrospectedMethodsTrunk myMethods;

	/** The Handler called if an exception occurs **/
	private final SimpleExceptionHandler exceptionHandler;

	//
	// Constructor
	//

	public BasicIntrospectiveShell(
			final DimaComponentInterface myComponent, 
			final IntrospectedMethodsTrunk methods) {
		super();
		this.myMethods = methods;
		this.exceptionHandler  = new SimpleExceptionHandler();

		this.myMethods.load(myComponent);
	}

	public BasicIntrospectiveShell(
			final DimaComponentInterface myComponent, 
			final IntrospectedMethodsTrunk methods,
			final SimpleExceptionHandler exceptionHandler) {
		super();
		this.myMethods = methods;
		this.exceptionHandler = exceptionHandler;

		this.myMethods.load(myComponent);
	}

	/*
	 *
	 */

	public BasicIntrospectiveShell(
			final DimaComponentInterface myComponent, final Date horloge) {
		super();
		this.myMethods = new BasicIntrospectedMethodsTrunk(horloge);
		this.exceptionHandler  = new SimpleExceptionHandler();

		this.myMethods.load(myComponent);
	}

	public BasicIntrospectiveShell(
			final DimaComponentInterface myComponent, final Date horloge,
			final SimpleExceptionHandler exceptionHandler) {
		super();
		this.myMethods = new BasicIntrospectedMethodsTrunk(horloge);
		this.exceptionHandler = exceptionHandler;

		this.myMethods.load(myComponent);
	}

	//
	// Accessors
	//

	/**
	 * @return the exceptionHandler
	 */
	public SimpleExceptionHandler getExceptionHandler() {
		return this.exceptionHandler;
	}

	/**
	 * @return the status
	 */
	public SimpleAgentStatus getStatus() {
		return this.getMyMethods().getStatus();
	}

	/*
	 *
	 */

	public IntrospectedMethodsTrunk getMyMethods() {
		return this.myMethods;
	}

	public void setMyMethods(final IntrospectedMethodsTrunk myMethods) {
		this.myMethods = myMethods;
	}

	/*
	 *
	 */


	//
	// Methods
	//
	
	public void proactivityInitialize(){
		this.executeBehaviors(ProactivityInitialisation.class);

	}

	public void preActivity(){
		this.executeBehaviors(PreStepComposant.class);

	}

	public void step(){
		this.executeBehaviors(StepComposant.class);

	}

	protected final Set<MethodHandler> metToRemove = new HashSet<MethodHandler>();
	public void postActivity(){
		this.executeBehaviors(PostStepComposant.class);
		for (final MethodHandler meth : this.metToRemove)
			this.myMethods.removeMethod(meth);
		this.metToRemove.clear();
	}

	public void proactivityTerminate(){
		this.executeBehaviors(ProactivityFinalisation.class);

	}

	//
	// Primitive
	//


	/**
	 * Execute all the reflective methods of ag
	 *
	 * @param myComponent
	 *            the agent to execute
	 */
	protected void executeBehaviors(Class<? extends Annotation> annotation) {
		for (final MethodHandler mt : this.myMethods.getMethods()){
			if (mt.isAnnotationPresent(annotation))
				try {
					final boolean toRemove = this.myMethods.executeStepMethod(mt);
					if (toRemove)
						this.metToRemove.add(mt);
				} catch (final Exception e) {
					// The exception is raised by the method
					this.getExceptionHandler().handleException(
							e, this.getStatus());
				}
		}

	}
}
