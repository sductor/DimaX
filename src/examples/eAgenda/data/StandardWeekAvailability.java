package examples.eAgenda.data;

import java.io.Serializable;

public class StandardWeekAvailability implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1439681317665536294L;
	StandardAvailability[] dayAvailability;

	public StandardWeekAvailability() {
		this.dayAvailability = new StandardAvailability[7];
		for(int i=0;i<7;i++) {
			this.dayAvailability[i] = new StandardAvailability();
		}
	}
	/** Return the usual availability for the day in week days (0 is sunday, 1 is monday, ... until 6) */
	public StandardAvailability getAvailabilityForDay(final int day) {
		return this.dayAvailability[day];
	}
	public void setStandard() {
		for(int i=0;i<7;i++) {
			this.dayAvailability[i].setStandard(i);
		}
	}
}
