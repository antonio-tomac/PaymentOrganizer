package paymentorganizer;

import com.mongodb.MongoClient;
import com.mongodb.MongoTimeoutException;
import com.mongodb.WriteConcern;
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

    // ---------------------------------------------------- mongodb config
	@Override
	protected String getDatabaseName() {
		return "paymentOrganizer";
	}

	@Override
	@Bean
	public MongoClient mongo() throws Exception {
		MongoClient client;
		try {
			client = new MongoClient();
		} catch (MongoTimeoutException ex) {
			client = new MongoClient();
		}
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
