package com.mysite.rest.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateInventoryRequest {

	private Long id;

	@PositiveOrZero
	@NotNull
	private Integer quantity;
	
}
