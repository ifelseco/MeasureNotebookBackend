package com.javaman.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderLineSummaryModel{
	
	private BaseModel baseModel;
	
	private OrderDetailModel order;
	
	private List<OrderLineDetailModel> orderLineDetailList;

}
