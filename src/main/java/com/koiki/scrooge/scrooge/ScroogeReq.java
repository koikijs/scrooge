package com.koiki.scrooge.scrooge;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class ScroogeReq {
	private String memberName;
	private BigDecimal paidAmount;
	private String forWhat;
}
