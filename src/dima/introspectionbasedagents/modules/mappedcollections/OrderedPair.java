package dima.introspectionbasedagents.modules.mappedcollections;

import java.io.Serializable;

public class OrderedPair<E extends Comparable<E>> implements Serializable {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 122829942747982781L;
	private final E e1, e2;

	public OrderedPair(final E e1, final E e2) {
		if (e1.compareTo(e2) < 0) {
			this.e1 = e1;
			this.e2 = e2;
		} else {
			this.e1 = e2;
			this.e2 = e1;
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof OrderedPair<?>)) {
			return false;
		}
		return this.e1.equals(((OrderedPair<?>) obj).e1)
				&& this.e2.equals(((OrderedPair<?>) obj).e2);
	}

	@Override
	public int hashCode() {
		return this.e1.hashCode() ^ this.e2.hashCode();
	}

	public E getFirst() {
		return this.e1;
	}

	public E getSecond() {
		return this.e2;
	}
}