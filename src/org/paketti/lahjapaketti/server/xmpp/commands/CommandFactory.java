package org.paketti.lahjapaketti.server.xmpp.commands;

import com.google.appengine.api.xmpp.Message;

public class CommandFactory {

	public static XmppCommand build(final Message message) {
		if ("s‰‰".equals(message.getBody())) {
			return new WeatherCommand(message);
		}

		return new DefaultCommand(message);

	}

}
