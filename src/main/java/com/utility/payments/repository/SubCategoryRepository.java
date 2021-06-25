package com.utility.payments.repository;

import com.utility.payments.obr.model.SubCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {

    public SubCategory findByName(String name);

    public List<SubCategory> findAll();

    @EntityGraph(attributePaths = "accounts")
    List<SubCategory> findDistinctBy();

    @Query("select sub from SubCategory sub left join fetch sub.accounts")
    List<SubCategory> findWithQuery();

    @Query("select distinct sc from SubCategory sc left join fetch sc.accounts")
    List<SubCategory> findDistinctWithQuery();


}
