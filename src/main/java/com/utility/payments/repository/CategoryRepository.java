package com.utility.payments.repository;

import com.utility.payments.obr.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CategoryRepository extends JpaRepository<Category, Long> {


    public Category findByName(String name);

    public List<Category> findAll();

    @EntityGraph(attributePaths = "subCategories")
    List<Category> findDistinctBy();

    @Query("select c from Category c left join fetch c.subCategories")
    List<Category> findWithQuery();

    @Query("select distinct c from Category c left join fetch c.subCategories")
    List<Category> findDistinctWithQuery();



}
