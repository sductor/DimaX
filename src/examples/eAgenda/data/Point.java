package examples.eAgenda.data;

import java.io.Serializable;

/** Serialized double version of Point2D.Double */
public class Point implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8521442352205550567L;
	public double x;
	public double y;

	public Point(final double theX, final double theY) {
		this.x = theX;
		this.y = theY;
	}
}
