package algo.topt;

import daj.Message;

public class CommitMsg extends Message {
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
