package dima.tools.concurrence;

/*
  File: LinkedNode.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  11Jun1998  dl               Create public version
 */

/** A standard linked list node used in various queue classes **/
class LinkedNode {
	Object value;
	LinkedNode next = null;
	LinkedNode(final Object x) { this.value = x; }
	LinkedNode(final Object x, final LinkedNode n) { this.value = x; this.next = n; }
}
