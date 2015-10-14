package paymentorganizer;

import com.mongodb.MongoClient;
import com.mongodb.MongoTimeoutException;
import com.mongodb.WriteConcern;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import paymentorganizer.model.User;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Configuration
@EnableMongoRepositories(basePackages = "paymentorganizer.repositories")
public class MongoConfiguration extends AbstractMongoConfiguration {

	public static final String addressSub = "10.38.41.";
	public static final String serverMongoIp = "10.38.41.138";
	public static final String localMongoIp = "127.0.0.1";

	public static boolean detectIsOnServer() {
		Enumeration<NetworkInterface> e;
		try {
			e = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException ex) {
			return false;
		}
		while (e.hasMoreElements()) {
			NetworkInterface n = e.nextElement();
			Enumeration<InetAddress> ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = ee.nextElement();
				if (i instanceof Inet4Address) {
					if (i.toString().contains(addressSub)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// ---------------------------------------------------- mongodb config
	@Override
	protected String getDatabaseName() {
		return "paymentOrganizer";
	}

	@Override
	@Bean
	public MongoClient mongo() throws Exception {
		MongoClient client;
		String ip = detectIsOnServer() ? serverMongoIp : localMongoIp;
		client = new MongoClient(ip);
		client.setWriteConcern(WriteConcern.SAFE);
		return client;
	}

	@Override
	protected String getMappingBasePackage() {
		return "paymentorganizer.model";
	}

	// ---------------------------------------------------- MongoTemplate
	@Bean
	@Override
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongo(), getDatabaseName());
	}

}
