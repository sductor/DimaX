package dima.introspectionbasedagents.services.core.communicating.remoteexecution;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import dima.introspectionbasedagents.services.core.communicating.execution.SystemCommunicationService;
import dima.introspectionbasedagents.services.core.communicating.execution.SystemCommunicationService.ErrorOnProcessExecutionException;
import dima.introspectionbasedagents.services.core.communicating.execution.SystemCommunicationService.WrongOSException;
import dima.introspectionbasedagents.services.core.deployment.hosts.RemoteHostInfo;


/**
 *
 * Cette classe utilise directement les métohdes de la librairie jcraft afin de
 * lancet une commade en ssh sur une machine donné
 *
 * @author Ductor Sylvain
 *
 */
public class SSHExecutor extends  SystemCommunicationService {

	private static final long serialVersionUID = -5702324561374287507L;

	protected final SSHInfo myInfo;
	//	private OutputStream commandWriter = null;
	//	private OutputStream output = System.out;
	private final JSch jsch = new JSch();

	private Channel channel = null;
	private Session session = null;

	//
	// Constructor
	//


	public SSHExecutor(final String url, final Integer port, final String privateKeyPath,
			final String knownHostsPath, final String dir) {
		myInfo = new SSHInfo(url, port, privateKeyPath, knownHostsPath, dir);
		this.init();
	}

	public SSHExecutor(final String url, final Integer port, final String privateKeyPath,
			final String knownHostsPath) {
		myInfo = new SSHInfo(url, port, privateKeyPath, knownHostsPath);
		this.init();
	}

	public SSHExecutor(final String url, final Integer port, final String dir) {
		myInfo = new SSHInfo(url, port, dir);
	}

	public SSHExecutor(final String url, final Integer port) {
		myInfo = new SSHInfo(url, port);
		this.init();
	}

	public SSHExecutor(final String user, final String url, final Integer port,
			final String privateKeyPath, final String knownHostsPath, final String dir) {
		myInfo = new SSHInfo(user, url, port, privateKeyPath, knownHostsPath, dir);
		this.init();
	}

	public SSHExecutor(final String user, final String url, final Integer port,
			final String privateKeyPath, final String knownHostsPath) {
		myInfo = new SSHInfo(user, url, port, privateKeyPath, knownHostsPath);
		this.init();
	}

	public SSHExecutor(final String user, final String url, final Integer port, final String dir) {
		myInfo = new SSHInfo(user, url, port, dir);
		this.init();
	}

	public SSHExecutor(final String user, final String url, final Integer port) {
		myInfo = new SSHInfo(user, url, port);
		this.init();

	}

	public SSHExecutor(SSHInfo i) {
		myInfo=i;
		this.init();
	}

	private void init(){
		if (myInfo.privateKeyPath != null) {
			try {
				this.jsch.addIdentity(myInfo.privateKeyPath);
			} catch (final JSchException e) {
				// The user will be asked of its credentials
			}
		}
		if (myInfo.knownHostsPath != null) {
			try {
				this.jsch.setKnownHosts(myInfo.knownHostsPath);
			} catch (final JSchException e) {
				// The user will be asked of its credentials
			}
		}
	}

	//
	// Accessor
	//

	//	/**
	//	 * Holds the output stream that write into the remote input stream
	//	 * Instanciated by connect()
	//	 */
	//	protected OutputStream getCommandWriter() {
	//		return this.commandWriter;
	//	}
	//
	//	/**
	//	 * Holds the output stream associated to the remote outputStream
	//	 * By default, System.out
	//	 */
	//	protected void setOutputStream(OutputStream remoteOutputStream) {
	//		if (this.channel!=null){
	//			this.channel.setOutputStream(remoteOutputStream);}
	//		this.output = remoteOutputStream;
	//	}
	//

	public String getUrl() {
		return myInfo.getUrl();
	}

	public Integer getPort() {
		return myInfo.getPort();
	}

	public String getConfDir() {
		return myInfo.getConfDir();
	}

	public void setGate(String gate) {
		myInfo.setGate(gate);
	}

	/*
	 * 
	 */

	public boolean isConnected(String[] args){
		return this.channel!=null && this.session!=null;
	}

	public boolean disconnect(String[] args) {
		if (!myInfo.isLocal()){
			this.channel.disconnect();
			this.session.disconnect();
			this.channel=null;
			this.session=null;
		}
		return true;
	}

	public boolean connect(String[] args) {
		try {
			// Connecting session
			this.session = 
					myInfo.hasGate()?
							this.jsch.getSession(myInfo.user, myInfo.gateUrl, 22):
								this.jsch.getSession(myInfo.user, myInfo.url, myInfo.port);
							final UserInfo ui = new MyUserInfo();
							this.session.setUserInfo(ui);
							this.session.connect();
							System.out.println("\n * * SSH : " + myInfo.url + " : Session connected");

							//		// Instanciating streams
							//		final PipedInputStream in = new PipedInputStream();
							//		this.commandWriter = new PipedOutputStream(in);

							this.channel=this.session.openChannel("shell");
							//		this.channel.setInputStream(in);
							//		this.channel.setOutputStream(System.out);
							this.channel.connect();

							if (myInfo.hasGate()) {
								this.execute("ssh "+myInfo.url);
							}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//
	// Methods
	//

	public void executeWithJava(final Class<?> classe, final String args)
			throws ErrorOnProcessExecutionException, JSchException, IOException {
		final String sourceDirectory = myInfo.dir;
		final String binPath = sourceDirectory + "bin";// "src/";// +
		final String libPath = sourceDirectory + "lib/*";
		final String command = "cd " + binPath + "; " + "java -cp $CLASSPATH:" + libPath + " "
				+ classe.getCanonicalName() + " " + args;

		this.execute(command);
	}

	/*
	 * Simple Execution
	 */

	public String execute(final String command) 
			throws ErrorOnProcessExecutionException {

		if (myInfo.isLocal()){
			final SystemCommunicationService exec = new SystemCommunicationService();
			return exec.execute(command);
		} else {
			try {
				return this.connectWithCommand(command);
			} catch (Exception e) {
				throw new ErrorOnProcessExecutionException(e);
			}
		}
	}

	/*
	 * In channel execution
	 */



	public String executeOnchannel(final String command) throws ErrorOnProcessExecutionException, JSchException, IOException{

		if (myInfo.isLocal()){
			final SystemCommunicationService exec = new SystemCommunicationService();
			return exec.execute(command);
		} else {
			if (!this.isConnected(null)) {
				this.connect(null);
			}
			return this.writeInChannel(command);
		}
	}



	//
	// Primitives
	//

	/**
	 * Connect the host
	 * @throws IOException
	 * @throws WrongOSException
	 * @throws ErrorOnProcessExecutionException
	 */
	private String connectWithCommand(final String command) throws JSchException, IOException{

		//		ssh -N -f ductors@gate-ia.lip6.fr -L7777:nirvana.lip6.fr:7777
		final Session session = this.jsch.getSession(myInfo.user, myInfo.url, 22);
		final UserInfo ui = new MyUserInfo();
		session.setUserInfo(ui);

		session.connect();
		System.out.println("\n * * SSH : " + myInfo.url + " : Session connected");

		final Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		System.out.println(" * * SSH : " + myInfo.url+":"+myInfo.port + " : Command executed "
				+ command);
		channel.setInputStream(System.in);
		((ChannelExec) channel).setOutputStream(System.out);
		((ChannelExec) channel).setErrStream(System.err);

		channel.connect();
		return "";//getChannelOutput();
	}

	/********
	 * Using Channel
	 * */


	private String writeInChannel(final String command) throws IOException{
		final OutputStream in = this.channel.getOutputStream();
		in.write(command.getBytes());
		in.flush();
		System.out.println(" * * SSH : " + myInfo.url+":"+myInfo.port + " : Command executed "
				+ command);
		return this.getChannelOutput();
	}

	private String getChannelOutput() throws IOException {
		final byte[] b = new byte[1024]; // Pour convertir la commande dans le
		// bon
		// format
		int n = 0; // Longueur de la chaine de retour

		final InputStream out = new BufferedInputStream(this.channel.getInputStream());
		n = out.read(b);
		String sortie;
		if (n > 0) {
			sortie = new String(b, 0, n);
		} else {
			sortie = "Le processus n'a pas renvoyé de sortie";
		}
		return sortie;
	}


	/*
	 *
	 *
	 *
	 *
	 */

	//
	// Test
	//

	//@Test
	public static void main(final String[] args)
			throws ErrorOnProcessExecutionException, WrongOSException{
		final SSHExecutor ssh = new SSHExecutor(
				"madinina",
				22);
		ssh.myInfo.setGate("gate-ia.lip6.fr");
		final String command1 = "echo $HOSTNAME";
		//		final String command2 = "set | grep SSH";
		//		final String command3 = "alias yo=\"echo yo\"";
		//		final String command4 = "yo";

		ssh.execute(command1);
	}
}

class MyUserInfo implements UserInfo, UIKeyboardInteractive {

	@Override
	public String getPassword() {
		return this.passwd;
	}

	@Override
	public boolean promptYesNo(final String str) {
		final Object[] options = { "yes", "no" };
		final int foo = JOptionPane.showOptionDialog(null, str, "Warning",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				options, options[0]);
		return foo == 0;
	}

	String passwd;
	JTextField passwordField = new JPasswordField(20);

	@Override
	public String getPassphrase() {
		return null;
	}

	@Override
	public boolean promptPassphrase(final String message) {
		return true;
	}

	@Override
	public boolean promptPassword(final String message) {
		final Object[] ob = { this.passwordField };
		final int result = JOptionPane.showConfirmDialog(null, ob, message,
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			this.passwd = this.passwordField.getText();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void showMessage(final String message) {
		JOptionPane.showMessageDialog(null, message);
	}

	final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1,
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(
					0, 0, 0, 0), 0, 0);
	private Container panel;

	@Override
	public String[] promptKeyboardInteractive(final String destination,
			final String name, final String instruction, final String[] prompt,
			final boolean[] echo) {
		this.panel = new JPanel();
		this.panel.setLayout(new GridBagLayout());

		this.gbc.weightx = 1.0;
		this.gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.gbc.gridx = 0;
		this.panel.add(new JLabel(instruction), this.gbc);
		this.gbc.gridy++;

		this.gbc.gridwidth = GridBagConstraints.RELATIVE;

		final JTextField[] texts = new JTextField[prompt.length];
		for (int i = 0; i < prompt.length; i++) {
			this.gbc.fill = GridBagConstraints.NONE;
			this.gbc.gridx = 0;
			this.gbc.weightx = 1;
			this.panel.add(new JLabel(prompt[i]), this.gbc);

			this.gbc.gridx = 1;
			this.gbc.fill = GridBagConstraints.HORIZONTAL;
			this.gbc.weighty = 1;
			if (echo[i]) {
				texts[i] = new JTextField(20);
			} else {
				texts[i] = new JPasswordField(20);
			}
			this.panel.add(texts[i], this.gbc);
			this.gbc.gridy++;
		}

		if (JOptionPane.showConfirmDialog(null, this.panel, destination + ": "
				+ name, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
			final String[] response = new String[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				response[i] = texts[i].getText();
			}
			return response;
		}
		else {
			return null; // cancel
		}
	}
}


//				String command = "echo $HOSTNAME";
//
//				String command=	JOptionPane.showInputDialog("Enter command","set|grep SSH");
//
//				Channel channel=session.openChannel("exec");
//				((ChannelExec)channel).setCommand(command);
//				channel.setInputStream(null);
//				channel.setOutputStream(System.out);
//				((ChannelExec)channel).setErrStream(System.err);
//
//				InputStream in=channel.getInputStream();
//				channel.connect();
//
//				byte[] tmp=new byte[1024];
//				while(true){
//					while(in.available()>0){
//						int i=in.read(tmp, 0, 1024);
//						if(i<0)break;
//						System.out.print(new String(tmp, 0, i));
//					}
//					if(channel.isClosed()){
//						System.out.println("ssh : closed! exit-status: "+channel.getExitStatus());
//						break;
//					}
//					try{Thread.sleep(1000);}catch(Exception ee){}
//				}
//
//				((ChannelExec)channel).setCommand("echo $HOSTNAME");
//
//				tmp=new byte[1024];
//				while(true){
//					while(in.available()>0){
//						int i=in.read(tmp, 0, 1024);
//						if(i<0)break;
//						System.out.print(new String(tmp, 0, i));
//					}
//					if(channel.isClosed()){
//						System.out.println("ssh : closed! exit-status: "+channel.getExitStatus());
//						break;
//					}
//					try{Thread.sleep(1000);}catch(Exception ee){}
//				}
//				channel.disconnect();
//				session.disconnect();
//				return channel;

//
//public void executeWithGate(final String gate, final String host, final String command,
//		final String privateKeyPath, final String knownHostPath,
//		String user) throws JSchException {//throws JSchException {
//
//	final JSch jsch = new JSch();
//
//	if (privateKeyPath != null)
//		try {
//			jsch.addIdentity(privateKeyPath);
//		} catch (JSchException e) {
//			// The user will be asked of its credentials
//		}
//
//		if (knownHostPath != null)
//			try {
//				jsch.setKnownHosts(knownHostPath);
//			} catch (JSchException e) {
//				// The user will be asked of its credentials
//			}
//
//			if (user == null)
//				user = System.getProperty("user.name");
//
//			final Session session = jsch.getSession(user, host, 22);
//			final UserInfo ui = new MyUserInfo();
//			session.setUserInfo(ui);
//
//
//		    //session.connect();
//		    session.connect(30000);   // making a connection with timeout.
//
//			System.out.println("\n * * SSH : " + host + " : Session connected");
//
//		    // Enable agent-forwarding.
//		    //((ChannelShell)channel).setAgentForwarding(true);
//
//			final Channel channel = session.openChannel("exec");
//			((ChannelExec) channel).setCommand(command);
//			System.out.println(" * * SSH : " + host + " : Command executed "
//					+ command);
//			channel.setInputStream(System.in);
//			((ChannelExec) channel).setOutputStream(System.out);
//			((ChannelExec) channel).setErrStream(System.err);
//
//			channel.connect();
//}
// final String binPath = myParameters.getDir(host)+"src/";// + "bin";
// final String libPath = myParameters.getDir(host)+"lib/*";

// String main =
// "cd "+binPath+"; "+
// "java -cp $CLASSPATH:"+ libPath +" "
// +SSH.class.getCanonicalName()+" ";
//
// String[] args = {
// myParameters.getPrivateKeyPath(),
// myParameters.getKnownHostsPath(),
// host, user, command};
//
// String gateCommand = main+args;
// public static void main(String[] args) throws JSchException{
// String privateKeyPath = args[0];
// String knownHostPath = args[1];
// String host = args[2];
// String user = args[3];
// String command = args[4];
// System.out.println("FROM MAIN : "+Arrays.asList(args));
// SSH s = new SSH();
// s.executeSSHCommand(host, command, privateKeyPath, knownHostPath, user);
// }

// try {
// //InputStream in=channel.getInputStream();
//
// //System.out.println("Execution de '"+command+"' sur l'hôte "+host);
//
// //byte[] tmp=new byte[1024];
// //while(true){
// //while(in.available()>0){
// // int i=in.read(tmp, 0, 1024);
// // if(i<0)break;
// // System.out.print(new String(tmp, 0, i));
// //}
// //if(channel.isClosed()){
// // System.out.println("exit-status: "+channel.getExitStatus());
// // break;
// //}
// //try{Thread.sleep(1000);}catch(Exception ee){}
// //}
// } catch (IOException e) {
// Logger.exception(this, "", e);
// }