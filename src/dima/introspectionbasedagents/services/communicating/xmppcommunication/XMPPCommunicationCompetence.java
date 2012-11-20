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

	/**
	 * 
	 */
	private static final long serialVersionUID = -7957143847716750946L;

	protected  ArrayList<PrivacyItem> getPrivacyForEveryone(){
		final ArrayList<PrivacyItem> privacyItems = new ArrayList<PrivacyItem>();

		final PrivacyItem 	item = new PrivacyItem(PrivacyItem.Type.subscription.toString(), true, 2);
		item.setValue(PrivacyRule.SUBSCRIPTION_BOTH);
		privacyItems.add(item);

		return privacyItems;
	}

	protected  ArrayList<PrivacyItem> getPrivacyFor(final Collection<AgentIdentifier> ids){
		final ArrayList<PrivacyItem> privacyItems = new ArrayList<PrivacyItem>();
		for (final AgentIdentifier user : ids){
			final PrivacyItem item = new PrivacyItem(PrivacyItem.Type.jid.toString(), true, 1);
			item.setValue(user.toString());
			privacyItems.add(item);
		}
		return privacyItems;
	}

	private boolean setPrivacy(final String listName, final List<PrivacyItem> privacyItems){
		// Get the privacy manager for the current connection.
		final PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(this.connection);
		// Create the new list.
		try {
			privacyManager.createPrivacyList(listName, privacyItems);
		} catch (final XMPPException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	//
	//
	//


}
