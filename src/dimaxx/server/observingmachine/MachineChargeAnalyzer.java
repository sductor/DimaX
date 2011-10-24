package dimaxx.server.observingmachine;

import java.util.Collection;
import java.util.List;

import dima.introspectionbasedagents.coreservices.loggingactivity.LogCompetence;
import dimaxx.hostcontrol.Executor;
import dimaxx.server.HostIdentifier;

/**
 * Cette classe fournit des informations sur les caractéristiques et
 * l'utilisation actuelle des ressources d'une machine. En l'état,
 * l'implémentation ne fonctinone que sur un système d'exploitation Linux.
 * Néanmoins il est possible de l'étendre en étendant la classe mère Executeur
 * (cf tools.Executeur) et en fournissant les commandes approprié à chaque OS.
 *
 * @author Sylvain Ductor
 *
 */
/*
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
 * http://www
 * .javaworld.com/javaworld/javaqa/2002-11/01-qa-1108-cpu.html#resources
 */

public class MachineChargeAnalyzer extends Executor implements
		MachineSelfChargeAnalyzable {

	// /**
	// * Cette classe stockent des information sur la machine (type et puissance
	// * du processeur, os,...)
	// *
	// * @author Sylvain Ductor
	// *
	// */
	// public class QoS {
	//
	// }

	/**
	 *
	 */
	private static final long serialVersionUID = 2070040886473835642L;
	private final int nbMessageUsedForLatencyCalculation;

	public MachineChargeAnalyzer(final int nbMessageUsedForLatencyCalculation) {
		this.nbMessageUsedForLatencyCalculation = nbMessageUsedForLatencyCalculation;
	}

	public MachineChargeAnalyzer() {
		this.nbMessageUsedForLatencyCalculation = 5;
	}

	/*
	 * Le paramètre nbmessage représente le nombre de message envoyé par le
	 * ping. La valeur retourné étant moyenne plus nbmessage est élevé plus la
	 * valeur de retour est précise mais plus la méthode est couteuse en temps
	 * et en message
	 */
	public double getLatencyTo(final String hostUrl) {
		final String getLatence = "ping " + hostUrl + " -q -c "
				+ this.nbMessageUsedForLatencyCalculation
				+ " | grep rtt | cut -d/ -f5";
		String latence;
		try {
			latence = this.executeWithBash(getLatence);
		} catch (final ExecutorException e) {
			latence = "0";
			// myLog.exception(e.toString() + " latence instancié a 0");
		}
		return new Double(latence).doubleValue();
	}

	public double getGlobalLatencyTo(final Collection<HostIdentifier> machines) {
		Double latence = new Double(0);
		for (final HostIdentifier machine : machines) {
			final String hostUrl = machine.getUrl();
			final String getLatence = "ping " + hostUrl + " -q -c "
					+ this.nbMessageUsedForLatencyCalculation
					+ " | grep rtt | cut -d/ -f5";
			try {
				latence = Math.max(latence, new Double(this
						.executeWithBash(getLatence)).doubleValue());
			} catch (final ExecutorException e) {
				latence = 0.;
				LogCompetence.writeException(this, e.toString()
						+ " latence instancié a 0", e);
			}
		}
		return latence;
	}

	public double getGlobalLatencyTo(final List<HostIdentifier> machines) {
		Double latence = new Double(0);
		for (final HostIdentifier machine : machines) {
			final String hostUrl = machine.getUrl();
			final String getLatence = "ping " + hostUrl + " -q -c "
					+ this.nbMessageUsedForLatencyCalculation
					+ " | grep rtt | cut -d/ -f5";
			try {
				latence = Math.max(latence, new Double(this
						.executeWithBash(getLatence)).doubleValue());
			} catch (final ExecutorException e) {
				latence = 0.;
				LogCompetence.writeException(this, e.toString()
						+ " latence instancié a 0", e);
			}
		}
		return latence;
	}

	/**
	 * @return the value of actual free memory in the jvm
	 */
	public long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	/**
	 * @return the value of actual total memory for the jvm
	 */
	public long getTotalMemory() {
		final long maxMemory = Runtime.getRuntime().maxMemory();
		if (maxMemory == Long.MAX_VALUE) {
			final String gettotalMem = "grep MemTotal /proc/meminfo | cut -d: -f2 | sed -e \"s/ \\([0-9]*.[0-9]*\\)/\\1/\""
					+ "| cut -d: -f2 | sed -e \"s/\\([0-9]*.[0-9]*\\) kB/\\1/\"";
			String totalMem;
			try {
				totalMem = this.executeWithBash(gettotalMem);
			} catch (final ExecutorException e) {
				totalMem = "1";
				LogCompetence.writeException(this,
						" totalMemory instancié à 1", e);
			}
			totalMem = totalMem.trim();
			return new Long(totalMem).longValue();
		} else
			return maxMemory;
	}

	public Double getAbsoluteMemoryCharge() {
		final long charge = (1 - this.getFreeMemory()) / this.getTotalMemory();
		return new Double(charge);
	}

	/**
	 * @return the value of average processor charge (over 1 minute)
	 */
	public Double getAverageProcessorCharge() {
		/*
		 * Le fichier /proc/loadAverage fournit la moyenne de charge processeur
		 * sur la derniere minute, les 5 ou les 10 dernières minutes. Nous
		 * conservons ici la moyenne sur la dernières minutes Les valeurs sont
		 * ramené à 1 pour chaque processeurs puis sommer Nous les divisons
		 * alors par le nombre de processeurs
		 */
		final String cmd = "cat /proc/loadavg";
		String loadavg;
		String avgProc;
		try {
			loadavg = this.executeWithBash(cmd);
			avgProc = loadavg.split(" ")[1];// 0 : la derniere minute
			// 1 : les 5 dernieres
			// 2 : les 10 dernieres
		} catch (final ExecutorException e) {
			avgProc = "0";
			LogCompetence.writeException(this,
					" charge processeur instancié a 0", e);
		}
		final long charge = new Double(avgProc).longValue()
				/ Runtime.getRuntime().availableProcessors();
		return new Double(charge);
	}

	public double getMyBogoMips() {
		// Cette valeur est totalement propre à une machine
		// Linux, il n'est donc pas souhiatable de
		// l'utiliser
		String getBMips = "grep bogomips /proc/cpuinfo | cut -d: -f2 | sed -e \"s/ \\([0-9]*.[0-9]*\\)/\\1/\"";
		getBMips += " | sed -e \"2 s/\\([0-9]*.[0-9]*\\)//\" ";
		final int nbProc = Runtime.getRuntime().availableProcessors();
		for (int i = 2; i <= nbProc; i++)
			getBMips += " | sed -e \"" + i + " s/\\([0-9]*.[0-9]*\\)//\" ";
		String bMips;
		try {
			bMips = this.executeWithBash(getBMips);
		} catch (final ExecutorException e) {
			bMips = "0";
			LogCompetence.writeException(this, e.toString()
					+ " bogoMips instancié a 0");
		}
		final double perf = new Double(bMips).doubleValue();

		return nbProc * perf;
	}

	public class RealCharge implements Charge {

		@Override
		public Double getAbsoluteCharge() {
			return Math.max(this.getMemoryCharge(), this.getProcessorCharge());
		}

		@Override
		public Double getMemoryCharge() {
			return MachineChargeAnalyzer.this.getAbsoluteMemoryCharge();
		}

		@Override
		public Double getProcessorCharge() {
			return MachineChargeAnalyzer.this.getAverageProcessorCharge();
		}

	}

	@Override
	public Charge getMyCharge() {
		return new RealCharge();
	}

	// /**
	// * @return les caractéristiques de la machines
	// */
	// public QoS getMyQoS() {
	// return new QoS();
	// }

	public static void main(final String[] args) {
		final MachineChargeAnalyzer hpa = new MachineChargeAnalyzer(5);
		System.out.println("OS = '" + hpa.getOperatingSystem() + "'");
		System.out.println("Tot Mem : " + hpa.getTotalMemory());
		System.out.println("Free Mem : " + hpa.getFreeMemory());
		System.out.println("Mem Charge: " + hpa.getAbsoluteMemoryCharge());
		System.out.println("avg Proc : " + hpa.getAverageProcessorCharge());
		System.out.println("BgM : " + hpa.getMyBogoMips());
	}

}
