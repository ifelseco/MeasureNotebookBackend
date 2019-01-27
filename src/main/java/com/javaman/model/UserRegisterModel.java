package com.javaman.model;

import lombok.Data;

@Data
public class UserRegisterModel {

    private String username;
    private String password;
    private String email;
    private String phone;
    private String nameSurname;
    private int role;
    private CompanyDetailModel companyDetailModel;
}
