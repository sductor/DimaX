package examples.dcop.algo;

public class Stats implements Comparable{
	public int gain;
	public int cycles;
	public int attempts;
	public int varChanged;
	public int varLocked;
	public int maxLockedDistance;
	//	max distance to locked node
	//for each lock response no of request

	@Override
	public int compareTo(final Object arg0) {
		final Stats st = (Stats)arg0;
		return this.gain - st.gain;
	}
}