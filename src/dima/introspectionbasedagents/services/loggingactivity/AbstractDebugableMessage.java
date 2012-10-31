package dima.introspectionbasedagents.services.loggingactivity;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.CommunicationObject;
import dima.introspectionbasedagents.kernel.MethodHandler;
import dima.introspectionbasedagents.services.communicating.AbstractMessageInterface;

public abstract class AbstractDebugableMessage extends CommunicationObject implements AbstractDebugableMessageInterface, AbstractMessageInterface {

	private MethodHandler callingMethod;
	private AbstractDebugableMessage inReplyTo;
	private Throwable localTrace;

	@Override
	public String getProtocolTrace() {
		String result = "Protocol Trace :";
		AbstractDebugableMessage inReplyToStack = this;
		while (inReplyToStack!=null){
			String debug = inReplyToStack.getDebugCallingMethod()!=null?inReplyToStack.getDebugCallingMethod().toGenericString():"erreur : pas d'info sur la méthode!";
			result+="\n"+" |--> ("+inReplyToStack.getSender()+") \t "+debug
					;
			inReplyToStack = inReplyToStack.getDebugInReplyTo();
		}
		return result;
	}

	@Override
	public MethodHandler getDebugCallingMethod() {
		return callingMethod;
	}

	@Override
	public void setDebugCallingMethod(MethodHandler callingMethod) {
		if (this.callingMethod==null){
			this.callingMethod = callingMethod;
			instanciateLocalStackTrace();
		}else {
			//on ignore
		}
	}

	@Override
	public Throwable getLocalStackTrace() {
		return localTrace;
	}
	public void printStackTrace(){
		localTrace.printStackTrace();
	}
	//	@Override
	private void instanciateLocalStackTrace() {
		try {
			throw new Exception();
		} catch (Exception e){
			this.localTrace = e;
		}
	}

	@Override
	public AbstractDebugableMessage getDebugInReplyTo() {
		return inReplyTo;
	}

	@Override
	public void setDebugInReplyTo(AbstractDebugableMessage inReplyTo) {
		if (this.inReplyTo==null)
			this.inReplyTo = inReplyTo;
		else {
			//on ignore
		}
	}

	public abstract AbstractDebugableMessage clone();
}
