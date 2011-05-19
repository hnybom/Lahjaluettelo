package org.paketti.lahjapaketti.server.xmpp.commands;

import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

public abstract class XmppCommand {

	protected final XMPPService xmpp = XMPPServiceFactory.getXMPPService();

	protected final Message message;

	public XmppCommand(final Message message) {
		this.message = message;
	}

	public abstract void execute();

}
