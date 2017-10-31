package com.koiki.scrooge;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Error {
	private String message;
	private String code;
}
