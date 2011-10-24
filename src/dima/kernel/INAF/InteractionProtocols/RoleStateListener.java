package dima.kernel.INAF.InteractionProtocols;

/**
 * Insert the type's description here.
 * Creation date: (18/09/2003 05:48:07)
 * @author:
 */
public interface RoleStateListener {
/**
 * Insert the method's description here.
 * Creation date: (18/09/2003 05:50:08)
 * @param convId java.lang.String
 */
void failureRoleProcess(AbstractRole r);
/**
 * Insert the method's description here.
 * Creation date: (18/09/2003 05:50:44)
 * @param convId java.lang.String
 */
void successRoleProcess(AbstractRole r);
}
