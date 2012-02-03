package dimaxx.hostcontrol;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import dima.introspectionbasedagents.services.loggingactivity.LogService;






public class Executor implements Serializable {
	private static final long serialVersionUID = -6786508858643326023L;

	//
	// Subclasses
	//

	/**
	 * This enum list the existing os name as reported by
	 * System.getProperty("os.name")
	 */
	public enum OperatingSystem {
		Linux, Windows, Mac;
	}

	/** Prototype des exception lancé par cette classe */
	public class ExecutorException extends Exception {
		private static final long serialVersionUID = 3738198391506059404L;
	}

	/**
	 * Cette exception est lancé quand on essaye de lancer une commande de
	 * l'éxcuteur non applicable à l'OS (e.g. Executeur.executeWithBash sous
	 * Windows)
	 */
	public class WrongOSException extends ExecutorException {
		private static final long serialVersionUID = 3738198391506059404L;
	}

	/** Cette erreur est lancé lorsque l'on n'a pas réussi a crée le process */
	public class ErrorOnProcessExecutionException extends ExecutorException {
		private static final long serialVersionUID = 7799515595704424248L;
	}

	//
	// Methods
	//

	/**
	 * The main method It analyse wich os is used and execute the appropriate
	 * method
	 *
	 * @throws ErrorOnProcessExecutionException
	 * @throws WrongOSException
	 */
	public String execute(final String cmd)
			throws ErrorOnProcessExecutionException {
		try {
			if (this.getOperatingSystem().equals(OperatingSystem.Linux) || this.getOperatingSystem().equals(OperatingSystem.Mac))
				return this.executeWithBash(cmd);
			else //case OperatingSystem.Windows:
				return  this.executeWithWindows(cmd);
		} catch (final WrongOSException e) {
			System.err.println("Impossible!!!!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * execute a command using linux process
	 *
	 * @param cmd
	 *            to be executed
	 * @return the ouptut of the process
	 * @throws ErrorOnProcessExecutionException
	 * @throws WrongOSException
	 */
	protected String executeWithBash(final String cmd)
			throws ErrorOnProcessExecutionException, WrongOSException {
		final Process p = this.getLinuxProcess();
		this.executeOnProcess(p, cmd);
		return this.getProcessOutput(p);
	}

	/**
	 * execute a command using windows process
	 *
	 * @param cmd
	 *            to be executed
	 * @return the ouptut of the process
	 * @throws ErrorOnProcessExecutionException
	 * @throws WrongOSException
	 */
	protected String executeWithWindows(final String cmd)
			throws ErrorOnProcessExecutionException, WrongOSException {
		final Process p = this.getWindowsProcess();
		this.executeOnProcess(p, cmd);
		return this.getProcessOutput(p);
	}

	//
	// Primitive
	//

	/**
	 * Execute a command on a process
	 *
	 * @return the process after the command being executed
	 */
	private Process executeOnProcess(final Process p, String cmd)
			throws ErrorOnProcessExecutionException {
		cmd += "\n";
		try {
			final OutputStream in = p.getOutputStream();
			in.write(cmd.getBytes());
			in.flush();
			return p;
		} catch (final Exception ex) {
			LogService.writeException(this, "Erreur execute commande ", ex);
			throw new ErrorOnProcessExecutionException();
		}
	}

	/**
	 * @return the current operating system
	 */
	protected OperatingSystem getOperatingSystem() {
		return OperatingSystem.valueOf(System.getProperty("os.name"));
	}

	/**
	 *
	 * @return a linux process
	 * @throws ErrorOnProcessExecutionException
	 * @throws WrongOSException
	 */
	private Process getLinuxProcess() throws ErrorOnProcessExecutionException,
	WrongOSException {
		if (!(this.getOperatingSystem().equals(OperatingSystem.Linux) || this
				.getOperatingSystem().equals(OperatingSystem.Mac)))
			throw new WrongOSException();
		try {
			return Runtime.getRuntime().exec("/bin/bash");
		} catch (final IOException ex) {
			LogService.writeException(this,
					"Erreur de creation de process ", ex);
			throw new ErrorOnProcessExecutionException();
		}
	}

	/**
	 * @return a windows process
	 * @throws ErrorOnProcessExecutionException
	 * @throws WrongOSException
	 */
	private Process getWindowsProcess()
			throws ErrorOnProcessExecutionException, WrongOSException {
		if (!this.getOperatingSystem().equals(OperatingSystem.Windows))
			throw new WrongOSException();
		try {
			// TODO
			return Runtime.getRuntime().exec("HUH!!! JEN SAIS RIEN!!!");
		} catch (final IOException ex) {
			LogService.writeException(this,
					"Erreur de creation de process ", ex);
			throw new ErrorOnProcessExecutionException();
		}
	}

	/**
	 *
	 * @return the ouptut of a process
	 * @throws ErrorOnProcessExecutionException
	 */
	private String getProcessOutput(final Process p)
			throws ErrorOnProcessExecutionException {
		final byte[] b = new byte[1024]; // Pour convertir la commande dans le
		// bon
		// format
		int n = 0; // Longueur de la chaine de retour

		final InputStream out = new BufferedInputStream(p.getInputStream());
		try {
			n = out.read(b);
			String sortie;
			if (n > 0)
				sortie = new String(b, 0, n);
			else
				sortie = "Le processus n'a pas renvoyé de sortie";
			return sortie;
		} catch (final IOException ex) {
			LogService.writeException(this, "Erreur lecture commande ", ex);
			throw new ErrorOnProcessExecutionException();
		}
	}

	public String executeWithBash(final String[] args)
			throws ErrorOnProcessExecutionException, WrongOSException {
		// A utiliser directement
		// dans le terminal

		int i;
		String cmd;

		cmd = "";
		for (i = 0; i < args.length; i++)
			cmd += args[i] + " ";
		cmd += "\n";
		String getBMips =
				"grep bogomips /proc/cpuinfo | cut -d: -f2 | sed -e \"s/ \\([0-9]*.[0-9]*\\)/\\1/\""
				; getBMips += " | sed -e \"2 s/\\([0-9]*.[0-9]*\\)//\" ";
				return this.executeWithBash(cmd);
	}


	public static void main(final String[] args) throws ErrorOnProcessExecutionException, WrongOSException{

		final Executor exec = new Executor();

		String getBMips =
				"grep bogomips /proc/cpuinfo | cut -d: -f2 | sed -e \"s/ \\([0-9]*.[0-9]*\\)/\\1/\"";
		getBMips += " | sed -e \"2 s/\\([0-9]*.[0-9]*\\)//\" ";

		//String getLatence = "ping " + args[0] + " -q -c 3 | grep rtt | cut -d/ -f5";

		final String cmd = getBMips;//getLatence;

		System.out.println("Commande : " + cmd);
		final String sortie = exec.executeWithBash(cmd);
		System.out.println("Sortie : " + sortie);
	}

}

/*
 * ANCIENNE VERSION :
 *
 * Cette classe permet d'executer des commandes du système d'exploitation En
 * l'état elle n'excute que des commande bash ce qui réduit son utilisation et
 * celle des classe fille à des plateformes Linux. Afin de rendre son
 * utilisation multi-plateforme, il s'agit de renseigner la table d'association
 * entre os et methode exemple : * Linux -> executeWithBash * Windows ->
 * executeWithMSDos * etc. Les classes filles devront lancer la commande
 * approprié avec le shell approprié
 *
 * La méthode propre est de tout d'abord rédiger une enum qui a un mot clé
 * associe la commande pour linux, la commande pour windows etc... Puis d'écrire
 * une fonction execute qui utilise se mot clé et, selon l'OS, appelle
 * executeWithBash ou executeWithMSDOS
 *
 * Une autre solution serait d'embarquer un programme C multi-plateforme (Voir
 * @HostPerfAnalyzer)
 */


/*@HostPerfAnalyzer:
 * Q : How do you determine CPU usage in Java?
 *
 * A : So, here is the good news and the bad news. The bad news is that
 * programmatically querying for CPU usage is impossible using pure Java. There
 * is simply no API for this. A suggested alternative might use Runtime.exec()
 * to determine the JVM's process ID (PID), call an external, platform-specific
 * command like ps, and parse its output for the PID of interest. But, this
 * approach is fragile at best.
 *
 * The good news, however, is that a reliable solution can be accomplished by
 * stepping outside Java and writing a few C code lines that integrate with the
 * Java application via Java Native Interface (JNI). I show below how easy it is
 * by creating a simple JNI library for the Win32 platform. (...) (@url
 * http://www.javaworld.com/javaworld/javaqa/2002-11/01-qa-1108-cpu.html#resources
 */


/*
 * public static void main(String[] args){
 *
 * Executeur exec = new Executeur();
 *
 * String getBMips =
 * "grep bogomips /proc/cpuinfo | cut -d: -f2 | sed -e \"s/ \\([0-9]*.[0-9]*\\)/\\1/\""
 * ; getBMips += " | sed -e \"2 s/\\([0-9]*.[0-9]*\\)//\" ";
 *
 * String getLatence = "ping " + args[0] + " -q -c 3 | grep rtt | cut -d/ -f5";
 *
 * String cmd = getLatence;
 *
 * System.out.println("Commande : " + cmd);
 * String sortie = exec.executeWithBash(cmd);
 * System.out.println("Sortie : " + sortie); }
 */

