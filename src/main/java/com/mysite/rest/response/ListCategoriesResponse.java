package com.mysite.rest.response;

import java.util.List;

import com.mysite.entity.Category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ListCategoriesResponse {

	private List<Category> categories;
	
	public ListCategoriesResponse(List<Category> categories) {
		this.categories = categories;
	}
	
}
