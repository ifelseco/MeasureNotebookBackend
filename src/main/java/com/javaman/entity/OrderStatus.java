package com.javaman.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {

	NONE(0), // Eksik sipariş
	GOTOMEASURE(1), // Ölçüye gidilecek
	ORDERED(2), // Sipariş kaydı alındı
	PROCESSING(3), // Sipariş terzide
	PROCESSED(4), // Terzi işlemi bitti
	DELIVERED(5),// Teslim edildi
	OFFER(6);//Teklif

	private static Map<Object, Object> map = new HashMap<>();

	static {
		for (OrderStatus orderStatus : OrderStatus.values()) {
			map.put(orderStatus.value, orderStatus);
		}
	}

	public static OrderStatus valueOf(int orderStatus) {
		return (OrderStatus) map.get(orderStatus);
	}

	private int value;

	OrderStatus(int value) {
		this.value = value;
	}

	@JsonValue
	public int getValue() {
		return value;
	}

}
