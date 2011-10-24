package examples.circle;

/** Un panel avec une zone d'attraction circulaire */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;

public class Sea extends Panel
{
	/**
	 *
	 */
	private static final long serialVersionUID = -5799537779821302267L;
	public int bloodSmellArea;

	public Sea()
	{
	}
	public void fixerRayon(final int rayon)
	{
		this.setBloodSmellArea(rayon);
	}
	public int getBloodSmellArea()
	{
		return this.bloodSmellArea;
	}
	@Override
	public void paint(final Graphics g)
	{
		g.setColor(Color.blue);
		g.fillRect(170, 138, 1, 1);
		g.drawOval(170 - this.getBloodSmellArea() / 2, 138 - this.getBloodSmellArea() / 2, this.getBloodSmellArea(), this.getBloodSmellArea());
	}
	private void setBloodSmellArea(final int newValue)
	{
		this.bloodSmellArea = newValue;
	}
}
