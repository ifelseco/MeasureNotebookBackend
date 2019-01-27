package com.javaman.entity;

import java.io.Serializable;
import java.math.BigDecimal;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Entity
@Data
public class OrderLine implements Serializable {

	private static final long serialVersionUID = 5116619543350624484L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id", nullable = false, updatable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "orderId")
	@JsonIgnore	
	private Order order;

	@OneToOne(cascade = CascadeType.ALL )
	private Product product;

	@ManyToOne
	@JoinColumn(name = "tenant_id")
	private Tenant tenant;

	@Column(columnDefinition = "text")
	private String lineDescription;

	private double propertyWidth;
	private double propertyHeight;
	private double propertyAlternativeWidth;
	private double propertyAlternativeHeight;
	private double sizeOfPile;
	private double unitPrice;
	private BigDecimal lineAmount;
	private double propertyLeftWidth;
	private double propertyRightWidth;
	private String skirtNo;
	private String beadNo;

	@Enumerated(EnumType.STRING)
	@Column(name = "mount_type")
	private MountType mountType;

	private String pileName;
	private int piecesCount;
	private BigDecimal usedMaterial;
	private String locationType;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "direction")
	private Direction direction;
	

	private String locationName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "mechanism_status")
	private MechanismStatus mechanismStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "fon_type")
	private FonType fonType;

	private String propertyModelName;


	
}
