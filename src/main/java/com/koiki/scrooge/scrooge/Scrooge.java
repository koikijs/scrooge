package com.koiki.scrooge.scrooge;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Scrooge {
	@Id
	private String id;
	private String eventId;
	private String memberName;
	private BigDecimal paidAmount;
	private String forWhat;

	//TODO change this to ZonedDateTime if possible
	@CreatedDate
	private LocalDateTime createdAt;
	@LastModifiedDate
	private LocalDateTime updatedAt;
}
