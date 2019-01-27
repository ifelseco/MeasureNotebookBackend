package com.javaman.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderSummaryModel {
	
	private BaseModel baseModel;
	private List<OrderDetailModel> orders=new ArrayList<>();

}
