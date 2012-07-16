package examples.dcop2.dcop;

import java.util.Random;


public class Helper {
	public static final String newline = System.getProperty("line.separator");

//	public static DCOPApplication app;

	public static Random random = new Random();
	public static void setSeed(final long seed){
		Helper.random.setSeed(seed);
	}
}
