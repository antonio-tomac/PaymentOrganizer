package paymentorganizer.modelold;

import java.util.Date;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class Payment implements Dynamic {

	private static final long serialVersionUID = 1L;

	private final double ammount;
	private final Date date;
	private final User user;

	public Payment(double ammount, Date date, User user) {
		this.ammount = ammount;
		this.date = date;
		this.user = user;
	}

	public double getAmmount() {
		return ammount;
	}

	@Override
	public Date getDate() {
		return date;
	}

	public User getUser() {
		return user;
	}

	@Override
	public void apply() {
		user.addPayment(this);
	}

	@Override
	public void delete() {
		user.removePayment(this);
	}

	@Override
	public double applyDiff(User user) {
		if (user.equals(this.user)) {
			return ammount;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return "Payment{" + "ammount=" + ammount + ", date=" + date + ", user=" + user.getName() + '}';
	}

}
