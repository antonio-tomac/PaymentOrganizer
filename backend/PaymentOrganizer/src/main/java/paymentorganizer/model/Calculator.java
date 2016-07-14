package paymentorganizer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class Calculator {

	public static final double MONEY_EPSILON = 10e-3;

	public static double round2dec(double ammount) {
		return Math.round(ammount * 100) / 100.;
	}

	@Data
	public static class Transaction {

		public final User from;
		public final User to;
		public final double ammount;
	}

	public static class UnbalancedAmmounts extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public UnbalancedAmmounts(String message) {
			super(message);
		}

	}

	public static double getGroupDisbalance(List<UserBalance> userBalances) {
		double groupBalance = 0;
		for (UserBalance userBalance : userBalances) {
			groupBalance += userBalance.getBalance();
		}
		return groupBalance;
	}

	public static List<Transaction> getSuggestedTransactions(List<UserBalance> userBalances) {
		final Map<User, Double> debtorsDebts = new HashMap<>();
		final Map<User, Double> creditorsCredits = new HashMap<>();
		List<User> debtors = new ArrayList<>(userBalances.size());
		List<User> creditors = new ArrayList<>(userBalances.size());
		for (UserBalance userBalance : userBalances) {
			double overallDebt = -userBalance.getBalance();
			User user = userBalance.getUser();
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
			throw new UnbalancedAmmounts("Debtoprs and creditors are not in balance in sum of their ammounts");
		}
		return transactions;
	}

	public static List<UserRatio> generateEqualRatios(List<User> users) {
		List<UserRatio> userRatios = new LinkedList<>();
		double ratio = 1. / users.size();
		for (User user : users) {
			userRatios.add(new UserRatio(user, ratio));
		}
		return userRatios;
	}

	public static void checkRatios(List<UserRatio> userRatios) {
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

}
