/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger is a utility class for logging messages to the console with different
 * levels of severity. It supports colored output in the console for better
 * visibility and categorization of log messages.
 */
public class Logger {

	// ANSI escape codes for console text colors
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m"; // Red for error messages
	public static final String ANSI_YELLOW = "\u001B[33m"; // Yellow for warning messages
	public static final String ANSI_PURPLE = "\u001B[35m"; // Purple for debug messages
	public static final String ANSI_GREEN = "\u001B[32m"; // Green for success messages

	// Log message levels
	public static final String INFO = "INFO";
	public static final String ERROR = "ERROR";
	public static final String WARN = "WARN";
	public static final String FATAL = "FATAL";
	public static final String DEBUG = "DEBUG";
	public static final String SUCCESS = "SUCCESS";

	/**
	 * Logs a message with the specified severity state, class name, and message
	 * content. The log output includes a timestamp, the severity level in brackets,
	 * the class name, and the actual message. The first part of the log (timestamp,
	 * state, class name) is printed in normal color, while the message is printed
	 * in a specific color based on the severity state.
	 *
	 * @param state
	 * 	The severity level of the log (e.g., INFO, ERROR).
	 * @param className
	 * 	The name of the class from which the log is made.
	 * @param message
	 * 	The message content to be logged.
	 * @param <T>
	 * 	The type of the message.
	 */
	public static <T> void log(String state, String className, T message) {
		String formattedMessage = formatMessage(state, className, String.valueOf(message));

		// Determine the log color based on the state and print the message
		String logColor = getLogColor(state);

		// Print the first part of the message (timestamp, state, class name) in normal
		// color
		println(formattedMessage.substring(0, formattedMessage.indexOf("\n")) + ANSI_RESET);
		// Print the message part in the determined log color
		println(logColor + formattedMessage.substring(formattedMessage.indexOf("\n") + 1) + ANSI_RESET);
	}

	/**
	 * Logs a default info message without specifying a state.
	 *
	 * @param message
	 * 	The message content to be logged.
	 * @param <T>
	 * 	The type of the message.
	 */
	public static <T> void log(T message) {
		logInfo(message);
	}

	/**
	 * Logs a success message.
	 *
	 * @param message
	 * 	The message content to be logged.
	 * @param <T>
	 * 	The type of the message.
	 */
	public static <T> void logSuccess(T message) {
		String className = getClassName();
		log(SUCCESS, className, message);
	}

	/**
	 * Logs an info message.
	 *
	 * @param message
	 * 	The message content to be logged.
	 * @param <T>
	 * 	The type of the message.
	 */
	public static <T> void logInfo(T message) {
		String className = getClassName();
		log(INFO, className, message);
	}

	/**
	 * Logs a warning message.
	 *
	 * @param message
	 * 	The message content to be logged.
	 * @param <T>
	 * 	The type of the message.
	 */
	public static <T> void logWarn(T message) {
		String className = getClassName();
		log(WARN, className, message);
	}

	/**
	 * Logs an error message.
	 *
	 * @param message
	 * 	The message content to be logged.
	 * @param <T>
	 * 	The type of the message.
	 */
	public static <T> void logError(T message) {
		String className = getClassName();
		log(ERROR, className, message);
	}

	/**
	 * Logs a fatal error message.
	 *
	 * @param message
	 * 	The message content to be logged.
	 * @param <T>
	 * 	The type of the message.
	 */
	public static <T> void logFatal(T message) {
		String className = getClassName();
		log(FATAL, className, message);
	}

	/**
	 * Logs a debug message.
	 *
	 * @param message
	 * 	The message content to be logged.
	 * @param <T>
	 * 	The type of the message.
	 */
	public static <T> void logDebug(T message) {
		String className = getClassName();
		log(DEBUG, className, message);
	}

	/**
	 * Formats the log message with a timestamp, state, class name, and the actual
	 * message. The message is displayed on the next line, indented by 7 spaces. The
	 * timestamp, state, and class name are formatted in normal color, while the
	 * message is formatted in the status color.
	 *
	 * @param state
	 * 	The severity level of the log (e.g., INFO, ERROR).
	 * @param className
	 * 	The name of the class logging the message.
	 * @param message
	 * 	The message content to be logged.
	 *
	 * @return The formatted log message as a String, with the message on a new
	 * 	line.
	 */
	private static String formatMessage(String state, String className, String message) {
		String logColor = getLogColor(state);
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		// Pad the state and class name for consistent formatting
		String paddedState = String.format("%s", logColor + "[" + state + "]" + ANSI_RESET);
		String paddedClassName = String.format("%s", "[" + className + "]");

		// Construct the final log message with the message on a new line
		return String.format("%s %s %s\n       %s", timestamp, paddedState, paddedClassName, message);
	}

	private static String getLogColor(String state) {
		return switch (state.toUpperCase()) {
			case ERROR, FATAL -> ANSI_RED;
			case WARN -> ANSI_YELLOW;
			case DEBUG -> ANSI_PURPLE;
			case SUCCESS -> ANSI_GREEN;
			default -> ANSI_RESET; // Normal color for other states
		};
	}

	/**
	 * Prints the given object to the console.
	 *
	 * @param object
	 * 	The object to be printed.
	 * @param <T>
	 * 	The type of the object.
	 */
	public static <T> void println(T object) {
		System.out.println(object);
	}

	/**
	 * Retrieves the name of the class that is calling the logging method.
	 *
	 * @return The name of the calling class, or "UnknownClass" if it cannot be
	 * 	determined.
	 */
	private static String getClassName() {
		StackTraceElement[] stackTrace = new Exception().getStackTrace();
		int index = 3; // The index in the stack trace to find the class name

		if (stackTrace.length > index) {
			return stackTrace[ index ].getClassName();
		} else {
			return "UnknownClass";
		}
	}

}
