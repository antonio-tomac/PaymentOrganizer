package paymentorganizer.modelold;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String id;
	private final String name;
	private final List<Payment> payments;
	private final List<Expense> participations;
	private final List<Exchange> sentExchanges;
	private final List<Exchange> receivedExchanges;
	private final Set<Group> groups;

	public User(String id, String name) {
		this.id = id;
		this.name = name;
		payments = new LinkedList<>();
		participations = new LinkedList<>();
		receivedExchanges = new LinkedList<>();
		sentExchanges = new LinkedList<>();
		groups = new HashSet<>();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void addPayment(Payment payment) {
		payments.add(payment);
	}

	public void removePayment(Payment payment) {
		payments.remove(payment);
	}

	public void addExpense(Expense expense) {
		participations.add(expense);
	}

	public void removeExpense(Expense expense) {
		participations.remove(expense);
	}

	public void addSentExchange(Exchange exchange) {
		sentExchanges.add(exchange);
	}

	public void removeSentExchange(Exchange exchange) {
		sentExchanges.remove(exchange);
	}

	public void addReceivedExchange(Exchange exchange) {
		receivedExchanges.add(exchange);
	}

	public void removeReceivedExchange(Exchange exchange) {
		receivedExchanges.remove(exchange);
	}

	public double getPaymentsAmmount() {
		double sum = 0;
		for (Payment payment : payments) {
			sum += payment.getAmmount();
		}
		return sum;
	}

	public double getExpensesAmmount() {
		double sum = 0;
		for (Expense expense : participations) {
			sum += expense.getAmmount() * expense.getUserContributionFactor(this);
		}
		return sum;
	}

	public double getSentExchanges() {
		double sum = 0;
		for (Exchange exchange : sentExchanges) {
			sum += exchange.getAmmount();
		}
		return sum;
	}

	public double getReceivedExchanges() {
		double sum = 0;
		for (Exchange exchange : receivedExchanges) {
			sum += exchange.getAmmount();
		}
		return sum;
	}

	public double getOverallDebt() {
		double debt = 0;
		debt += getExpensesAmmount();
		debt -= getPaymentsAmmount();
		debt += getReceivedExchanges();
		debt -= getSentExchanges();
		return debt;
	}

	public List<Dynamic> getAllEventsInTime() {
		return getAllEventsInTime(new Date());
	}

	public List<Dynamic> getAllEventsInTime(Date toTime) {
		List<Dynamic> allEvents = new ArrayList<>(
				payments.size() + participations.size() + sentExchanges.size() + receivedExchanges.size()
		);
		allEvents.addAll(payments);
		allEvents.addAll(participations);
		allEvents.addAll(sentExchanges);
		allEvents.addAll(receivedExchanges);
		Collections.sort(allEvents, new Comparator<Dynamic>() {

			@Override
			public int compare(Dynamic o1, Dynamic o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		List<Dynamic> result = new LinkedList<>();
		for (Dynamic dynamic : allEvents) {
			if (!dynamic.getDate().after(toTime)) {
				result.add(dynamic);
			}
		}
		return result;
	}

	public double getBalance(Date atTime) {
		double balance = 0;
		List<Dynamic> allEventsInTime = getAllEventsInTime(atTime);
		for (Dynamic dynamic : allEventsInTime) {
			balance += dynamic.applyDiff(this);
		}
		return balance;
	}

	@Override
	public String toString() {
		return "User{" + "name=" + name + '}';
	}

}
