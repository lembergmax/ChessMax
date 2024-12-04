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
public class Contact {

	private String email;
	private String secondEmail;
	private String phoneNumber;

	public Contact(String email, String secondEmail, String phoneNumber) {
		this.email = email;
		this.secondEmail = secondEmail;
		this.phoneNumber = phoneNumber;
	}

}
