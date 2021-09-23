package com.mysite.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

	@Id
	@GeneratedValue
	private Integer id;

	@Pattern(regexp = "[0-9a-zA-Z\\- ]+")
	@NotBlank
	@Column(nullable = false, length = 255)
	private String name;

	@OneToMany(mappedBy = "parent")
	@Fetch(FetchMode.SELECT)
	private Collection<Category> subCategories;

	@ManyToOne
	@Fetch(FetchMode.SELECT)
	private Category parent;

}
