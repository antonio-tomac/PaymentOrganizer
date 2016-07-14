package paymentorganizer.model;

import lombok.Data;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Data
public class UserBalance {
	
	private final User user;
	private final double balance;
}
