package vieux.dcop.save.dcop;

import java.util.Random;

import vieux.dcop.save.exec.DCOPApplication;


public class Helper {
	public static final String newline = System.getProperty("line.separator");
	
	public static DCOPApplication app;
	
	public static Random random = new Random();
	public static void setSeed(long seed){
		random.setSeed(seed);
	}
}
