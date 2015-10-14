package paymentorganizer.model;

import paymentorganizer.modelold.User;
import paymentorganizer.modelold.Payment;
import paymentorganizer.modelold.Calculator;
import paymentorganizer.modelold.Expense;
import paymentorganizer.modelold.Exchange;
import paymentorganizer.modelold.Dynamic;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class UserTest {

	public UserTest() {
	}

	@Test
	public void testGetOverallDebt() throws Exception {
		User u1 = new User("1L", "A");
		User u2 = new User("2L", "B");
		User u3 = new User("3L", "C");
		User u4 = new User("4L", "D");
		User u5 = new User("5L", "E");

		List<User> allUsers = Arrays.asList(u1, u2, u3, u4, u5);

		assertEquals(0, u1.getOverallDebt(), 10e-3);
		assertEquals(0, u2.getOverallDebt(), 10e-3);
		assertEquals(0, u3.getOverallDebt(), 10e-3);
		assertEquals(0, u4.getOverallDebt(), 10e-3);
		assertEquals(0, u5.getOverallDebt(), 10e-3);

		Thread.sleep(10);
		Expense expense1 = new Expense("e1", "", new Date(), 100,
				Arrays.asList(u1, u2, u3)
		);
		expense1.apply();

		assertEquals(33.333, u1.getOverallDebt(), 10e-3);
		assertEquals(33.333, u2.getOverallDebt(), 10e-3);
		assertEquals(33.333, u3.getOverallDebt(), 10e-3);
		assertEquals(0, u4.getOverallDebt(), 10e-3);
		assertEquals(0, u5.getOverallDebt(), 10e-3);

		Thread.sleep(10);
		Payment payment1 = new Payment(100, new Date(), u1);
		payment1.apply();

		assertEquals(-66.667, u1.getOverallDebt(), 10e-3);
		assertEquals(33.333, u2.getOverallDebt(), 10e-3);
		assertEquals(33.333, u3.getOverallDebt(), 10e-3);
		assertEquals(0, u4.getOverallDebt(), 10e-3);
		assertEquals(0, u5.getOverallDebt(), 10e-3);

		List<Calculator.Transaction> suggestedTransactions1 = Calculator.getSuggestedTransactions(allUsers);
		for (Calculator.Transaction transaction : suggestedTransactions1) {
			System.out.println("1: " + transaction);
		}

		Thread.sleep(10);
		List<Exchange> exchanges = Calculator.createExchangesFromTransactions(suggestedTransactions1);
		for (Exchange exchange : exchanges) {
			exchange.apply();
		}

		assertEquals(0, u1.getOverallDebt(), 10e-3);
		assertEquals(0, u2.getOverallDebt(), 10e-3);
		assertEquals(0, u3.getOverallDebt(), 10e-3);
		assertEquals(0, u4.getOverallDebt(), 10e-3);
		assertEquals(0, u5.getOverallDebt(), 10e-3);

		Thread.sleep(10);
		Expense expense2 = new Expense("e2", "", new Date(), 500,
				allUsers
		);
		expense2.apply();
		Map<User, Double> userRatios = new HashMap<>();
		userRatios.put(u1, 0.3);
		userRatios.put(u4, 0.5);
		userRatios.put(u5, 0.2);
		Thread.sleep(10);
		Expense expense3 = new Expense("e3", "", new Date(), 100,
				userRatios
		);
		expense3.apply();

		assertEquals(130, u1.getOverallDebt(), 10e-3);
		assertEquals(100, u2.getOverallDebt(), 10e-3);
		assertEquals(100, u3.getOverallDebt(), 10e-3);
		assertEquals(150, u4.getOverallDebt(), 10e-3);
		assertEquals(120, u5.getOverallDebt(), 10e-3);

		Thread.sleep(10);
		Payment payment2 = new Payment(400, new Date(), u1);
		payment2.apply();
		Thread.sleep(10);
		Payment payment3 = new Payment(200, new Date(), u4);
		payment3.apply();

		assertEquals(-270, u1.getOverallDebt(), 10e-3);
		assertEquals(100, u2.getOverallDebt(), 10e-3);
		assertEquals(100, u3.getOverallDebt(), 10e-3);
		assertEquals(-50, u4.getOverallDebt(), 10e-3);
		assertEquals(120, u5.getOverallDebt(), 10e-3);

		List<Calculator.Transaction> suggestedTransactions2 = Calculator.getSuggestedTransactions(allUsers);
		for (Calculator.Transaction transaction : suggestedTransactions2) {
			System.out.println("2: " + transaction);
		}

		Exchange exchange1 = new Exchange(u3, 200, u1, new Date());
		exchange1.apply();

		assertEquals(-70, u1.getOverallDebt(), 10e-3);
		assertEquals(100, u2.getOverallDebt(), 10e-3);
		assertEquals(-100, u3.getOverallDebt(), 10e-3);
		assertEquals(-50, u4.getOverallDebt(), 10e-3);
		assertEquals(120, u5.getOverallDebt(), 10e-3);

		Thread.sleep(10);
		List<Calculator.Transaction> suggestedTransactions3 = Calculator.getSuggestedTransactions(allUsers);
		for (Calculator.Transaction transaction : suggestedTransactions3) {
			System.out.println("3: " + transaction);
		}

		for (User u : allUsers) {
			System.out.println("---------------------------------");
			System.out.println("USER: " + u.getName());
			System.out.println("\tBalance: " + Calculator.round2dec(u.getBalance(new Date(0))));
			for (Dynamic action : u.getAllEventsInTime()) {
				System.out.println("\t" + action);
				System.out.println("\tBalance: " + Calculator.round2dec(u.getBalance(action.getDate())));
			}
			System.out.println("---------------------------------");
		}
		try (ObjectOutput output = new ObjectOutputStream(new FileOutputStream("saved.model"))) {
			output.writeObject(allUsers);
		}
		

	}

}
