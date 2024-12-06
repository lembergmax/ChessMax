/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.utils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Utility class for managing custom tab traversal between specified components.
 * Allows for cycling focus through a predefined list of components using the Tab key.
 */
public class TabTraversalManager {

	/**
	 * Enables custom Tab traversal for a given list of components.
	 * Pressing Tab moves focus to the next component in the list,
	 * and pressing Shift+Tab moves to the previous component.
	 *
	 * @param components
	 * 	the list of components to include in the custom tab traversal
	 */
	public static void enableTabTraversal(List<Component> components) {
		// Add a KeyEventDispatcher to intercept Tab key events globally
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event -> {
			// Handle Tab key presses
			if (isTabKeyPress(event)) {
				Component currentFocus = getCurrentFocusedComponent();

				// Only proceed if the current focus is within the provided components
				if (components.contains(currentFocus)) {
					boolean isShiftPressed = isShiftKeyPressed(event);
					int currentIndex = components.indexOf(currentFocus);

					// Determine the next index based on the direction (forward or backward)
					int nextIndex = calculateNextIndex(currentIndex, components.size(), isShiftPressed);

					// Request focus for the next component
					components.get(nextIndex).requestFocus();
					return true; // Consume the Tab key event
				}
			}

			return false; // Allow the event to be processed by other listeners
		});
	}

	/**
	 * Checks if the given KeyEvent corresponds to a Tab key press.
	 *
	 * @param event
	 * 	the KeyEvent to evaluate
	 *
	 * @return true if the event is a Tab key press, false otherwise
	 */
	private static boolean isTabKeyPress(KeyEvent event) {
		return event.getKeyCode() == KeyEvent.VK_TAB && event.getID() == KeyEvent.KEY_PRESSED;
	}

	/**
	 * Retrieves the currently focused component.
	 *
	 * @return the currently focused component, or null if none is focused
	 */
	private static Component getCurrentFocusedComponent() {
		return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
	}

	/**
	 * Determines if the Shift key is pressed during the given KeyEvent.
	 *
	 * @param event
	 * 	the KeyEvent to evaluate
	 *
	 * @return true if the Shift key is pressed, false otherwise
	 */
	private static boolean isShiftKeyPressed(KeyEvent event) {
		return (event.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;
	}

	/**
	 * Calculates the next index in the traversal sequence.
	 * Ensures that the index wraps around the list bounds when reaching the start or end.
	 *
	 * @param currentIndex
	 * 	the current index of the focused component
	 * @param listSize
	 * 	the total number of components in the list
	 * @param isBackwardTraversal
	 * 	true if moving backward (Shift+Tab), false if moving forward (Tab)
	 *
	 * @return the next index in the traversal sequence
	 */
	private static int calculateNextIndex(int currentIndex, int listSize, boolean isBackwardTraversal) {
		int direction = isBackwardTraversal ? -1 : 1;
		return (currentIndex + direction + listSize) % listSize;
	}
}
