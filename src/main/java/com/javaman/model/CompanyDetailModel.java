package com.javaman.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyDetailModel {

    private Long id;
    private String tenantName;
    private String phone;
    private String address;
    private String email;
    private String tenantCode;
    private int tenantUserCount;
    private int maxUserCount;
    private boolean enabled=true;
    private List<UserDetailModel> users=new ArrayList<>();
}
