package com.javaman.model;

import java.util.Date;


import com.javaman.entity.OrderStatus;
import lombok.Data;

@Data
public class OrderDetailModel {

	private Long id;
	private String userNameSurname;
	private Date orderDate;
	private double totalAmount;
	private double depositeAmount;
	private Date deliveryDate;
	private Date measureDate;
	private OrderStatus orderStatus;
	private CustomerDetailModel customer;
	private boolean mountExist;
	private BaseModel baseModel;
	private String orderNumber;
	private int tailorOrderLineCount;



}
