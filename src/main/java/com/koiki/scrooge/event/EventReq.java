package com.koiki.scrooge.event;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class EventReq {
	@NotEmpty(message = "ERROR0001")
	//@Min(1)
	//@Length(min = 1, max = 5)
	private String name;
}
