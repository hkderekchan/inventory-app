package com.mysite.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mysite.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	@Query(value = "select a from Inventory a join a.subCategory sc where (0=:category) or (sc.id=:category)")
	Page<Inventory> findByCategory(@Param("category") int category,
			Pageable pageable);

}