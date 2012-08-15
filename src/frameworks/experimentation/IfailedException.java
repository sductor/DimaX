package frameworks.experimentation;


public class IfailedException extends Exception{
	private static final long serialVersionUID = -1670361769897265687L;

	public Exception e;
	//	public IfailedException(final Exception e2) {
	//		this.e=e2;
	//	}
	public IfailedException(final String string) {
		super(string);
	}

}