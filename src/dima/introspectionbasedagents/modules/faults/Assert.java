package dima.introspectionbasedagents.modules.faults;

public class Assert {

	public static boolean Imply(boolean a, boolean b){
		return !a || b;
	}
	public static boolean IIF(boolean a, boolean b){
		return Imply(a,b) && Imply(b,a);
	}
}
