package dima.basiccommunicationcomponents;

/**
 * A class designed to invoke .
 * Creation date: (17/01/00 17:59:03)
 * @author: Zahia Guessoum
 */
import java.lang.reflect.Method;

import dima.support.GimaObject;




public class MessageSend2 extends GimaObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -1597489905711998805L;
	private Object receiver;
	private String message;
	private Object[] arguments; // make a Message class with these 3
	private Object result;
	private java.lang.reflect.Method method;
	private java.lang.Class receiverClass;
	/**
	 * Perform constructor comment.
	 */
	public MessageSend2() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:08:15)
	 * @param o java.lang.Object
	 * @param m java.lang.String
	 */
	public MessageSend2(final Object o, final String m, final Class[] a) {
		super();
		this.initialize(o, m, a);
	}
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
	 * Could raise an exception if result doesn't satisfy type checking etc.
	 */
	public void checkResult() {
		System.out.println("Result in MessageSend2 is " + this.result);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:15:36)
	 * Testing instanciation
	 */
	static void example01() {
		final MessageSend p = new MessageSend("coucou@", "size");

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:15:36)
	 * to raise java.lang.NoSuchMethodException
	 */
	static void example02() {
		final MessageSend p = new MessageSend("coucou@", "undefinedMethod");
		p.invoke();

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:15:36)
	 * to raise java.lang.NoSuchMethodException
	 */
	static void example03() {
		final MessageSend p = new MessageSend("coucou@", "length");
		p.invoke();

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:15:36)
	 * to raise java.lang.NoSuchMethodException
	 */
	static void example04() {
		MessageSend.invoke("coucou@", "length");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/02/2000 23:18:35)
	 * @return java.lang.reflect.Method
	 */
	public java.lang.reflect.Method getMethod() {
		return this.method;
	}

	/** updated by J.-B. Potonnier **/

	public Method getMethod(final Class c, final String methodName, final Object[] args)
			throws NoSuchMethodException {


		final Method[] methods = c.getMethods();

		for (final Method method2 : methods) {
			final Class[] margs =  method2.getParameterTypes();
			if (method2.getName().equals(methodName)) {
				boolean t = true;
				if (args.length==margs.length) {
					//System.out.println("  while looking for "+methodName);
					for (int j=0; j<args.length; j++) {
						t= t && margs[j].isAssignableFrom((Class)args[j]);
					}
				}
				//if (t) System.out.println("The method " + methodName + " has been found");
				if (t) {
					return method2;
				}
			}
		}

		throw new NoSuchMethodException("Looking for " + methodName);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/03/2000 23:08:08)
	 * @return java.lang.Class
	 */
	public java.lang.Class getReceiverClass() {
		return this.receiverClass;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:08:15)
	 * @param o java.lang.Object
	 * @param m java.lang.String
	 */
	public void initialize(final Object o, final String m, final Class[] a) {
		this.receiver = o;
		this.message = m;

		this.receiverClass = this.receiver.getClass();
		try {
			/*String ar = "";
			for (int i=0;i<a.length;i++) ar+=""+a[i]+",";
			if (receiver != null)
			{
				System.out.println("Looking in "+receiver.getClass()+" for "+message+"("+ar+")");
			}
			else {
				System.out.println("Receiver is null (while call "+m+")");
			}*/

			this.method = this.getMethod(this.receiverClass, this.message, a);
			//method = receiverClass.getMethod(message, a);
		} catch (final NoSuchMethodException e) {
			System.out.println("While initializing MessagSend2: " + e);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:24:50)
	 * @return java.lang.Object
	 */
	public Object invoke() {
		this.checkInvocation();
		this.privateInvoke(null);
		//this.checkResult();
		return this.result;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:24:50)
	 * @return java.lang.Object
	 */
	public Object invoke(final Object[] args) {
		//System.out.println("Tarluf args1 1");
		this.checkInvocation();
		//System.out.println("Tarluf args1 2");
		this.privateInvoke(args);
		//System.out.println("Tarluf args1 3");
		//this.checkResult();
		return this.result;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:15:36)
	 * Invoke receiver r with message m.
	 *
	 */
	public static Object invoke(final Object r, final String m) {
		if (r == null) {
			return null;
		} else {
			final MessageSend p = new MessageSend(r, m);
			return p.invoke();
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:15:36)
	 * Invoke receiver r with message m.
	 *
	 */
	public static Object invoke(final Object r, final String m, final Object[] a) {
		try {

			if (r == null) {
				return null;
			} else {
				Class[] c = new Class[0]; //null avant
				//if (a==null) return invoke(r, m);
				if (a != null) {
					c = new Class[a.length];
					for (int i = 0; i < a.length; i++) {
						c[i] = a[i].getClass();
					}
				}

				final MessageSend2 p = new MessageSend2(r, m, c);
				return p.invoke(a);
			}
		} catch (final Exception ex) {
			System.err.println("MessageSend2::invoke : Error while invoking " + m + " on  " + a.length);
			//System.exit(2);
			return null;
		}
	}
	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 */
	public static void main(final java.lang.String[] args) {
		String a[];
		a = new String[1];
		a[0] = "QQQQQQQQQQ";
		MessageSend2.invoke(new MessageSend2(), "test", a);
		MessageSend2.example03();
		MessageSend2.example04();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/01/00 18:24:50)
	 * @return java.lang.Object
	 */
	private void privateInvoke(final Object[] args) {

		try {

			this.result = this.method.invoke(this.receiver, args);
		} catch (final Exception e) {
			System.out.println("In MessageSend2.privateInvoke " + e + "bye" + this.receiver);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/02/2000 23:18:35)
	 * @param newMethod java.lang.reflect.Method
	 */
	public void setMethod(final java.lang.reflect.Method newMethod) {
		this.method = newMethod;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/03/2000 23:08:08)
	 * @param newReceiverClass java.lang.Class
	 */
	public void setReceiverClass(final java.lang.Class newReceiverClass) {
		this.receiverClass = newReceiverClass;
	}
	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 */
	public static void test(final String a) {
		System.out.println("@@@@@@@@@@@@@@@@@" + a);
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
