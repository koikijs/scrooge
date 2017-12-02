package com.koiki.scrooge.event;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferAmount {
	private String from;
	private String to;
	private BigDecimal amount;
}
