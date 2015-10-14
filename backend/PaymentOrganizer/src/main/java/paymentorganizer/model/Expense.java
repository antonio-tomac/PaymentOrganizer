package paymentorganizer.model;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Document(collection = "expenses")
public class Expense implements Sortable {

	@Id
	private final ObjectId id = new ObjectId();
	private final String name;
	private final Date date;
	private final double ammount;
	private final List<UserRatio> userRatios;

	public static class UserRatio {

		@DBRef
		public final User user;
		public final double ratio;

		public UserRatio(User user, double ratio) {
			this.user = user;
			this.ratio = ratio;
		}

		public User getUser() {
			return user;
		}

		public double getRatio() {
			return ratio;
		}
	}

	public static List<UserRatio> generateEqualRatios(List<User> users) {
		List<UserRatio> userRatios = new LinkedList<>();
		double ratio = 1. / users.size();
		for (User user : users) {
			userRatios.add(new UserRatio(user, ratio));
		}
		return userRatios;
	}

	private static void chackRatios(List<UserRatio> userRatios) {
		double sum = 0;
		for (UserRatio userRatio : userRatios) {
			Double ratio = userRatio.ratio;
			if (ratio < 0 || ratio > 1) {
				throw new RuntimeException("Ratios must be in range <0, 1] (0 exclusive, 1 inclusive)");
			}
			if (Math.abs(ratio) < 10e-4) {
				throw new RuntimeException("Ratio must be greather than 0");
			}
			sum += ratio;
		}
		if (Math.abs(sum - 1) > 10e-4) {
			throw new RuntimeException("Sum of al rtio factors must be equal to 1");
		}
	}

	public Expense(String name, Date date, double ammount,
			List<UserRatio> userRatios) {
		this.name = name;
		this.date = date;
		this.ammount = ammount;
		this.userRatios = userRatios;
		chackRatios(userRatios);
	}

	public String getId() {
		return id.toString();
	}

	@Override
	public ObjectId getObjectId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getDate() {
		return date;
	}

	public double getAmmount() {
		return ammount;
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
