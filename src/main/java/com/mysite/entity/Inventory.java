package com.mysite.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Immutable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Immutable
public class Inventory {

	@Id
	@GeneratedValue
	private Long id;

	@Pattern(regexp = "[0-9a-zA-Z\\- ]+")
	@NotBlank
	@Column(nullable = false, length = 255)
	private String name;

	@Transient
	private Category category;

	@JoinColumn(name = "belong_to", nullable = false)
	@ManyToOne(optional = false)
	@Fetch(FetchMode.SELECT)
	private Category subCategory;

	@PositiveOrZero
	@NotNull
	private Integer quantity;

}
