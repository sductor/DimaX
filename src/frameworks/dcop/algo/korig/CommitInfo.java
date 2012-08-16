package frameworks.dcop.algo.korig;

public class CommitInfo {
	public int id;
	public int leader;
	
	public CommitInfo(int i, int l) {
		id = i;
		leader = l;
	}
	
	public String toString() {
		return "" + id + "->" + leader;
	}
}
