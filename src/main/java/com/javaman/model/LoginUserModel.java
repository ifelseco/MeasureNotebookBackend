package com.javaman.model;

import lombok.Data;

@Data
public class LoginUserModel {

    private BaseModel baseModel;
    private UserDetailModel userDetailModel;
    //sessionId
    private String token;

}
