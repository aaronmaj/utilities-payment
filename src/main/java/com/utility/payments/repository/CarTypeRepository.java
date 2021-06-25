package com.utility.payments.repository;


import com.utility.payments.model.CarType;
import org.springframework.data.jpa.repository.JpaRepository;

interface CarTypeRepository extends JpaRepository<CarType,Long> {

}
