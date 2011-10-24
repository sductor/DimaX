package examples.circle;

/**
*  ProactiveComponent example
*/


import java.awt.Color;
import java.awt.Graphics;

import dima.kernel.BasicAgents.BasicReactiveAgent;




public class Shark extends BasicReactiveAgent {

	/**
	 *
	 */
	private static final long serialVersionUID = 9017254377682934821L;
	private int posX;
	private int posY;
	private static int bloodSmell;
	private static Sea water= OceanAPI.getSea();
	private static int deadShark;

	public Shark() {
		super();

	}
	public int getPosX() {
		return this.posX;
	}
	public int getPosY() {
		return this.posY;
	}
	@Override
	public  boolean isActive() {
		final int dist = (int)Math.sqrt(Math.pow(Math.abs(this.getPosX() - 170), 2D) + Math.pow(Math.abs(this.getPosY() - 138), 2D));
		return dist != bloodSmell / 2;
	}
	@Override
	public void proactivityInitialize() {

		this.setPosX((int)(Math.random() * 340D));
		this.setPosY((int)(Math.random() * 276D));
		bloodSmell = water.getBloodSmellArea();
		final Graphics g = water.getGraphics();
		g.setColor(Color.red);
		g.fillRect(this.getPosX() - 1, this.getPosY() - 1, 3, 3);

	}
/* this method is synchronized because we access and modifify the class (static) field deadShark. */

@Override
public  synchronized void proactivityTerminate() {
		deadShark++;
		System.out.println(deadShark);
		if(deadShark >= 1000) {
			deadShark = 0;
			System.gc();
			System.out.println("*** GC ***");
		}

	}
	public void setPosX(final int newValue) {
		this.posX = newValue;
	}
	public void setPosY(final int newValue) {
		this.posY = newValue;
	}
/** Try to get closer to the central circle ! */


@Override
public void step() {
		final int distX = this.getPosX() - 170;
		final int distY = this.getPosY() - 138;
		final int dist = (int)Math.sqrt(Math.pow(Math.abs(distX), 2D) + Math.pow(Math.abs(distY), 2D));
		final Graphics g = water.getGraphics();
		g.setColor(Color.cyan);
		g.fillRect(this.getPosX() - 1, this.getPosY() - 1, 3, 3);
		if(dist < bloodSmell / 2)
			switch((int)(Math.random() * 2D)) {
			case 0: // '\0'
				if(distX < 0)
					this.setPosX(this.getPosX() - 1);
				else
					this.setPosX(this.getPosX() + 1);
				break;

			case 1: // '\001'
				if(distY < 0)
					this.setPosY(this.getPosY() - 1);
				else
					this.setPosY(this.getPosY() + 1);
				break;

			default:
				if(distX < 0)
					this.setPosX(this.getPosX() - 1);
				else
					this.setPosX(this.getPosX() + 1);
				if(distY < 0)
					this.setPosY(this.getPosY() - 1);
				else
					this.setPosY(this.getPosY() + 1);
				break;
			}
		else
			switch((int)(Math.random() * 2D)) {
			case 0: // '\0'
				if(distX < 0)
					this.setPosX(this.getPosX() + 1);
				else
					this.setPosX(this.getPosX() - 1);
				break;

			case 1: // '\001'
				if(distY < 0)
					this.setPosY(this.getPosY() + 1);
				else
					this.setPosY(this.getPosY() - 1);
				break;

			default:
				if(distX < 0)
					this.setPosX(this.getPosX() + 1);
				else
					this.setPosX(this.getPosX() - 1);
				if(distY < 0)
					this.setPosY(this.getPosY() + 1);
				else
					this.setPosY(this.getPosY() - 1);
				break;
			}
		g.setColor(Color.red);
		g.fillRect(this.getPosX() - 1, this.getPosY() - 1, 3, 3);
	}
}
