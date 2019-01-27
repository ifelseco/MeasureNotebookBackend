package com.javaman.model;

import lombok.Data;

import java.util.List;

@Data
public class DeleteOrdersModel {
	
	private List<Long> orderIds;

	}
