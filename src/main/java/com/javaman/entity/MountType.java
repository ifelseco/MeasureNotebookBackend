package com.javaman.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MountType {

	NONE(0), CORNICE(1), RUSTIC(2);
	
	private static Map<Object, Object> map = new HashMap<>();

	static {
		for (MountType mountType : MountType.values()) {
			map.put(mountType.value, mountType);
		}
	}

	public static MountType valueOf(int mountType) {
		return (MountType) map.get(mountType);
	}

	private int value;

	MountType(int value) {
		this.value = value;
	}

	@JsonValue
	public int getValue() {
		return value;
	}
}
