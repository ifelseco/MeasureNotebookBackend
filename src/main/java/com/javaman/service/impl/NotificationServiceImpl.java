package com.javaman.service.impl;

import com.javaman.entity.Notification;
import com.javaman.repository.NotificationRepository;
import com.javaman.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService{

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void save(List<Notification> notifications) {
        notificationRepository.save(notifications);
    }

    @Override
    public Notification findOne(Long id) {
        return notificationRepository.findOne(id);
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void remove(Long id) {
        notificationRepository.delete(id);
    }
}
