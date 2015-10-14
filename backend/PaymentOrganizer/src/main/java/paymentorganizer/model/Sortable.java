
package paymentorganizer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import org.bson.types.ObjectId;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public interface Sortable {
	
	Date getDate();
	
	@JsonIgnore
	ObjectId getObjectId();
	
}
