package frameworks.faulttolerance.dcop.algo.korig;

import java.util.HashMap;

public class GainInfo {

	public int id;
	public double gain;
	public HashMap<Integer, Integer> valMap;

	public GainInfo(int i, double g, HashMap<Integer, Integer> vm) {
		id = i;
		gain = g;
		valMap = vm;
	}

	public int getSize() {
		return valMap.size() * 8 + 8;
	}
	
	public String toString() {
		return "" + id + ":" + gain;
	}

}
