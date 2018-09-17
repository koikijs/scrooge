package com.koiki.scrooge.scrooge;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Scrooge extends ScroogeReq {
	@Id
	private String id;
	private String eventId;

	//TODO change this to ZonedDateTime if possible
	@CreatedDate
	private LocalDateTime createdAt;
	@LastModifiedDate
	private LocalDateTime updatedAt;

	public Scrooge(ScroogeReq scroogeReq) {
		super();
		super.setMemberName(scroogeReq.getMemberName());
		super.setPaidAmount(scroogeReq.getPaidAmount());
		super.setForWhat(scroogeReq.getForWhat());
	}
}
