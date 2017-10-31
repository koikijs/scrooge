package com.koiki.scrooge;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorRes {
	private List<Error> errors;
}
