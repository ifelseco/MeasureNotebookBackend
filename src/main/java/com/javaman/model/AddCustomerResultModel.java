package com.javaman.model;

import lombok.Data;

import java.util.Date;

@Data
public class AddCustomerResultModel {
	
	private BaseModel baseModel;
	private Long id;
	private String orderNumber;
	private Date orderDate;
	private Long customerId;
	private String customerNameSurname;



}
