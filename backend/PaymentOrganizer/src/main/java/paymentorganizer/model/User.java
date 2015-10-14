package paymentorganizer.model;

import java.util.Objects;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final User other = (User) obj;
		return Objects.equals(this.id, other.id);
	}

	@Override
	public String toString() {
		return "User{" + "id=" + id + ", name=" + name + '}';
	}

}
