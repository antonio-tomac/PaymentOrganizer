package paymentorganizer.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import paymentorganizer.MongoConfiguration;
import paymentorganizer.model.Exchange;
import paymentorganizer.model.Expense;
import paymentorganizer.model.Group;
import paymentorganizer.model.Income;
import paymentorganizer.model.Payment;
import paymentorganizer.model.Receivement;
import paymentorganizer.model.User;
import paymentorganizer.model.UserEventBalance;
import paymentorganizer.model.UserRatio;
import paymentorganizer.repositories.GroupRepository;
import paymentorganizer.repositories.Repository;
import paymentorganizer.repositories.UserRepository;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@RestController
@SpringBootApplication
@Import({MongoConfiguration.class})
@EnableMongoRepositories(basePackageClasses = Repository.class)
@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
@EnableWebSocket
public class ApiController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GroupRepository groupRepository;

	private void checkGroupExist(String groupId) {
		if (!groupRepository.exists(groupId)) {
			throw new RuntimeException("Group does not exist, id: " + groupId);
		}
	}

	private void checkUserExist(String userId) {
		if (!userRepository.exists(userId)) {
			throw new RuntimeException("Use does not exist, id: " + userId);
		}
	}

	@RequestMapping(value = "/groups", method = RequestMethod.GET)
	public List<Group> getGroups() {
		return groupRepository.findAll();
	}

	@RequestMapping(value = "/groups/{groupId}", method = RequestMethod.GET)
	public Group getGroup(@PathVariable String groupId) {
		checkGroupExist(groupId);
		return groupRepository.findOne(groupId);
	}

	@RequestMapping(value = "/groups/{groupId}/users/{userId}/events", method = RequestMethod.GET)
	public List<UserEventBalance> getUserEvents(@PathVariable String groupId, @PathVariable String userId) {
		checkGroupExist(groupId);
		checkUserExist(userId);
		Group group = groupRepository.findOne(groupId);
		User user = userRepository.findOne(userId);
		return group.getUserEventsBalances(user);
	}

	@RequestMapping(value = "/groups", method = RequestMethod.POST)
	public Group createGroup(@RequestParam String name) {
		Group group = new Group(name);
		groupRepository.save(group);
		return group;
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public User createUser(@RequestParam String name) {
		User user = new User(name);
		userRepository.save(user);
		return user;
	}

	@RequestMapping(value = "/groups/{groupId}/users", method = RequestMethod.POST)
	public User addUserToGroup(@PathVariable String groupId, @RequestParam String userId) {
		checkGroupExist(groupId);
		Group group = groupRepository.findOne(groupId);
		checkUserExist(userId);
		User user = userRepository.findOne(userId);
		group.addUser(user);
		groupRepository.save(group);
		publishReload(groupId);
		return user;
	}

	public static class PaymentData {

		protected String userId;
		@Min(0)
		protected double ammount;
		@JsonDeserialize(using = DateJson.DateDeserializer.class)
		protected Date date;

		public String getUserId() {
			return userId;
		}

		public double getAmmount() {
			return ammount;
		}

		public Date getDate() {
			return date;
		}
	}

	@RequestMapping(value = "/groups/{groupId}/payments", method = RequestMethod.POST)
	public Payment addPayment(@PathVariable String groupId, @RequestBody PaymentData paymentData) {
		checkGroupExist(groupId);
		Group group = groupRepository.findOne(groupId);
		checkUserExist(paymentData.userId);
		User user = userRepository.findOne(paymentData.userId);
		Payment payment = new Payment(paymentData.ammount, paymentData.date, user);
		group.addPayment(payment);
		groupRepository.save(group);
		publishReload(groupId);
		return payment;
	}

	@RequestMapping(value = "/groups/{groupId}/payments/{paymentId}", method = RequestMethod.DELETE)
	public Group deletePayment(@PathVariable String groupId, @PathVariable String paymentId) {
		checkGroupExist(groupId);
		Group group = groupRepository.findOne(groupId);
		for (Payment payment : group.getPayments()) {
			if (payment.getId().equals(paymentId)) {
				group.removePayment(payment);
				groupRepository.save(group);
				break;
			}
		}
		publishReload(groupId);
		return group;
	}

	public static class ExchangeData {

		protected String fromUserId;
		protected String toUserId;
		@Min(0)
		protected double ammount;
		@JsonDeserialize(using = DateJson.DateDeserializer.class)
		protected Date date;

		public String getFromUserId() {
			return fromUserId;
		}

		public String getToUserId() {
			return toUserId;
		}

		public double getAmmount() {
			return ammount;
		}

		public Date getDate() {
			return date;
		}
	}

	@RequestMapping(value = "/groups/{groupId}/exchanges", method = RequestMethod.POST)
	public Exchange addExchange(@PathVariable String groupId,
		@Valid @RequestBody ExchangeData exchangeData) {
		checkGroupExist(groupId);
		Group group = groupRepository.findOne(groupId);
		checkUserExist(exchangeData.fromUserId);
		User fromUser = userRepository.findOne(exchangeData.fromUserId);
		checkUserExist(exchangeData.toUserId);
		User toUser = userRepository.findOne(exchangeData.toUserId);
		Exchange exchange = new Exchange(fromUser, toUser, exchangeData.ammount, exchangeData.date);
		group.addExchange(exchange);
		groupRepository.save(group);
		publishReload(groupId);
		return exchange;
	}

	@RequestMapping(value = "/groups/{groupId}/exchanges/{exchangeId}", method = RequestMethod.DELETE)
	public Group deleteExchange(@PathVariable String groupId, @PathVariable String exchangeId) {
		checkGroupExist(groupId);
		Group group = groupRepository.findOne(groupId);
		for (Exchange exchange : group.getExchanges()) {
			if (exchange.getId().equals(exchangeId)) {
				group.removeExchange(exchange);
				groupRepository.save(group);
				break;
			}
		}
		publishReload(groupId);
		return group;
	}

	public static class ExpenseData {

		@Min(0)
		protected double ammount;
		@JsonDeserialize(using = DateJson.DateDeserializer.class)
		protected Date date;
		@Size(min = 1)
		protected String name;
		@NotNull
		protected Map<String, Double> userRatios;

		public double getAmmount() {
			return ammount;
		}

		public Date getDate() {
			return date;
		}

		public String getName() {
			return name;
		}

		public Map<String, Double> getUserRatios() {
			return Collections.unmodifiableMap(userRatios);
		}
	}

	@RequestMapping(value = "/groups/{groupId}/expenses", method = RequestMethod.POST)
	public Expense addExpense(@PathVariable String groupId, @RequestBody ExpenseData expenseData) {
		checkGroupExist(groupId);
		Group group = groupRepository.findOne(groupId);
		List<UserRatio> userRatiosList = new ArrayList<>(expenseData.userRatios.size());
		for (Map.Entry<String, Double> userRatio : expenseData.userRatios.entrySet()) {
			String userId = userRatio.getKey();
			Double ratio = userRatio.getValue();
			checkUserExist(userId);
			User user = userRepository.findOne(userId);
			userRatiosList.add(new UserRatio(user, ratio));
		}
		Expense expense = new Expense(expenseData.name, expenseData.date, expenseData.ammount, userRatiosList);
		group.addExpense(expense);
		groupRepository.save(group);
		publishReload(groupId);
		return expense;
	}

	@RequestMapping(value = "/groups/{groupId}/expenses/{expenseId}", method = RequestMethod.DELETE)
	public Group deleteExpense(@PathVariable String groupId, @PathVariable String expenseId) {
		checkGroupExist(groupId);
		Group group = groupRepository.findOne(groupId);
		for (Expense expense : group.getExpenses()) {
			if (expense.getId().equals(expenseId)) {
				group.removeExpense(expense);
				groupRepository.save(group);
				break;
			}
		}
		publishReload(groupId);
		return group;
	}

	public static class IncomeData {

		@Min(0)
		protected double ammount;
		@JsonDeserialize(using = DateJson.DateDeserializer.class)
		protected Date date;
		@Size(min = 1)
		protected String name;
		@NotNull
		protected Map<String, Double> userRatios;

		public double getAmmount() {
			return ammount;
		}

		public Date getDate() {
			return date;
		}

		public String getName() {
			return name;
		}

		public Map<String, Double> getUserRatios() {
			return Collections.unmodifiableMap(userRatios);
		}
	}

	@RequestMapping(value = "/groups/{groupId}/incomes", method = RequestMethod.POST)
	public Income addIncome(@PathVariable String groupId, @RequestBody IncomeData incomeData) {
		checkGroupExist(groupId);
		Group group = groupRepository.findOne(groupId);
		List<UserRatio> userRatiosList = new ArrayList<>(incomeData.userRatios.size());
		for (Map.Entry<String, Double> userRatio : incomeData.userRatios.entrySet()) {
			String userId = userRatio.getKey();
			Double ratio = userRatio.getValue();
			checkUserExist(userId);
			User user = userRepository.findOne(userId);
			userRatiosList.add(new UserRatio(user, ratio));
		}
		Income income = new Income(incomeData.name, incomeData.date, incomeData.ammount, userRatiosList);
		group.addIncome(income);
		groupRepository.save(group);
		publishReload(groupId);
		return income;
	}

	@RequestMapping(value = "/groups/{groupId}/incomes/{incomeId}", method = RequestMethod.DELETE)
	public Group deleteIncome(@PathVariable String groupId, @PathVariable String incomeId) {
		checkGroupExist(groupId);
		Group group = groupRepository.findOne(groupId);
		for (Income income : group.getIncomes()) {
			if (income.getId().equals(incomeId)) {
				group.removeIncome(income);
				groupRepository.save(group);
				break;
			}
		}
		publishReload(groupId);
		return group;
	}

	public static class ReceivementData {

		protected String userId;
		@Min(0)
		protected double ammount;
		@JsonDeserialize(using = DateJson.DateDeserializer.class)
		protected Date date;

		public String getUserId() {
			return userId;
		}

		public double getAmmount() {
			return ammount;
		}

		public Date getDate() {
			return date;
		}
	}

	@RequestMapping(value = "/groups/{groupId}/receivements", method = RequestMethod.POST)
	public Receivement addReceivement(@PathVariable String groupId, @RequestBody ReceivementData receivementData) {
		checkGroupExist(groupId);
		Group group = groupRepository.findOne(groupId);
		checkUserExist(receivementData.userId);
		User user = userRepository.findOne(receivementData.userId);
		Receivement receivement = new Receivement(receivementData.ammount, receivementData.date, user);
		group.addReceivement(receivement);
		groupRepository.save(group);
		publishReload(groupId);
		return receivement;
	}

	@RequestMapping(value = "/groups/{groupId}/receivements/{receivmentId}", method = RequestMethod.DELETE)
	public Group deleteReceivement(@PathVariable String groupId, @PathVariable String receivmentId) {
		checkGroupExist(groupId);
		Group group = groupRepository.findOne(groupId);
		for (Receivement receivement : group.getReceivements()) {
			if (receivement.getId().equals(receivmentId)) {
				group.removeReceivement(receivement);
				groupRepository.save(group);
				break;
			}
		}
		publishReload(groupId);
		return group;
	}

	@Autowired
	private SimpMessagingTemplate template;

	@RequestMapping("/publish")
	public String publishMessage(@RequestParam String channel,
		@RequestParam String message) {
		this.template.convertAndSend(channel, message);
		return "ok";
	}

	public void publishReload(String groupId) {
		this.template.convertAndSend(groupId, "reload");
	}

	@Bean
	EmbeddedServletContainerCustomizer containerCustomizer() throws Exception {

		return (ConfigurableEmbeddedServletContainer container) -> {
			TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
			tomcat.setPort(8081);
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiController.class, args);
	}

}
