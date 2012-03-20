package examples.eAgenda.mas;

import java.io.Serializable;

public class TransactionID implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7749285188054093838L;

	public static final TransactionID transactionRoot = new	TransactionID(0);

	long myValue;

	public TransactionID(final long value) {
		this.myValue = value;

	}
	public boolean egale(final TransactionID tid) {
		if(tid==null) {
			return false;
		} else {
			return this.myValue== tid.getMyValue();
		}
	}

	public long getMyValue(){
		return this.myValue;
	}



}
