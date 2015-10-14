
package paymentorganizer;

import org.springframework.boot.SpringApplication;
import paymentorganizer.web.WebController;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class MainWeb {
		
	public static void main(String[] args) {
		SpringApplication.run(WebController.class, args);
	}
}
