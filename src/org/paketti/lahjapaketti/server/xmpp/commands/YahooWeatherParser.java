package org.paketti.lahjapaketti.server.xmpp.commands;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class YahooWeatherParser extends DefaultHandler {
	private String urlString;
	private RssFeed rssFeed;
	private StringBuilder text;
	private Item item;
	private boolean imgStatus;

	public YahooWeatherParser(String url) {
		this.urlString = url;
		this.text = new StringBuilder();
	}

	public void parse() {
		InputStream urlInputStream = null;
		SAXParserFactory spf = null;
		SAXParser sp = null;

		try {
			URL url = new URL(this.urlString);
			urlInputStream = url.openConnection().getInputStream();
			spf = SAXParserFactory.newInstance();
			if (spf != null) {
				sp = spf.newSAXParser();
				sp.parse(urlInputStream, this);
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

	public RssFeed getFeed() {
		return (this.rssFeed);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equalsIgnoreCase("channel"))
			this.rssFeed = new RssFeed();
		else if (qName.equalsIgnoreCase("item") && (this.rssFeed != null)) {
			this.item = new Item();
			this.rssFeed.addItem(this.item);
		} else if (qName.equalsIgnoreCase("image") && (this.rssFeed != null)) {
			this.imgStatus = true;
		} else if (qName.equalsIgnoreCase("yweather:wind") && (this.rssFeed != null)) {
			this.rssFeed.weatherWindSpeed = attributes.getValue("speed");
		} else if (qName.equalsIgnoreCase("yweather:atmosphere") && (this.rssFeed != null)) {
			this.rssFeed.weatherHumidity = attributes.getValue("humidity");
			this.rssFeed.weatherPressure = attributes.getValue("pressure");
		} else if (qName.equalsIgnoreCase("yweather:astronomy") && (this.rssFeed != null)) {
			this.rssFeed.weatherSunrise = attributes.getValue("sunrise");
			this.rssFeed.weatherSunset = attributes.getValue("sunset");
		} else if (qName.equalsIgnoreCase("yweather:astronomy") && (this.item != null)) {
			this.item.weatherTemperature = attributes.getValue("temp");
			this.item.weatherText = attributes.getValue("text");
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (this.rssFeed == null)
			return;

		if (qName.equalsIgnoreCase("item"))
			this.item = null;

		else if (qName.equalsIgnoreCase("image"))
			this.imgStatus = false;

		else if (qName.equalsIgnoreCase("title")) {
			if (this.item != null)
				this.item.title = this.text.toString().trim();
			else if (this.imgStatus)
				this.rssFeed.imageTitle = this.text.toString().trim();
			else
				this.rssFeed.title = this.text.toString().trim();
		}

		else if (qName.equalsIgnoreCase("link")) {
			if (this.item != null)
				this.item.link = this.text.toString().trim();
			else if (this.imgStatus)
				this.rssFeed.imageLink = this.text.toString().trim();
			else
				this.rssFeed.link = this.text.toString().trim();
		}

		else if (qName.equalsIgnoreCase("description")) {
			if (this.item != null)
				this.item.description = this.text.toString().trim();
			else
				this.rssFeed.description = this.text.toString().trim();
		}

		else if (qName.equalsIgnoreCase("url") && this.imgStatus)
			this.rssFeed.imageUrl = this.text.toString().trim();

		else if (qName.equalsIgnoreCase("language"))
			this.rssFeed.language = this.text.toString().trim();

		else if (qName.equalsIgnoreCase("generator"))
			this.rssFeed.generator = this.text.toString().trim();

		else if (qName.equalsIgnoreCase("copyright"))
			this.rssFeed.copyright = this.text.toString().trim();

		else if (qName.equalsIgnoreCase("pubDate") && (this.item != null))
			this.item.pubDate = this.text.toString().trim();

		else if (qName.equalsIgnoreCase("category") && (this.item != null))
			this.rssFeed.addItem(this.text.toString().trim(), this.item);

		this.text.setLength(0);
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		this.text.append(ch, start, length);
	}

	public static class RssFeed {
		public String title;
		public String description;
		public String link;
		public String language;
		public String generator;
		public String copyright;
		public String imageUrl;
		public String imageTitle;
		public String imageLink;
		public String weatherWindSpeed;
		public String weatherHumidity;
		public String weatherPressure;
		public String weatherSunrise;
		public String weatherSunset;

		private ArrayList<Item> items = new ArrayList<Item>();
		private HashMap<String, ArrayList<Item>> category = new HashMap<String, ArrayList<Item>>();;

		public void addItem(Item item) {
			this.items.add(item);
		}

		public void addItem(String cat, Item item) {
			if (!this.category.containsKey(cat))
				this.category.put(cat, new ArrayList<Item>());
			this.category.get(cat).add(item);
		}

		public ArrayList<Item> getItems() {
			return items;
		}

		public HashMap<String, ArrayList<Item>> getCategory() {
			return category;
		}

	}

	public static class Item {
		public String title;
		public String description;
		public String link;
		public String pubDate;
		public String weatherText;
		public String weatherTemperature;

		@Override
		public String toString() {
			return (this.title + ": " + this.pubDate + "n" + this.description);
		}
	}

}
