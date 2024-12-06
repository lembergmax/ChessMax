/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.server;

import com.mlprograms.chess.utils.ConfigReader;

public class UserClient {

	private final ConfigReader configReader = new ConfigReader();

	private final String SERVER_SECTION = "Server";
	private final String SERVER_HOST = configReader.getValue(SERVER_SECTION, "SERVER_HOST");
	private final int SERVER_PORT = Integer.parseInt(configReader.getValue(SERVER_SECTION, "SERVER_PORT"));

	public UserClient() {

	}

}
