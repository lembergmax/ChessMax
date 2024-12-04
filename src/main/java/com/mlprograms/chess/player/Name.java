/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.player;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class Name {

	private String username;
	private String firstName;
	private String lastName;

	public Name(String username, String firstName, String lastName) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
	}

}
