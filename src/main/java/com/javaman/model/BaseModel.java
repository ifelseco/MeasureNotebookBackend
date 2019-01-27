package com.javaman.model;

import lombok.Data;

@Data
public class BaseModel {

	private int responseCode;
	private String responseMessage;
	private long data;



}