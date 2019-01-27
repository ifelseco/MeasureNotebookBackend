package com.javaman.model;

import lombok.Data;

@Data
public class AddOrderLineResultModel {
	
	private BaseModel baseModel;
	private Long id;
	private String orderOrderNumber;
	private double lineAmount;
	private double orderTotalAmount;
	private double orderDepositeAmount;

}
