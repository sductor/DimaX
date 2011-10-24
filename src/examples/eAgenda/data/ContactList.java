package examples.eAgenda.data;

import java.util.ArrayList;
import java.util.Iterator;

/** A list of people */
public class ContactList extends People {

	/**
	 *
	 */
	private static final long serialVersionUID = -3276421220674035271L;
	ArrayList myPeople;
	/** Relation among the different people in this list */
	int operator;
	public static final int UNSPECIFIED_RELATION = 0;
	public static final int AND_RELATION = 1;
	public static final int OR_RELATION = 2;

	public ContactList(final String listName) {
		super(listName);

		// By default this is a and relation
		this.operator = AND_RELATION;
		this.myPeople = new ArrayList();
	}
	public void addPeople(final People p) {
		this.myPeople.add(p);
	}
	public Iterator getAllPeople() {
		return this.myPeople.iterator();
	}
	@Override
	public ArrayList getCanonicalList() {
		final ArrayList list = new ArrayList(1);
		for (final Iterator it = this.myPeople.iterator();it.hasNext(); )
			list.addAll(((People)it.next()).getCanonicalList());
		return list;
	}

    @Override
	public int getSize(){
	return this.myPeople.size();

    }
	public int getRelation() {
		return this.operator;
	}
	public void setRelation(final int relation) {
		this.operator = relation;
	}

public ArrayList getMyPeople(){
return this.myPeople;
}
}
