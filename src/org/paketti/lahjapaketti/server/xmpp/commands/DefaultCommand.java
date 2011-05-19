package org.paketti.lahjapaketti.server.xmpp.commands;

import com.google.appengine.api.xmpp.Message;

public class DefaultCommand extends XmppCommand {

	public DefaultCommand(Message message) {
		super(message);
	}

	@Override
	public void execute() {

	}

}
