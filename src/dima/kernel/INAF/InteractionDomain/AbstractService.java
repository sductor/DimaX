package dima.kernel.INAF.InteractionDomain;

/**
 * Insert the type's description here.
 * Creation date: (19/03/03 17:35:23)
 * @author: Tarek JARRAYA
 */

public abstract class AbstractService
{
	public String identifier;
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 18:22:01)
	 */
	public AbstractService()
	{
		super();
		this.setIdentifier(new String());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 18:22:01)
	 */
	public AbstractService(final String id)
	{
		super();
		this.setIdentifier(id);

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 18:57:43)
	 * @return boolean
	 */
	public boolean equals(final AbstractService s)
	{
		return this.identifier.equalsIgnoreCase(s.getIdentifier());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (03/04/2003 15:03:43)
	 * @return java.lang.String
	 */
	public java.lang.String getIdentifier()
	{
		return this.identifier;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (30/08/2003 20:20:06)
	 * @return boolean
	 * @param s Gdima.kernel.INAF2.InteractionDomain.AbstractService
	 */
	public boolean isBetterThan(final AbstractService s)
	{
		return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (03/04/2003 15:03:43)
	 * @param newIdentifier java.lang.String
	 */
	public void setIdentifier(final String newIdentifier)
	{
		this.identifier = newIdentifier;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/06/2003 11:04:37)
	 * @return java.lang.String
	 */
	@Override
	public String toString()
	{
		return this.getIdentifier();
	}
}
