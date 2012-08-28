package frameworks.faulttolerance.dcop.algo.korig;

import java.util.ArrayList;
import java.util.HashMap;

import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;

public class LocalInfo<Value> {
	int id;
	int value;
	int domain;
	ArrayList<double[]> data;
	HashMap<Integer, Integer> valMap;

	public LocalInfo(AbstractVariable<Value> v) {
		id = v.id;
		domain = v.getDomain();
		value = v.getValue();
		data = new ArrayList<double[]>();
		valMap = new HashMap<Integer, Integer>();
		for (AbstractConstraint<Value> c : v.neighbors) {
			AbstractVariable<Value> n = c.getNeighbor(v);
			valMap.put(n.id, n.getValue());
			data.add(c.encode());
		}
	}

	public int getSize() {
		int size = 0;
		for (double[] array : data)
			size += array.length * 4;
		return 12 + size + valMap.size() * 4;
	}
}
