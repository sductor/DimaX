package dima.introspectionbasedagents.shells;

import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.ActiveComponentInterface;
import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.introspectionbasedagents.annotations.PostStepComposant;
import dima.introspectionbasedagents.annotations.PreStepComposant;
import dima.introspectionbasedagents.annotations.ProactivityFinalisation;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.ResumeActivity;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.support.GimaObject;

public class BasicIntrospectiveShell extends GimaObject {

	private static final long serialVersionUID = -8399072656535198387L;

	//
	// Fields
	//

	//	private final DimaComponentInterface myMainComponent;
	private final AgentIdentifier myComponentIdentifier;

	/** The,agent and its methods **/
	private IntrospectedMethodsTrunk myMethods;

	/** The Handler called if an exception occurs **/
	private final SimpleExceptionHandler exceptionHandler;

	//
	// Constructor
	//

	public <Component extends ActiveComponentInterface & IdentifiedComponentInterface> BasicIntrospectiveShell(
			final Component myComponent,
			final IntrospectedMethodsTrunk methods) {
		super();
		this.myComponentIdentifier=myComponent.getIdentifier();
		this.myMethods = methods;
		this.exceptionHandler  = new SimpleExceptionHandler();

		this.myMethods.load(myComponent);
	}

	public <Component extends ActiveComponentInterface & IdentifiedComponentInterface> BasicIntrospectiveShell(
			final Component myComponent,
			final IntrospectedMethodsTrunk methods,
			final SimpleExceptionHandler exceptionHandler) {
		super();
		this.myComponentIdentifier=myComponent.getIdentifier();
		this.myMethods = methods;
		this.exceptionHandler = exceptionHandler;

		this.myMethods.load(myComponent);
	}

	/*
	 *
	 */

	public <Component extends ActiveComponentInterface & IdentifiedComponentInterface> BasicIntrospectiveShell(
			final Component myComponent) {
		super();
		this.myComponentIdentifier=myComponent.getIdentifier();
		this.myMethods = new BasicIntrospectedMethodsTrunk();
		this.exceptionHandler  = new SimpleExceptionHandler();

		this.myMethods.load(myComponent);
	}

	public <Component extends ActiveComponentInterface & IdentifiedComponentInterface> BasicIntrospectiveShell(
			final Component myComponent, final Date horloge,
			final SimpleExceptionHandler exceptionHandler) {
		super();
		this.myComponentIdentifier=myComponent.getIdentifier();
		this.myMethods = new BasicIntrospectedMethodsTrunk();
		this.exceptionHandler = exceptionHandler;

		this.myMethods.load(myComponent);
	}

	//
	// Accessors
	//




	/**
	 * @return the component identifer
	 */
	public AgentIdentifier getIdentifier() {
		return this.myComponentIdentifier;
	}

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

	public final void proactivityInitialize(final Date creation){
		this.executeBehaviors(ProactivityInitialisation.class, creation, true);

	}

	public final void preActivity(final Date creation){
		this.executeBehaviors(PreStepComposant.class, creation, false);

	}

	public void step(final Date creation){
		this.executeBehaviors(StepComposant.class, creation, false);

	}

	protected final Set<MethodHandler> metToRemove = new HashSet<MethodHandler>();
	public final void postActivity(final Date creation){
		this.executeBehaviors(PostStepComposant.class, creation, false);
		for (final MethodHandler meth : this.metToRemove) {
			this.myMethods.removeMethod(meth);
		}
		this.metToRemove.clear();
	}

	public final void resumeActivity(final Date creation){
		this.executeBehaviors(ResumeActivity.class, creation, true);
	}

	public void proactivityTerminate(final Date creation){
		this.executeBehaviors(ProactivityFinalisation.class, creation, true);
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
	protected void executeBehaviors(final Class<? extends Annotation> annotation, final Date creation, boolean forceExecution) {
		for (final MethodHandler mt : this.myMethods.getMethods()) {
			if (mt.isAnnotationPresent(annotation)) {
				try {
					boolean toRemove = false;
					if (forceExecution || mt.isActive())
						toRemove = this.myMethods.executeStepMethod(mt, creation);
					if (toRemove) {
						this.metToRemove.add(mt);
					}
				} catch (final Throwable e) {
					// The exception is raised by the method
					this.getExceptionHandler().handleException(
							e, this.getStatus());
				}
			}
		}

	}
}
