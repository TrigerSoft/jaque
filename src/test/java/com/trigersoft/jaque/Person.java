package com.trigersoft.jaque;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "name")
public class Person {
	private String name;
	private int age;
	private Integer height = 1;

	public boolean isAdult() {
		return age >= 18;
	}
}
