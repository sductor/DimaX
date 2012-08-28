package vieux.myDCOP.dcop;

import java.util.HashMap;

public class RewardMatrix {

	public HashMap<Integer, Integer> map;
	public HashMap<Integer, Integer> domain;

	public int size;

	public int[] data;
	public int[] value;

	public RewardMatrix(final HashMap<Integer, Integer> dmap) {
		this.map = new HashMap<Integer, Integer>();
		this.domain = dmap;
		this.size = 1;
		for (final Integer i : dmap.keySet()) {
			this.map.put(i, this.size);
			this.size *= dmap.get(i);
		}
		this.data = new int[this.size];
		this.value = new int[this.size];
	}

	public int getBase(final int id) {
		if (!this.map.containsKey(id)) {
			return 0;
		}
		return this.map.get(id);
	}

	public int getSize() {
		return this.size;
	}

	public boolean contains(final int id) {
		return this.map.containsKey(id);
	}

	public int getValue(final int offset, final int id) {
		if (!this.map.containsKey(id)) {
			return -1;
		}
		final int base = this.map.get(id);
		final int tmp = offset / base;
		return tmp % this.domain.get(id);
	}

	@Override
	public String toString() {
		String val = "[";
		for (final int element : this.data) {
			val += element + " ";
		}
		val += "]";
		return this.map.toString() + "\n" + val;
	}
}
