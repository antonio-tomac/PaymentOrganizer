package paymentorganizer.modelold;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class Expense implements Dynamic {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final String description;
	private final Date date;
	private final double ammount;
	private final Map<User, Double> userRatios;

	private static Map<User, Double> generateEqualRatios(List<User> users) {
		Map<User, Double> userRatios = new HashMap<>(users.size());
		double ratio = 1. / users.size();
		for (User user : users) {
			userRatios.put(user, ratio);
		}
		return userRatios;
	}

	private static void chackRatios(Map<User, Double> userRatios) {
		double sum = 0;
		for (Map.Entry<User, Double> entry : userRatios.entrySet()) {
			Double ratio = entry.getValue();
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

	public Expense(String name, String description, Date date, double ammount, List<User> users) {
		this(name, description, date, ammount, generateEqualRatios(users));
	}

	public Expense(String name, String description, Date date, double ammount, Map<User, Double> userRatios) {
		this.name = name;
		this.description = description;
		this.date = date;
		this.ammount = ammount;
		this.userRatios = userRatios;
		chackRatios(userRatios);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public Date getDate() {
		return date;
	}

	public double getAmmount() {
		return ammount;
	}

	public double getUserContributionFactor(User user) {
		return userRatios.get(user);
	}

	@Override
	public void apply() {
		for (User user : userRatios.keySet()) {
			user.addExpense(this);
		}
	}

	@Override
	public void delete() {
		for (User user : userRatios.keySet()) {
			user.removeExpense(this);
		}
	}

	@Override
	public double applyDiff(User user) {
		if (userRatios.containsKey(user)) {
			return -ammount * userRatios.get(user);
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return "Expense{" + "name=" + name + ", description=" + description + ", date=" + date + ", ammount=" + ammount + ", userRatios=" + userRatios + '}';
	}

}
