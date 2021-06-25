package com.utility.payments.repository;


import com.utility.payments.obr.model.Detail;
import com.utility.payments.obr.model.DetailKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailRepository extends JpaRepository<Detail, DetailKey> {

    public List<Detail> findByPaymentId(Long id);
}
