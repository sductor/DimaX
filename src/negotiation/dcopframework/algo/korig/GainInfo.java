package negotiation.dcopframework.algo.korig;

import java.util.HashMap;

public class GainInfo {

	public int id;
	public int gain;
	public HashMap<Integer, Integer> valMap;

	public GainInfo(int i, int g, HashMap<Integer, Integer> vm) {
		id = i;
		gain = g;
		valMap = vm;
	}

	public int getSize() {
		return valMap.size() * 8 + 8;
	}
	
	@Override
	public String toString() {
		return "" + id + ":" + gain;
	}

}
