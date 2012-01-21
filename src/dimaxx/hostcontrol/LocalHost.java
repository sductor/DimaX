package dimaxx.hostcontrol;

import java.net.InetAddress;

import dima.introspectionbasedagents.services.core.loggingactivity.LogService;


public class LocalHost {

	public static Integer port = new Integer(22);


	public static String getUrl() {
		try {
			return InetAddress.getLocalHost().getHostName().toString();

		} catch (final java.net.UnknownHostException e) {
			LogService.writeException("could not retrieve local ip", e);
			return null;
		}
	}

	public static String getDir() {

		/*Going to root of an java app*/
		if (System.getProperty("user.dir").endsWith("/bin")
				|| System.getProperty("user.dir").endsWith("/src"))
			return System.getProperty("user.dir").substring(0,
					System.getProperty("user.dir").length() - 4)
					+ "/";
		else
			/* default*/
			return System.getProperty("user.dir") + "/";
	}

	public static String getConfDir() {
		return LocalHost.getDir()+"conf/";
	}
}
