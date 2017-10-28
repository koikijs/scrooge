package com.koiki.scrooge.scrooge;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Scrooge {
	@Id
	private String id;
	private String eventId;
	private String memberName;
	private BigDecimal paidAmount;
	private String forWhat;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
}
