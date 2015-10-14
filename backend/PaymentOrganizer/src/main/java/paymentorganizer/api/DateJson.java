package paymentorganizer.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class DateJson {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");

	public static class DateSerializer extends JsonSerializer<Date> {

		@Override
		public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			String formattedDate = dateFormat.format(date);
			gen.writeString(formattedDate);
		}
	}
	
	public static class DateDeserializer extends JsonDeserializer<Date> {

		public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			String formattedDate = dateFormat.format(date);
			gen.writeString(formattedDate);
		}

		@Override
		public Date deserialize(JsonParser jp, DeserializationContext ctxt) 
				throws IOException, JsonProcessingException {
			String dateString = jp.getText();
			try {
				return dateFormat.parse(dateString);
			} catch (ParseException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
	public static void main(String[] args) throws UnsupportedEncodingException {
		String s = "ć";
		System.out.println(URLEncoder.encode(s, "utf-8"));
	}
}
