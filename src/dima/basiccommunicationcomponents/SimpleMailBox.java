package dima.basiccommunicationcomponents;

import java.util.LinkedList;
/**
 * This is a simple MailBox implemented basically with a LinkedListe of
 * messages.
 * Creation date: (01/03/2000 21:55:34)
 * @author: Gerard Rozsavolgyi
 */
public class SimpleMailBox extends AbstractMailBox
{
	/**
	 *
	 */
	private static final long serialVersionUID = -8707578669937655981L;
	LinkedList messageList;

	/**
	 * SimpleMailBox constructor comment.
	 */
	public SimpleMailBox() {
		super();
		this.initialize();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (01/03/2000 23:19:52)
	 * @return java.lang.Object
	 */
	@Override
	public AbstractMessage getFirstMessage(){
		return (AbstractMessage)this.messageList.getFirst();
	}
	/**
	 * SimpleMailBox .
	 */
	@Override
	public  boolean hasMail(){ return !this.messageList.isEmpty();}
	/**
	 * SimpleMailBox initialization.
	 */
	public void initialize() {
		this.messageList = /*Collections.synchronizedList(*/ new LinkedList();
	}
	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 */
	public static void main(final java.lang.String[] args) {
		// Insert code to start the application here.
		MessageSend.example04();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (01/03/2000 23:19:52)
	 * @return java.lang.Object
	 */
	@Override
	synchronized public AbstractMessage readMail(){
		return (AbstractMessage)this.messageList.removeFirst();
	}
	/**
	 * Returns a String that represents the value of this object.
	 * @return a string representation of the receiver
	 */
	@Override
	public String toString() {

		return this.messageList.toString();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (01/03/2000 23:22:26)
	 */
	@Override
	public synchronized boolean writeMail(final AbstractMessage m) {this.messageList.add(m); return true; }

	/**
	 * Insert the method's description here.
	 * Creation date: (01/03/2000 23:19:52)
	 * @return java.lang.Object
	 */
	public AbstractMessage removeFirstMessage(){
		return (AbstractMessage)this.messageList.remove(0);
	}
}
