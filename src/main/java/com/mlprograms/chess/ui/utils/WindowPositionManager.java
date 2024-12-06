/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.ui.utils;

import com.mlprograms.chess.utils.JsonFileManager;
import org.json.JSONObject;

import java.awt.*;

/**
 * Manages the position of a window by saving and loading its coordinates
 * to and from a JSON file. This ensures the window reopens at its last position.
 */
public class WindowPositionManager {

    private static final String FILE_PATH = "window_settings.json"; // Path to the JSON file for saving settings
    private final Window window; // The window whose position is being managed

    /**
     * Constructs a WindowPositionManager for a specific window.
     *
     * @param window the window whose position will be managed
     */
    public WindowPositionManager(Window window) {
        this.window = window;
    }

    /**
     * Saves the current position of the window to a JSON file.
     * The x and y coordinates of the window are stored as "x" and "y" in the JSON file.
     */
    public void savePosition() {
        JSONObject jsonObject = new JSONObject();

        // Store the current window position in the JSON object
        jsonObject.put("x", window.getX());
        jsonObject.put("y", window.getY());

        // Save the JSON object to a file
        JsonFileManager.createJsonFileAndWrite(FILE_PATH, jsonObject);
    }

    /**
     * Loads the window position from the JSON file and applies it to the window.
     * If the file exists and contains valid data, the window is moved to the saved position.
     * Otherwise, the position remains unchanged.
     */
    public void loadPosition() {
        JSONObject jsonObject = JsonFileManager.readJsonFile(FILE_PATH);

        // Ensure the JSON object is not null before accessing its values
        if (jsonObject != null) {
            int x = jsonObject.optInt("x", window.getX()); // Default to current x if not found
            int y = jsonObject.optInt("y", window.getY()); // Default to current y if not found

            // Set the window's location to the loaded position
            window.setLocation(x, y);
        } else {
            System.out.println("No saved position found. Using default location.");
        }
    }
}
