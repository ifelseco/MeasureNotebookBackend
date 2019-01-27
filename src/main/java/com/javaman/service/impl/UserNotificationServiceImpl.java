package com.javaman.service.impl;

import com.javaman.entity.Notification;
import com.javaman.entity.Tenant;
import com.javaman.entity.User;
import com.javaman.entity.UserNotification;
import com.javaman.repository.NotificationRepository;
import com.javaman.repository.UserNotificationRepository;
import com.javaman.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserNotificationServiceImpl implements UserNotificationService {


    @Autowired
    UserNotificationRepository userNotificationRepository;

    @Override
    public List<UserNotification> findByTenantAndUser(Tenant tenant, User user) {
        return userNotificationRepository.findByTenantAndUser(tenant,user);
    }

    @Override
    public int findNotificationCountByTenantAndUser(Tenant tenant, User user) {
        return userNotificationRepository.countByTenantAndUser(tenant,user);
    }

    @Override
    public List<UserNotification> findByTenantAndNotification(Tenant tenant, Notification notification) {
        return userNotificationRepository.findByTenantAndNotification(tenant,notification);
    }

    @Override
    public UserNotification findByTenantAndNotificationAndUser(Tenant tenant, Notification notification, User user) {
        return userNotificationRepository.findByTenantAndNotificationAndUser(tenant,notification,user);
    }


    @Override
    public UserNotification findOne(Long id) {
        return userNotificationRepository.findOne(id);
    }

    @Override
    public void remove(Long id) {
        userNotificationRepository.delete(id);
    }


    @Override
    public void remove(List<UserNotification> notifications) {
        userNotificationRepository.delete(notifications);
    }


}
