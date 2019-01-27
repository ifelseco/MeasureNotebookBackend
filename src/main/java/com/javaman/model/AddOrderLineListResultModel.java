package com.javaman.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AddOrderLineListResultModel {
	
	private BaseModel baseModel;
	
	private double orderTotalAmount;
	
	private ArrayList<OrderLineDetailModel> orderLines=new ArrayList<>();

}
