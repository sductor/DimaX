package frameworks.faulttolerance.dcop.dcop;

import java.util.ArrayList;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import dima.basicagentcomponents.AgentIdentifier;
import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.rationality.AgentState;

public class MemFreeConstraint extends ReplicationConstraint {

	public MemFreeConstraint(ReplicationVariable a,
			ReplicationVariable b) {
		super(a, b);
	}


}


//@Override
//public double evaluate() {
//	if (first.getValue()!=-1){
//		((MemFreeVariable) first).instanciate();
//		if (!first.getState().isValid()){
//			((MemFreeVariable) first).instanciate();
//			System.out.println("²²²²²²²²²²f "+first.getValue()+" "+first.getState());
//		}
//	}
//	if (second.getValue()!=-1){
//		((MemFreeVariable) second).instanciate();
//		if (!second.getState().isValid()){
//			((MemFreeVariable) second).instanciate();
//			System.out.println("²²²²²²²²²²s "+second.getValue()+" "+second.getState());
//		}
//	}
//	if (!currentValuesAreConsistant()){
//		System.out.println("cons ("+first.id+","+second.id+") : "+DCOPFactory.intToSubset(first.getNeighborsIdentifiers(), first.getValue())
//				+" <=> "+DCOPFactory.intToSubset(second.getNeighborsIdentifiers(), second.getValue()));
//	}
//	if (first.getValue()!=-1){
//		((MemFreeVariable) first).instanciate();
//		if (!first.getState().isValid())
//			return Double.NEGATIVE_INFINITY;
//	}
//	if (second.getValue()!=-1){
//		((MemFreeVariable) second).instanciate();
//		if (!second.getState().isValid())
//			return Double.NEGATIVE_INFINITY;
//	}	
//	if (!currentValuesAreConsistant())
//		return Double.NEGATIVE_INFINITY;
//
//	System.out.println("yo");
//	double result;
//
//	if (getAgent().getValue()==-1){
//		getAgent().backupValue();
//		getAgent().setValue(getAgent().getDomain()-1);
//		result=getAgent().evaluate();
//		getAgent().recoverValue();
//	} else {
//		result=getAgent().evaluate();
//	}
//
//	return result;
//}