package com.mysite.rest.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateInventoryRequest {

	@Pattern(regexp = "[0-9a-zA-Z\\- ]+")
	@NotBlank
	private String name;

	@PositiveOrZero
	@NotNull
	private Integer categoryId;

	@PositiveOrZero
	@NotNull
	private Integer subCategoryId;

	@PositiveOrZero
	@NotNull
	private Integer quantity;
	
}
