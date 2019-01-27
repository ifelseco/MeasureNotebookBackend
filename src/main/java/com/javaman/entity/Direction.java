package com.javaman.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Direction {

	NONE(0), LEFT(1), RIGHT(2);
	
	private static Map<Object, Object> map = new HashMap<>();

	static {
		for (Direction direction : Direction.values()) {
			map.put(direction.value, direction);
		}
	}

	public static Direction valueOf(int direction) {
		return (Direction) map.get(direction);
	}

	private int value;

	Direction(int value) {
		this.value = value;
	}

	@JsonValue
	public int getValue() {
		return value;
	}

}
