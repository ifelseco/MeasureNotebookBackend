package com.javaman.service;

import com.javaman.entity.Notification;

import java.util.List;

public interface NotificationService {
    void save(List<Notification> notifications);
    Notification findOne(Long id);
    Notification save(Notification notification);
    void remove(Long id);
}
