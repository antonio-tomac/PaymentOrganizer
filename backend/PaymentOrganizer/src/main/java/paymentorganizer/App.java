package paymentorganizer;

import java.util.Date;
import paymentorganizer.modelold.Calculator;
import paymentorganizer.modelold.Dynamic;
import paymentorganizer.modelold.Repository;
import paymentorganizer.modelold.User;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) throws Exception {
		Repository repository = new Repository();
		for (User u : repository.getUsers()) {
			System.out.println("---------------------------------");
			System.out.println("USER: " + u.getName());
			System.out.println("\tBalance: " + Calculator.round2dec(u.getBalance(new Date(0))));
			for (Dynamic action : u.getAllEventsInTime()) {
				System.out.println("\t" + action);
				System.out.println("\tBalance: " + Calculator.round2dec(u.getBalance(action.getDate())));
			}
			System.out.println("---------------------------------");
		}
	}
}
