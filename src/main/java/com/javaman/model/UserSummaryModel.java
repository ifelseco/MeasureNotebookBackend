package com.javaman.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserSummaryModel {
    private BaseModel baseModel;
    private List<UserDetailModel> users=new ArrayList<>();

}
