package dima.introspectionbasedagents.services.communicating.xmppcommunication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.PrivacyListManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.PrivacyItem;
import org.jivesoftware.smack.packet.PrivacyItem.PrivacyRule;

import dima.basicagentcomponents.AgentIdentifier;

public class XMPPCommunicationCompetence extends JabberCommunicationCompetence{

	//
	// Privacy List
	//
	
	protected  ArrayList<PrivacyItem> getPrivacyForEveryone(){
		ArrayList<PrivacyItem> privacyItems = new ArrayList<PrivacyItem>();

		PrivacyItem 	item = new PrivacyItem(PrivacyItem.Type.subscription.toString(), true, 2);
		item.setValue(PrivacyRule.SUBSCRIPTION_BOTH);
		privacyItems.add(item);

		return privacyItems;
	}

	protected  ArrayList<PrivacyItem> getPrivacyFor(Collection<AgentIdentifier> ids){
		ArrayList<PrivacyItem> privacyItems = new ArrayList<PrivacyItem>();
		for (AgentIdentifier user : ids){
			PrivacyItem item = new PrivacyItem(PrivacyItem.Type.jid.toString(), true, 1);
			item.setValue(user.toString());
			privacyItems.add(item);
		}
		return privacyItems;
	}

	private boolean setPrivacy(String listName, List<PrivacyItem> privacyItems){
		// Get the privacy manager for the current connection.
		PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(connection);
		// Create the new list.
		try {
			privacyManager.createPrivacyList(listName, privacyItems);
		} catch (XMPPException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	//
	//
	//
	
	
}
