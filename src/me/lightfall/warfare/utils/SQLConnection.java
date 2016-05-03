package me.lightfall.warfare.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class SQLConnection {
	private String url;
	private String user;
	private String pass;
	private String database;
	private Connection sql;
	private Statement statement;
	private Map<String, PreparedStatement> cachedStatements = new HashMap<String, PreparedStatement>();
	
	
	public SQLConnection(String url, String user, String pass, String database)
	{
		this.url = url;
		this.user = user;
		this.pass = pass;
		this.database = database;
	}
	
	public void connect() throws SQLException
	{
		System.out.println(Msg.CONSOLE_PREFIX + "Connecting to SQL Database: " + url + "/" + database);
		sql = DriverManager.getConnection("jdbc:mysql://" + url + "/" + database, user, pass);
		cachedStatements.clear();
	}
	
	public boolean isValid()
	{
		try{
			checkConnection();
			if(sql.isValid(5))
				return true;
			return false;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	private void checkConnection() throws SQLException
	{
		try{
			if(sql.isValid(5))
				return;
			else
				connect();
		}
		catch(NullPointerException e)
		{
			connect();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public PreparedStatement prepare(String s) throws SQLException
	{
		checkConnection();
		
		PreparedStatement statement = cachedStatements.get(s);
		if(statement == null)
		{
			statement = sql.prepareStatement(s);
			cachedStatements.put(s, statement);
		}
		
		return statement;
	}
	
	protected void finalize() throws Throwable {
        try {
            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            super.finalize();
        }
    }	
}
