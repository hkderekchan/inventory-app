package com.mysite.rest.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCategoryResponse {

	private int id;
	
	public CreateCategoryResponse(int id) {
		this.id = id;
	}
	
}
