package frameworks.faulttolerance.dcop.dcop;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.faults.Assert;
import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class ReplicationVariable {

	//	final DcopReplicationGraph graph;

	public int id;
	protected final int domain;
	private int value;
	private int _value;
	private ArrayList<MemFreeConstraint> neighbors;
	public boolean fixed;
	AgentState s;

	final SocialChoiceType socialWelfare;

	public ReplicationVariable(int i, int d, AgentState s, DcopReplicationGraph g) {
		assert g!=null;
		id = i;
		socialWelfare=g.getSocialWelfare();
		//		graph = g;
		this.s=s;
		domain=d;
		neighbors=new ArrayList<MemFreeConstraint>();
		fixed = false;
		setValue(-1);
	}

	public AgentIdentifier getAgentIdentifier() {
		return DCOPFactory.intToIdentifier(id);
	}

	public  int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		if (value!=-1)
			instanciateValueInState();
	}


	public int getInitialValue() {
		if (getState()!=null){
			return DCOPFactory.subsetToInt(getNeighborsIdentifiers(), s.getMyResourceIdentifiers());
		} else {
			assert false;
			return Helper.random.nextInt(getDomain());
		}
	}

	public Collection<MemFreeConstraint> getNeighbors() {
		return Collections.unmodifiableCollection(neighbors);
	}
	
	public List<? extends AgentIdentifier> getNeighborsIdentifiers() {
		List<AgentIdentifier> result = new ArrayList<AgentIdentifier>();
		for (int i = 0; i < neighbors.size(); i++){
			boolean b = result.add(getIdentifierOfNeighborNumber(i));
			assert b;
		}
		return result;
	}


	public List<AgentState> getNeighborsStates() {
		List<AgentState> result = new ArrayList<AgentState>();
		for (int i = 0; i < neighbors.size(); i++){
			boolean b = result.add(getStateOfNeighborNumber(i));
			assert b;
		}
		return result;
	}
	public AgentState getState() {
		return s;
	}

	public SocialChoiceType getSocialWelfare() {
		return socialWelfare;
	}

	public  int getDomain(){
		return domain;
	}

	public void backupValue() {
		_value = getValue();
	}

	public void recoverValue() {
		setValue(_value);
	}

	public boolean addConstraint(MemFreeConstraint c) {
		boolean b =neighbors.add(c);
		Collections.sort(neighbors);
		return b;
	}

	public boolean hasNeighbor(int nid) {
		for (MemFreeConstraint c : getNeighbors()) {
			if (c.getNeighbor(this).id == nid)
				return true;
		}
		return false;
	}

	public MemFreeConstraint getNeighbor(int nid) {
		for (MemFreeConstraint c : getNeighbors()) {
			if (c.getNeighbor(this).id == nid)
				return c;
		}
		return null;
	}

	public AgentIdentifier getIdentifierOfNeighborNumber(int numero){
		assert numero<neighbors.size():numero+" "+neighbors;
		return DCOPFactory.intToIdentifier(neighbors.get(numero).getNeighbor(this).id);
	}

	public AgentState getStateOfNeighborNumber(int numero){
		return neighbors.get(numero).getNeighbor(this).getState();
	}

	public int getDegree() {
		return getNeighbors().size();
	}
	
	public Collection<? extends AgentIdentifier> getAllocatedRessources(){
		assert getValue()<getDomain():getValue()+" "+getDomain()+" "+getNeighborsIdentifiers();
		assert getValue()!=-1;		
		return getState().getMyResourceIdentifiers();
	}

	public Collection<? extends AgentIdentifier> getAllocatedRessources(int value){
		assert value<getDomain():value+" "+getDomain()+" "+getNeighborsIdentifiers();
		String alloc = Integer.toBinaryString(value);
		Collection<AgentIdentifier> result = new HashSet<AgentIdentifier>();
		for (int i = 0; i < alloc.length(); i++)
			if (alloc.charAt(i)=='1')
				result.add(getIdentifierOfNeighborNumber(i));
		return result;
	}

	public int getValue(Collection<AgentIdentifier> ress){
		return DCOPFactory.subsetToInt(getNeighborsIdentifiers(), ress);
	}
	
	public boolean hasAllocatedRessource(AgentIdentifier ress, int value){
		//		assert allKnownRess.contains(ress):get+" 0\n"+ress;
		assert getNeighborsIdentifiers().contains(ress):this+" "+neighbors+" "+value+" "+ress+" "+getNeighborsIdentifiers();
		String alloc = Integer.toBinaryString(value);

		for (int i = 0; i < alloc.length(); i++)
			if (getIdentifierOfNeighborNumber(i).equals(ress))				
				return alloc.charAt(i)=='1';

		return false;
	}

	public double evaluate() {
		assert getValue()<getDomain():getValue()+" "+getDomain()+" "+getNeighborsIdentifiers();

		if (getValue()==-1){

			if (s instanceof HostState)
				return 0;
			else if (s instanceof ReplicaState){
				double result;
				setValue(getDomain()-1);
				result= ((ReplicaState)s).getMyReliability();
				setValue(-1);
				return result;
			}	else
				throw new RuntimeException();

		} else {

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
	
	/*
	 * 
	 */


	public void clear() {
		if (!fixed)
			setValue(-1);
	}

	public String toString() {
		return "VARIABLE " + id + " 1 " + getDomain() + Helper.newline;
	}

	/*
	 * 
	 */
	
	private void instanciateValueInState(){
		//		System.out.println("*********************************beginning instanciating "+this+" value is "+getValue()+" : "+Integer.toBinaryString(getValue()));
		assert getValue()!=-1;
		assert getValue()<getDomain():getValue()+" "+domain;
//		assert Assert.Imply(getDomain()!=1,getDomain()==(int)Math.pow(2, getNeighbors().size())):id+" "+getValue()+" "+getDomain()+" "+getNeighbors();
//		assert Integer.toBinaryString(getValue()).length()<=getDegree():
//			id+" "+getValue()+" \n"+getNeighbors()+" "+getDomain();
		assert (s instanceof ReplicaState)|| (s instanceof HostState):s;
		if (s instanceof ReplicaState){
			s = new ReplicaState(s.getMyAgentIdentifier(), ((ReplicaState) s).getMyCriticity(), 
					((ReplicaState) s).getMyProcCharge(), ((ReplicaState) s).getMyMemCharge(), 
					((ReplicaState) s).getSocialWelfare());
		}else if (s instanceof HostState){
			s = new HostState(
					((HostState) s).getMyAgentIdentifier(), 
					((HostState) s).getProcChargeMax(), 
					((HostState) s).getMemChargeMax(), 
					((HostState) s).getLambda());
		}else
			throw new RuntimeException(s.toString());
		//		System.out.println("initial --- "+s);
		//		System.out.println("ressource --- "+getNeighborsIdentifiers());
		Collection<? extends AgentState> toAllocate = DCOPFactory.intToSubset(getNeighborsStates(), getValue());
		//		System.out.println("toAllocate : "+toAllocate);
			if (s instanceof ReplicaState){				
				s =  ((ReplicaState)s).allocateAll((Collection<HostState>) toAllocate);
				//				System.out.println(s);
			}
			else if (s instanceof HostState){
				s =  ((HostState)s).allocateAll((Collection<ReplicaState>) toAllocate);
				//				System.out.println(s);
			}else
				throw new RuntimeException();
		//		System.out.println("*********************************ending instanciating "+this);
	}
	
}


///*
//* Static
//*/
//
//public static double evaluate(ReplicationVariable v) {
//	assert v.getValue()<v.getDomain():v.getValue()+" "+v.getDomain()+" "+v.getNeighborsIdentifiers();
//	AgentState s  = v.getState();
//	if (v.getValue()==-1){
//
//		if (s instanceof HostState)
//			return 0;
//		else if (s instanceof ReplicaState){
//			double result;
//			v.setValue(v.getDomain()-1);
//			instanciate(v);
//			result= ((ReplicaState)s).getMyReliability();
//			v.setValue(-1);
//			return result;
//		}	else
//			throw new RuntimeException();
//
//	} else {
//		instanciate(v);
//
//		if (!s.isValid())
//			return Double.NEGATIVE_INFINITY;
//
//
//		if (s instanceof HostState)
//			return 0;
//		else if (s instanceof ReplicaState)
//			return ((ReplicaState)s).getMyReliability();
//		else
//			throw new RuntimeException();
//	}
//}
////
////
////
//
//private static void instanciate(ReplicationVariable v){
//	//		System.out.println("*********************************beginning instanciating "+this+" value is "+getValue()+" : "+Integer.toBinaryString(getValue()));
//	assert v.getValue()!=-1;
//	assert v.getValue()<v.getDomain():v.getValue()+" "+v.getDomain();
//	assert Assert.Imply(v.getDomain()!=1,v.getDomain()==(int)Math.pow(2, v.getNeighbors().size())):
//		v.id+" "+v.getValue()+" "+v.getDomain()+" "+v.getNeighbors();
//	assert Integer.toBinaryString(v.getValue()).length()<=v.getDegree():
//		v.id+" "+v.getValue()+" \n"+v.getNeighbors()+" "+v.getDomain();
//	AgentState s  = v.getState();
//	assert (s instanceof ReplicaState)|| (s instanceof HostState):s;
//	if (s instanceof ReplicaState){
//		s = new ReplicaState(s.getMyAgentIdentifier(), ((ReplicaState) s).getMyCriticity(), 
//				((ReplicaState) s).getMyProcCharge(), ((ReplicaState) s).getMyMemCharge(), 
//				((ReplicaState) s).getSocialWelfare());
//	}else if (s instanceof HostState){
//		s = new HostState(
//				((HostState) s).getMyAgentIdentifier(), 
//				((HostState) s).getProcChargeMax(), ((HostState) s).getMemChargeMax(), 
//				((HostState) s).getLambda());
//	}else
//		throw new RuntimeException(s.toString());
//	//		System.out.println("initial --- "+s);
//	//		System.out.println("ressource --- "+getNeighborsIdentifiers());
//	Collection<AgentState> toAllocate = DCOPFactory.intToSubset(v.getNeighborsStates(), v.getValue());
//	//		System.out.println("toAllocate : "+toAllocate);
//	for (AgentState ress : toAllocate)	
//		if (s instanceof ReplicaState){				
//			s =  ((ReplicaState)s).allocate((HostState) ress);
//			//				System.out.println(s);
//		}
//		else if (s instanceof HostState){
//			s =  ((HostState)s).allocate((ReplicaState) ress);
//			//				System.out.println(s);
//		}else
//			throw new RuntimeException();
//	//		System.out.println("*********************************ending instanciating "+this);
//}