package framework.dcop.algo.topt;

import java.util.HashMap;

public class RewardMatrix {

	public HashMap<Integer, Integer> map;
	public HashMap<Integer, Integer> domain;

	public int size;

	public int[] data;
	public int[] value;

	public RewardMatrix(HashMap<Integer, Integer> dmap) {
		map = new HashMap<Integer, Integer>();
		domain = dmap;
		size = 1;
		for (Integer i : dmap.keySet()) {
			map.put(i, size);
			size *= dmap.get(i);
		}
		data = new int[size];
		value = new int[size];
	}

	public int getBase(int id) {
		if (!map.containsKey(id))
			return 0;
		return map.get(id);
	}

	public int getSize() {
		return size;
	}

	public boolean contains(int id) {
		return map.containsKey(id);
	}

	public int getValue(int offset, int id) {
		if (!map.containsKey(id))
			return -1;
		int base = map.get(id);
		int tmp = offset / base;
		return tmp % domain.get(id);
	}

	@Override
	public String toString() {
		String val = "[";
		for (int i = 0;i<data.length;i++)
			val += data[i] + " ";
		val += "]";
		return map.toString() + "\n" + val;
	}
}
