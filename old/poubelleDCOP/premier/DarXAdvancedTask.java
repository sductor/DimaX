package negotiation.dcopframework;

import java.rmi.RemoteException;

import darx.DarxException;
import darx.DarxTask;
import darx.RemoteTask;
import darx.TaskShell;

public class DarXAdvancedTask extends DarxTask{

	protected DarXAdvancedTask(String name) {
		super(name);
	}

	public DarxTask getTask(String t){
		try {
			RemoteTask remote = this.findTask(t);
			return ((TaskShell) remote.handle).getTask();
		}
		catch(final DarxException e) {
			System.out.println("Getting " + t + " from nameserver failed : " + e);
			return null;
		} catch (RemoteException e) {
			System.out.println("Getting " + t + " from nameserver failed : " + e);
			return null;
		}
	}
}
