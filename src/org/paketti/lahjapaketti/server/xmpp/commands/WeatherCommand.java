package org.paketti.lahjapaketti.server.xmpp.commands;

import org.paketti.lahjapaketti.server.xmpp.commands.YahooWeatherParser.Item;
import org.paketti.lahjapaketti.server.xmpp.commands.YahooWeatherParser.RssFeed;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;

public class WeatherCommand extends XmppCommand {

	private static final String URL = "http://weather.yahooapis.com/forecastrss?u=c&w=";

	private enum CITY {
		TAMPERE {
			@Override
			public String getWOIEDCode() {
				return "573760";
			}
		},

		TURKU {
			@Override
			public String getWOIEDCode() {
				return "574224";
			}
		},

		HELSINKI {
			@Override
			public String getWOIEDCode() {
				return "565346";
			}
		};

		public abstract String getWOIEDCode();
	}

	public WeatherCommand(Message message) {
		super(message);
	}

	@Override
	public void execute() {
		for (final CITY city : CITY.values()) {
			final YahooWeatherParser yahooWeatherParser = new YahooWeatherParser(URL + city.getWOIEDCode());
			yahooWeatherParser.parse();
			printWeather(yahooWeatherParser.getFeed());
		}
	}

	private void printWeather(final RssFeed feed) {
		final JID fromJid = message.getFromJid();
		final StringBuilder builder = new StringBuilder(feed.title);
		builder.append("\n");
		builder.append("Humidity: " + feed.weatherHumidity);
		builder.append("\n");
		builder.append("Wind speed: " + feed.weatherWindSpeed);
		builder.append("\n");
		builder.append("Air pressure: " + feed.weatherPressure);
		builder.append("\n");
		builder.append("Sun rise: " + feed.weatherSunrise);
		builder.append("\n");
		builder.append("Sun set: " + feed.weatherSunset);
		builder.append("\n");

		for (final Item item : feed.getItems()) {
			builder.append("Temperature: " + item.weatherTemperature);
			builder.append("\n");
			builder.append("Forecast: " + item.weatherText);
			builder.append("\n");
		}
		xmpp.sendMessage(new MessageBuilder().withRecipientJids(fromJid).withBody(builder.toString()).build());
	}
}