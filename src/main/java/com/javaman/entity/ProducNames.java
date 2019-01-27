package com.javaman.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProducNames {

	TUL(0), GUNESLIK(1), STOR(2), ZEBRA(3), JALUZI(4), DIKEY(5), KRUVAZETUL(6), BRIZ(7), FARBELA(8), FON(9), TULSTOR(10);
	
	private static Map<Object, Object> map = new HashMap<>();

	static {
		for (ProducNames producNames : ProducNames.values()) {
			map.put(producNames.value, producNames);
		}
	}

	public static ProducNames valueOf(int producNames) {
		return (ProducNames) map.get(producNames);
	}

	private int value;

	ProducNames(int value) {
		this.value = value;
	}

	@JsonValue
	public int getValue() {
		return value;
	}

}
