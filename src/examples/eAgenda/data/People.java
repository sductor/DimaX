package examples.eAgenda.data;

import java.io.Serializable;
import java.util.ArrayList;

/** Any eAgenda user(s) */
public abstract class People implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -7166283418238766514L;
	String name;

	public People(final String myName) {
		this.name = myName;
	}
	public abstract ArrayList getCanonicalList();
    public abstract int getSize();
	public String getName() {
		return this.name;
	}
}
