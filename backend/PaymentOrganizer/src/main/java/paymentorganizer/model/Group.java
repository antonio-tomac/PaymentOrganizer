package paymentorganizer.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import paymentorganizer.model.Calculator.UnbalancedAmmounts;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Document(collection = "groups")
public class Group {

	@Id
	private ObjectId id;
	@Indexed
	private String name;
	@DBRef
	@Indexed
	private final List<User> users;
	private final List<Payment> payments;
	private final List<Exchange> exchanges;
	private final List<Expense> expenses;

	public Group(String name) {
		this.name = name;
		users = new LinkedList<>();
		payments = new LinkedList<>();
		exchanges = new LinkedList<>();
		expenses = new LinkedList<>();
	}

	public String getId() {
		return id.toString();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Group other = (Group) obj;
		return Objects.equals(this.id, other.id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return Collections.unmodifiableList(users);
	}

	public void addUser(User user) {
		users.add(user);
	}

	public void removeUser(User user) {
		users.remove(user);
	}

	public List<Payment> getPayments() {
		return Collections.unmodifiableList(payments);
	}

	public void addPayment(Payment payment) {
		payments.add(payment);
	}

	public void removePayment(Payment payment) {
		payments.remove(payment);
	}

	public List<Exchange> getExchanges() {
		return Collections.unmodifiableList(exchanges);
	}

	public void addExchange(Exchange exchange) {
		exchanges.add(exchange);
	}

	public void removeExchange(Exchange exchange) {
		exchanges.remove(exchange);
	}

	public List<Expense> getExpenses() {
		return Collections.unmodifiableList(expenses);
	}

	public void addExpense(Expense expense) {
		expenses.add(expense);
	}

	public void removeExpense(Expense expense) {
		expenses.remove(expense);
	}

	public static class UserBalance {

		private final User user;
		private final double balance;

		public UserBalance(User user, double balance) {
			this.user = user;
			this.balance = balance;
		}

		public User getUser() {
			return user;
		}

		public double getBalance() {
			return balance;
		}
	}

	public List<UserBalance> getUserBalances() {
		List<UserBalance> userBalances = new LinkedList<>();
		Map<User, Double> userBalanceMap = new HashMap<>();
		for (Payment payment : payments) {
			User user = payment.getUser();
			if (!userBalanceMap.containsKey(user)) {
				userBalanceMap.put(user, 0.);
			}
			userBalanceMap.put(user, userBalanceMap.get(user) + payment.getAmmount());
		}
		for (Exchange exchange : exchanges) {
			User userGiver = exchange.getFrom();
			if (!userBalanceMap.containsKey(userGiver)) {
				userBalanceMap.put(userGiver, 0.);
			}
			userBalanceMap.put(userGiver, userBalanceMap.get(userGiver) + exchange.getAmmount());
			User userReceiver = exchange.getTo();
			if (!userBalanceMap.containsKey(userReceiver)) {
				userBalanceMap.put(userReceiver, 0.);
			}
			userBalanceMap.put(userReceiver, userBalanceMap.get(userReceiver) - exchange.getAmmount());
		}
		for (Expense expense : expenses) {
			for (Expense.UserRatio userRatio : expense.getUserRatios()) {
				User user = userRatio.getUser();
				if (!userBalanceMap.containsKey(user)) {
					userBalanceMap.put(user, 0.);
				}
				userBalanceMap.put(user, userBalanceMap.get(user) - userRatio.getRatio() * expense.getAmmount());
			}
		}
		for (Map.Entry<User, Double> entry : userBalanceMap.entrySet()) {
			User user = entry.getKey();
			Double balance = entry.getValue();
			userBalances.add(new UserBalance(user, balance));
		}
		return userBalances;
	}

	public List<Calculator.Transaction> getSugestedTransactions() {
		List<Calculator.Transaction> suggestedTransactions = null;
		try {
			suggestedTransactions = Calculator.getSuggestedTransactions(getUserBalances());
		} catch (UnbalancedAmmounts ex) {

		}
		return suggestedTransactions;
	}
	
	public double getGroupBalance() {
		return Calculator.getGroupDisbalance(getUserBalances());
	}
	
	public static class PaymentEvent implements Comparable<PaymentEvent>{
		private final Sortable event;
		private final Date date;
		private final String type;

		public PaymentEvent(Sortable event, Date date) {
			this.event = event;
			this.date = date;
			type = event.getClass().getSimpleName();
		}

		public Sortable getEvent() {
			return event;
		}

		public Date getDate() {
			return date;
		}

		public String getType() {
			return type;
		}

		@Override
		public int compareTo(PaymentEvent o) {
			int compareTo = date.compareTo(o.getDate());
			if (compareTo == 0) {
				int timestamp = event.getObjectId().getTimestamp();
				int otherTimestamp = o.getEvent().getObjectId().getTimestamp();
				return Integer.compare(timestamp, otherTimestamp);
			}
			return compareTo;
		}
		
	}
	
	public List<PaymentEvent> getPaymentEvents() {
		List<PaymentEvent> paymentEvents = new LinkedList<>();
		for (Payment payment : payments) {
			paymentEvents.add(new PaymentEvent(payment, payment.getDate()));
		}
		for (Expense expense : expenses) {
			paymentEvents.add(new PaymentEvent(expense, expense.getDate()));
		}
		for (Exchange exchange : exchanges) {
			paymentEvents.add(new PaymentEvent(exchange, exchange.getDate()));
		}
		Collections.sort(paymentEvents);
		return paymentEvents;
	}

	@Override
	public String toString() {
		return "Group{" + "id=" + id + ", name=" + name + ", users=" + users + '}';
	}

}
