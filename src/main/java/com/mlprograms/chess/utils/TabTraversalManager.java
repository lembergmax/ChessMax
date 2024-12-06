/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.utils;

import java.awt.KeyboardFocusManager;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.List;

public class TabTraversalManager {

    public static void enableTabTraversal(List<Component> components) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getKeyCode() == KeyEvent.VK_TAB && e.getID() == KeyEvent.KEY_PRESSED) {
                Component currentFocus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                // Check if current focus is one of the given components
                if (components.contains(currentFocus)) {
                    boolean shiftPressed = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;
                    int currentIndex = components.indexOf(currentFocus);

                    // Calculate next or previous index
                    int nextIndex = (currentIndex + (shiftPressed ? -1 : 1) + components.size()) % components.size();
                    components.get(nextIndex).requestFocus();
                    return true; // Consume the event
                }
            }

            return false; // Pass the event to other listeners
        });
    }
}
