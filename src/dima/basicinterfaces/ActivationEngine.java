package dima.basicinterfaces;

import dima.basiccommunicationcomponents.CometCommunicationComponent;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import dima.kernel.ProactiveComponents.ProactiveComponentEngine;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
import dima.support.GimaObject;
import dimaxx.exceptions.UninstanciableMonitorException;
import dimaxx.kernel.DimaXCommunicationComponent;
import dimaxx.kernel.DimaXTask;
import dimaxx.monitoring.AgentMonitor;
import dimaxx.monitoring.MonitoredTask;

public abstract class ActivationEngine extends GimaObject implements ActivableComponent{

	/**
	 *
	 */
	private static final long serialVersionUID = -7504867452215601716L;

	public abstract CommunicationComponentInterface getCommunicationComponent();
	public abstract void setCommunicationComponent(CommunicationComponentInterface com);

//	final private ProactiveComponentInterface myComponent;
	final private BasicCommunicatingAgent myComponent;
	CommunicationComponentInterface com;

	public ActivationEngine(final BasicCommunicatingAgent myComponent) {
		super();
		this.myComponent = myComponent;
	}

//	public ActivationEngine(ProactiveComponentInterface myComponent) {
//		super();
//		this.myComponent = myComponent;
//	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/05/00 00:24:58)
	 * @param param Gdima.kernel.AgentAddress
	 */
	public void activate() {
		final ProactiveComponentEngine engine = new ProactiveComponentEngine(this.myComponent);
		engine.startUp();

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (07/05/00 00:24:58)
	 * @param param Gdima.kernel.AgentAddress
	 */
	public void activateWithFipa() {
		final ProactiveComponentEngine engine = new ProactiveComponentEngine(this.myComponent);
		AgentManagementSystem.getDIMAams().addAquaintance(this.myComponent);
		engine.startUp();

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/05/00 00:24:58)
	 * @param param Gdima.kernel.AgentAddress
	 */
	public void activateWithComet() {
		final ProactiveComponentEngine engine = new ProactiveComponentEngine(this.myComponent);
		this.com = new CometCommunicationComponent();
		engine.startUp();
	}




	public void activateWithDarx(final int PortNb)
	{
		DimaXTask<BasicCommunicatingAgent> darxEngine;

		darxEngine = new  DimaXTask<BasicCommunicatingAgent>(this.myComponent);
		this.com = new DimaXCommunicationComponent<BasicCommunicatingAgent>(darxEngine);
		try	{
			darxEngine.activateTask(PortNb);
		}  catch (final java.rmi.RemoteException e){
			LogService.writeException(this,"Error during Activation : ",e);
		}
	}


	public void activateWithDarx(final String url,final int PortNb)
	{
		DimaXTask<BasicCommunicatingAgent> darxEngine;

		darxEngine = new  DimaXTask<BasicCommunicatingAgent>(this.myComponent);
		this.com = new DimaXCommunicationComponent<BasicCommunicatingAgent>(darxEngine);
		try	{
			darxEngine.activateTask(url,PortNb);
		}  catch (final java.rmi.RemoteException e){
			LogService.writeException(this,"Error during Activation : ",e);
		}
	}

	public void activateWithMonitor(final String url,final int PortNb, final Class<? extends AgentMonitor>... monitors) throws UninstanciableMonitorException
	{
		MonitoredTask<BasicCommunicatingAgent> darxEngine;

		darxEngine = new  MonitoredTask<BasicCommunicatingAgent>(this.myComponent, monitors);
		this.com = new DimaXCommunicationComponent<BasicCommunicatingAgent>(darxEngine);
		try	{
			darxEngine.activateTask(url,PortNb);
		}  catch (final java.rmi.RemoteException e){
			LogService.writeException(this,"Error during Activation : ",e);
		}
	}
}
