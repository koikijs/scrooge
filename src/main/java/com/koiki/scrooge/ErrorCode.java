package com.koiki.scrooge;

import java.util.Arrays;

public enum ErrorCode {
	ERROR0001("event name should not be empty"),
	UNKNOWN("unknown error happens")
	;

	public final String errorMessage;

	private ErrorCode(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public static ErrorCode findByErrorCode(String errorCode) {
		return Arrays.stream(values())
				.filter(value -> value.name().equals(errorCode))
				.findFirst()
				.orElse(UNKNOWN);
	}
}
