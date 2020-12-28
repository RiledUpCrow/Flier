package pl.betoncraft.flier.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public abstract class Database {
	private Connection con;
	private Saver saver;
	private HashMap<String, PreparedStatement> statements = new HashMap<>();

	public Database() {
		this.saver = new Saver();
	}

	public void disconnect() throws SQLException {
		this.saver.end();
		this.con.close();
	}

	public void registerStatement(String name, String mysql, String sqlite) throws SQLException {
		this.statements.put(name, getConnection().prepareStatement((this instanceof MySQL) ? mysql : sqlite));
	}

	public void registerStatement(String name, String statement) throws SQLException {
		registerStatement(name, statement, statement);
	}

	public void execute(String name, Object[] args) throws SQLException {
		PreparedStatement stmt = (PreparedStatement) this.statements.get(name);
		for (int i = 0; i < args.length; i++) {
			stmt.setObject(i + 1, args[i]);
		}
		stmt.executeUpdate();
	}

	public void execute(String mysql, String sqlite) throws SQLException {
		getConnection().createStatement().execute((this instanceof MySQL) ? mysql : sqlite);
	}

	public void execute(String query) throws SQLException {
		execute(query, query);
	}

	public void update(String name, Object[] args) {
		this.saver.add((PreparedStatement) this.statements.get(name), args);
	}

	public void update(String mysql, String sqlite) throws SQLException {
		getConnection().createStatement().executeUpdate((this instanceof MySQL) ? mysql : sqlite);
	}

	public void update(String query) throws SQLException {
		update(query, query);
	}

	public ResultSet query(String name, Object[] args) throws SQLException {
		PreparedStatement stmt = (PreparedStatement) this.statements.get(name);
		for (int i = 0; i < args.length; i++) {
			stmt.setObject(i + 1, args[i]);
		}
		return stmt.executeQuery();
	}

	public ResultSet query(String mysql, String sqlite) throws SQLException {
		return getConnection().createStatement().executeQuery((this instanceof MySQL) ? mysql : sqlite);
	}

	public ResultSet query(String query) throws SQLException {
		return query(query, query);
	}

	Connection getConnection() throws SQLException {
		if (this.con == null) {
			this.con = openConnection();
		}
		return this.con;
	}

	protected abstract Connection openConnection() throws SQLException;
}
