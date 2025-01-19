package com.mlprograms.chess.human;

import com.mlprograms.chess.game.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Human extends Player {

	private Id id;
	private Name name;
	private Language language;
	private Elo elo;
	private Birthday birthday;
	private Contact contact;
	private String password;
	private PasswordRecovery passwordRecovery;
	private String timeZone;
	private boolean isWhite;

	public Human(Id id, Name name, Language language, Elo elo, Birthday birthday, Contact contact, String password, PasswordRecovery passwordRecovery, String timezone) {
		super();
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
