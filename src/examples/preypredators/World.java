package examples.preypredators;

import java.util.Vector;

import dima.kernel.ProactiveComponents.ProactiveComponentsManager;


/**
 * Insert the type's description here.
 * Creation date: (08/11/00 13:56:20)
 * @author: Administrator
 */
public class World {
	public Vector animal;
	private int size;
	private int nbPredator;
	private int nbFood;
/**
 * World constructor comment.
 */
public World() {
	super();
}
/**
 * World constructor comment.
 */
public World(final int nbPredator, final int nbFood, final int size) {
	super();
	this.nbPredator = nbPredator;
	this.nbFood = nbFood;
	this.size = size;
	this.animal = new Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (08/11/00 14:37:24)
 * @return int
 * @param a TPsma.Animal
 * @param b TPsma.Animal
 */
public int distance(final Animal a, final Animal b) {
	final int distX = (int)a.getPos().getX() - (int)b.getPos().getX();
	final int distY = (int)a.getPos().getX() - (int)b.getPos().getX();

	return (int)Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
}
	public Vector getEnvironement(final Animal an){
		final Vector resultat = new Vector();
		for(int i=0; i< this.animal.size(); i++)
			if(this.distance(an, (Animal)this.animal.elementAt(i)) <= an.getDistanceView()  && (Animal)this.animal.elementAt(i) != an)
				resultat.addElement(this.animal.elementAt(i));
		return resultat;
	}
/**
 * Insert the method's description here.
 * Creation date: (08/11/00 14:58:01)
 * @return int
 */
public int getSize() {
	return this.size;
}
/**
 * Insert the method's description here.
 * Creation date: (08/11/00 15:13:29)
 */
public void initialise(){
	for (int i=0; i<this.nbPredator;i++)
	{final Predator a = new Predator(this); a.activate();}
	for (int i=0; i<this.nbFood;i++)
	{final Food a = new Food(this); a.activate();}

}
/**
 * Insert the method's description here.
 * Creation date: (08/11/00 15:13:29)
 */
public void initialiseOld(){
	for (int i=0; i<this.nbPredator;i++)
	this.animal.addElement(new Predator(this));
	for (int i=0; i<this.nbFood;i++)
	this.animal.addElement(new Food(this));

	final ProactiveComponentsManager pam = new ProactiveComponentsManager(this.animal);
	pam.startAll();
}
}
