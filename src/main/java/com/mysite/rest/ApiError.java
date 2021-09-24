package com.mysite.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {

	// f/e has flexibility to map to its message bundle by using this key
	private String messageKey;
	
	private String message;
}
