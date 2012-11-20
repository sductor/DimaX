package dima.introspectionbasedagents.services.loggingactivity;

import dima.basiccommunicationcomponents.CommunicationObject;
import dima.introspectionbasedagents.kernel.MethodHandler;
import dima.introspectionbasedagents.services.communicating.AbstractMessageInterface;

public abstract class AbstractDebugableMessage extends CommunicationObject implements AbstractDebugableMessageInterface, AbstractMessageInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2221783027931038958L;
	private MethodHandler callingMethod;
	private AbstractDebugableMessage inReplyTo;
	private Throwable localTrace;

	@Override
	public String getProtocolTrace() {
		String result = "Protocol Trace :";
		AbstractDebugableMessage inReplyToStack = this;
		while (inReplyToStack!=null){
			final String debug = inReplyToStack.getDebugCallingMethod()!=null?inReplyToStack.getDebugCallingMethod().toGenericString():"erreur : pas d'info sur la méthode!";
			result+="\n"+" |--> ("+inReplyToStack.getSender()+") \t "+debug
					;
			inReplyToStack = inReplyToStack.getDebugInReplyTo();
		}
		return result;
	}

	@Override
	public MethodHandler getDebugCallingMethod() {
		return this.callingMethod;
	}

	@Override
	public void setDebugCallingMethod(final MethodHandler callingMethod) {
		if (this.callingMethod==null){
			this.callingMethod = callingMethod;
			this.instanciateLocalStackTrace();
		}else {
			//on ignore
		}
	}

	@Override
	public Throwable getLocalStackTrace() {
		return this.localTrace;
	}
	@Override
	public void printStackTrace(){
		this.localTrace.printStackTrace();
	}
	//	@Override
	private void instanciateLocalStackTrace() {
		try {
			throw new Exception();
		} catch (final Exception e){
			this.localTrace = e;
		}
	}

	@Override
	public AbstractDebugableMessage getDebugInReplyTo() {
		return this.inReplyTo;
	}

	@Override
	public void setDebugInReplyTo(final AbstractDebugableMessage inReplyTo) {
		if (this.inReplyTo==null) {
			this.inReplyTo = inReplyTo;
		} else {
			//on ignore
		}
	}

	@Override
	public abstract AbstractDebugableMessage clone();
}
