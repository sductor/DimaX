package dimaxx.server;

import dima.basicagentcomponents.AgentName;

/**
 *
 * Cette classe permet d'identifier un host en stockant son adresse physique et
 * le nom de la simulation auquel il est associé
 *
 * @author Sylvain Ductor
 *
 */
public class HostIdentifier extends AgentName {

	private static final long serialVersionUID = -2759755935152707300L;

	//
	// Fields
	//

	protected String url;
	protected Integer portNumber;

	//
	// Constructors
	//

	public HostIdentifier(final String url, final Integer port) {
		super();
		this.url = url;
		this.portNumber = new Integer(port);
		this.setId(this.toString());
	}

	//
	// Accessors
	//

	public String getUrl() {
		return this.url;
	}

	public Integer getPort() {
		return this.portNumber;
	}

	/*
	 *
	 */

	@Override
	public String toString() {
		return "#HOST_MANAGER#" + this.url + ":" + this.portNumber;
	}

}

// public void setUrl(String url) {
// this.url = url;
// }
//
// public void setInstanceName(String instanceID) {
// this.instanceName = instanceID;
// }
//
// public void setManagerClass(Class<? extends BasicHostManager> c) {
// managerClass = c;
// }