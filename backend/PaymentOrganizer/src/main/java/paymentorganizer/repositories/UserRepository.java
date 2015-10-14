
package paymentorganizer.repositories;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import paymentorganizer.model.User;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public interface UserRepository extends MongoRepository<User, String> {
	
	
	List<User> findByName(String name);
	
}
