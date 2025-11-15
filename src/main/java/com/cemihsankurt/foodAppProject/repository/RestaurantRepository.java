package com.cemihsankurt.foodAppProject.repository;

import com.cemihsankurt.foodAppProject.entity.ApprovalStatus;
import com.cemihsankurt.foodAppProject.entity.Restaurant;
import com.cemihsankurt.foodAppProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByName(String name);

    Optional<Restaurant> findByPhoneNumber(String phoneNumber);

    Optional<Restaurant> findByUserId(Long userId);

    List<Restaurant> findByApprovalStatus(ApprovalStatus approvalStatus);

    List<Restaurant> findByApprovalStatusAndIsAvailable(ApprovalStatus status, boolean isAvailable);

    @Query("SELECT c.id FROM Restaurant c JOIN c.user u WHERE u.email = :email")
    Long findRestaurantIdByUserEmail(String email);




}
