package paymentorganizer.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Data
@Document(collection = "incomes")
public class Income implements Sortable {

	@Id
	private final ObjectId id = new ObjectId();
	private final String name;
	private final Date date;
	private final double ammount;
	private final List<UserRatio> userRatios;

	public Income(String name, Date date, double ammount,
			List<UserRatio> userRatios) {
		this.name = name;
		this.date = date;
		this.ammount = ammount;
		this.userRatios = userRatios;
		Calculator.checkRatios(userRatios);
	}

	public String getId() {
		return id.toString();
	}

	@Override
	public ObjectId getObjectId() {
		return id;
	}

	public List<UserRatio> getUserRatios() {
		return Collections.unmodifiableList(userRatios);
	}
	
	public double getUserRatio(User user) {
		for (UserRatio userRatio : userRatios) {
			if (userRatio.getUser().equals(user)) {
				return userRatio.getRatio();
			}
		}
		return 0;
	}

}
