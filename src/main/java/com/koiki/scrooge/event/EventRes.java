package com.koiki.scrooge.event;

import com.koiki.scrooge.scrooge.Scrooge;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class EventRes extends Event {
	private List<Scrooge> scrooges;
	private List<Object> aggPaidAmount = new ArrayList<>();
	private List<TransferAmount> transferAmounts;

	public EventRes() {
		super();
	}

	/**
	 * down cast
	 */
	public EventRes(Event event) {
		super();
		super.setName(event.getName());
		super.setId(event.getId());
		super.setCreatedAt(event.getCreatedAt());
		super.setUpdatedAt(event.getUpdatedAt());
	}
}
