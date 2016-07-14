package paymentorganizer.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Data
public class PaymentEvent implements Comparable<PaymentEvent> {
	
	private final Sortable event;
	private final Date date;
	private final String type;

	public PaymentEvent(Sortable event, Date date) {
		this.event = event;
		this.date = date;
		type = event.getClass().getSimpleName();
	}

	@Override
	public int compareTo(PaymentEvent o) {
		int compareTo = date.compareTo(o.getDate());
		if (compareTo == 0) {
			int timestamp = event.getObjectId().getTimestamp();
			int otherTimestamp = o.getEvent().getObjectId().getTimestamp();
			return Integer.compare(timestamp, otherTimestamp);
		}
		return compareTo;
	}
	
}
