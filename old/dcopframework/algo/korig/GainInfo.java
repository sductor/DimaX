package negotiation.dcopframework.algo.korig;

import java.util.HashMap;

public class GainInfo {

	public int id;
	public int gain;
	public HashMap<Integer, Integer> valMap;

	public GainInfo(final int i, final int g, final HashMap<Integer, Integer> vm) {
		this.id = i;
		this.gain = g;
		this.valMap = vm;
	}

	public int getSize() {
		return this.valMap.size() * 8 + 8;
	}

	@Override
	public String toString() {
		return "" + this.id + ":" + this.gain;
	}

}
