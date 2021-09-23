package com.mysite.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Pattern(regexp = "[0-9a-zA-Z\\- ]+")
	@NotBlank
	@Column(nullable = false, length = 255)
	private String name;

	@JsonIgnore
	@JoinColumn(name = "belong_to", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_category"))
	@ManyToOne(optional = false)
	@Fetch(FetchMode.SELECT)
	private Category subCategory;

	@PositiveOrZero
	@NotNull
	private Integer quantity;

	public Integer getCategoryId() {
		Category parent = this.getSubCategory().getParent();
		if(parent != null) {
			return parent.getId();
		}
		return null;
	}
	
	public Integer getSubCategoryId() {
		return this.getSubCategory().getId();
	}
	
}
