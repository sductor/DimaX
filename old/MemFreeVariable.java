package frameworks.faulttolerance.dcop.dcop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.faults.Assert;
import dima.kernel.INAF.InteractionDomain.Constraint;
import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.rationality.AgentState;

public class MemFreeVariable extends ReplicationVariable{

	public MemFreeVariable(int i, int d, AgentState s, DcopReplicationGraph g) {
		super(i, d, s, g);
		assert getValue()<getDomain();
		assert s!= null;
	}


	@Override
	public double evaluate() {
		assert getValue()<getDomain():getValue()+" "+getDomain()+" "+getNeighborsIdentifiers();

		if (getValue()==-1){

			if (s instanceof HostState)
				return 0;
			else if (s instanceof ReplicaState){
				double result;
				setValue(getDomain()-1);
				instanciate();
				result= ((ReplicaState)s).getMyReliability();
				setValue(-1);
				return result;
			}	else
				throw new RuntimeException();

		} else {
			instanciate();

			if (!s.isValid())
				return Double.NEGATIVE_INFINITY;


			if (s instanceof HostState)
				return 0;
			else if (s instanceof ReplicaState)
				return ((ReplicaState)s).getMyReliability();
			else
				throw new RuntimeException();
		}
	}
	public static double evaluate() {
		assert getValue()<getDomain():getValue()+" "+getDomain()+" "+getNeighborsIdentifiers();

		if (getValue()==-1){

			if (s instanceof HostState)
				return 0;
			else if (s instanceof ReplicaState){
				double result;
				setValue(getDomain()-1);
				instanciate();
				result= ((ReplicaState)s).getMyReliability();
				setValue(-1);
				return result;
			}	else
				throw new RuntimeException();

		} else {
			instanciate();

			if (!s.isValid())
				return Double.NEGATIVE_INFINITY;


			if (s instanceof HostState)
				return 0;
			else if (s instanceof ReplicaState)
				return ((ReplicaState)s).getMyReliability();
			else
				throw new RuntimeException();
		}
	}
	//
	//
	//

	private void instanciate(){
		//		System.out.println("*********************************beginning instanciating "+this+" value is "+getValue()+" : "+Integer.toBinaryString(getValue()));
		assert getValue()!=-1;
		assert getValue()<getDomain():getValue()+" "+domain;
		assert Assert.Imply(getDomain()!=1,getDomain()==(int)Math.pow(2, getNeighbors().size())):id+" "+getValue()+" "+getDomain()+" "+getNeighbors();
		assert Integer.toBinaryString(getValue()).length()<=getDegree():
			id+" "+getValue()+" \n"+getNeighbors()+" "+getDomain();
		assert (s instanceof ReplicaState)|| (s instanceof HostState):s;
		if (s instanceof ReplicaState){
			s = new ReplicaState(s.getMyAgentIdentifier(), ((ReplicaState) s).getMyCriticity(), 
					((ReplicaState) s).getMyProcCharge(), ((ReplicaState) s).getMyMemCharge(), 
					((ReplicaState) s).getSocialWelfare());
		}else if (s instanceof HostState){
			s = new HostState(
					((HostState) s).getMyAgentIdentifier(), 
					((HostState) s).getProcChargeMax(), ((HostState) s).getMemChargeMax(), 
					((HostState) s).getLambda());
		}else
			throw new RuntimeException(s.toString());
		//		System.out.println("initial --- "+s);
		//		System.out.println("ressource --- "+getNeighborsIdentifiers());
		Collection<AgentState> toAllocate = DCOPFactory.intToSubset(getNeighborsStates(), getValue());
		//		System.out.println("toAllocate : "+toAllocate);
		for (AgentState ress : toAllocate)	
			if (s instanceof ReplicaState){				
				s =  ((ReplicaState)s).allocate((HostState) ress);
				//				System.out.println(s);
			}
			else if (s instanceof HostState){
				s =  ((HostState)s).allocate((ReplicaState) ress);
				//				System.out.println(s);
			}else
				throw new RuntimeException();
		//		System.out.println("*********************************ending instanciating "+this);
	}

}








////for (int i = 0; i < alloc.length(); i++)
////if (alloc.charAt(i)=='1'){
////	if (s instanceof ReplicaState)
////		s =  ((ReplicaState)s).allocate((HostState) getStateOfNeighborNumber(i));
////	else if (s instanceof HostState)
////		s =  ((HostState)s).allocate((ReplicaState) getStateOfNeighborNumber(i));
////	else
////		throw new RuntimeException();
////}
//
//int pos = getDegree()-1;
//for (int j =alloc.length()-1; j > 0; j--){
//if (alloc.charAt(j)=='1') {
//	if (s instanceof ReplicaState)
//		s =  ((ReplicaState)s).allocate((HostState) getStateOfNeighborNumber(pos));
//	else if (s instanceof HostState)
//		s =  ((HostState)s).allocate((ReplicaState) getStateOfNeighborNumber(pos));
//	else
//		throw new RuntimeException();
//}
//pos--;
//}
//}
//
//@Override
//public int getValue() {
//	if (!instanciated)
//		return -1;
//
//	StringBuilder v = new StringBuilder();
//	for (int i = 0; i < getNeighbors().size(); i++)
//		if (s.getMyResourceIdentifiers().contains(getIdentifierOfNeighborNumber(i)))
//			v.append('1');
//		else
//			v.append('0');
//	return Integer.parseInt(v.toString(),2);
//}
//
//@Override
//public void setValue(int value) {
//	if (value==-1){
//		instanciated=false;
//	} else {
//
//		instanciated=true;
//
//}