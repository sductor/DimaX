package dima.introspectionbasedagents.ontologies;

import dima.basicinterfaces.AbstractMessageInterface;

public class ClassEnveloppe implements Envelope {

	/**
	 *
	 */
	private static final long serialVersionUID = -9106004047528431484L;
	Class<? extends AbstractMessageInterface> messageClass;

	public ClassEnveloppe(final Class<? extends AbstractMessageInterface> messageClass) {
		this.messageClass = messageClass;
	}

	public ClassEnveloppe(final AbstractMessageInterface mess) {
		this.messageClass = mess.getClass();
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this)
			return true;
		else if (o instanceof ClassEnveloppe)
			return this.messageClass.equals(((ClassEnveloppe) o).messageClass);
		else
			return false;
	}

	@Override
	public int hashCode() {
		return this.messageClass.hashCode();
	}

	@Override
	public String toString() {
		return "*CLASS ENVELLOPE* : messages of class: " + this.messageClass + " (hashcode="
		+ this.hashCode() + ")";
	}
}
