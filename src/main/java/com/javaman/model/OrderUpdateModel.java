package com.javaman.model;

import java.util.Date;

import com.javaman.entity.OrderStatus;
import lombok.Data;

@Data
public class OrderUpdateModel{

	private Long id;
	private String userNameSurname;
	private Date orderDate;
	private double totalAmount;
	private double depositeAmount;
	private Date deliveryDate;
	private Date measureDate;
	private boolean mountExist;
	private OrderStatus orderStatus;
	private String orderNumber;
	private int tailorOrderLineCount;



}
