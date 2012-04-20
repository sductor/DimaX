package examples.dcop.algo.topt;

import examples.dcop.daj.DcopMessage;

public class CommitMsg extends DcopMessage {
	/**
	 *
	 */
	private static final long serialVersionUID = 3917948316844096929L;
	int gid;
	int attempt;
	TreeNode node;

	public CommitMsg() {
		super();
	}

	public CommitMsg(final int i, final int a, final TreeNode n) {
		this.gid = i;
		this.attempt = a;
		this.node = n;
	}

	@Override
	public String getText() {
		return ("COMMIT ") + this.gid + "\n";
	}

	@Override
	public int getSize() {
		return 5;
	}
}
