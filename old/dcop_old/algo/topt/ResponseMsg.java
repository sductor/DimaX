package examples.dcop_old.algo.topt;

import examples.dcop_old.daj.DCOPMessage;

public class ResponseMsg extends DCOPMessage {
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

	@Override
	public String getText() {
		return (accept ? "ACCEPT " : "DENY ") + id + "\n";
	}

	@Override
	public int getSize() {
		return 10;
	}
}

