package paymentorganizer.modelold;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class Repository {

	private final Map<String, User> idToUser;

	@SuppressWarnings("unchecked")
	private static <T> List<T> castTo(Object object) {
		return List.class.cast(object);
	}
	
	public Repository() {
		List<User> allUsers = null;
		try {
			ObjectInput input = new ObjectInputStream(new FileInputStream("saved.model"));
			Object readObject = input.readObject();
			allUsers = castTo(readObject);
		} catch (FileNotFoundException ex) {
		} catch (ClassNotFoundException | IOException ex) {
		}
		if (allUsers == null) {
			allUsers = new LinkedList<>();
		}
		this.idToUser = new HashMap<>(allUsers.size());
		for (User user : allUsers) {
			idToUser.put(user.getId(), user);
		}
	}
	
	public User getById(String id) {
		return idToUser.get(id);
	}

	public void addUser(User user) {
		if (idToUser.containsKey(user.getId())) {
			throw new RuntimeException("User already exist");
		}
		idToUser.put(user.getId(), user);
		save();
	}
	
	public Collection<User> getUsers() {
		return idToUser.values();
	}
	
	public void save() {
		List<User> users = new LinkedList<>(idToUser.values());
		try (ObjectOutput output = new ObjectOutputStream(new FileOutputStream("saved.model"))) {
			output.writeObject(users);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
