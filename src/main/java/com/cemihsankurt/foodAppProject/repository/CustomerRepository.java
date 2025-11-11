package com.cemihsankurt.foodAppProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.cemihsankurt.foodAppProject.entity.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Optional<Customer> findByFirstName(String email);

    Optional<Customer> findByLastName(String lastName);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByUserId(Long userId);

    Optional<Customer> findByUserEmail(String email);

    @Query("SELECT c.id FROM Customer c JOIN c.user u WHERE u.email = :email")
    Long findCustomerIdByUserEmail(String email);
}
