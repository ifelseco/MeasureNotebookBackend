package com.javaman.entity;

import lombok.Data;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Data
public class Product implements Serializable {

	private static final long serialVersionUID = 523154929215774852L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id", nullable = false, updatable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "product_name")
	private ProducNames productValue;

	private String variantCode;

	private String aliasName;

	private String patternCode;

	@ManyToOne
	@JoinColumn(name = "tenant_id")
	private Tenant tenant;



}
