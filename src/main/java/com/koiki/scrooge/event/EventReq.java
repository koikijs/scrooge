package com.koiki.scrooge.event;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class EventReq {
	@NotEmpty(message = "ERROR0001")
	private String name;
}
