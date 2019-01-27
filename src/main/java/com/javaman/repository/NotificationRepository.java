package com.javaman.repository;

import com.javaman.entity.Notification;
import com.javaman.entity.Order;
import com.javaman.entity.Product;
import com.javaman.entity.Tenant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {

}
