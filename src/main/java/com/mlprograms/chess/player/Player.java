package com.mlprograms.chess.player;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class Player {

	private Id id;
	private Name name;
	private Language language;
	private Elo elo;
	private Birthday birthday;
	private Contact contact;
	private String password;
	private PasswordRecovery passwordRecovery;
	private String timeZone;

	public Player(Id id, Name name, Language language, Elo elo, Birthday birthday, Contact contact, String password, PasswordRecovery passwordRecovery, String timezone) {
		this.id = id;
		this.name = name;
		this.language = language;
		this.elo = elo;
		this.birthday = birthday;
		this.contact = contact;
		this.password = password;
		this.passwordRecovery = passwordRecovery;
		this.timeZone = timezone;
	}
}
