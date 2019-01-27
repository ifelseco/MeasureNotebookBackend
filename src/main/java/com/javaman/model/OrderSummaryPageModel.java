package com.javaman.model;


import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class OrderSummaryPageModel {
	
	private BaseModel baseModel;
	
	private Page<OrderDetailModel> orderDetailPage;

}
