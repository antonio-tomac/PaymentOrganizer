package paymentorganizer.modelold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class Calculator {

	public static final double MONEY_EPSILON = 10e-3;
	
	public static double round2dec(double ammount) {
		return Math.round(ammount * 100) / 100.;
	}

	public static class Transaction {

		public final User from;
		public final User to;
		public final double ammount;

		public Transaction(User from, User to, double ammount) {
			this.from = from;
			this.to = to;
			this.ammount = ammount;
		}

		@Override
		public String toString() {
			return "Transaction{" + "from=" + from.getName() + ", to=" + to.getName() + ", ammount=" + round2dec(ammount) + '}';
		}
	}

	public static List<Transaction> getSuggestedTransactions(List<User> users) {
		final Map<User, Double> debtorsDebts = new HashMap<>();
		final Map<User, Double> creditorsCredits = new HashMap<>();
		List<User> debtors = new ArrayList<>(users.size());
		List<User> creditors = new ArrayList<>(users.size());
		for (User user : users) {
			double overallDebt = user.getOverallDebt();
			if (Math.abs(overallDebt) < MONEY_EPSILON) {
			} else if (overallDebt > 0) {
				debtors.add(user);
				debtorsDebts.put(user, overallDebt);
			} else {
				creditors.add(user);
				creditorsCredits.put(user, -overallDebt);
			}
		}
		List<Transaction> transactions = new LinkedList<>();

		while (!debtors.isEmpty() && !creditors.isEmpty()) {
			Collections.sort(debtors, new Comparator<User>() {

				@Override
				public int compare(User u1, User u2) {
					return -debtorsDebts.get(u1).compareTo(debtorsDebts.get(u2));
				}
			});
			Collections.sort(creditors, new Comparator<User>() {

				@Override
				public int compare(User u1, User u2) {
					return -creditorsCredits.get(u1).compareTo(creditorsCredits.get(u2));
				}
			});
			User debtor = debtors.get(0);
			User creditor = creditors.get(0);
			double ammount = Math.min(debtorsDebts.get(debtor), creditorsCredits.get(creditor));
			transactions.add(new Transaction(debtor, creditor, ammount));
			double newDebt = debtorsDebts.get(debtor) - ammount;
			if (Math.abs(newDebt) < MONEY_EPSILON) {
				debtors.remove(debtor);
				debtorsDebts.remove(debtor);
			} else {
				debtorsDebts.put(debtor, newDebt);
			}
			double newCredit = creditorsCredits.get(creditor) - ammount;
			if (Math.abs(newCredit) < MONEY_EPSILON) {
				creditors.remove(creditor);
				creditorsCredits.remove(creditor);
			} else {
				creditorsCredits.put(creditor, newCredit);
			}
		}
		if (!debtors.isEmpty() || !creditors.isEmpty()) {
			throw new RuntimeException("Debtoprs and creditors are not in balance in sum of their ammounts");
		}
		return transactions;
	}

	private static void sleep() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException ex) {
		}
	}

	public static List<Exchange> createExchangesFromTransactions(
			List<Transaction> transactions) {
		List<Exchange> exchanges = new ArrayList<>(transactions.size());
		for (Transaction t : transactions) {
			sleep();
			exchanges.add(new Exchange(t.from, t.ammount, t.to, new Date()));
		}
		return exchanges;
	}

}
