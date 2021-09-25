package com.mysite.rest.exception;

import javax.validation.ValidationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		log.error(ex.toString());
		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
		}
		final ApiError apiError = new ApiError("api.error", ex.toString());
		return new ResponseEntity<>(apiError, headers, status);
	}
	
	@ExceptionHandler({ ValidationException.class })
	public ResponseEntity<Object> handleValidationExceptions(ValidationException ex, WebRequest request) {
		log.error(ex.toString());
		final ApiError apiError = new ApiError("validation.error", ex.toString());
		return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleValidationExceptions(Exception ex, WebRequest request) {
		log.error(ex.toString());
		final ApiError apiError = new ApiError("api.error", ex.toString());
		return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
