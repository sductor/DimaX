package examples.dcopAmeliorer.algo.topt;

import examples.dcopAmeliorer.daj.DcopMessage;

public class ResponseMsg extends DcopMessage {
	/**
	 *
	 */
	private static final long serialVersionUID = 4190262639464260051L;
	int id;
	TreeNode node;
	int attempt;
	boolean accept;

	public ResponseMsg() {
		super();
	}

	public ResponseMsg(final int i, final int att, final TreeNode n, final boolean a) {
		this.id = i;
		this.attempt = att;
		this.node = n;
		this.accept = a;
	}

	@Override
	public String getText() {
		return (this.accept ? "ACCEPT " : "DENY ") + this.id + "\n";
	}

	@Override
	public int getSize() {
		return 10;
	}
}

