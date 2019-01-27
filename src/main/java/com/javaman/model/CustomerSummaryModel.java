package com.javaman.model;

import lombok.Data;

import java.util.List;

@Data
public class CustomerSummaryModel {
	
	private BaseModel baseModel;
	private List<CustomerDetailModel> customers;

}
