package pl.betoncraft.flier.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite
  extends Database
{
  private final String dbLocation;
  
  public SQLite(String dbLocation)
  {
    this.dbLocation = dbLocation;
  }
  
  public Connection openConnection()
    throws SQLException
  {
    File file = new File(this.dbLocation);
    if (!file.exists()) {
      try
      {
        file.createNewFile();
      }
      catch (IOException localIOException) {}
    }
    Connection connection = null;
    connection = 
      DriverManager.getConnection("jdbc:sqlite:" + this.dbLocation);
    return connection;
  }
}
