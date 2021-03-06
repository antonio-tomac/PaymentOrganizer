package paymentorganizer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
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
	private final List<Income> incomes;
	private final List<Receivement> receivements;

	public Group(String name) {
		this.name = name;
		users = new LinkedList<>();
		payments = new LinkedList<>();
		exchanges = new LinkedList<>();
		expenses = new LinkedList<>();
		incomes = new LinkedList<>();
		receivements = new LinkedList<>();
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

	public List<Income> getIncomes() {
		return Collections.unmodifiableList(incomes);
	}

	public void addIncome(Income income) {
		incomes.add(income);
	}

	public void removeIncome(Income income) {
		incomes.remove(income);
	}

	public List<Receivement> getReceivements() {
		return Collections.unmodifiableList(receivements);
	}

	public void addReceivement(Receivement receivement) {
		receivements.add(receivement);
	}

	public void removeReceivement(Receivement receivement) {
		receivements.remove(receivement);
	}

	public List<UserBalance> getUserBalances() {
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
			for (UserRatio userRatio : expense.getUserRatios()) {
				User user = userRatio.getUser();
				if (!userBalanceMap.containsKey(user)) {
					userBalanceMap.put(user, 0.);
				}
				userBalanceMap.put(user, userBalanceMap.get(user) - userRatio.getRatio() * expense.getAmmount());
			}
		}
		for (Income income : incomes) {
			for (UserRatio userRatio : income.getUserRatios()) {
				User user = userRatio.getUser();
				if (!userBalanceMap.containsKey(user)) {
					userBalanceMap.put(user, 0.);
				}
				userBalanceMap.put(user, userBalanceMap.get(user) + userRatio.getRatio() * income.getAmmount());
			}
		}
		for (Receivement receivement : receivements) {
			User user = receivement.getUser();
			if (!userBalanceMap.containsKey(user)) {
				userBalanceMap.put(user, 0.);
			}
			userBalanceMap.put(user, userBalanceMap.get(user) - receivement.getAmmount());
		}
		List<UserBalance> userBalances = userBalanceMap.entrySet().stream()
			.map(entry -> new UserBalance(entry.getKey(), entry.getValue()))
			.collect(Collectors.toList());
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
		for (Income income : incomes) {
			paymentEvents.add(new PaymentEvent(income, income.getDate()));
		}
		for (Receivement receivement : receivements) {
			paymentEvents.add(new PaymentEvent(receivement, receivement.getDate()));
		}
		Collections.sort(paymentEvents);
		return paymentEvents;
	}

	public List<UserEventBalance> getUserEventsBalances(User user) {
		TreeMap<PaymentEvent, Double> eventToImpact = new TreeMap<>();
		payments.stream()
			.filter((payment) -> (payment.getUser().equals(user)))
			.forEach((payment) -> {
				eventToImpact.put(new PaymentEvent(payment, payment.getDate()), payment.getAmmount());
			});
		expenses.stream().forEach((expense) -> {
			double userRatio = expense.getUserRatio(user);
			if (userRatio > 0) {
				eventToImpact.put(new PaymentEvent(expense, expense.getDate()), -userRatio * expense.getAmmount());
			}
		});
		exchanges.forEach(exchange -> {
			if (exchange.getFrom().equals(user)) {
				eventToImpact.put(new PaymentEvent(exchange, exchange.getDate()), exchange.getAmmount());
			}
			if (exchange.getTo().equals(user)) {
				eventToImpact.put(new PaymentEvent(exchange, exchange.getDate()), -exchange.getAmmount());
			}
		});
		incomes.stream().forEach((income) -> {
			double userRatio = income.getUserRatio(user);
			if (userRatio > 0) {
				eventToImpact.put(new PaymentEvent(income, income.getDate()), userRatio * income.getAmmount());
			}
		});
		receivements.stream()
			.filter((receivement) -> (receivement.getUser().equals(user))
			).forEach((receivement) -> {
				eventToImpact.put(new PaymentEvent(receivement, receivement.getDate()), -receivement.getAmmount());
			});
		double balance = 0;
		List<UserEventBalance> result = new ArrayList<>(eventToImpact.size());
		for (Entry<PaymentEvent, Double> entry : eventToImpact.entrySet()) {
			PaymentEvent event = entry.getKey();
			Double impact = entry.getValue();
			balance += impact;
			result.add(new UserEventBalance(event, balance, impact));
		}
		return result;
	}
	
	public GroupStats getGroupStats() {
		Double allExpenses = expenses.stream().collect(Collectors.summingDouble(Expense::getAmmount));
		Double allIncomes = incomes.stream().collect(Collectors.summingDouble(Income::getAmmount));
		return GroupStats.builder()
			.sumOfExpenses(allExpenses)
			.sumOfIncomes(allIncomes)
			.build();
	}

	@Override
	public String toString() {
		return "Group{" + "id=" + id + ", name=" + name + ", users=" + users + '}';
	}

}
