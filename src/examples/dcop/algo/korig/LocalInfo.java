package examples.dcop.algo.korig;

import java.util.ArrayList;
import java.util.HashMap;

import examples.dcop.dcop.Constraint;
import examples.dcop.dcop.Variable;



public class LocalInfo {
	int id;
	int value;
	int domain;
	ArrayList<int[]> data;
	HashMap<Integer, Integer> valMap;

	public LocalInfo(Variable v) {
		id = v.id;
		domain = v.domain;
		value = v.value;
		data = new ArrayList<int[]>();
		valMap = new HashMap<Integer, Integer>();
		for (Constraint c : v.neighbors) {
			Variable n = c.getNeighbor(v);
			valMap.put(n.id, n.value);
			data.add(c.encode());
		}
	}

	public int getSize() {
		int size = 0;
		for (int[] array : data)
			size += array.length * 4;
		return 12 + size + valMap.size() * 4;
	}
}
