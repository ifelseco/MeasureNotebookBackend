package com.javaman.repository;

import com.javaman.entity.Notification;
import com.javaman.entity.Tenant;
import com.javaman.entity.User;
import com.javaman.entity.UserNotification;
import com.javaman.security.UserRole;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends CrudRepository<UserNotification, Long> {
	List<UserNotification> findByTenantAndUser(Tenant tenant, User user);
    List<UserNotification> findByTenantAndNotification(Tenant tenant,Notification notification);
	UserNotification findByTenantAndNotificationAndUser(Tenant tenant, Notification notification, User user);
	int countByTenantAndUser(Tenant tenant,User user);
}