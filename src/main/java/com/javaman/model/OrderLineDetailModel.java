package com.javaman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.javaman.entity.Direction;
import com.javaman.entity.FonType;
import com.javaman.entity.MechanismStatus;
import com.javaman.entity.MountType;

public class OrderLineDetailModel {

	private Long id;
	private ProductDetailModel product;
	@JsonIgnore
	private OrderDetailModel order;
	private String lineDescription;
	private double propertyWidth;
	private double propertyHeight;
	private double propertyAlternativeWidth;
	private double propertyAlternativeHeight;
	private double sizeOfPile;
	private double unitPrice;
	private double lineAmount;
	private double propertyLeftWidth;
	private double propertyRightWidth;
	private String skirtNo;
	private String beadNo;
	private MountType mountType;
	private String pileName;
	private int piecesCount;
	private double usedMaterial;
	private String locationType;
	private Direction direction;
	private String locationName;
	private MechanismStatus mechanismStatus;
	private FonType fonType;
	private String propertyModelName;
	
	@JsonIgnore
	public OrderDetailModel getOrder() {
		return order;
	}
	
	@JsonProperty
	public void setOrder(OrderDetailModel order) {
		this.order = order;
	}

	public String getBeadNo() {
		return beadNo;
	}

	public Direction getDirection() {
		return direction;
	}

	public FonType getFonType() {
		return fonType;
	}

	public Long getId() {
		return id;
	}

	public double getLineAmount() {
		return lineAmount;
	}

	public String getLineDescription() {
		return lineDescription;
	}


	public String getLocationType() {
		return locationType;
	}

	public MechanismStatus getMechanismStatus() {
		return mechanismStatus;
	}

	public MountType getMountType() {
		return mountType;
	}


	public int getPiecesCount() {
		return piecesCount;
	}

	public String getPileName() {
		return pileName;
	}

	public ProductDetailModel getProduct() {
		return product;
	}

	public double getPropertyAlternativeHeight() {
		return propertyAlternativeHeight;
	}

	public double getPropertyAlternativeWidth() {
		return propertyAlternativeWidth;
	}

	public double getPropertyHeight() {
		return propertyHeight;
	}

	public double getPropertyLeftWidth() {
		return propertyLeftWidth;
	}

	public String getPropertyModelName() {
		return propertyModelName;
	}

	public double getPropertyRightWidth() {
		return propertyRightWidth;
	}

	public double getPropertyWidth() {
		return propertyWidth;
	}

	public double getSizeOfPile() {
		return sizeOfPile;
	}

	public String getSkirtNo() {
		return skirtNo;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public double getUsedMaterial() {
		return usedMaterial;
	}

	public void setBeadNo(String beadNo) {
		this.beadNo = beadNo;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setFonType(FonType fonType) {
		this.fonType = fonType;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLineAmount(double lineAmount) {
		this.lineAmount = lineAmount;
	}

	public void setLineDescription(String lineDescription) {
		this.lineDescription = lineDescription;
	}


	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public void setMechanismStatus(MechanismStatus mechanismStatus) {
		this.mechanismStatus = mechanismStatus;
	}

	public void setMountType(MountType mountType) {
		this.mountType = mountType;
	}


	public void setPiecesCount(int piecesCount) {
		this.piecesCount = piecesCount;
	}

	public void setPileName(String pileName) {
		this.pileName = pileName;
	}

	public void setProduct(ProductDetailModel product) {
		this.product = product;
	}

	public void setPropertyAlternativeHeight(double propertyAlternativeHeight) {
		this.propertyAlternativeHeight = propertyAlternativeHeight;
	}

	public void setPropertyAlternativeWidth(double propertyAlternativeWidth) {
		this.propertyAlternativeWidth = propertyAlternativeWidth;
	}

	public void setPropertyHeight(double propertyHeight) {
		this.propertyHeight = propertyHeight;
	}

	public void setPropertyLeftWidth(double propertyLeftWidth) {
		this.propertyLeftWidth = propertyLeftWidth;
	}

	public void setPropertyModelName(String propertyModelName) {
		this.propertyModelName = propertyModelName;
	}

	public void setPropertyRightWidth(double propertyRightWidth) {
		this.propertyRightWidth = propertyRightWidth;
	}

	public void setPropertyWidth(double propertyWidth) {
		this.propertyWidth = propertyWidth;
	}

	public void setSizeOfPile(double sizeOfPile) {
		this.sizeOfPile = sizeOfPile;
	}

	public void setSkirtNo(String skirtNo) {
		this.skirtNo = skirtNo;
	}



	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public void setUsedMaterial(double usedMaterial) {
		this.usedMaterial = usedMaterial;
	}

}
