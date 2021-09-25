package com.mysite.rest.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import com.mysite.Constants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateInventoryRequest {

	@Pattern(regexp = Constants.NAME_PATTERN)
	@NotBlank
	private String name;
	
	@PositiveOrZero
	@NotNull
	private Integer quantity;

	@PositiveOrZero
	@NotNull
	private Integer categoryId;

	@PositiveOrZero
	@NotNull
	private Integer subCategoryId;
	
}
