package com.javaman.model;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

@Data
public class PasswordForgatModel {

    private String email;
}
