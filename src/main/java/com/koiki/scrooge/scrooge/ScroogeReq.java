package com.koiki.scrooge.scrooge;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class ScroogeReq {
	@NotNull(message = "ERROR0002")
	private String memberName;
	@NotNull(message = "ERROR0003")
	private BigDecimal paidAmount;
	@NotNull(message = "ERROR0004")
	private String forWhat;
}
