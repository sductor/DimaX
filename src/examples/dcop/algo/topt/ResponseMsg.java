package examples.dcop.algo.topt;

import examples.dcop.daj.DcopMessage;

public class ResponseMsg extends DcopMessage {
	int id;
	TreeNode node;
	int attempt;
	boolean accept;

	public ResponseMsg() {
		super();
	}

	public ResponseMsg(int i, int att, TreeNode n, boolean a) {
		id = i;
		attempt = att;
		node = n;
		accept = a;
	}

	public String getText() {
		return (accept ? "ACCEPT " : "DENY ") + id + "\n";
	}

	public int getSize() {
		return 10;
	}
}

