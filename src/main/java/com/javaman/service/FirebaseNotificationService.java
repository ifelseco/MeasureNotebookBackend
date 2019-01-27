package com.javaman.service;

import com.javaman.entity.Notification;
import com.javaman.entity.User;

public interface FirebaseNotificationService {

    void sendNotificationToMobile(User user, Notification notification);
    void sendNotificationToWeb(User user, Notification notification);
}
