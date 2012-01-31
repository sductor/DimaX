package dima.introspectionbasedagents.shells;

import java.util.Date;

import dima.support.GimaObject;

public class Ticker extends GimaObject{

	/**
	 *
	 */
	private static final long serialVersionUID = 2889277826081776124L;
	private final long timeToWait;
	private int lastStepExecution = 0;

	public Ticker(final long timeToWait) {
		this.timeToWait = timeToWait;
	}

	/*
	 * NB : Cette méthode reinitialise le point de référence du Ticker si
	 * elle retourne true
	 */

	public boolean isReady(final Date creation) {
		if (this.timeToWait==0)
			return true;
		else {
			final int stepNumber = Ticker.getStepNumber(creation, this.timeToWait);
			if (stepNumber>this.lastStepExecution) {
				this.lastStepExecution=stepNumber;
				return true;
			} else
				return false;
		}
	}

	public static int getStepNumber(final Date creation, final long cycleTime){
		final long elapsedTime =
				new Date().getTime() - creation.getTime();
		return (int) (elapsedTime/cycleTime);
	}


	public static void main(final String[] args) throws InterruptedException {
		final Date creation=new Date();
		final Ticker t  = new Ticker(2000);
		System.out.println(creation);

		while(true){
			Thread.sleep(567);
			System.out.println(new Date()+" t is ready?"+t.isReady(creation));
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
