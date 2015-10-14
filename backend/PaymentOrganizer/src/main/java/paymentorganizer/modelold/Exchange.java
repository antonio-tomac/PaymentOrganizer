package paymentorganizer.modelold;

import java.util.Date;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class Exchange implements Dynamic {

	private static final long serialVersionUID = 1L;

	private final User from;
	private final double ammount;
	private final User to;
	private final Date date;

	public Exchange(User from, double ammount, User to, Date date) {
		this.from = from;
		this.ammount = ammount;
		this.to = to;
		this.date = date;
	}

	@Override
	public void apply() {
		from.addSentExchange(this);
		to.addReceivedExchange(this);
	}

	@Override
	public void delete() {
		from.removeSentExchange(this);
		to.removeReceivedExchange(this);
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

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public double applyDiff(User user) {
		if (user.equals(from)) {
			return ammount;
		} else if (user.equals(to)) {
			return -ammount;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return "Exchange{" + "from=" + from.getName() + ", to=" + to.getName() + ", ammount=" + Calculator.round2dec(ammount) + ", date=" + date + '}';
	}

}
