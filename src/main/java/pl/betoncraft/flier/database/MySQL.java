package pl.betoncraft.flier.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends Database {
	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;

	public MySQL(String hostname, String port, String database, String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
	}

	public Connection openConnection() throws SQLException {
		Connection connection = null;
		connection = DriverManager.getConnection(
				"jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?&useSSL=false", this.user,
				this.password);
		return connection;
	}
}
