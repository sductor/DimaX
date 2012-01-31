package dima.kernel.INAF.InteractionTools;

/**
 * Insert the type's description here.
 * Creation date: (17/03/2003 13:29:31)
 * @author: Tarek JARRAYA
 */
import java.io.Serializable;
public class Operator implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4944076289381220009L;
	public java.lang.String name;
	/**
	 * Operator constructor comment.
	 */
	public Operator() {
		super();
	}
	/**
	 * Operator constructor comment.
	 */
	public Operator(final String oper)
	{
		super();
		this.setName(oper);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/03/2003 14:01:50)
	 * @return java.lang.String
	 */
	public java.lang.String getName() {
		return this.name;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/03/2003 14:07:28)
	 * @return java.lang.Boolean
	 */
	public boolean isAND() {
		return this.name.equalsIgnoreCase("and");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/03/2003 14:06:18)
	 * @return java.lang.Boolean
	 */
	public boolean isConcurrent() {
		return this.name.equalsIgnoreCase("||");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (01/04/03 02:35:24)
	 * @return boolean
	 */
	public boolean isDifferent()
	{
		return this.name.equalsIgnoreCase("!=");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (01/04/03 02:35:24)
	 * @return boolean
	 */
	public boolean isLittleThan()
	{
		return this.name.equalsIgnoreCase("<");
	}

	public boolean isEqual()
	{
		return this.name.equalsIgnoreCase("==");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/03/2003 14:07:42)
	 */
	public boolean isOR()
	{
		return this.name.equalsIgnoreCase("or");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/03/2003 14:06:48)
	 * @return java.lang.Boolean
	 */
	public boolean isSeqential() {
		return this.name.equalsIgnoreCase("--");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/03/2003 14:07:42)
	 */
	public boolean isXOR()
	{
		return this.name.equalsIgnoreCase("xor");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/03/2003 14:01:50)
	 * @param newName java.lang.String
	 */
	public void setName(final java.lang.String newName)
	{
		this.name = new String(newName);
	}
}
