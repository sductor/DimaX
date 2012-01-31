package dimaxx.hostcontrol;


public class SSHInfo {//implements HostIdentifier {

	protected String url;
	protected Integer port;
	protected String user;
	protected String privateKeyPath;
	protected String knownHostsPath;
	protected String gateUrl = null;
	protected String dir;

	//
	// Constructors
	//

	public SSHInfo(final String user, final String url, final Integer port, final String privateKeyPath,
			final String knownHostsPath, final String dir) {
		super();
		this.url = url;
		this.port = port;
		this.user = user;
		this.privateKeyPath = privateKeyPath;
		this.knownHostsPath = knownHostsPath;
		this.dir = dir;
	}

	public SSHInfo(final String url, final Integer port, final String privateKeyPath,
			final String knownHostsPath, final String dir) {
		super();
		this.url = url;
		this.port = port;
		this.setDefaultUser();
		this.privateKeyPath = privateKeyPath;
		this.knownHostsPath = knownHostsPath;
		this.gateUrl = null;
		this.dir = dir;
	}


	public SSHInfo(final String user, final String url, final Integer port,  final String dir) {
		super();
		this.url = url;
		this.port = port;
		this.user = user;
		this.setDefaultPaths();
		this.dir = dir;
	}


	public SSHInfo(final String url, final Integer port, final String dir) {
		super();
		this.url = url;
		this.port = port;
		this.setDefaultUser();
		this.setDefaultPaths();
		this.dir = dir;
	}



	public SSHInfo(final String user, final String url, final Integer port, final String privateKeyPath,
			final String knownHostsPath) {
		super();
		this.url = url;
		this.port = port;
		this.user = user;
		this.privateKeyPath = privateKeyPath;
		this.knownHostsPath = knownHostsPath;
		this.setDefaultDir();
	}

	public SSHInfo(final String url, final Integer port, final String privateKeyPath,
			final String knownHostsPath) {
		super();
		this.url = url;
		this.port = port;
		this.setDefaultUser();
		this.privateKeyPath = privateKeyPath;
		this.knownHostsPath = knownHostsPath;
		this.setDefaultDir();
	}


	public SSHInfo(final String user, final String url, final Integer port) {
		super();
		this.url = url;
		this.port = port;
		this.user = user;
		this.setDefaultPaths();
		this.setDefaultDir();
	}


	public SSHInfo(final String url, final Integer port) {
		super();
		this.url = url;
		this.port = port;
		this.setDefaultUser();
		this.setDefaultPaths();
		this.setDefaultDir();
	}

	//
	// Accessors
	//

	//	@Override
	public String getUrl() {
		return this.url;
	}

	//	@Override
	public Integer getPort() {
		return this.port;
	}

	//	@Override
	public String getConfDir() {
		return this.dir+"conf";
	}

	/*
	 *
	 */

	public void setGate(final String gate){
		this.gateUrl= gate;
	}

	/*
	 *
	 */


	protected boolean hasGate(){
		return this.gateUrl!=null;
	}

	protected boolean isLocal(){
		return this.url.equals(LocalHost.getUrl());
	}

	/*
	 *
	 */


	private void setDefaultUser(){
		this.user = System.getProperty("user.name");

	}

	private void setDefaultPaths(){
		this.privateKeyPath = System.getProperty("user.home")+"/.ssh/id_rsa";
		this.knownHostsPath =System.getProperty("user.home")+"/.ssh/known_hosts";
	}

	private void setDefaultDir(){
		this.dir=LocalHost.getDir();
	}
}