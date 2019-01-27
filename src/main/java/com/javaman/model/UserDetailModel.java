package com.javaman.model;

import lombok.Data;

@Data
public class UserDetailModel {

    private BaseModel baseModel;
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private String nameSurname;
    private boolean enabled=true;

}
