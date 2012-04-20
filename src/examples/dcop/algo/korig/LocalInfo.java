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

	public LocalInfo(final Variable v) {
		this.id = v.id;
		this.domain = v.domain;
		this.value = v.value;
		this.data = new ArrayList<int[]>();
		this.valMap = new HashMap<Integer, Integer>();
		for (final Constraint c : v.neighbors) {
			final Variable n = c.getNeighbor(v);
			this.valMap.put(n.id, n.value);
			this.data.add(c.encode());
		}
	}

	public int getSize() {
		int size = 0;
		for (final int[] array : this.data) {
			size += array.length * 4;
		}
		return 12 + size + this.valMap.size() * 4;
	}
}
