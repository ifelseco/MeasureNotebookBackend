package com.javaman.model;

import lombok.Data;

import java.util.List;

@Data
public class AddOrderLineDetailListModel {
	
	private List<OrderLineDetailModel> orderLineDetailModelList;
}
