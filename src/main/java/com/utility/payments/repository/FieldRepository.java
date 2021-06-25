package com.utility.payments.repository;

import com.utility.payments.obr.model.Field;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldRepository extends JpaRepository<Field,Integer> {
    public Field findByName(String name);
}
