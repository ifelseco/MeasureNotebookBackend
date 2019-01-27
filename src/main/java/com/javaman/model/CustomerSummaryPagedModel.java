package com.javaman.model;


import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class CustomerSummaryPagedModel {
	
	private BaseModel baseModel;
	
	private Page<CustomerDetailModel> customerDetailPage;

}
