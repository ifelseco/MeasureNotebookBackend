package com.javaman.model;

import lombok.Data;

import java.util.List;

@Data
public class NotificationSummaryModel {

    private BaseModel baseModel;
    private List<NotificationDetailModel> notificationDetailModelList;


}
