package com.mysite.rest.response;

import java.util.List;

import com.mysite.entity.Category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetCategoriesResponse {

	private List<Category> categories;
	
	public GetCategoriesResponse(List<Category> categories) {
		this.categories = categories;
	}
	
}
