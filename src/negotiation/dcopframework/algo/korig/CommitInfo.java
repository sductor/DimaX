package negotiation.dcopframework.algo.korig;

public class CommitInfo {
	public int id;
	public int leader;
	
	public CommitInfo(int i, int l) {
		id = i;
		leader = l;
	}
	
	@Override
	public String toString() {
		return "" + id + "->" + leader;
	}
}
