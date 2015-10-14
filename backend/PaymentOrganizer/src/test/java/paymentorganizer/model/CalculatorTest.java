package paymentorganizer.model;

import paymentorganizer.modelold.User;
import paymentorganizer.modelold.Calculator;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import paymentorganizer.modelold.Calculator.Transaction;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class CalculatorTest extends TestCase {

	public CalculatorTest(String testName) {
		super(testName);
	}

	public void testGetSuggestedTransactions1() {
		User u1 = new User("1L", "A") {
			@Override
			public double getOverallDebt() {
				return -1;
			}
		};
		User u2 = new User("2L", "B") {
			@Override
			public double getOverallDebt() {
				return 2;
			}
		};
		User u3 = new User("3L", "C") {
			@Override
			public double getOverallDebt() {
				return 3;
			}
		};
		User u4 = new User("4L", "D") {
			@Override
			public double getOverallDebt() {
				return -4;
			}
		};
		List<Transaction> suggestedTransactions = Calculator.getSuggestedTransactions(Arrays.asList(u1, u2, u3, u4));
		for (Transaction transaction : suggestedTransactions) {
			System.out.println(transaction);
		}
	}

	public void testGetSuggestedTransactions2() {
		User u1 = new User("1L", "A") {
			@Override
			public double getOverallDebt() {
				return -100;
			}
		};
		User u2 = new User("2L", "B") {
			@Override
			public double getOverallDebt() {
				return -50;
			}
		};
		User u3 = new User("3L", "C") {
			@Override
			public double getOverallDebt() {
				return 120;
			}
		};
		User u4 = new User("4L", "D") {
			@Override
			public double getOverallDebt() {
				return 25;
			}
		};
		User u5 = new User("5L", "E") {
			@Override
			public double getOverallDebt() {
				return 5;
			}
		};
		List<Transaction> suggestedTransactions = Calculator.getSuggestedTransactions(Arrays.asList(u1, u2, u3, u4, u5));
		for (Transaction transaction : suggestedTransactions) {
			System.out.println(transaction);
		}
	}

	public void testGetSuggestedTransactions3() {
		User u1 = new User("1L", "A") {
			@Override
			public double getOverallDebt() {
				return -1;
			}
		};
		User u2 = new User("2L", "B") {
			@Override
			public double getOverallDebt() {
				return -2;
			}
		};
		boolean caught = false;
		try {
			List<Transaction> suggestedTransactions = Calculator.getSuggestedTransactions(Arrays.asList(u1, u2));

			for (Transaction transaction : suggestedTransactions) {
				System.out.println(transaction);
			}
		} catch (RuntimeException ex) {
			caught = true;
			System.out.println(ex.getMessage());
		}
		assertTrue(caught);
	}

}
