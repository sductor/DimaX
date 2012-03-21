package negotiation.dcopframework.algo.topt;

public class AsyncHelper {
	public static int translate(RewardMatrix parent, RewardMatrix child,
			int offset) {
		int newoffset = 0;
		for (Integer i : parent.map.keySet()) {
			if (child.map.containsKey(i)) {
				newoffset += child.getBase(i) * parent.getValue(offset, i);
			}
		}
		return newoffset;
	}
}
