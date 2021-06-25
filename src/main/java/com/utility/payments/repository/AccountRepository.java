package com.utility.payments.repository;


import com.utility.payments.obr.model.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account,Long> {

    public Account findByName(String name);
    List<Account> findAll();

    @EntityGraph(attributePaths = "payments")
    List<Account> findDistinctBy();

    @Query("select acc from Account acc left join fetch acc.payments")
    List<Account> findWithQuery();

    @Query("select distinct acc from Account acc left join fetch acc.payments")
    List<Account> findDistinctWithQuery();


}
