package com.mysite.rest.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInventoryResponse {

	private final long id;

	public CreateInventoryResponse(long id) {
		this.id = id;
	}
	
}
