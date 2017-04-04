package paymentorganizer.model;

import lombok.Data;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Data
public class UserEventBalance {
	
	private final PaymentEvent paymentEvent;
	private final double balance;
	private final double impact;
}
