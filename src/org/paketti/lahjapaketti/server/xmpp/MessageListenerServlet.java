package org.paketti.lahjapaketti.server.xmpp;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

public class MessageListenerServlet extends HttpServlet {

	private static final long serialVersionUID = 824816459842352579L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		final XMPPService xmpp = XMPPServiceFactory.getXMPPService();
		final Message message = xmpp.parseMessage(req);

		final JID fromJid = message.getFromJid();
		final String body = message.getBody();
		if (xmpp.getPresence(fromJid).isAvailable()) {
			xmpp.sendMessage(new MessageBuilder().withRecipientJids(fromJid).withBody("echo: " + body).build());
		}
	}
}
