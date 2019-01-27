package com.javaman.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Entity
@Data
public class Customer implements Serializable {

	private static final long serialVersionUID = -5653139674708066307L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id", nullable = false, updatable = false)
	private Long id;

	private String nameSurname;
	private String mobilePhone;
	private String fixedPhone;
	private String address;
	private boolean newsletterAccepted;

	@ManyToOne
	@JoinColumn(name = "tenant_id")
	private Tenant tenant;

	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER ,cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonIgnore
	private List<Order> orders=new ArrayList<>();



}
