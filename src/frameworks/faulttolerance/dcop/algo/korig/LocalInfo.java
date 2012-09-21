package frameworks.faulttolerance.dcop.algo.korig;

import java.util.ArrayList;
import java.util.HashMap;

<<<<<<< HEAD
import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;
=======
import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.negotiation.rationality.AgentState;
>>>>>>> dcopX

public class LocalInfo {
	int id;
	public AgentState state;
	int value;
	int domain;
	ArrayList<double[]> data;
	HashMap<Integer,AgentState>dataStates;
	HashMap<Integer, Integer> valMap;

<<<<<<< HEAD
	public LocalInfo(AbstractVariable v) {
=======
	public LocalInfo(ReplicationVariable v) {
>>>>>>> dcopX
		id = v.id;
		domain = v.getDomain();
		state = v.getState();
		value = v.getValue();
		data = new ArrayList<double[]>();
		if (!DCOPFactory.isClassical())
			dataStates=new HashMap<Integer, AgentState>();
		valMap = new HashMap<Integer, Integer>();
<<<<<<< HEAD
		for (AbstractConstraint c : v.neighbors) {
			AbstractVariable n = c.getNeighbor(v);
			valMap.put(n.id, n.value);
=======
		for (MemFreeConstraint c : v.getNeighbors()) {
			ReplicationVariable n = c.getNeighbor(v);
			valMap.put(n.id, n.getValue());
>>>>>>> dcopX
			data.add(c.encode());

			if (!DCOPFactory.isClassical()){
				dataStates.put(c.first.id, c.first.getState());
				dataStates.put(c.second.id, c.second.getState());
			}
		}
	}

	public int getSize() {
		int size = 0;
		for (double[] array : data)
			size += array.length * 4;
		return 12 + size + valMap.size() * 4;
	}
}
