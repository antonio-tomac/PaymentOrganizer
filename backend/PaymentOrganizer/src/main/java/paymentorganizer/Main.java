package paymentorganizer;

import java.util.Arrays;
import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.beans.BeansException;
import paymentorganizer.repositories.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import paymentorganizer.model.Exchange;
import paymentorganizer.model.Expense;
import paymentorganizer.model.UserRatio;
import paymentorganizer.model.Group;
import paymentorganizer.model.Payment;
import paymentorganizer.model.User;
import paymentorganizer.repositories.GroupRepository;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class Main {

	public static void main(String[] args) {
		create();
	}

	private static void getByUser() throws BeansException {
		ApplicationContext context = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		UserRepository ur = context.getBean(UserRepository.class);
		GroupRepository gr = context.getBean(GroupRepository.class);
		User user = ur.findByName("antonio").get(0);
		Group group = gr.findByUserId(new ObjectId(user.getId())).get(0);
//		Expense expense = new Expense("bla", "za nešto", new Date(), 123.45, 
//				Expense.generateEqualRatios(Arrays.asList(user)));
//		er.save(expense);
		System.out.println(group);
	}

	public static void create() {
		ApplicationContext context = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		UserRepository ur = context.getBean(UserRepository.class);
		GroupRepository gr = context.getBean(GroupRepository.class);
		Group group = new Group("Hladni lem");
		User antonio = new User("Tomac");
		User marko = new User("Jeff");
		User gaso = new User("Gašo");
		User tomo = new User("Tomo");
		User mirela = new User("Mirela");
		group.addUser(antonio);
		group.addUser(marko);
		group.addUser(gaso);
		group.addUser(tomo);
		group.addUser(mirela);
		ur.save(antonio);
		ur.save(marko);
		ur.save(gaso);
		ur.save(tomo);
		ur.save(mirela);
/*		Expense expense1 = new Expense("foo", new Date(), 120, 
				Expense.generateEqualRatios(Arrays.asList(antonio, gaso, marko)));
		group.addExpense(expense1);
		Payment payment1 = new Payment(120, new Date(), antonio);
		group.addPayment(payment1);
		Exchange exchange1 = new Exchange(gaso, antonio, 50, new Date());
		group.addExchange(exchange1);
		Expense expense2 = new Expense("foo2", new Date(), 100, 
				Arrays.asList(new UserRatio(antonio, 0.8), new UserRatio(marko, 0.2)));
		group.addExpense(expense2);
		Payment payment2 = new Payment(100, new Date(), gaso);
		group.addPayment(payment2);
	*/
		gr.save(group);
	
	}

	private static void readAll() throws BeansException {
		ApplicationContext context = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		GroupRepository gr = context.getBean(GroupRepository.class);
		UserRepository ur = context.getBean(UserRepository.class);
		for (Group group : gr.findAll()) {
			System.out.println(group.getName());
			for (User user : group.getUsers()) {
				System.out.println("\t" + user.getName() + " - " + user.getId());
				ur.save(user);
			}
		}
	}
}
