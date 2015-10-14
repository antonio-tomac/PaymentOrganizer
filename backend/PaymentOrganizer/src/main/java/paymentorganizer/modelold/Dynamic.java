package paymentorganizer.modelold;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public interface Dynamic extends Serializable {

	void apply();

	void delete();

	double applyDiff(User user);

	Date getDate();
}
