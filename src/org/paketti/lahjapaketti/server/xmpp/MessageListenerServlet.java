package org.paketti.lahjapaketti.server.xmpp;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.paketti.lahjapaketti.server.xmpp.commands.CommandFactory;

import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

public class MessageListenerServlet extends HttpServlet {

	private static final long serialVersionUID = 824816459842352579L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		final XMPPService xmpp = XMPPServiceFactory.getXMPPService();
		final Message message = xmpp.parseMessage(req);
		CommandFactory.build(message).execute();
	}
}
