package dima.introspectionbasedagents.modules.faults;

public class Assert {

	public static boolean Imply(Boolean a, Boolean b){
		assert a!=null && b!=null:a+" "+b;
		return !a || b;
	}
	public static boolean IIF(Boolean a, Boolean b){
		return Imply(a,b) && Imply(b,a);
	}
}
