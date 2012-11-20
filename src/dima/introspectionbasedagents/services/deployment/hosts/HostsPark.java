package dima.introspectionbasedagents.services.deployment.hosts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.jcraft.jsch.JSchException;

import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.communicating.execution.SystemCommunicationService.ErrorOnProcessExecutionException;
import dima.introspectionbasedagents.services.communicating.execution.SystemCommunicationService.WrongOSException;
import dima.introspectionbasedagents.services.communicating.remoteexecution.SSHExecutor;
import dima.introspectionbasedagents.services.deployment.server.HostIdentifier;

/**
 * This class  parses an xml that respect park.dtd.
 * It loads the required hosts ssh information as RemoteHostIdentifier and associate them to networks.
 *
 * @author Sylvain Ductor
 */
public class HostsPark {

	private static final long serialVersionUID = -8207701331374508610L;


	//
	// Fields
	//

	private RemoteHostInfo nameServer;

	/**
	 * @key a network name
	 * @value the list of hosts of this network
	 */
	private final HashedHashSet<String, RemoteHostInfo> hostsOfNetworks=
			new HashedHashSet<String, RemoteHostInfo>();

	/**
	 * @key an host url
	 * @value the associated network
	 */
	private final Map<RemoteHostInfo, String> networksOfHosts =
			new HashMap<RemoteHostInfo, String>();

	//
	// Constructor
	//

	public HostsPark(final File machineFile) throws JDOMException,
	IOException {
		this.parseXML(machineFile);
		System.out.println("NameServer is : "+this.nameServer);
		System.out.println("Known hosts are : "+this.getHosts());
	}


	public HostsPark(final String listeMachines) throws JDOMException, IOException {
		final File machineFile = new File(LocalHost.getConfDir()+listeMachines);
		this.parseXML(machineFile);
		System.out.println("NameServer is : "+this.nameServer);
		System.out.println("Known hosts are : "+this.getHosts());
	}

	//
	// Accessors
	//


	/*
	 * NameServer
	 */


	public RemoteHostInfo getNameServer(){
		return this.nameServer;
	}

	/*
	 * Host Search
	 */

	/**
	 * @return the hosts associated to a network
	 */
	public Collection<RemoteHostInfo> getHostsOfNetwork(final String network) {
		return this.hostsOfNetworks.get(network);
	}

	/**
	 * @return All the registered networks
	 */
	public Collection<String> getNetworks() {
		return this.hostsOfNetworks.keySet();
	}

	public Collection<RemoteHostInfo> getHosts() {
		return this.networksOfHosts.keySet();
	}

	/**
	 * @return The set of host and the name server
	 */
	public Collection<RemoteHostInfo> getAllHosts() {
		final ArrayList<RemoteHostInfo> hosts = new ArrayList<RemoteHostInfo>();
		hosts.addAll(this.getHosts());
		hosts.add(this.getNameServer());
		return hosts;
	}

	/**
	 * @return The set of host and the name server
	 */
	public Collection<HostIdentifier> getDarxServersIdentifier() {
		final ArrayList<HostIdentifier> hosts = new ArrayList<HostIdentifier>();

		for (final RemoteHostInfo h : this.getHosts()) {
			hosts.add(h.generateHostIdentifier());
		}

		return hosts;
	}
	/*
	 * Dynamic extension of the network
	 */

	protected RemoteHostInfo addRemoteHost(final RemoteHostInfo remoteHost) {
		this.hostsOfNetworks.add(remoteHost.getGroupID(), remoteHost);
		this.networksOfHosts.put(remoteHost, remoteHost.getGroupID());
		return remoteHost;
	}

	protected boolean removeRemoteHost(final RemoteHostInfo host){
		this.hostsOfNetworks.get(host.getGroupID()).remove(host);
		this.networksOfHosts.remove(host);
		return true;
	}

	//
	// Methods
	//


	/**
	 * This methods allow to remotely or locally execute a class
	 * @param host : the host where to execute the application
	 * @param the main : the main class to execute
	 * @param args : the execution arguments
	 * @throws JSchException
	 * @throws WrongOSException
	 * @throws ErrorOnProcessExecutionException
	 * @throws IOException
	 */
	public void execute(final RemoteHostInfo host, final Class<?> main, final String args) throws ErrorOnProcessExecutionException, WrongOSException, JSchException, IOException{
		new SSHExecutor(host).executeWithJava(main, args);
	}

	public void executeOnHosts(final Class<?> main, final String args){
		for (final RemoteHostInfo host : this.getHosts()) {
			try {
				new SSHExecutor(host).executeWithJava(main, args);
			} catch (final Exception e) {
				System.err.println("Unable to connect to "+host);
				this.removeRemoteHost(host);
				e.printStackTrace();
			}
		}
	}


	public void executeOnGroup(final String groupID, final Class<?> main, final String args) {
		for (final RemoteHostInfo host : this.getHostsOfNetwork(groupID)) {
			try {
				new SSHExecutor(host).executeWithJava(main, args);
			} catch (final Exception e) {
				System.err.println("Unable to connect to "+host);
				this.removeRemoteHost(host);
				e.printStackTrace();
			}
		}
	}

	/*
	 * Destruction
	 */


	/**
	 * Self Destruction Using Thread hook
	 * Détruit les processus java lancés par ssh lorsque le systeme recoit le signal d'extinction
	 * @author Ductor Sylvain
	 */
	public void activateSelfDestruction(){
		new NetworkShutdownHook(this.getAllHosts()).activate();
	}

	//
	// XML Parsing : Primitives
	//

	@SuppressWarnings("unchecked")
	private void parseXML(final File machineFile) throws JDOMException,
	IOException {

		final SAXBuilder builder = new SAXBuilder();
		final Document doc = builder.build(machineFile);
		final Element park = doc.getRootElement();

		this.parseNameServer(park);

		final List<Element> networks = park.getChildren("network");

		final List<Element> activatedGroupsElement =
				park.getChild("activatedGroups").getChildren("groupID");
		final List<Element> activatedNetworksElement =
				park.getChild("activatedNetworks").getChildren("networkID");

		final Collection<String> activatedGroups = new ArrayList<String>();
		for (final Element groupID : activatedGroupsElement) {
			activatedGroups.add(groupID.getText().trim());
		}

		final Collection<String> activatedNetworks = new ArrayList<String>();
		for (final Element networkID : activatedNetworksElement) {
			activatedNetworks.add(networkID.getText().trim());
		}
		System.out.println("Activated network "+activatedNetworks+", activated groups "+activatedGroups);

		for (final Element network : networks) {
			if (activatedNetworks.contains(network.getChildText("networkID").trim())) {
				this.parseNetwork(network, activatedGroups);
			}
		}
	}

	private void parseNameServer(final Element park){
		// Getting name server
		final Element ns = park.getChild("nameServer");
		this.nameServer = new RemoteHostInfo("nameServer");
		this.nameServer.setAdress(ns.getChild("host"));
		this.nameServer.setSSH(ns.getChild("ssh"));
	}

	@SuppressWarnings("unchecked")
	private void parseNetwork(final Element network, final Collection<String> activatedGroups) {

		final Element ssh = network.getChild("ssh");

		for (final Element group : (List<Element>) network.getChildren("group")){
			final String groupID = group.getChildText("groupID").trim();
			if (activatedGroups.contains(groupID)) {
				for (final Element host : (List<Element>) group.getChildren("host"))	{
					final RemoteHostInfo rhost =
							new RemoteHostInfo(groupID);
					rhost.setAdress(host);
					rhost.setSSH(ssh);
					this.addRemoteHost(rhost);
				}
			}
		}
	}


}




//
// /**
// * @return all the groups associated to a network
// */
// protected Collection<String> getGroupOfNetwork(String network) {
// return groupsOfNetwork.get(network);
// }
//
// /**
// * @return the gate associated to a network or null if no gate is needed
// */
// protected String getNetwork_gate(String network) {
// return network_gate.get(network);
// }
//
// /**
// * @return the hosts associated to a group
// */
// protected Collection<HostIdentifier> getHostsOfGroup(String group) {
// return hostsOfGroup.get(group);
// }

//
// }
///**
//* Allow to obtain ssh information of a specific host
//*/
//Map<HostIdentifier, XmlHostExecutor> sshinfo =
//	new HashMap<HostIdentifier, XmlHostExecutor>();

//public HostIdentifier getNameServerIdentifier(){
//	return nameServer;
//}

//protected XmlHostExecutor getSSHInfo(HostIdentifier host) throws UnknownHostException {
//	if (this.sshinfo.get(host)!=null)
//		return this.sshinfo.get(host);
//	else
//		throw new UnknownHostException(host);
//}

//this.sshinfo.put(remoteHost.generateHostIdentifier(), remoteHost);