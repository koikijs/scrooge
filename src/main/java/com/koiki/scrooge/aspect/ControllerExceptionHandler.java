package com.koiki.scrooge.aspect;

import com.koiki.scrooge.Error;
import com.koiki.scrooge.ErrorCode;
import com.koiki.scrooge.ErrorRes;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
		List<String> errorCodes = e.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());

		List<Error> errors = errorCodes.stream()
				.map(ErrorCode::findByErrorCode)
				.map(errorCode -> Error.builder()
						.code(errorCode.name())
						.message(errorCode.errorMessage)
						.build())
				.collect(Collectors.toList());

		return ResponseEntity.badRequest()
				.body(ErrorRes.builder()
						.errors(errors)
						.build());
	}
}
