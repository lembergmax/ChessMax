/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.account;

import com.mlprograms.chess.account.ui.Login;
import com.mlprograms.chess.player.*;
import com.mlprograms.chess.server.database.UserDatabaseManager;
import com.mlprograms.chess.utils.ConfigReader;
import com.mlprograms.chess.utils.EncryptionUtils;
import com.mlprograms.chess.utils.Logger;
import com.mlprograms.chess.utils.ui.ErrorMessage;
import com.mlprograms.chess.utils.ui.InformationMessage;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

	private static final ArrayList<String> ACCOUNT_INFO = new ArrayList<>();
	private static final UserDatabaseManager USER_DATABASE_MANAGER = new UserDatabaseManager();

	private static final ConfigReader configReader = new ConfigReader();
	private static final String SECRET_KEY = configReader.getValue("Security", "SECRET_KEY");

	private static final String TEXT_SECTION = "Text";
	private static final String INFORMATION_MESSAGE_TITLE = configReader.getValue(TEXT_SECTION, "INFORMATION_MESSAGE_TITLE");

	private static final String ERROR_MESSAGE_TITLE = configReader.getValue(TEXT_SECTION, "ERROR_MESSAGE_TITLE");
	private static final String ERROR_MESSAGE_FILL_ALL_FIELDS = configReader.getValue(TEXT_SECTION, "ERROR_MESSAGE_FILL_ALL_FIELDS");
	private static final String ERROR_MESSAGE_PASSWORDS_NOT_EQUAL = configReader.getValue(TEXT_SECTION, "ERROR_MESSAGE_PASSWORDS_NOT_EQUAL");
	private static final String ERROR_MESSAGE_USERNAME_OR_EMAIL_ALREADY_EXISTS = configReader.getValue(TEXT_SECTION, "ERROR_MESSAGE_USERNAME_OR_EMAIL_ALREADY_EXISTS");
	private static final String ERROR_MESSAGE_CANNOT_ADD_USER_TO_DB = configReader.getValue(TEXT_SECTION, "ERROR_MESSAGE_CANNOT_ADD_USER_TO_DB");
	private static final String ERROR_MESSAGE_WRONG_USERNAME_OR_PASSWORD = configReader.getValue(TEXT_SECTION, "ERROR_MESSAGE_WRONG_USERNAME_OR_PASSWORD");

	/**
	 * Creates a new account based on the input fields in the given window.
	 *
	 * @param window
	 * 	the parent window containing user input fields
	 */
	public static void createAccount(Window window) {
		ACCOUNT_INFO.clear();

		if (areFieldsEmpty(window)) {
			showErrorMessage(ERROR_MESSAGE_FILL_ALL_FIELDS);
			return;
		}

		// Extract user data from fields
		String username = ACCOUNT_INFO.get(5);
		String email = ACCOUNT_INFO.get(4);
		String firstname = ACCOUNT_INFO.get(3);
		String lastname = ACCOUNT_INFO.get(2);
		String password = ACCOUNT_INFO.get(1);
		String confirmPassword = ACCOUNT_INFO.get(0);

		if (!password.equals(confirmPassword)) {
			showErrorMessage(ERROR_MESSAGE_PASSWORDS_NOT_EQUAL);
			return;
		}

		if (userAlreadyExists(username, email)) {
			showErrorMessage(ERROR_MESSAGE_USERNAME_OR_EMAIL_ALREADY_EXISTS);
			return;
		}

		Player player = createPlayerObject(username, email, firstname, lastname, password);

		if (player == null) {
			return;
		}

		if (!USER_DATABASE_MANAGER.addUser(player)) {
			showErrorMessage(ERROR_MESSAGE_CANNOT_ADD_USER_TO_DB);
			return;
		}

		showInformationMessage("Konto erfolgreich erstellt!");

		// Redirect to login
		window.dispose();
		new Login().setVisible(true);
	}

	public static void login(Window window) {
		ACCOUNT_INFO.clear();

		if (areFieldsEmpty(window)) {
			showErrorMessage(ERROR_MESSAGE_FILL_ALL_FIELDS);
			return;
		}

		// Extract user data from fields
		String username = ACCOUNT_INFO.get(1);
		String password = ACCOUNT_INFO.get(0);

		// Query to fetch user details
		String sqlQuery = "SELECT playerName, password FROM users WHERE playerName = ? LIMIT 1;";

		try (PreparedStatement preparedStatement = USER_DATABASE_MANAGER.getConnection().prepareStatement(sqlQuery)) {
			preparedStatement.setString(1, EncryptionUtils.encrypt(username, SECRET_KEY));

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					String storedEncryptedPassword = resultSet.getString("password");
					String storedDecryptedPassword = EncryptionUtils.decrypt(storedEncryptedPassword, SECRET_KEY);

					if (storedDecryptedPassword.equals(password)) {
						// Successful login
						showInformationMessage("Login erfolgreich!");

						// window.dispose();
						// Redirect to main application window or dashboard
						// Example: new Dashboard().setVisible(true);
					} else {
						showErrorMessage(ERROR_MESSAGE_WRONG_USERNAME_OR_PASSWORD);
					}
				} else {
					showErrorMessage(ERROR_MESSAGE_WRONG_USERNAME_OR_PASSWORD);
				}
			}
		} catch (Exception e) {
			Logger.logError("Error during login process: " + e.getMessage());
			showErrorMessage("Es ist ein unerwarteter Fehler aufgetreten. Bitte versuche es später erneut.");
		}
	}


	/**
	 * Checks if the input fields in the given window are empty.
	 *
	 * @param window
	 * 	the parent window containing user input fields
	 *
	 * @return true if any field is empty; false otherwise
	 */
	private static boolean areFieldsEmpty(Window window) {
		collectTextsFromWindow(window, ACCOUNT_INFO);
		return ACCOUNT_INFO.stream().anyMatch(String::isEmpty);
	}

	/**
	 * Checks if a user with the given username or email already exists in the database.
	 *
	 * @param username
	 * 	the username to check
	 * @param email
	 * 	the email to check
	 *
	 * @return true if the user exists; false otherwise
	 */
	private static boolean userAlreadyExists(String username, String email) {
		String sqlQuery = "SELECT 1 FROM users WHERE playerName = ? OR email = ? LIMIT 1;";
		try (PreparedStatement preparedStatement = USER_DATABASE_MANAGER.getConnection().prepareStatement(sqlQuery)) {
			preparedStatement.setString(1, EncryptionUtils.encrypt(username, SECRET_KEY));
			preparedStatement.setString(2, EncryptionUtils.encrypt(email, SECRET_KEY));

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next(); // True if a user exists
			}
		} catch (Exception e) {
			Logger.logError("Error while checking username and email: " + e.getMessage());
		}

		return false;
	}

	/**
	 * Creates a Player object with the provided user details.
	 *
	 * @param username
	 * 	the username of the player
	 * @param email
	 * 	the email of the player
	 * @param firstname
	 * 	the first name of the player
	 * @param lastname
	 * 	the last name of the player
	 * @param password
	 * 	the password of the player
	 *
	 * @return a new Player object
	 */
	private static Player createPlayerObject(String username, String email, String firstname, String lastname, String password) {
		String encryptedUsername;
		String encryptedEmail;
		String encryptedFirstname;
		String encryptedLastname;
		String encryptedPassword;

		try {
			encryptedUsername = EncryptionUtils.encrypt(username, SECRET_KEY);
			encryptedEmail = EncryptionUtils.encrypt(email, SECRET_KEY);
			encryptedFirstname = EncryptionUtils.encrypt(firstname, SECRET_KEY);
			encryptedLastname = EncryptionUtils.encrypt(lastname, SECRET_KEY);
			encryptedPassword = EncryptionUtils.encrypt(password, SECRET_KEY);

		} catch (Exception e) {
			Logger.logError("Error while encrypting user data: " + e.getMessage());
			showErrorMessage(ERROR_MESSAGE_CANNOT_ADD_USER_TO_DB);
			return null;
		}

		return new Player(
			new Id("", ""),
			new Name(encryptedUsername, encryptedFirstname, encryptedLastname),
			new Language("", ""),
			new Elo(1600, 0),
			new Birthday(0, 0, 0),
			new Contact(encryptedEmail, "", ""),
			encryptedPassword,
			new PasswordRecovery("", ""),
			""
		);
	}

	/**
	 * Displays an error message in a dialog box.
	 *
	 * @param message
	 * 	the error message to display
	 */
	private static void showErrorMessage(String message) {
		new ErrorMessage(AccountManager.ERROR_MESSAGE_TITLE, message);
	}

	/**
	 * Displays an information message in a dialog box.
	 *
	 * @param message
	 * 	the information message to display
	 */
	private static void showInformationMessage(String message) {
		new InformationMessage(AccountManager.INFORMATION_MESSAGE_TITLE, message);
	}

	/**
	 * Recursively collects text from JTextPane and JPasswordField components within the given window.
	 *
	 * @param component
	 * 	the current component to process
	 * @param collectedText
	 * 	the list to store collected texts
	 */
	private static void collectTextsFromWindow(Component component, List<String> collectedText) {
		if (component instanceof JTextPane textPane) {
			collectedText.add(textPane.getText().trim());
		} else if (component instanceof JPasswordField passwordField) {
			collectedText.add(new String(passwordField.getPassword()).trim()); // Passwort als String
		} else if (component instanceof Container container) {
			for (Component child : container.getComponents()) {
				collectTextsFromWindow(child, collectedText);
			}
		}
	}
}
