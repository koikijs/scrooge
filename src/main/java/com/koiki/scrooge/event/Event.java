package com.koiki.scrooge.event;

import java.time.ZonedDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Event {
	@Id
	private String id;
	private String name;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
}
