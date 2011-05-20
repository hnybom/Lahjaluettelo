package org.paketti.lahjapaketti.server.xmpp.commands.weather;

import java.text.MessageFormat;

import org.paketti.lahjapaketti.server.xmpp.commands.XmppCommand;
import org.paketti.lahjapaketti.server.xmpp.commands.weather.YahooWeatherParser.Item;
import org.paketti.lahjapaketti.server.xmpp.commands.weather.YahooWeatherParser.RssFeed;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;

public class WeatherCommand extends XmppCommand {

	private final MessageFormat yahooLocationRequestUrl = new MessageFormat(
			"http://where.yahooapis.com/v1/places.q({0})?appid=UkewUW7V34FYMFT3pjPVTH7jUQD1rJg0TgrSkf.tDimJn0bYRUU0bjdPXfRosW7Zy64");

	private static final String YAHOO_WEATHER_URL = "http://weather.yahooapis.com/forecastrss?u=c&w=";

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
		final String[] splittedBody = message.getBody().split(" ");
		if (splittedBody.length > 1) {
			final YahooLocationParser yahooLocationParser = new YahooLocationParser(
					yahooLocationRequestUrl.format(splittedBody[1]));
			yahooLocationParser.parse();
			fetchAndPrintWeather(yahooLocationParser.getWoeid());

		} else {
			fetchAndPrintWeather(CITY.TAMPERE.getWOIEDCode());
		}
	}

	private void fetchAndPrintWeather(final String woeid) {
		final YahooWeatherParser yahooWeatherParser = new YahooWeatherParser(YAHOO_WEATHER_URL + woeid);
		yahooWeatherParser.parse();
		printWeather(yahooWeatherParser.getFeed());
	}

	private void printWeather(final RssFeed feed) {
		final JID fromJid = message.getFromJid();
		final StringBuilder builder = new StringBuilder(feed.title);
		builder.append("\n");
		builder.append("Humidity: " + feed.weatherHumidity + "%");
		builder.append("\n");
		builder.append("Wind speed: " + feed.weatherWindSpeed + " km/h");
		builder.append("\n");
		builder.append("Air pressure: " + feed.weatherPressure + " mb");
		builder.append("\n");
		builder.append("Sunrise: " + feed.weatherSunrise);
		builder.append("\n");
		builder.append("Sunset: " + feed.weatherSunset);
		builder.append("\n");

		for (final Item item : feed.getItems()) {
			builder.append("Temperature: " + item.weatherTemperature + " C");
			builder.append("\n");
			builder.append("Forecast: " + item.weatherText);
			builder.append("\n");
		}
		xmpp.sendMessage(new MessageBuilder().withRecipientJids(fromJid).withBody(builder.toString()).build());
	}
}
