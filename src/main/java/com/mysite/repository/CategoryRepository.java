package com.mysite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mysite.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}