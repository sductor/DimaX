package dima.basiccommunicationcomponents;

/**
 * Insert the type's description here.
 * Creation date: (17/01/00 17:59:03)
 * @author: Gerard Rozsavolgyi
 */
import dima.support.GimaObject;

public class MessageSend extends GimaObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -4450063679672844141L;
	Object receiver;
	String message;
	Object [] arguments; // make a Message class with these 3
	Object result;
	/**
	 * Perform constructor comment.
	 */
	public MessageSend() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:08:15)
	 * @param o java.lang.Object
	 * @param m java.lang.String
	 */
	public MessageSend(final Object o, final String m) {
		super();
		this.initialize(o,m);}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:24:50)
	 * @return java.lang.Object
	 */
	public void checkInvocation() {
		// add controls and eventually raise an exception
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:24:50)
	 * can raise eventually an exception if result don't satisfies type checking etc.
	 */
	public void checkResult() {
		//System.out.println(result);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:15:36)
	 * Testing instanciation
	 */
	static void  example01() {
		final MessageSend p = new MessageSend("coucou@","size");

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:15:36)
	 * to raise java.lang.NoSuchMethodException
	 */
	static void  example02() {
		final MessageSend p = new MessageSend("coucou@","undefinedMethod");
		p.invoke();

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:15:36)
	 * to raise java.lang.NoSuchMethodException
	 */
	static void  example03() {
		final MessageSend p = new MessageSend("coucou@","length");
		p.invoke();

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:15:36)
	 * to raise java.lang.NoSuchMethodException
	 */
	static void  example04() {
		MessageSend.invoke("coucou@","length");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:08:15)
	 * @param o java.lang.Object
	 * @param m java.lang.String
	 */
	public void initialize(final Object o, final String m) {

		this.receiver=o;
		this.message=m;

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:24:50)
	 * @return java.lang.Object
	 */
	public Object invoke() {
		this.checkInvocation();
		this.privateInvoke();
		//this.checkResult();
		return this.result;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:15:36)
	 * Invoke receiver r with message m.
	 *
	 */
	public static Object  invoke(final Object r,final String m) {
		final MessageSend p = new MessageSend(r,m);
		return p.invoke();

	}
	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 */
	public static void main(final java.lang.String[] args) {
		MessageSend.example04();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:24:50)
	 * @return java.lang.Object
	 */
	public void privateInvoke() {

		//	get Method hrows NoSuchMethodException, SecurityException {
		try{
			final java.lang.Class aClass= this.receiver.getClass();

			final java.lang.reflect.Method aMethod=aClass.getMethod(this.message, null);

			aMethod.invoke(this.receiver,null);
		}
		catch(final Exception e) {
			System.out.println(e+":"+this.message+"in object :"+this.receiver);
		}
	}
	/**
	 * Returns a String that represents the value of this object.
	 * @return a string representation of the receiver
	 */
	@Override
	public String toString() {
		// Insert code to print the receiver here.
		// This implementation forwards the message to super. You may replace or supplement this.
		return super.toString();
	}
}
