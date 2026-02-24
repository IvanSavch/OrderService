package com.innowise.orderservice.repository;

import com.innowise.orderservice.model.entity.Order;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Query("SELECT o FROM Order o WHERE o.userId = ?1 AND o.deleted = false")
    List<Order> findByUserId(Long userId);

    @Query("select o from Order o where o.id= ?1 and o.deleted = false ")
    @NotNull
    Optional<Order> findById(Long id);
    @Query(nativeQuery = true,value = "DELETE FROM orders o WHERE o.created_at < NOW() - INTERVAL '1' YEAR AND o.deleted = true;")
    void deletedRemarkedOrder();

}
