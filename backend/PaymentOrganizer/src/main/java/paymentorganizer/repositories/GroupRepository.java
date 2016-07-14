package paymentorganizer.repositories;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import paymentorganizer.model.Group;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public interface GroupRepository extends MongoRepository<Group, String> {

	@Query(value = "{users:{$elemMatch:{$id:?0}}}")
	List<Group> findByUserId(ObjectId userId);
}
