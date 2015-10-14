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
@Document(collection = "payments")
public class Payment implements Sortable {

	@Id
	private final ObjectId id = new ObjectId();
	private final double ammount;
	private final Date date;
	@DBRef
	private final User user;

	public Payment(double ammount, Date date, User user) {
		this.ammount = ammount;
		this.date = date;
		this.user = user;
	}

	public String getId() {
		return id.toString();
	}

	@Override
	public ObjectId getObjectId() {
		return id;
	}

	public double getAmmount() {
		return ammount;
	}

	public Date getDate() {
		return date;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "Payment{" + "ammount=" + ammount + ", date=" + date + ", user=" + user + '}';
	}

}
