package frameworks.faulttolerance.dcop;

import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;
import frameworks.faulttolerance.dcop.dcop.ClassicalConstraint;
import frameworks.faulttolerance.dcop.dcop.ClassicalVariable;
import frameworks.faulttolerance.dcop.dcop.DcopAbstractGraph;
import frameworks.faulttolerance.dcop.dcop.DcopClassicalGraph;

public class DcopFactory {

	public enum DCOPType { Classical, Negotiation}
	
	public final static  DCOPType type = DCOPType.Classical;

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
	
	public static AbstractConstraint constructConstraint(AbstractVariable a, AbstractVariable b){
		return new ClassicalConstraint(a, b);
	}
	public static AbstractVariable constructVariable(int i, int d, DcopAbstractGraph g){
		return new ClassicalVariable(i,d,g);
	}

	public static AbstractVariable constructVariable(String line,
			DcopAbstractGraph g) {
		return new ClassicalVariable(line,g);
	}

	public static boolean isClassical() {
		return type==DCOPType.Classical;
	}
}
