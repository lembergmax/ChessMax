/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.player;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Birthday {

	private int day;
	private int month;
	private int year;

	public Birthday(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}

	public boolean isLeapYear() {
		return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
	}

	public boolean isValid() {
		if (day < 1 || month < 1 || month > 12 || year < 1) {
			return false;
		}

		if (month == 2) {
			if (isLeapYear()) {
				return day <= 29;
			} else {
				return day <= 28;
			}
		}

		if (month == 4 || month == 6 || month == 9 || month == 11) {
			return day <= 30;
		}

		return day <= 31;
	}

	@Override
	public String toString() {
		return day + "." + month + "." + year;
	}
}
