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
@Document(collection = "exchanges")
public class Exchange implements Sortable {

	@Id
	private final ObjectId id = new ObjectId();
	@DBRef
	private final User from;
	@DBRef
	private final User to;
	private final double ammount;
	private final Date date;

	public String getId() {
		return id.toString();
	}

	@Override
	public ObjectId getObjectId() {
		return id;
	}
}
