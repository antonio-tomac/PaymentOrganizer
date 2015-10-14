package paymentorganizer.model;

import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Document(collection = "exchanges")
public class Exchange implements Sortable {

	@Id
	private final ObjectId id = new ObjectId();
	@DBRef
	private final User from;
	private final double ammount;
	@DBRef
	private final User to;
	private final Date date;

	public Exchange(User from, User to, double ammount, Date date) {
		this.from = from;
		this.ammount = ammount;
		this.to = to;
		this.date = date;
	}

	public String getId() {
		return id.toString();
	}

	@Override
	public ObjectId getObjectId() {
		return id;
	}

	public User getFrom() {
		return from;
	}

	public double getAmmount() {
		return ammount;
	}

	public User getTo() {
		return to;
	}

	public Date getDate() {
		return date;
	}

}
