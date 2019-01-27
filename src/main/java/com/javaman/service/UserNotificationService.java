package com.javaman.service;

import com.javaman.entity.*;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface UserNotificationService {


	List<UserNotification> findByTenantAndUser(Tenant tenant,User user);
	int findNotificationCountByTenantAndUser(Tenant tenant,User user);
	List<UserNotification> findByTenantAndNotification(Tenant tenant,Notification notification);
	UserNotification findByTenantAndNotificationAndUser(Tenant tenant, Notification notification, User user);
	UserNotification findOne(Long id);
	void remove(Long id);
	void remove(List<UserNotification> userNotifications);


}
