package com.koiki.scrooge.aspect;

import com.koiki.scrooge.aspect.Error;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorRes {
	private List<Error> errors;
}
