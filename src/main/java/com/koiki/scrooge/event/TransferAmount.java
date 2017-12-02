package com.koiki.scrooge.event;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransferAmount {
	private String from;
	private String to;
	private BigDecimal amount;
}
