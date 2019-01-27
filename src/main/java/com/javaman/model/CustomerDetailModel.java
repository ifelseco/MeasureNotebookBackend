package com.javaman.model;

import lombok.Data;

@Data
public class CustomerDetailModel {

	private Long id;
	private String nameSurname;
	private String mobilePhone;
	private String fixedPhone;
	private String address;
	private boolean newsletterAccepted;

}
