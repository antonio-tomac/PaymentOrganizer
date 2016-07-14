package paymentorganizer.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Data
public class UserRatio {
	
	@DBRef
	public final User user;
	public final double ratio;	
}
