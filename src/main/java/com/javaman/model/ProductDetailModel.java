package com.javaman.model;

import com.javaman.entity.ProducNames;
import lombok.Data;

@Data
public class ProductDetailModel {

	private ProducNames productValue;

	private String variantCode;

	private String aliasName;

	private String patternCode;


}
