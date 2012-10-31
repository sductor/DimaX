package dima.introspectionbasedagents.modules.faults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Assert {

	public static boolean Imply(Boolean a, Boolean b){
		assert a!=null && b!=null:a+" "+b;
		return !a || b;
	}
	public static boolean IIF(Boolean a, Boolean b){
		return Imply(a,b) && Imply(b,a);//en fait c a.equals(b) tout simplement...
	}
	
	public static <T> boolean allDiferent(Collection<T> items ){
		List<T> itemsList = new ArrayList<T>();
		itemsList.addAll(items);
		for (int i = 0; i < itemsList.size(); i++){
			for (int j = i+1; j < itemsList.size(); j++){
				T c1 = itemsList.get(i);
				T c2 = itemsList.get(j);
				assert !c1.equals(c2):"wrong !!! \n -------> hash are :"+c1.hashCode()+" "+c2.hashCode()+"\n ------------------- "+c1+" equals "+c2;
			}
		}
		return true;
	}
}
