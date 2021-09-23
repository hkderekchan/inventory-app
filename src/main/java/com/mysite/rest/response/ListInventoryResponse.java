package com.mysite.rest.response;

import java.util.List;

import com.mysite.entity.Inventory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ListInventoryResponse {

	private List<Inventory> inventory;
	
	public ListInventoryResponse(List<Inventory> inventory) {
		this.inventory = inventory;
	}
	
}
