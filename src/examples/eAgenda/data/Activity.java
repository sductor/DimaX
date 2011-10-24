package examples.eAgenda.data;

import java.io.Serializable;

/** General super class of all kind of activities that can occurs whithin the agenda */
public abstract class Activity implements Cloneable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8675772476606206975L;
	boolean moveable;
	String title;
	String description;

	public Activity() {
		this("", "", false);
	}
	public Activity(final String titl, final String desc, final boolean movable) {
		this.moveable = movable;
		this.title = titl;
		this.description = desc;
	}
	public String getDescription() {
		return this.description;
	}
	public String getTitle() {
		return this.title;
	}
	public boolean isMoveable() {
		return this.moveable;
	}
	public void setDescription(final String desc) {
		this.description = desc;
	}
	public void setTitle(final String titl) {
		this.title = titl;
	}
}
