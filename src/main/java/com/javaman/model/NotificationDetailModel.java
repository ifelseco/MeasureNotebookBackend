package com.javaman.model;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationDetailModel {

    private Long id;
    private String title;
    private String message;
    private String data;
    private boolean readNotification;
    private Date createdDate;


}
