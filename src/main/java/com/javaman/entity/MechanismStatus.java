package com.javaman.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MechanismStatus {

	NONE(0), NORMAL(1), PIECES(2), MULTIPLE_MECHANISM(3);
	
	private static Map<Object, Object> map = new HashMap<>();

	static {
		for (MechanismStatus mechanismStatus : MechanismStatus.values()) {
			map.put(mechanismStatus.value, mechanismStatus);
		}
	}

	public static MechanismStatus valueOf(int mechanismStatus) {
		return (MechanismStatus) map.get(mechanismStatus);
	}

	private int value;

	MechanismStatus(int value) {
		this.value = value;
	}

	@JsonValue
	public int getValue() {
		return value;
	}

}
