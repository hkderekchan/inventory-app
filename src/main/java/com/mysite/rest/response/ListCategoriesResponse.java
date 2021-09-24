package com.mysite.rest.response;

import java.util.List;

import com.mysite.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ListCategoriesResponse {

	private List<Category> categories;

}
