package examples.Timetable;

/**
 * Insert the type's description here.
 * Creation date: (04/04/2003 18:20:41)
 * @author: Tarek JARRAYA
 */
import dima.kernel.INAF.InteractionDomain.AbstractService;

public class Seance extends AbstractService {
	public int jour;
	public int creneau;
	/**
	 * Seance constructor comment.
	 */
	public Seance() {
		super();
	}
	/**
	 * Seance constructor comment.
	 * @param id java.lang.String
	 * @param categ java.lang.String
	 */
	public Seance(final int j, final int c)
	{
		super();
		this.setJour(j);
		this.setCreneau(c);
	}
	/**
	 * Seance constructor comment.
	 * @param id java.lang.String
	 * @param categ java.lang.String
	 */
	public Seance(final String id, final int j, final int c)
	{
		super(id);
		this.setJour(j);
		this.setCreneau(c);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 19:03:13)
	 * @return boolean
	 */
	@Override
	public boolean equals(final AbstractService s)
	{
		if( this.getJour() == ((Seance)s).getJour() && this.getCreneau() == ((Seance)s).getCreneau())
			return true;
		else
			return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 18:25:47)
	 * @return int
	 */
	public int getCreneau() {
		return this.creneau;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 18:25:47)
	 * @return int
	 */
	public int getJour() {
		return this.jour;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 18:25:47)
	 * @param newCreneau int
	 */
	public void setCreneau(final int newCreneau) {
		this.creneau = newCreneau;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 18:25:47)
	 * @param newJour int
	 */
	public void setJour(final int newJour) {
		this.jour = newJour;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (14/04/2003 15:25:46)
	 * @return java.lang.String
	 */
	@Override
	public String toString()
	{
		return new String("Le jour:"+this.jour+", Le creneau: "+this.creneau);
	}
}
