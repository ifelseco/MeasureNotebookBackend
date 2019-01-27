package com.javaman.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.javaman.entity.Order;
import com.javaman.entity.OrderLine;

@Repository
public interface OrderLineRepository extends CrudRepository<OrderLine, Long> {
	List<OrderLine> findByOrder(Order order);
}
