package frameworks.faulttolerance.olddcop.dcop;

import java.util.List;

import dima.basicagentcomponents.AgentIdentifier;
import frameworks.faulttolerance.olddcop.DCOPFactory;
import frameworks.negotiation.contracts.ResourceIdentifier;


public class MemFreeConstraint implements Comparable<MemFreeConstraint>{

	//	DcopReplicationGraph graph;

	public ReplicationVariable first;
	public ReplicationVariable second;

	public int d1;
	public int d2;

	public MemFreeConstraint(ReplicationVariable a, ReplicationVariable b) {
		//		assert a.graph == b.graph;
		first = a;
		second = b;
		first.addConstraint(this);
		second.addConstraint(this);
		//		graph = a.graph;
		d1 = a.getDomain();
		d2 = b.getDomain();
	}

	public ReplicationVariable getNeighbor(ReplicationVariable v) {
		if (v == first)
			return second;
		if (v == second)
			return first;
		return null;
	}
	public int getNeighbor(int vid) {
		if (vid == first.id)
			return second.id;
		if (vid == second.id)
			return first.id;
		return -1;
	}

	public ReplicationVariable getHost(){
		if (DCOPFactory.intToIdentifier(first.id) instanceof ResourceIdentifier)
			return first;
		else
			return second;
	}

	public ReplicationVariable getAgent(){
		if (DCOPFactory.intToIdentifier(first.id) instanceof ResourceIdentifier)
			return second;
		else
			return first;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CONSTRAINT ");
		buffer.append(first.id);
		buffer.append(" ");
		buffer.append(second.id);
		buffer.append(Helper.newline);
		return buffer.toString();
	}

	public double[] encode() {
		double[] msg = new double[4];
		msg[0] = first.id;
		msg[1] = d1;
		msg[2] = second.id;
		msg[3] = d2;
		return msg;
	}

	public double evaluate() {

		return Math.max(getAgent().evaluate(), getHost().evaluate());
	}

	public double evaluate(Integer val1, Integer val2) {
		double result;
		first.backupValue();
		second.backupValue();
		first.setValue(val1);
		second.setValue(val2);
		result = evaluate();
		first.recoverValue();
		second.recoverValue();
		return result;
	}

	public int compareTo(MemFreeConstraint that){
		return (this.first.id+this.second.id)-(that.first.id-that.second.id);
	}

	/*
	 * 
	 */
	public boolean valuesAreConsistant (int firstValue,	int secondValue){
		if (firstValue==-1 || secondValue==-1)
			return true;
		else
			return (first.hasAllocatedRessource(DCOPFactory.intToIdentifier(second.id), firstValue) &&
					second.hasAllocatedRessource(DCOPFactory.intToIdentifier(first.id), secondValue))
					|| (!first.hasAllocatedRessource(DCOPFactory.intToIdentifier(second.id), firstValue) &&
							!second.hasAllocatedRessource(DCOPFactory.intToIdentifier(first.id), secondValue));
	}
	public boolean currentValuesAreConsistant (){
		return valuesAreConsistant(first.getValue(),second.getValue());
	}
}
