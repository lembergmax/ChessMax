/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.server;

import com.mlprograms.chess.utils.ConfigReader;
import com.mlprograms.chess.utils.Logger;

import java.net.ServerSocket;
import java.net.Socket;

public class SecureServer {

	private static final ConfigReader configReader = new ConfigReader();

	private final String SERVER_SECTION = "Server";
	private final String SERVER_HOST = configReader.getValue(SERVER_SECTION, "SERVER_HOST");
	private final int SERVER_PORT = Integer.parseInt(configReader.getValue(SERVER_SECTION, "SERVER_PORT"));

	private ServerSocket serverSocket;

	public SecureServer() {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);

			run();
		} catch (Exception e) {
			Logger.logError("Error creating server socket: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// TODO: https://youtu.be/O7TuxKJXBII?si=ZgoIxwLEJxfcvpv4&t=372

	public void run() {
		while (true) {
			try {
				Logger.logInfo("Waiting for client connection..." + serverSocket.getInetAddress());
				Socket client = serverSocket.accept();

			} catch (Exception e) {
				Logger.logError("Error accepting client connection: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new SecureServer();
	}

}
