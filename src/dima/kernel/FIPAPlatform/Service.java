package dima.kernel.FIPAPlatform;

import dima.support.GimaObject;

/**
 * Insert the type's description here.
 * Creation date: (05/07/2002 11:27:14)
 * @author:
 */
public class Service extends GimaObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -3663611778066354092L;
	private java.lang.String id;
/**
 * Service constructor comment.
 */
public Service() {
	super();
}
/**
 * Service constructor comment.
 */
public Service(final String s) {
	super();
	this.id = s;
}
/**
 * Insert the method's description here.
 * Creation date: (05/07/2002 11:29:12)
 * @return java.lang.String
 */
public java.lang.String getId() {
	return this.id;
}
/**
 * Insert the method's description here.
 * Creation date: (05/07/2002 11:29:12)
 * @param newId java.lang.String
 */
void setId(final java.lang.String newId) {
	this.id = newId;
}
}
