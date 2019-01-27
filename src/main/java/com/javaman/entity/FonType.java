package com.javaman.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FonType {

	NONE(0), KK(1), FK(2), JP(3);
	
	private static Map<Object, Object> map = new HashMap<>();

	static {
		for (FonType fonType : FonType.values()) {
			map.put(fonType.value, fonType);
		}
	}

	public static FonType valueOf(int fonType) {
		return (FonType) map.get(fonType);
	}

	private int value;

	FonType(int value) {
		this.value = value;
	}

	@JsonValue
	public int getValue() {
		return value;
	}

}
