package dima.introspectionbasedagents.tools;

import java.util.Date;

public class Ticker {

	private final long timeToWait;
	private final Date creation;
	private int lastStepExecution = 0;

	public Ticker(final long timeToWait, final Date creation) {
		this.timeToWait = timeToWait;
		this.creation = creation;
	}

	/*
	 * NB : Cette méthode reinitialise le point de référence du Ticker si
	 * elle retourne true
	 */

	public boolean isReady() {
		final long elapsedTime =
			new Date().getTime() - this.creation.getTime();
		if (this.timeToWait==0)
			return true;
		else
			if (elapsedTime/this.timeToWait>this.lastStepExecution) {
				this.lastStepExecution = (int) (elapsedTime/this.timeToWait);
				return true;
			} else
				return false;
	}


	public static void main(final String[] args) throws InterruptedException {
		final Ticker t  = new Ticker(2000, new Date());
		System.out.println(t.creation);

		while(true){
			Thread.sleep(567);
			System.out.println(new Date()+" t is ready?"+t.isReady());
		}
	}
}

///**
// * This class implements a ticker that is reinitialised each time it is
// * executed after the time value has expired
// */
//public class Ticker_1ereVersion implements Serializable {
//
//	private static final long serialVersionUID = -5770091091023391164L;
//
//	private final long timeToWait;
//	private Date creation;
//
//	public Ticker_1ereVersion(final long timeToWait) {
//		this.timeToWait = timeToWait;
//		this.creation = new Date();
//	}
//
//	/*
//	 * NB : Cette méthode reinitialise le point de référence du Ticker si
//	 * elle retourne true
//	 */
//
//	public boolean isReady() {
//		final long elapsedTime =
//			new Date().getTime() - this.creation.getTime();
//		if (elapsedTime > this.timeToWait) {
//			this.creation = new Date();
//			return true;
//		} else
//			return false;
//	}
//}
