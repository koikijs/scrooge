package com.koiki.scrooge.event;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
public class Event extends EventReq {
	@Id
	private String id;

	//TODO change this to ZonedDateTime if possible
	@CreatedDate
	private LocalDateTime createdAt;
	@LastModifiedDate
	private LocalDateTime updatedAt;

	/**
	 * down cast
	 */
	public Event(EventReq eventReq) {
		super();
		super.setName(eventReq.getName());
	}
}
