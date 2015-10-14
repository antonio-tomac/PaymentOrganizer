
package paymentorganizer.web;

import java.util.Date;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import paymentorganizer.modelold.Payment;
import paymentorganizer.modelold.User;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@SpringBootApplication
@Controller
@EnableAutoConfiguration(exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class)
//@EnableWebMvc
public class WebController {
	
    @RequestMapping("/greeting")
    public String greeting(
			@RequestParam(value="name", required=false, defaultValue="World") String name, 
			Model model
	) {
        model.addAttribute("name", name);
		//Payment payment = new Payment(123, new Date(), new User(name, name));
		//payment.apply();
		//model.addAttribute("payment", payment);
        return "greeting";
    }	
}
