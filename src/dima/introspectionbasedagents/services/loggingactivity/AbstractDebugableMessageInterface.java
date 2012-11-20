package dima.introspectionbasedagents.services.loggingactivity;

import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.kernel.MethodHandler;

public interface AbstractDebugableMessageInterface extends DimaComponentInterface{

	public MethodHandler getDebugCallingMethod();
	public AbstractDebugableMessage getDebugInReplyTo();
	Throwable getLocalStackTrace();

	public String getProtocolTrace();
	public void printStackTrace();

	public void setDebugCallingMethod(MethodHandler m);
	void setDebugInReplyTo(AbstractDebugableMessage inReplyTo);
	//	void instanciateLocalStackTrace();


	public int getSerial();

	//boolean pour l'assert
	public boolean setSerial(int i);
}
