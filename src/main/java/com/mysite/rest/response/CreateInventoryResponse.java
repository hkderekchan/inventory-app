package com.mysite.rest.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateInventoryResponse {

	private long id;

	public CreateInventoryResponse(long id) {
		this.id = id;
	}
	
}
