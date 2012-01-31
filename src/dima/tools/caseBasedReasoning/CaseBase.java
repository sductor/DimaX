package dima.tools.caseBasedReasoning;

/**
 * Insert the type's description here.
 * Creation date: (19/04/02 11:25:05)
 * @author: Zahia Guessoum
 * this class implements a base of case that can be used to have a case-based reasoning
 * to use this class, you need to define a list of cases.
 */
public abstract class CaseBase {
	protected java.util.Vector caseList;
	/**
	 * CaseBase constructor comment.
	 */
	public CaseBase() {
		super();
		this.caseList=new java.util.Vector();
	}
	/**
	 * CaseBase constructor comment.
	 */
	public void addCase(final Case a) {
		this.caseList.add(a);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (05/11/02 09:45:58)
	 * @return boolean
	 */
	public boolean caseBaseNotEmpty() {
		return ! this.caseList.isEmpty();
	}
	/**
	 * CaseBase constructor comment.
	 */
	public boolean isActive() {
		return true;
	}
	/**
	 * CaseBase constructor comment.
	 */
	public void removeCase(final Case a) {
		this.caseList.remove(a);
	}
	/**
	 * CaseBase constructor comment.
	 */
	public boolean similarCaseExists(final Case a)
	{Case c;
	int i = 0;
	c= (Case) this.caseList.elementAt(i);
	while (i<= this.caseList.size()&& !c.similar(a)){i++; c= (Case) this.caseList.elementAt(i);}
	if (c.similar(a)) {this.updateNewCase(a, c); return true;}
	return false;
	}
	/**
	 * CaseBase constructor comment.
	 */
	public void step(final Case newCase) {
		if (!this.similarCaseExists(newCase))
			this.updateNewCase(newCase);
		newCase.apply();
	}
	/**
	 * CaseBase constructor comment.
	 * update a case that has bo similar case
	 */
	public abstract Case updateNewCase(Case newCase);
	/**
	 * CaseBase constructor comment.
	 * upadte a newCase with some date of the exiting case old
	 */
	public abstract Case updateNewCase(Case newCase, Case old);
}
