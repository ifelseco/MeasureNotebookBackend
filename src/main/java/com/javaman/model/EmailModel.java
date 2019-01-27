package com.javaman.model;

import lombok.Data;

import java.util.Map;

@Data
public class EmailModel {
    private String from;
    private String to;
    private String subject;
    private Map<String, Object> model;

    public EmailModel() {

    }

}
