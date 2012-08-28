package frameworks.faulttolerance.dcop;

import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.ClassicalConstraint;
import frameworks.faulttolerance.dcop.dcop.ClassicalVariable;
import frameworks.faulttolerance.dcop.dcop.DcopAbstractGraph;
import frameworks.faulttolerance.dcop.dcop.DcopClassicalGraph;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;

public class DCOPFactory {

	public static DcopAbstractGraph constructDCOPGraph(){
//		return new DcopAbstractGraph();
		return new DcopClassicalGraph();
	}

	public static DcopAbstractGraph constructDCOPGraph(String filename) {
//		return new DcopAbstractGraph(filename);
//			return new DcopClassicalGraph(filename);
		try {
			return new DcopClassicalGraph("yo", 566668, 
					4, 2,//nbAgent,nbHost 
					0.5, DispersionSymbolicValue.Moyen, //criticity
					0.25, DispersionSymbolicValue.Moyen, //agent load
					1., DispersionSymbolicValue.Nul, //hostCap
					0.5, DispersionSymbolicValue.Moyen, //hostDisp
					true,2);
		} catch (IfailedException e) {
			e.printStackTrace();
			throw new RuntimeException("arrggh");
		}
	}

	public static boolean isClassical(){
		return true;
	}
	public  static AbstractConstraint constructConstraint(AbstractVariable a, AbstractVariable b){
		return new ClassicalConstraint(a, b);
	}
	public  static AbstractVariable constructVariable(int i, int d, DcopAbstractGraph g){
		return new ClassicalVariable(i,d,g);
	}
	public  static AbstractVariable constructVariable(String s, DcopAbstractGraph g){
		return new ClassicalVariable(s,g);
	}

	public static String[] getArgs() {
		return new String[]{"conf/1.dcop","","1","50"};
	}
}
