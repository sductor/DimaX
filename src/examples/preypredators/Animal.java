package examples.preypredators;

import java.awt.Point;
import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (08/11/00 13:48:52)
 * @author: Administrator
 */
public interface Animal {
	public int getDistanceView();
	public Point getPos();
/**
 * Insert the method's description here.
 * Creation date: (08/11/00 16:11:53)
 * @return TPsma.Animal
 */
Animal getTarget(Vector env);
}
