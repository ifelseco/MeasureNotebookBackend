package com.javaman.model;

import lombok.Data;

@Data
public class CalculationResultModel {
	
	private BaseModel baseModel;
	private double totalAmount;
	private double usedMaterial;

}
