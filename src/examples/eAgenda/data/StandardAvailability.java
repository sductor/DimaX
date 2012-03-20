package examples.eAgenda.data;

import java.io.Serializable;
import java.util.ArrayList;

/** Contains all the information about the standard (ie usually) weekly availability of the agenda owner */
public class StandardAvailability implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 9115737110899218315L;
	/** x = time in hours, y = 1.0 is unavailable, whereas 0.0 is available
	 * This list should be sorted by x
	 */
	ArrayList points;

	public StandardAvailability() {
		this.points = new ArrayList();
		// Set unavailable all day
		this.points.add(new Point(0.0, 1.0));
		this.points.add(new Point(24.0, 1.0));
	}
	public void addPoint(final double time, final double availability) {
		// Add at the right position so it will be still sorted
		this.points.add(this.findIndex(time)+1, new Point(time, availability));
	}
	/** Find the last point index before the point's time become bigger than the speficied value */
	private int findIndex(final double time) {
		for(int i=1;i<this.points.size();i++) {
			if ( ((Point)this.points.get(i)).x > time) {
				return i-1;
			}
		}
		System.err.println("In StandaraedAvailability.findIndex: This should not happen");
		return -1;
	}
	/** Return the availabilit [0, 1] precisely at this minute since the beginning of the day */
	public double getAvailability(final int minute) {
		final double time = minute/60.0;
		final int before = this.findIndex(time);
		Point p = (Point)this.points.get(before);
		final double t1 = p.x;
		final double a1 = p.y;
		p = (Point)this.points.get(before+1);
		final double t2 = p.x;
		final double a2 = p.y;

		final double slope = (a2-a1)/(t2-t1);
		return (time-t1)*slope+a1;
	}
	/** return when for the first time the availability is not 1.0 anymore */
	public int getFirstAvailableTime() {
		return (int)(((Point)this.points.get(1)).x*60);
	}
	/** return when availability become 1.0 for ever */
	public int getLastAvailableTime() {
		return (int)(((Point)this.points.get(this.points.size()-2)).x*60);
	}
	/** Set the availability as usual for a dayInWeak as specified (0 is sunday,...) */
	public void setStandard(final int dayInWeak) {
		if (dayInWeak == 0 || dayInWeak == 6)
		{
			// Closed all day: add nothing
		}
		else {
			this.addPoint(8.0, 1.0);
			this.addPoint(8.5, 0.0);
			this.addPoint(12.0, 0.0);
			this.addPoint(12.5, 1.0);
			this.addPoint(13.0, 0.0);
			this.addPoint(16.5, 0.0);
			this.addPoint(17.0, 1.0);
		}
	}
}
