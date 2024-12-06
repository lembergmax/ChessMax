/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.database;

import com.mlprograms.chess.player.Player;
import com.mlprograms.chess.utils.Logger;

import java.sql.*;

public class UserDatabaseManager {

	private static final String DATABASE_URL = "jdbc:sqlite:db/user_data.db";

	/**
	 * Initializes the database and creates the user table if it does not exist.
	 */
	public void initializeDatabase() {
		String createTableSQL = """
			CREATE TABLE IF NOT EXISTS users (
			    playerId INTEGER PRIMARY KEY AUTOINCREMENT,
			    playerName TEXT UNIQUE NOT NULL,
			    firstName TEXT NOT NULL,
			    lastName TEXT NOT NULL,
			    language TEXT NOT NULL,
			    languageCode TEXT NOT NULL,
			    eloGame INTEGER DEFAULT 1200,
			    eloOfficial INTEGER DEFAULT 0,
			    birthDate TEXT NOT NULL,
			    email TEXT NOT NULL UNIQUE,
			    secondaryEmail TEXT,
			    phoneNumber TEXT,
			    password TEXT NOT NULL,
			    passwordRecoveryQuestion TEXT,
			    passwordRecoveryAnswer TEXT,
			    timezone TEXT NOT NULL
			);
			""";

		try (Connection connection = DriverManager.getConnection(DATABASE_URL);
		     Statement statement = connection.createStatement()) {

			statement.execute(createTableSQL);
			Logger.logSuccess("Database initialized successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts a new player into the database.
	 *
	 * @param player
	 * 	the player to be added.
	 *
	 * @throws Exception
	 * 	if insertion fails or if the player name already exists.
	 */
	public void addUser(Player player) {
		String insertSQL = """
			INSERT INTO users (
			    playerName, firstName, lastName, language, languageCode,
			    eloGame, eloOfficial, birthDate, email, secondaryEmail, phoneNumber,
			    password, passwordRecoveryQuestion, passwordRecoveryAnswer, timezone
			) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
			""";

		try (Connection connection = DriverManager.getConnection(DATABASE_URL);
		     PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

			if (isPlayerNameExists(player.getName().getUsername())) {
				handleDuplicatePlayerName(player.getName().getUsername());
				return;
			}

			preparedStatement.setString(1, player.getName().getUsername());
			preparedStatement.setString(2, player.getName().getFirstName());
			preparedStatement.setString(3, player.getName().getLastName());
			preparedStatement.setString(4, player.getLanguage().getLanguageName());
			preparedStatement.setString(5, player.getLanguage().getLanguageShortName());
			preparedStatement.setInt(6, player.getElo().getInGameElo());
			preparedStatement.setInt(7, player.getElo().getOfficialElo());
			preparedStatement.setString(8, player.getBirthday().toString());
			preparedStatement.setString(9, player.getContact().getEmail());
			preparedStatement.setString(10, player.getContact().getSecondEmail());
			preparedStatement.setString(11, player.getContact().getPhoneNumber());
			preparedStatement.setString(12, player.getPassword());
			preparedStatement.setString(13, player.getPasswordRecovery().getQuestion());
			preparedStatement.setString(14, player.getPasswordRecovery().getAnswer());
			preparedStatement.setString(15, player.getTimeZone());

			preparedStatement.executeUpdate();
			Logger.logSuccess("User successfully added: " + player.getName().getUsername());
		} catch (Exception e) {
			Logger.logError("Failed to add user: " + e.getMessage());
		}
	}

	/**
	 * Checks if a player name already exists in the database.
	 *
	 * @param playerName
	 * 	the player name to check.
	 *
	 * @return true if the player name exists, false otherwise.
	 *
	 * @throws SQLException
	 * 	if the query fails.
	 */
	private boolean isPlayerNameExists(String playerName) throws SQLException {
		String querySQL = "SELECT COUNT(*) FROM users WHERE playerName = ?;";
		try (Connection connection = DriverManager.getConnection(DATABASE_URL);
		     PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
			preparedStatement.setString(1, playerName);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.getInt(1) > 0;
			}
		}
	}

	/**
	 * Deletes a user from the database by player ID.
	 *
	 * @param playerId
	 * 	the ID of the player to be deleted.
	 *
	 * @throws SQLException
	 * 	if the deletion fails.
	 */
	public void deleteUserById(int playerId) {
		String deleteSQL = "DELETE FROM users WHERE playerId = ?;";

		try (Connection connection = DriverManager.getConnection(DATABASE_URL);
		     PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

			preparedStatement.setInt(1, playerId);

			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows == 0) {
				Logger.logError("No user found with the given player ID: " + playerId);
			} else {
				Logger.logSuccess("User successfully deleted with ID: " + playerId);
			}
		} catch (SQLException e) {
			Logger.logError("Failed to delete user by ID: " + e.getMessage());
		}
	}

	/**
	 * Deletes a user from the database by player name.
	 *
	 * @param playerName
	 * 	the name of the player to be deleted.
	 *
	 * @throws SQLException
	 * 	if the deletion fails.
	 */
	public void deleteUserByName(String playerName) {
		String deleteSQL = "DELETE FROM users WHERE playerName = ?;";

		try (Connection connection = DriverManager.getConnection(DATABASE_URL);
		     PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

			preparedStatement.setString(1, playerName);

			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows == 0) {
				Logger.logError("No user found with the given player name: " + playerName);
			} else {
				Logger.log("User successfully deleted with name: " + playerName);
			}
		} catch (SQLException e) {
			Logger.logError("Failed to delete user by name: " + e.getMessage());
		}
	}

	/**
	 * Executes a SELECT query on the database.
	 *
	 * @param query
	 * 	the SQL SELECT query to execute.
	 * @param params
	 * 	optional parameters for the prepared statement.
	 *
	 * @return the Player object if found, null otherwise.
	 */
	public ResultSet executeSelectQuery(String query, Object... params) {
		try (Connection connection = DriverManager.getConnection(DATABASE_URL);
		     PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[ i ]);
			}

			return preparedStatement.executeQuery();
		} catch (SQLException e) {
			Logger.logError("Failed to execute SELECT query: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Handles the scenario when a duplicate player name is found.
	 *
	 * @param playerName
	 * 	the duplicate player name.
	 */
	private void handleDuplicatePlayerName(String playerName) {
		Logger.logError("Duplicate player name found: " + playerName);
		// TODO: Custom logic for handling duplicate player names (e.g., logging, notifying the user)
	}
}
