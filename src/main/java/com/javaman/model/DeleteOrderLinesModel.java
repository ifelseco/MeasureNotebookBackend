package com.javaman.model;

import lombok.Data;

import java.util.List;

@Data
public class DeleteOrderLinesModel {
	
	private List<Long> orderLineIds;
}
