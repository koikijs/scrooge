package com.koiki.scrooge;

import java.util.Arrays;

public enum ErrorCode {
	ERROR0001("event name should not be empty"),
	ERROR0002("memberName should not be null"),
	ERROR0003("paidAmount should not be null"),
	ERROR0004("forWhat should not be null"),
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
