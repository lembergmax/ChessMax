package com.mlprograms.chess.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public static final String INFO = "INFO";
    public static final String ERROR = "ERROR";
    public static final String WARN = "WARN";
    public static final String FATAL = "FATAL";
    public static final String DEBUG = "DEBUG";

    public static final int MAX_STATE_LENGTH = 7;
    public static final int MAX_CLASS_LENGTH = 50;

    public static <T> void log(String state, String className, T message) {
        String formattedMessage = formatMessage(state, className, String.valueOf(message));

        switch (state.toUpperCase()) {
            case ERROR:
            case FATAL:
                println(ANSI_RED + formattedMessage + ANSI_RESET);
                break;
            case WARN:
                println(ANSI_YELLOW + formattedMessage + ANSI_RESET);
                break;
            case DEBUG:
                println(ANSI_PURPLE + formattedMessage + ANSI_RESET);
                break;
            default:
                println(formattedMessage);
                break;
        }
    }

    public static <T> void log(T message) {
        logInfo(message);
    }

    public static <T> void logInfo(T message) {
        String className = getClassName();
        log(INFO, className, message);
    }

    public static <T> void logWarn(T message) {
        String className = getClassName();
        log(WARN, className, message);
    }

    public static <T> void logError(T message) {
        String className = getClassName();
        log(ERROR, className, message);
    }

    public static <T> void logFatal(T message) {
        String className = getClassName();
        log(FATAL, className, message);
    }

    public static <T> void logDebug(T message) {
        String className = getClassName();
        log(DEBUG, className, message);
    }

    private static String formatMessage(String state, String className, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String paddedState = String.format("%-" + MAX_STATE_LENGTH + "s", "[" + state + "]");
        String paddedClassName = String.format("%-" + MAX_CLASS_LENGTH + "s", "[" + className + "]");

        return String.format("%s %s %s %s", timestamp, paddedState, paddedClassName, message);
    }

    private static <T> void println(T object) {
        System.out.println(object);
    }

    private static String getClassName() {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        int index = 3;

        if (stackTrace.length > index) {
            return stackTrace[index].getClassName();
        } else {
            return "UnknownClass";
        }
    }
}
