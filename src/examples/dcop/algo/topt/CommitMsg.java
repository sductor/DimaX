package examples.dcop.algo.topt;

import examples.dcop.daj.DcopMessage;

public class CommitMsg extends DcopMessage {
	int gid;
	int attempt;
	TreeNode node;

	public CommitMsg() {
		super();
	}

	public CommitMsg(int i, int a, TreeNode n) {
		gid = i;
		attempt = a;
		node = n;
	}

	public String getText() {
		return ("COMMIT ") + gid + "\n";
	}

	public int getSize() {
		return 5;
	}
}
