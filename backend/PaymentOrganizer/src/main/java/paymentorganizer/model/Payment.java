package paymentorganizer.model;

import java.util.Date;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Data
@Document(collection = "payments")
public class Payment implements Sortable {

	@Id
	private final ObjectId id = new ObjectId();
	private final double ammount;
	private final Date date;
	@DBRef
	private final User user;

	public String getId() {
		return id.toString();
	}

	@Override
	public ObjectId getObjectId() {
		return id;
	}

}
