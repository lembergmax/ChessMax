/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.utils;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Utility class for managing JSON files. Provides methods to create, read,
 * write, update, and delete JSON files, as well as modify their content.
 */
public class JsonFileManager {

	/**
	 * Creates a new JSON file with the specified content.
	 *
	 * @param filePath
	 * 	the path to the JSON file to be created
	 * @param jsonObject
	 * 	the JSON content to be written into the file
	 */
	public static void createJsonFileAndWrite(String filePath, JSONObject jsonObject) {
		try (FileWriter fileWriter = new FileWriter(filePath)) {

			// If the JSONObject is empty, add default values
			if(jsonObject.isEmpty()) {
				jsonObject.put("x", 0);
				jsonObject.put("y", 0);
			}

			fileWriter.write(jsonObject.toString(4)); // Format JSON with indentation
			// Logger.logSuccess("JSON file created successfully: " + filePath);
		} catch (IOException e) {
			Logger.logError("Error creating JSON file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Reads a JSON file and returns its content as a JSONObject.
	 *
	 * @param filePath
	 * 	the path to the JSON file to be read
	 *
	 * @return the JSONObject representation of the file content, or null if an error occurs
	 */
	public static JSONObject readJsonFile(String filePath) {
		try {
			Path path = Paths.get(filePath);

			// If the File does not exist create it
			if(Files.notExists(path)) {
				createJsonFileAndWrite(filePath, new JSONObject());
			}

			String content = Files.readString(path); // Read all bytes as a string
			return new JSONObject(content);
		} catch (IOException e) {
			Logger.logError("Error reading JSON file: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Writes data into an existing JSON file, overwriting its current content.
	 *
	 * @param filePath
	 * 	the path to the JSON file to be updated
	 * @param jsonObject
	 * 	the new JSON content to be written into the file
	 */
	public static void writeJsonFile(String filePath, JSONObject jsonObject) {
		try (FileWriter fileWriter = new FileWriter(filePath)) {
			fileWriter.write(jsonObject.toString(4)); // Format JSON with indentation
			Logger.logSuccess("JSON file updated successfully: " + filePath);
		} catch (IOException e) {
			Logger.logError("Error writing to JSON file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Deletes a JSON file at the specified path.
	 *
	 * @param filePath
	 * 	the path to the JSON file to be deleted
	 */
	public static void deleteJsonFile(String filePath) {
		try {
			Files.delete(Paths.get(filePath));
			Logger.logSuccess("JSON file deleted successfully: " + filePath);
		} catch (IOException e) {
			Logger.logError("Error deleting JSON file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Adds a new key-value pair to an existing JSON file.
	 *
	 * @param filePath
	 * 	the path to the JSON file to be updated
	 * @param key
	 * 	the key to be added
	 * @param value
	 * 	the value to be associated with the key
	 */
	public static void addElementToJsonFile(String filePath, String key, Object value) {
		JSONObject jsonObject = readJsonFile(filePath);
		if (jsonObject != null) {
			jsonObject.put(key, value);
			writeJsonFile(filePath, jsonObject);
		} else {
			Logger.logError("Failed to add element: JSON file could not be read.");
		}
	}

	/**
	 * Removes a key-value pair from an existing JSON file by the specified key.
	 *
	 * @param filePath
	 * 	the path to the JSON file to be updated
	 * @param key
	 * 	the key to be removed
	 */
	public static void removeElementFromJsonFile(String filePath, String key) {
		JSONObject jsonObject = readJsonFile(filePath);
		if (jsonObject != null) {
			if (jsonObject.has(key)) {
				jsonObject.remove(key);
				writeJsonFile(filePath, jsonObject);
			} else {
				Logger.logError("Key not found: " + key);
			}
		} else {
			Logger.logError("Failed to remove element: JSON file could not be read.");
		}
	}
}
