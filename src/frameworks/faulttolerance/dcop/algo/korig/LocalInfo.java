package frameworks.faulttolerance.dcop.algo.korig;

import java.util.ArrayList;
import java.util.HashMap;

import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;

public class LocalInfo {
	int id;
	int value;
	int domain;
	ArrayList<double[]> data;
	HashMap<Integer, Integer> valMap;

	public LocalInfo(AbstractVariable v) {
		id = v.id;
		domain = v.domain;
		value = v.value;
		data = new ArrayList<double[]>();
		valMap = new HashMap<Integer, Integer>();
		for (AbstractConstraint c : v.neighbors) {
			AbstractVariable n = c.getNeighbor(v);
			valMap.put(n.id, n.value);
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
