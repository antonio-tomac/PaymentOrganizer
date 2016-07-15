package paymentorganizer.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Data
@EqualsAndHashCode(of = "id")
@Document(collection = "users")
public class User {

	@Id
	private ObjectId id;
	private String name;

	public User(String name) {
		this.name = name;
	}

	public String getId() {
		return id.toString();
	}
}
