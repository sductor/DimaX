/*
 * Created on 27 mai 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package dima.kernel.INAF.InteractionDomain;

import java.io.Serializable;

/**
 * @author faci
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Resource extends AbstractService implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 149728366121431583L;
	public Float cost;
	public Double tpsRep;
	/**
	 * Seance constructor comment.
	 */
	public Resource() {
		super();
	}
	/**
	 * Seance constructor comment.
	 * @param id java.lang.String
	 * @param categ java.lang.String
	 */
	public Resource(final Float j, final Double c)
	{
		super();
		this.setCost(j.floatValue());
		this.setTpsRep(c.doubleValue());
	}
	/**
	 * Resource constructor comment.
	 * @param id java.lang.String
	 * @param categ java.lang.String
	 */
	public Resource(final String id, final Float j, final Double c)
	{
		super(id);
		this.setCost(j.floatValue());
		this.setTpsRep(c.doubleValue());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/04/2004 13:30:13)
	 * @return boolean
	 */
	public boolean equals(final Resource s)
	{
		return this.getCost() == s.getCost() && this.getTpsRep() ==s.getTpsRep();
	}

	public boolean littleThan(final Resource s){

		return this.getCost() < s.getCost();

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/04/2004 13:31:47)
	 * @return double
	 */
	public double getTpsRep() {
		return this.tpsRep.doubleValue();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/04/2004 13:32:47)
	 * @return float
	 */
	public float getCost() {
		return this.cost.floatValue();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/04/2004 13:33:47)
	 * @param newTpsRep double
	 */
	public void setTpsRep(final double newTpsRep) {
		this.tpsRep = new Double(newTpsRep);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/04/2004 13:35:47)
	 * @param newCost float
	 */
	public void setCost(final float newCost) {
		this.cost = new Float(newCost);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/04/2004 13:36:46)
	 * @return java.lang.String
	 */
	@Override
	public String toString()
	{
		return new String("Le prix:"+this.cost+", Le tpsRep: "+this.tpsRep);
	}
}



