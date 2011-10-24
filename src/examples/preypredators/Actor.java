package examples.preypredators;

	/**
	 * Insert the type's description here.
	 * Creation date: (06/11/00 17:23:24)
	 * @author: Administrator
	 */
	import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

import dima.kernel.communicatingAgent.BasicCommunicatingAgent;

public class Actor extends BasicCommunicatingAgent {

	/**
	 *
	 */
	private static final long serialVersionUID = -5181190927014604267L;
	private int posX;
	private int posY;
	private int deadProies = 0;

	public int QuiSuisJe;

	public static Grid grid = PredProieAPI.getGrille();
	private static int NbMaxRessources = 1000;

	private Dimension d;
	private final int xoff;
	private final int yoff;
	private final int xoffqua;
	private final int yoffqua;
	private final int xoffmid;
	private final int yoffmid;

	private int ressources;
	public static Actor[][] Tableau;

	public int vue = 3;


/**
 * Acteur constructor comment.
 */
public Actor() {
	//super();
	final Dimension d = grid.getSize();
	this.xoff = d.width / grid.getTailleGrille();
	this.yoff = d.height / grid.getTailleGrille();
	this.xoffqua = 3 * this.xoff / 4;
	this.yoffqua = 3 * this.yoff / 4;
	this.xoffmid = this.xoff / 2;
	this.yoffmid = this.yoff / 2;
	this.ressources = Actor.aleatoire(NbMaxRessources);
}
	static public int aleatoire(final int num){
	return (int) ( Math.random() * (num-1));
}
	public void dessinerActeur(final Graphics g,final int xpos, final int ypos) {
		g.fillRect(this.xoff*(xpos+1) - this.xoffqua, this.yoff*(ypos+1) - this.yoffqua, this.xoffmid, this.yoffmid);
	}
	public void dessinerActeur(final Graphics g,final int xpos, final int ypos,final Color col) {
		g.setColor(col);
		g.fillRect(this.xoff*(xpos+1) - this.xoffqua, this.yoff*(ypos+1) - this.yoffqua, this.xoffmid, this.yoffmid);

	}
		public Dimension getDim() {
			return this.d;
		}
		public Grid getGrille() {
			return grid;
		}
		public int getPosX() {
		return this.posX;
	}
public int getPosY() {
		return this.posY;
	}
	public int getRessources() {
		return this.ressources;
	}
		public int getXoff() {
			return this.xoff;
		}
		public int getXoffmid() {
			return this.xoffmid;
		}
		public int getXoffqua() {
			return this.xoffqua;
		}
public int getYoff() {
			return this.yoff;
		}
public int getYoffmid() {
			return this.yoffmid;
		}
		public int getYoffqua() {
			return this.yoffqua;
		}
	@Override
	public  boolean isActive() {
		return true;
	}
		public void kill() {
			final Graphics g = this.getGrille().getGraphics();
			synchronized (Actor.Tableau) {
				this.dessinerActeur(g,this.getPosX(),this.getPosY(), Color.green);

			}
			this.setAlive(false);
		}
public int max(final int i,final int j){
	return i>j?i:j;

}
public int min(final int i,final int j){
	return i>j?j:i;

}
		@Override
		public void proactivityInitialize() {

	}
@Override
public synchronized void proactivityTerminate() {
		this.deadProies++;
		//System.out.println("Dead Proies : "+deadProies);
		if(this.deadProies >= 1000) {
			this.deadProies = 0;
			System.gc();
			System.out.println("*** GC ***");
		}

	}
public java.util.Vector Scan(final int quisuisje) {
	final int x = this.posX;
	final int y = this.posY;
	int ennemi;
	final Vector liste = new Vector();
	if (quisuisje == 1)
		ennemi = 2;
	else
		ennemi = 1;
	boolean bool = true;
	//synchronized (Acteur.Tableau) {
	for (int i = 1;i < this.vue + 1 & bool; i++) {
		for (int j = this.max(x - i, 0); j < this.min(x + i + 1, grid.getTailleGrille()); j++)
			for (int k = this.max(y - i, 0); k < this.min(y + i + 1, grid.getTailleGrille()); k++)
				//System.out.println("This : "+this+" i : "+i+" j : "+j+" k : "+k);
				if (ennemi == 1) {
					final Predator pred = (Predator) Actor.Tableau[j][k];
					if (pred != null)
						if (pred.QuiSuisJe == ennemi)
							liste.addElement(pred);
				} else {
					final Food proi = (Food) Actor.Tableau[j][k];
					if (proi != null)
						if (proi.QuiSuisJe == ennemi)
							liste.addElement(proi);
				}
		if (!liste.isEmpty())
			bool = false;
	}
	//}
	return liste;
}
	public void setDeadProies(final int valDeadPr) {
		this.deadProies = valDeadPr;
	}
public void setPosX(final int newValue) {
		this.posX = newValue;
	}
public void setPosY(final int newValue) {
		this.posY = newValue;
	}
	public void setRessources(final int res) {
		this.ressources = res;
	}
public  void setServiceProviders(final String a, final Vector v)
{}
	@Override
	public void step() {
		//System.out.println("Ressources : "+ressources);


	}
}
