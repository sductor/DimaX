package negotiation.dcopframework.algo.topt;

public class AsyncHelper {
	public static int translate(final RewardMatrix parent, final RewardMatrix child,
			final int offset) {
		int newoffset = 0;
		for (final Integer i : parent.map.keySet()) {
			if (child.map.containsKey(i)) {
				newoffset += child.getBase(i) * parent.getValue(offset, i);
			}
		}
		return newoffset;
	}
}
