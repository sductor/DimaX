package negotiation.dcopframework.dcop;

import java.util.Random;

import negotiation.dcopframework.exec.DCOPLaborantin;


public class Helper {
	public static final String newline = System.getProperty("line.separator");
	
	public static DCOPLaborantin app;
	
	public static Random random = new Random();
	public static void setSeed(long seed){
		random.setSeed(seed);
	}
}
