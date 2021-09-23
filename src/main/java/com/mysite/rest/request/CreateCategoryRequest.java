package com.mysite.rest.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCategoryRequest {

	@Pattern(regexp = "[0-9a-zA-Z\\- ]+")
	@NotBlank
	private String name;

	@PositiveOrZero
	private Integer parent;

}
