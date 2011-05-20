package org.paketti.lahjapaketti.server.xmpp.commands.weather;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;

public class YahooLocationParser extends DefaultHandler {
	private String url;
	private StringBuilder text = new StringBuilder();
	private String woeid;

	public YahooLocationParser(String url) {
		this.url = url;
	}

	public void parse() {

		woeid = getFromCache(url);
		if (woeid != null) {
			return;
		}

		InputStream urlInputStream = null;
		SAXParserFactory spf = null;
		SAXParser sp = null;

		try {
			URL rssUrl = new URL(this.url);
			urlInputStream = rssUrl.openConnection().getInputStream();
			spf = SAXParserFactory.newInstance();
			if (spf != null) {
				sp = spf.newSAXParser();
				sp.parse(urlInputStream, this);
				putToCache(url, woeid);
			}
		}
		/*
		 * Exceptions need to be handled MalformedURLException
		 * ParserConfigurationException IOException SAXException
		 */

		catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
		} finally {
			try {
				if (urlInputStream != null)
					urlInputStream.close();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {

	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equalsIgnoreCase("woeid")) {
			this.woeid = this.text.toString().trim();
		}
		this.text.setLength(0);
	}

	public String getWoeid() {
		return woeid;
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		this.text.append(ch, start, length);
	}

	private String getFromCache(final Object key) {
		final Cache cache = getCache();
		if (cache != null) {
			return (String) cache.get(key);
		}
		return null;
	}

	private void putToCache(final Object key, final Object o) {
		final Cache cache = getCache();
		if (cache != null) {
			cache.put(key, o);
		}
	}

	private Cache getCache() {
		final Map<Integer, Integer> props = new HashMap<Integer, Integer>();
		props.put(GCacheFactory.EXPIRATION_DELTA, 3600);

		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			return cacheFactory.createCache(props);
		} catch (CacheException e) {
			return null;
		}
	}
}
