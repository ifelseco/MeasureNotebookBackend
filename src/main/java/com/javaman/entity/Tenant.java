package com.javaman.entity;

import lombok.Data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Tenant implements Serializable {

	
	private static final long serialVersionUID = -6562195283785095675L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id", nullable = false, updatable = false)
	private Long id;
	

	private String tenantName;
	private String phone;
	private String email;
	private String address;
	private String tenantCode;
	private int tenantUserCount;
	private int maxUserCount;
	private boolean enabled=true;

}
