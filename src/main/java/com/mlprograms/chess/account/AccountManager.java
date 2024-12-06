/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.account;

import com.mlprograms.chess.account.ui.Login;
import com.mlprograms.chess.database.UserDatabaseManager;
import com.mlprograms.chess.player.*;
import com.mlprograms.chess.utils.Logger;
import com.mlprograms.chess.utils.ui.ErrorMessage;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

	private static final ArrayList<String> accountInfo = new ArrayList<>();
	private static final UserDatabaseManager userDatabaseManager = new UserDatabaseManager();

	/**
	 * Creates a new account based on the input fields in the given window.
	 *
	 * @param window
	 * 	the parent window containing user input fields
	 */
	public static void createAccount(Window window) {
		accountInfo.clear();

		if (areFieldsEmpty(window)) {
			showErrorMessage("Attention", "Please fill in all fields to continue.");
			return;
		}

		// Extract user data from fields
		String username = accountInfo.get(5);
		String email = accountInfo.get(4);
		String firstname = accountInfo.get(3);
		String lastname = accountInfo.get(2);
		String password = accountInfo.get(1);
		String confirmPassword = accountInfo.get(0);

		if (!password.equals(confirmPassword)) {
			showErrorMessage("Attention", "Passwords do not match.");
			return;
		}

		if (isUserAlreadyExists(username, email)) {
			showErrorMessage("Attention", "Username or email address is already taken.");
			return;
		}

		Player player = createPlayerObject(username, email, firstname, lastname, password);

		if (!userDatabaseManager.addUser(player)) {
			showErrorMessage("Attention", "An error occurred while adding the user. Please try again later.");
			return;
		}

		// Redirect to login
		window.dispose();
		new Login().setVisible(true);
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
		collectTextsFromWindow(window, accountInfo);
		return accountInfo.stream().anyMatch(String::isEmpty);
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
	private static boolean isUserAlreadyExists(String username, String email) {
		String sqlQuery = "SELECT 1 FROM users WHERE playerName = ? OR email = ? LIMIT 1;";
		try (PreparedStatement preparedStatement = userDatabaseManager.getConnection().prepareStatement(sqlQuery)) {
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, email);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next(); // True if a user exists
			}
		} catch (SQLException e) {
			Logger.logError("Error while checking username and email: " + e.getMessage());
			return false;
		}
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
		return new Player(
			new Id("", ""),
			new Name(username, firstname, lastname),
			new Language("", ""),
			new Elo(1600, 0),
			new Birthday(0, 0, 0),
			new Contact(email, "", ""),
			password,
			new PasswordRecovery("", ""),
			""
		);
	}

	/**
	 * Displays an error message in a dialog box.
	 *
	 * @param title
	 * 	the title of the dialog box
	 * @param message
	 * 	the error message to display
	 */
	private static void showErrorMessage(String title, String message) {
		new ErrorMessage(title, message);
	}

	/**
	 * Recursively collects text from JTextPane components within the given window.
	 *
	 * @param component
	 * 	the current component to process
	 * @param collectedText
	 * 	the list to store collected texts
	 */
	private static void collectTextsFromWindow(Component component, List<String> collectedText) {
		if (component instanceof JTextPane textPane) {
			collectedText.add(textPane.getText().trim());
		} else if (component instanceof Container container) {
			for (Component child : container.getComponents()) {
				collectTextsFromWindow(child, collectedText);
			}
		}
	}
}
