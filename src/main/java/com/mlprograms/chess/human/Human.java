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

	public Human() {
		this(
			new Id("db123", "user123"),
			new Name("username", "Vorname", "Nachname"),
			new Language("Deutsch", "DE"),
			new Elo(1500, 1600),
			new Birthday(1, 1, 2000),
			new Contact("email@example.com", "second@example.com", "1234567890"),
			"123456",
			new PasswordRecovery("Lieblingsfarbe?", "Blau"),
			"Deutschland/Berlin"
		);
	}

}
