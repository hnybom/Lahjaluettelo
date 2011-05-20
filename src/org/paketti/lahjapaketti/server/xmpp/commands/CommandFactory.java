package org.paketti.lahjapaketti.server.xmpp.commands;

import org.apache.commons.lang.StringUtils;
import org.paketti.lahjapaketti.server.xmpp.commands.weather.WeatherCommand;

import com.google.appengine.api.xmpp.Message;

public class CommandFactory {

	public static XmppCommand build(final Message message) {
		if (StringUtils.isNotEmpty(message.getBody()) && message.getBody().toLowerCase().startsWith("s‰‰")) {
			return new WeatherCommand(message);
		}

		return new DefaultCommand(message);

	}

}
