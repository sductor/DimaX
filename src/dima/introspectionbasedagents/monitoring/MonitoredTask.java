package dima.introspectionbasedagents.monitoring;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Collection;

import darx.DarxServer;
import darx.RemoteTask;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.basicinterfaces.ProactiveComponentInterface;
import dima.introspectionbasedagents.services.darxkernel.DimaXTask;
import dima.introspectionbasedagents.services.deployment.exceptions.UninstanciableMonitorException;

/**
 *
 * @author Sylvain Ductor
 *
 * @param <Component>
 */
public class MonitoredTask<Component extends ProactiveComponentInterface & IdentifiedComponentInterface> extends DimaXTask<Component> {

	/**
	 *
	 */
	private static final long serialVersionUID = -4777784354341233503L;
	Collection<Class<? extends AgentMonitor>> monitorClasses;

	/**
	 * The monitored task executors keep information about the monitors name and their class in order to recreate them.
	 * A monitor must be instanciated with a constructor
	 * @param component
	 * @param monitors
	 * @throws UninstanciableMonitorException
	 */
	public MonitoredTask(final Component dimaComponent, final Class<? extends AgentMonitor>... monitors) throws UninstanciableMonitorException {
		super(dimaComponent);

		this.monitorClasses.addAll(Arrays.asList(monitors));

		for (final Class<? extends AgentMonitor> m : monitors) {
			this.recoverMonitor(m);
		}
	}

	@Override
	public RemoteTask activateTask(final String url, final int port_nb)
			throws RemoteException {
		final String server_path = url + ":" + Integer.toString(port_nb)
				+ "/DarxServer";
		try {
			// System.out.println("Getting registry at:"+url+":"+port_nb);
			final Registry server_reg = LocateRegistry
					.getRegistry(url, port_nb);
			// System.out.println("Getting server:"+ server_path);
			this.server = (DarxServer) server_reg.lookup(server_path);
			this.handle = this.server.startTask(this);
		} catch (final NotBoundException e) {
			System.out.println("Error : " + server_path + " is not bound ?!?!");
		}
		return this.handle;
	}

	public MonitorIdentifier getMonitorIdentifier(final Class<? extends AgentMonitor> monitorClass){
		return new MonitorIdentifier(this.getComponent().getIdentifier(), monitorClass);
	}

	public void recoverMonitor(final Class<? extends AgentMonitor> monitorClass) throws UninstanciableMonitorException{
		AgentMonitor monitor;
		try {
			monitor = monitorClass.getConstructor(AgentIdentifier.class, MonitoredTask.class).newInstance(this.getComponent().getIdentifier(), this);
		} catch (final Exception e) {
			throw new UninstanciableMonitorException();
		}
		monitor.activate();
	}

}
