package negotiation.dcopframework.algo.korig;

public class CommitInfo {
	public int id;
	public int leader;

	public CommitInfo(final int i, final int l) {
		this.id = i;
		this.leader = l;
	}

	@Override
	public String toString() {
		return "" + this.id + "->" + this.leader;
	}
}
