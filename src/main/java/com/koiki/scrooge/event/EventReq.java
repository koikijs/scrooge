package com.koiki.scrooge.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class EventReq {
	@NotEmpty(message = "ERROR0001")
	@JsonProperty(required = true) // duplicated..
	private String name;
}
