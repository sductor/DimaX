package dima.introspectionbasedagents.shells;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import dima.basicinterfaces.ActiveComponentInterface;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.annotations.PostStepComposant;
import dima.introspectionbasedagents.annotations.PreStepComposant;
import dima.introspectionbasedagents.annotations.ProactivityFinalisation;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;


/**
 * This class allow to stock the methods to be executed by introspection
 * The addMethods() method must be explicitly called after construction
 * @author Ductor Sylvain
 */
public class BasicIntrospectedMethodsTrunk implements IntrospectedMethodsTrunk {

	//
	// Fields
	//

	/**
	 *
	 */
	private static final long serialVersionUID = 9112902710722635207L;

	private final Collection<MethodHandler> methods = new ArrayList<MethodHandler>();

	/** The methods list of each agent associated with the tickers */
	private final Map<MethodHandler, Ticker> tickers = new Hashtable<MethodHandler, Ticker>();

	/** Allow to obtain up to date information about what is being executed **/
	private final SimpleAgentStatus status;

	//
	// Constructor
	//

	public BasicIntrospectedMethodsTrunk() {
		super();
		this.status = new SimpleAgentStatus();
	}

	//
	// Accessor
	//

	/**
	 * @return the status
	 */
	@Override
	public SimpleAgentStatus getStatus() {
		return this.status;
	}

	/* (non-Javadoc)
	 * @see dima.introspectionBasedAgent.shells.IntrospedMethodsTrunk#getMethods()
	 */
	@Override
	public Collection<MethodHandler> getMethods(){
		return this.methods;
	}

	protected boolean isStepMethod(final MethodHandler mt) {
		return
				mt.isAnnotationPresent(ProactivityInitialisation.class)
				|| mt.isAnnotationPresent(PreStepComposant.class)
				|| mt.isAnnotationPresent(StepComposant.class)
				|| mt.isAnnotationPresent(PostStepComposant.class)
				|| mt.isAnnotationPresent(ProactivityFinalisation.class);
	}

	/**
	 * Return the list of methods of object o that have annotation c
	 */
	protected Collection<MethodHandler> getMethods(final ActiveComponentInterface o, final Class<? extends Annotation>... a){

		final Collection<MethodHandler> annotatedMethods = new ArrayList<MethodHandler>();
		for (final Method mt : IntrospectionStaticPrimitivesLibrary.getAllMethods(o.getClass())) {
			for (final Class<? extends Annotation> c : a) {
				if (mt.isAnnotationPresent(c)){
					annotatedMethods.add(new MethodHandler(o,mt));
					break;
				}
			}
		}
		return annotatedMethods;
	}

	/* (non-Javadoc)
	 * @see dima.introspectionBasedAgent.shells.IntrospedMethodsTrunk#removeMethod(dima.introspectionBasedAgent.tools.MethodHandler)
	 */
	@Override
	public void removeMethod(final MethodHandler meth) {
		this.methods.remove(meth);
		this.tickers.remove(meth);
	}

	//
	// Methods
	//

	/* (non-Javadoc)
	 * @see dima.introspectionBasedAgent.shells.IntrospedMethodsTrunk#init()
	 */
	@Override
	public void load(final ActiveComponentInterface a) {
		for (final MethodHandler mt : this.getRelevantMethods(a)) {
			if (!this.checkMethodValidity(mt)) {
				LogService.writeException(
						a,"cannot add " + mt+" method not valid ");
			} else {
				this.addMethod(mt);
			}
		}


	}

	/* (non-Javadoc)
	 * @see dima.introspectionBasedAgent.shells.IntrospedMethodsTrunk#executeStepMethod(dima.introspectionBasedAgent.tools.MethodHandler)
	 */
	@Override
	public boolean executeStepMethod(final MethodHandler mt, final Date creation)
			throws IllegalArgumentException, Throwable {
		if (this.isReady(mt, creation)){
			this.status.setCurrentlyExecutedAgent(mt.getMyComponent());
			this.status.setCurrentlyExecutedMethod(mt);
			final Object resultat = mt.execute(null);
			this.status.resetCurrentlyExecutedMethod();
			this.status.resetCurrentlyExecutedAgent();
			return this.toRemove(mt, resultat);
		} else {
			return false;
		}
	}

	/*
	 *
	 */

	@SuppressWarnings("unchecked")
	protected Collection<MethodHandler> getRelevantMethods(final ActiveComponentInterface o){
		return this.getMethods(o,
				ProactivityInitialisation.class,
				PreStepComposant.class,StepComposant.class,PostStepComposant.class,
				ProactivityFinalisation.class);
	}

	protected boolean checkMethodValidity(final MethodHandler mt) {
		return this.checkTransientMethodValidity(mt) && this.checkStepMethodValidity(mt);
	}

	protected void addMethod(final MethodHandler mt) {
		if (mt.isAnnotationPresent(ProactivityInitialisation.class)) {
			this.methods.add(mt);
		} else if (mt.isAnnotationPresent(PreStepComposant.class)){
			this.methods.add(mt);
			if (mt.getAnnotation(PreStepComposant.class).ticker() != -1) {
				this.addTicker(mt, mt.getAnnotation(PreStepComposant.class).ticker());
			}
		}else if (mt.isAnnotationPresent(StepComposant.class)){
			this.methods.add(mt);
			if (mt.getAnnotation(StepComposant.class).ticker() != -1) {
				this.addTicker(mt, mt.getAnnotation(StepComposant.class).ticker());
			}
		}else if (mt.isAnnotationPresent(PostStepComposant.class)){
			this.methods.add(mt);
			if (mt.getAnnotation(PostStepComposant.class).ticker() != -1) {
				this.addTicker(mt, mt.getAnnotation(PostStepComposant.class).ticker());
			}
		}else if (mt.isAnnotationPresent(ProactivityFinalisation.class)) {
			this.methods.add(mt);
		}
	}

	//
	// Primitives
	//

	private boolean checkStepMethodValidity(final MethodHandler mt) {
		if (mt.isAnnotationPresent(ProactivityInitialisation.class)
				|| mt.isAnnotationPresent(PreStepComposant.class)
				|| mt.isAnnotationPresent(StepComposant.class)
				|| mt.isAnnotationPresent(PostStepComposant.class)
				|| mt.isAnnotationPresent(ProactivityFinalisation.class)){
			if (mt.getParameterTypes().length > 0) {
				LogService.writeException(mt.getMyComponent(),"StepComposant method " + mt
						+ " can not take any argument!");
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	private boolean checkTransientMethodValidity(final MethodHandler mt) {
		if (mt.isAnnotationPresent(Transient.class)) {
			if (!(mt.getReturnType().equals(boolean.class) || mt
					.getReturnType().equals(Boolean.class))) {
				LogService.writeException(mt.getMyComponent(),"method " + mt
						+ " must return a boolean value");
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	private boolean isReady(final MethodHandler m, final Date creation){
		try{
			return this.tickers.get(m).isReady(creation);
		} catch (final NullPointerException e){
			return true;
		}
	}

	private void addTicker(final MethodHandler mt, final long ticker) {
		this.tickers.put(
				mt,
				new Ticker(ticker));
	}

	protected boolean toRemove(
			final MethodHandler mt,  final Object resultat){
		if (mt.isAnnotationPresent(Transient.class)
				&& resultat != null
				&& resultat.equals(new Boolean(true))) {
			return true;
		} else if (mt.isAnnotationPresent(ProactivityInitialisation.class) || mt.isAnnotationPresent(ProactivityFinalisation.class)) {
			return true;
		} else {
			return false;
		}
	}

}


