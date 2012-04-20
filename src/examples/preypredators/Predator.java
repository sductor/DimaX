package examples.preypredators;

/**
 * Insert the type's description here.
 * Creation date: (04/11/00 13:39:12)
 * @author: Administrator
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

public class Predator extends Actor implements Animal  {
	/**
	 *
	 */
	private static final long serialVersionUID = 9144868781223646486L;
	private int energy;
	private Point pos;
	public boolean isAlive = true;
	private World world;
	private final int distanceView;
	/**
	 * Predator constructor comment.
	 */
	public Predator() {
		super();
		this.energy = 1000;
		this.distanceView = 20;
		this.pos = new Point();
	}
	/**
	 * Predator constructor comment.
	 */
	public Predator(final World world) {
		super();
		this.energy = 1000;
		this.distanceView = 2;
		this.pos = new Point();
		this.world = world;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/11/00 17:48:14)
	 * @param nrj int
	 */
	public void addEnergy(final int nrj) {
		this.energy= this.energy+ nrj;

	}
	/* private int posX = 100;
	private int posY = 100 ;
	private static int deadProies;

	private static Grid fpp = FramePredProie.getGrille();

	Dimension d = fpp.getSize();
	int xoff = d.width / fpp.getTailleGrille();
	int yoff = d.height / fpp.getTailleGrille();
	int xoffqua = 3 * xoff / 4;
	int yoffqua = 3 * yoff / 4;
	int xoffmid = xoff / 2;
	int yoffmid = yoff / 2; */
	public void dessinerPredateur(final int xpos, final int ypos) {
		final Graphics g = this.getGrille().getGraphics();
		this.dessinerActeur(g,xpos,ypos,Color.black);
	}
	/* private int posX = 100;
	private int posY = 100 ;
	private static int deadProies;

	private static Grid fpp = FramePredProie.getGrille();

	Dimension d = fpp.getSize();
	int xoff = d.width / fpp.getTailleGrille();
	int yoff = d.height / fpp.getTailleGrille();
	int xoffqua = 3 * xoff / 4;
	int yoffqua = 3 * yoff / 4;
	int xoffmid = xoff / 2;
	int yoffmid = yoff / 2; */
	public void dessinerPredateur(final int xpos, final int ypos, final Color col) {
		final Graphics g = this.getGrille().getGraphics();
		this.dessinerActeur(g,xpos,ypos,col);
	}
	@Override
	public int getDistanceView(){
		return this.distanceView;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/11/00 20:08:52)
	 * @return java.awt.Point
	 */
	public Point getEmptyPlace() {
		return null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/11/00 20:08:52)
	 * @return java.awt.Point
	 */
	public Point getEmptyPlace(final Point pt) {

		return null;
	}
	@Override
	public Point getPos(){
		return this.pos;
	}
	public Point getRandomPlace() {
		final Point res = new Point (this.pos.x,this.pos.y);
		final Vector env = this.world.getEnvironement(this);
		switch((int) (Math.random() * 7D)){
		case 0:
			if(res.y > 0) {
				res.y--;
			}

			break;

		case 1:
			if(res.x > 0) {
				res.x--;
			}
			break;

		case 2:
			if(res.x < this.world.getSize()) {
				res.x++;
			}
			break;

		case 3:
			if(res.y < this.world.getSize()) {
				res.y++;
			}
			break;

		case 4:
			if(res.x < this.world.getSize()&&res.y > 0){
				res.x++;
				res.y--;
			}
			break;

		case 5:
			if(res.y < this.world.getSize()&&res.x > 0){
				res.x--;
				res.y++;
			}
			break;

		case 6:
			if(res.x < this.world.getSize()&&this.pos.y < this.world.getSize()){
				res.x++;
				res.y++;
			}
			break;
		case 7:
			if(res.x > 0&&res.y > 0){
				res.x--;
				res.y--;
			}
			break;
		}


		return res;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/11/00 16:12:49)
	 * @return TPsma.Animal
	 * @param env java.util.Vector
	 */
	@Override
	public Animal getTarget(final Vector env) {
		Animal target = null;

		//Cherche la premiere instance de Food
		for(int i=0; i<env.size() ; i++) {
			if(env.elementAt(i) instanceof Food) {
				target = (Food)env.elementAt(i);
			}
		}

		// Pas a manger !!!
		if(target == null) {
			return target;
		}

		//On va manger
		for(int i=0; i<env.size() ; i++) {
			if(this.world.distance(target, this) > this.world.distance(target, (Animal)env.elementAt(i)) && env.elementAt(i) instanceof Food) {
				target = (Food)env.elementAt(i);
			}
		}

		return target;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/11/00 13:44:14)
	 * @return boolean
	 */
	@Override
	public boolean isActive() {
		return this.isAlive;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/11/00 13:45:41)
	 */
	@Override
	public void proactivityInitialize(){
		this.pos.setLocation((int)(Math.random() * this.world.getSize()), (int)(Math.random() * this.world.getSize()));
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/11/00 13:46:28)
	 */
	@Override
	public synchronized void proactivityTerminate() {
		System.out.println("Predateur kaput - Position: "+ this.pos.x+" "+this.pos.y);
	}
	@Override
	public void step() {
		final Vector env = this.world.getEnvironement(this);
		Food target = null;
		int distX;
		int distY;

		this.energy--;
		if(this.energy == 0) {
			this.isAlive = false;
		}

		this.dessinerPredateur(this.pos.x,this.pos.y, Color.lightGray);
		target = (Food)this.getTarget(env);
		if(target != null){
			// On se deplace vers la proie la plus proche
			distX = (int)target.getPos().getX() - (int)this.pos.getX();
			distY = (int)target.getPos().getY() - (int)this.pos.getY();

			if(Math.pow(distX,2) > Math.pow(distY,2)){
				if(distX > 0){
					if(this.pos.getX() < this.world.getSize()) {
						this.pos.x++;
					}
				} else if(this.pos.getX() > 0) {
					this.pos.x--;
				}
			} else if(distY > 0){
				if(this.pos.getY() < this.world.getSize()) {
					this.pos.y++;
				}
			} else if(this.pos.getY() > 0) {
				this.pos.y--;
			}
		} else {
			this.pos = this.getRandomPlace();
		}
		this.dessinerPredateur(this.pos.x,this.pos.y);
	}
}
