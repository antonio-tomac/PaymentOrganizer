
package paymentorganizer.modelold;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class Group {
	
	private String name;
	private User owner;
	
	@DBRef()
	private final Set<User> users;

	public Group(String name, User owner) {
		this.name = name;
		this.owner = owner;
		users = new HashSet<>();
		users.add(owner);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		if (!users.contains(owner)) {
			throw new RuntimeException("User "+users+" is not member of group");
		}
		this.owner = owner;
	}
	
	public void addUser(User user) {
		users.add(user);
	}
	
	public void removeUser(User user) {
		users.remove(user);
	}
	
}
