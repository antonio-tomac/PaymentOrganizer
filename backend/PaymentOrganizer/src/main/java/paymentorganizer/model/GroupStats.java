package paymentorganizer.model;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Data
@Builder
public class GroupStats {
	
	private final double sumOfIncomes;
	private final double sumOfExpenses;
}
