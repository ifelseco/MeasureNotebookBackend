package com.javaman.entity;

import lombok.Data;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@Data
public class Order implements Serializable {

	private static final long serialVersionUID = -5285919080457262037L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id", nullable = false, updatable = false)
	private Long id;

	@ManyToOne(cascade = {CascadeType.PERSIST})
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@OneToMany(mappedBy="order" , cascade=CascadeType.REMOVE , fetch=FetchType.LAZY ,orphanRemoval = true)
	private List<OrderLine> orderLines=new ArrayList<>();


	private Date orderDate;

	private BigDecimal totalAmount;

	private BigDecimal depositeAmount;

	private Date deliveryDate;

	private Date measureDate;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	private boolean mountExist;
	private String orderNumber;

	private int tailorOrderLineCount;
	
	@ManyToOne
	@JoinColumn(name = "tenant_id")
	private Tenant tenant;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;


}
